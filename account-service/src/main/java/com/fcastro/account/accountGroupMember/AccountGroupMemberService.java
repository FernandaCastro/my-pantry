package com.fcastro.account.accountGroupMember;

import com.fcastro.account.account.Account;
import com.fcastro.account.exception.AtLeastOneMemberMustExistException;
import com.fcastro.app.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountGroupMemberService {

    private final AccountGroupMemberRepository repository;

    public AccountGroupMemberService(AccountGroupMemberRepository repository) {
        this.repository = repository;
    }

    public Optional<AccountGroupMemberDto> get(long accountGroupId, long accountId) {
        return repository.findById(AccountGroupMemberKey.builder().accountId(accountGroupId).accountId(accountId).build())
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<AccountGroupMemberDto> getAll(String email, long accountGroupId) {
        var listEntity = repository.findAllByAccountIdAndGroupId(email, accountGroupId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AccountGroupMemberDto createAdminGroupMember(long accountId, long accountGroupId) {
        var groupMember = AccountGroupMember.builder()
                .accountId(accountId)
                .accountGroupId(accountGroupId)
                .role(AccountGroupMemberRole.ADMIN.value)
                .build();
        return convertToDTO(repository.save(groupMember));
    }

    public AccountGroupMemberDto save(AccountGroupMemberDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long accountGroupId, long accountId) {
        var key = AccountGroupMemberKey.builder().accountGroupId(accountGroupId).accountId(accountId).build();
        repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroupMember not found"));

        var members = repository.findAllByAccountGroupId(accountGroupId);
        if (members == null || members.size() == 1)
            new AtLeastOneMemberMustExistException("Member cannot be deleted. There should be At least one admin member in the group.");

        var admins = members.stream().filter(m ->
                        AccountGroupMemberRole.ADMIN.value.equals(m.getRole()))
                .collect(Collectors.toList());
        if (admins.isEmpty() || //no admins in the group
                (admins.size() == 1 && admins.get(0).getAccountId() == accountId))//Trying to delete the only admin of the group
            new AtLeastOneMemberMustExistException("This member cannot be deleted. There should be At least one admin member in the group.");
        repository.deleteById(key);
    }

    private AccountGroupMemberDto convertToDTO(AccountGroupMember entity) {
        if (entity == null) return null;

        Account account = entity.getAccount();
        return AccountGroupMemberDto.builder()
                .accountGroupId(entity.getAccountGroupId())
                .accountId(entity.getAccountId())
                .role(entity.getRole())
                .account(account)
                .build();
    }

    private AccountGroupMember convertToEntity(AccountGroupMemberDto dto) {
        if (dto == null) return null;
        return AccountGroupMember.builder()
                .accountGroupId(dto.getAccountGroupId())
                .accountId(dto.getAccountId())
                .role(dto.getRole())
                .build();
    }
}
