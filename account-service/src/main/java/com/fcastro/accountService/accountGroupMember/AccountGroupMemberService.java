package com.fcastro.accountService.accountGroupMember;

import com.fcastro.accountService.account.Account;
import com.fcastro.accountService.account.AccountRepository;
import com.fcastro.accountService.exception.AtLeastOneMemberMustExistException;
import com.fcastro.accountService.role.Role;
import com.fcastro.accountService.role.RoleService;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.model.AccountDto;
import com.fcastro.security.model.AccountGroupMemberDto;
import com.fcastro.security.model.PermissionDto;
import com.fcastro.security.model.RoleDto;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        var ownerRole = roleService.getRole(AccountGroupMemberRole.OWNER.value);

        var groupMember = AccountGroupMember.builder()
                .accountId(account.getId())
                .accountGroupId(accountGroupId)
                .role(Role.builder().id(ownerRole.getId()).build())
                .build();

        return convertToDTO(repository.save(groupMember));
    }

    public AccountGroupMemberDto save(AccountGroupMemberDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long accountGroupId, long accountId) {
        var key = AccountGroupMemberKey.builder().accountGroupId(accountGroupId).accountId(accountId).build();
        var member = repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroupMember not found"));

        var ownerRole = roleService.getRole(member.getRole().getId());
        if (ownerRole.getName().equals(AccountGroupMemberRole.OWNER.value))
            throw new AtLeastOneMemberMustExistException("Owner cannot be deleted.");

//        var admins = repository.findAllByRoleAndAccountGroupId(AccountGroupMemberRole.ADMIN.value, accountGroupId);
//        if (admins.size() == 1 && accountId == admins.get(0).getAccountId())
//            throw new AtLeastOneMemberMustExistException("Member cannot be deleted. There should be at least one admin member in the group.");

        repository.deleteById(key);
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
                            .map(p -> PermissionDto.builder().id(p.getId()).name(p.getName()).build())
                            .collect(Collectors.toList());

            roleDto = RoleDto.builder()
                    .id(role.getId())
                    .name(role.getName())
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
                    .name(dto.getRole().getName())
                    .build();
        }

        return AccountGroupMember.builder()
                .accountGroupId(dto.getAccountGroupId())
                .accountId(dto.getAccountId())
                .role(role)
                .build();
    }
}
