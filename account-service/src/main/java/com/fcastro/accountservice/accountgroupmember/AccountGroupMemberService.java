package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.account.AccountRepository;
import com.fcastro.accountservice.exception.NotAllowedException;
import com.fcastro.accountservice.exception.OneOwnerMemberMustExistException;
import com.fcastro.accountservice.role.Role;
import com.fcastro.accountservice.role.RoleService;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountDto;
import com.fcastro.security.core.model.AccountGroupMemberDto;
import com.fcastro.security.core.model.PermissionDto;
import com.fcastro.security.core.model.RoleDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountGroupMemberService {

    private final AccountGroupMemberRepository repository;
    private final AccountRepository accountRepository;
    private final RoleService roleService;

    public AccountGroupMemberService(AccountGroupMemberRepository repository, AccountRepository accountRepository, RoleService roleService) {
        this.repository = repository;
        this.accountRepository = accountRepository;
        this.roleService = roleService;
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

    @Transactional
    public AccountGroupMemberDto createParentGroupMember(long accountId, long accountGroupId) {

        var ownerRole = roleService.getRole(AccountGroupMemberRole.OWNER.value);

        var groupMember = AccountGroupMember.builder()
                .accountId(accountId)
                .accountGroupId(accountGroupId)
                .role(Role.builder().id(ownerRole.getId()).build())
                .build();
        return convertToDTO(repository.save(groupMember));
    }

    public AccountGroupMemberDto createChildGroupMember(String email, long accountGroupId) {

        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var ownerRole = roleService.getRole(AccountGroupMemberRole.OWNER.value);

        var groupMember = AccountGroupMember.builder()
                .accountId(account.getId())
                .accountGroupId(accountGroupId)
                .role(Role.builder().id(ownerRole.getId()).build())
                .build();

        return convertToDTO(repository.save(groupMember));
    }

    public AccountGroupMemberDto save(AccountGroupMemberDto dto) {
        //User should be OWNER in the group
        var member = repository.findByGroupIdAndEmail(dto.getAccountGroupId(), SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (!AccountGroupMemberRole.OWNER.value.equals(member.getRole().getId())) {
            throw new NotAllowedException(MessageTranslator.getMessage("error.update.group.not.allowed"));
        }

        if (AccountGroupMemberRole.OWNER.value.equals(dto.getRole().getId())) {
            throw new OneOwnerMemberMustExistException(MessageTranslator.getMessage("error.only.one.owner.allowed"));
        }

        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long accountGroupId, long accountId) {
        //User should be OWNER in the Group, or User is deleting his own access to the group
        var member = repository.findByGroupIdAndEmail(accountGroupId, SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (member.getAccountId() != accountId && !AccountGroupMemberRole.OWNER.value.equals(member.getRole().getId())) {
            throw new NotAllowedException(MessageTranslator.getMessage("error.update.group.not.allowed"));
        }

        var key = AccountGroupMemberKey.builder().accountGroupId(accountGroupId).accountId(accountId).build();
        member = repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.member.not.found")));

        //deleting an OWNER: there should be at least one Owner in the group
        if (AccountGroupMemberRole.OWNER.value.equals(member.getRole().getId())) {
            throw new OneOwnerMemberMustExistException(MessageTranslator.getMessage("error.delete.owner"));
        }

        repository.deleteById(key);
    }

    //Authorization methods
    public List<AccountGroupMemberDto> hasPermissionInAnyGroup(String email, String permission) {
        var list = repository.hasPermissionInAnyGroup(email, permission);
        return list.stream().map(this::convertToDTO).toList();
    }

    //Authorization method
    public List<AccountGroupMemberDto> hasPermissionInGroup(String email, String permission, Long accountGroupId) {
        var member = convertToDTO(repository.hasPermissionInGroup(email, permission, accountGroupId));
        return member == null ? List.of() : List.of(member);
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
