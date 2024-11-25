package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.account.AccountDto;
import com.fcastro.accountservice.account.AccountRepository;
import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.cache.MemberCacheService;
import com.fcastro.accountservice.exception.NotAllowedException;
import com.fcastro.accountservice.exception.OneOwnerMemberMustExistException;
import com.fcastro.accountservice.permission.PermissionDto;
import com.fcastro.accountservice.role.Role;
import com.fcastro.accountservice.role.RoleDto;
import com.fcastro.accountservice.role.RoleEnum;
import com.fcastro.accountservice.role.RoleService;
import com.fcastro.commons.config.MessageTranslator;
import com.fcastro.commons.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountGroupMemberService {

    private final AccountGroupMemberRepository repository;
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final MemberCacheService groupMemberCacheService;

    public AccountGroupMemberService(AccountGroupMemberRepository repository, AccountRepository accountRepository, RoleService roleService, MemberCacheService groupMemberCacheService) {
        this.repository = repository;
        this.accountRepository = accountRepository;
        this.roleService = roleService;
        this.groupMemberCacheService = groupMemberCacheService;
    }

    public Optional<AccountGroupMemberDto> get(long accountGroupId, long accountId) {
        return repository.findById(AccountGroupMemberKey.builder().accountId(accountGroupId).accountId(accountId).build())
                .map(this::convertToDTO);
    }

    public Optional<AccountGroupMemberDto> getByGroupIdAndEmail(long accountGroupId, String email) {
        return repository.findByGroupIdAndEmail(accountGroupId, email)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<AccountGroupMemberDto> getAllByGroupId(long accountGroupId) {
        var listEntity = repository.findAllByAccountGroupId(accountGroupId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<AccountGroupMemberDto> getAllByAccountId(long accountId) {
        var listEntity = repository.findAllByAccountId(accountId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<AccountGroupMemberDto> getAllByEmail(String email) {
        var listEntity = repository.findAllByEmail(email);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //@Transactional
    public AccountGroupMemberDto createParentGroupMember(long accountId, long accountGroupId) {

        var groupMember = AccountGroupMember.builder()
                .accountId(accountId)
                .accountGroupId(accountGroupId)
                .role(Role.builder().id(RoleEnum.OWNER.value).build())
                .build();
        return convertToDTO(repository.save(groupMember));
    }

    //Adding OWNER to a Child group
    public AccountGroupMemberDto createChildGroupMember(String email, AccountGroup accountGroup) {

        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var groupMember = AccountGroupMember.builder()
                .accountId(account.getId())
                .accountGroupId(accountGroup.getId())
                .role(Role.builder().id(RoleEnum.OWNER.value).build())
                .build();

        var savedGroupMember = repository.save(groupMember);
        groupMemberCacheService.updateCache(account.getEmail(), accountGroup, RoleEnum.OWNER.value);
        return convertToDTO(savedGroupMember);
    }

    //Adding other members to an existing group
    //It should check and update MemberCache
    public AccountGroupMemberDto save(AccountGroupMemberDto dto) {

        //new member cannot be OWNER in the group
        if (RoleEnum.OWNER.value.equals(dto.getRole().getId())) {
            throw new OneOwnerMemberMustExistException(MessageTranslator.getMessage("error.only.one.owner.allowed"));
        }

        //User adding a member should be OWNER in the group
        var owner = repository.findByGroupIdAndEmail(dto.getAccountGroupId(), SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (owner != null && !RoleEnum.OWNER.value.equals(owner.getRole().getId())) {
            throw new NotAllowedException(MessageTranslator.getMessage("error.update.group.not.allowed"));
        }

        //New member's account exists
        var newMember = accountRepository.getReferenceById(dto.getAccountId());
        if (newMember == null) {
            throw new ResourceNotFoundException(MessageTranslator.getMessage("error.account.not.found"));
        }

        var entity = repository.save(convertToEntity(dto));
        groupMemberCacheService.updateCache(newMember.getEmail(), owner.getAccountGroup(), dto.getRole().getId());

        return convertToDTO(entity);
    }

    public void delete(long accountGroupId, long accountId) {
        //User should be OWNER in the Group, or User is deleting his own access to the group
        var owner = repository.findByGroupIdAndEmail(accountGroupId, SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (owner.getAccountId() != accountId && !RoleEnum.OWNER.value.equals(owner.getRole().getId())) {
            throw new NotAllowedException(MessageTranslator.getMessage("error.update.group.not.allowed"));
        }

        var key = AccountGroupMemberKey.builder().accountGroupId(accountGroupId).accountId(accountId).build();
        var member = repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.member.not.found")));

        //deleting an OWNER: there should be at least one Owner in the group
        if (RoleEnum.OWNER.value.equals(member.getRole().getId())) {
            throw new OneOwnerMemberMustExistException(MessageTranslator.getMessage("error.delete.owner"));
        }

        repository.delete(member);
        groupMemberCacheService.deleteFromCache(member.getAccount().getEmail(), member.getAccountGroup().getId(), member.getRole().getId());
    }

    public AccountGroupMember deleteAccount(long accountGroupId, long accountId) {

        var key = AccountGroupMemberKey.builder().accountGroupId(accountGroupId).accountId(accountId).build();

        var member = repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.member.not.found")));

        repository.delete(member);

        groupMemberCacheService.deleteFromCache(member.getAccount().getEmail(), member.getAccountGroup().getId(), member.getRole().getId());

        return member;
    }

    private AccountGroupMemberDto convertToDTO(AccountGroupMember entity) {
        if (entity == null) return null;

        Account account = entity.getAccount();
        AccountDto accountDto = null;
        if (account != null) {
            accountDto = AccountDto.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .email(account.getEmail())
                    .pictureUrl(account.getPictureUrl())
                    .build();
        }

        Role role = entity.getRole();
        RoleDto roleDto = null;
        if (role != null) {
            var permissions = role.getPermissions() == null ? null :
                    role.getPermissions().stream()
                            .map(p -> PermissionDto.builder().id(p.getId()).build())
                            .collect(Collectors.toList());

            roleDto = RoleDto.builder()
                    .id(role.getId())
                    .permissions(permissions)
                    .build();
        }

        return AccountGroupMemberDto.builder()
                .accountGroupId(entity.getAccountGroupId())
                .accountId(entity.getAccountId())
                .role(roleDto)
                .account(accountDto)
                .build();
    }

    private AccountGroupMember convertToEntity(AccountGroupMemberDto dto) {
        if (dto == null) return null;
        Role role = null;
        if (dto.getRole() != null) {
            role = Role.builder()
                    .id(dto.getRole().getId())
                    .build();
        }

        return AccountGroupMember.builder()
                .accountGroupId(dto.getAccountGroupId())
                .accountId(dto.getAccountId())
                .role(role)
                .build();
    }
}
