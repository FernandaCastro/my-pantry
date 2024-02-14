package com.fcastro.account.accountGroup;

import com.fcastro.account.accountGroupMember.AccountGroupMemberService;
import com.fcastro.app.exception.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountGroupService {

    private final AccountGroupRepository repository;
    private final AccountGroupMemberService groupMemberService;

    public AccountGroupService(AccountGroupRepository repository, AccountGroupMemberService groupMemberService) {
        this.repository = repository;
        this.groupMemberService = groupMemberService;
    }

    public Optional<AccountGroupDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public List<AccountGroupDto> getAll() {
        var listEntity = repository.findAll(Sort.by("name"));
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //TODO: Pageable
    public List<AccountGroupDto> getAll(long accountId) {
        var listEntity = repository.findAllStrict(accountId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AccountGroupDto createDefaultGroup(long accountId) {
        var accountGroup = AccountGroup.builder()
                .name("Default Group")
                .build();

        accountGroup = repository.save(accountGroup);
        groupMemberService.createAdminGroupMember(accountId, accountGroup.getId());

        return convertToDTO(accountGroup);
    }

    public AccountGroupDto save(long accountId, AccountGroupDto dto) {
        if (dto.getId() != null && repository.findById(dto.getId()).isPresent()) {
            var accountGroup = repository.save(convertToEntity(dto));
            return convertToDTO(accountGroup);
        }

        var accountGroup = repository.save(convertToEntity(dto));
        groupMemberService.createAdminGroupMember(accountId, accountGroup.getId());

        return convertToDTO(accountGroup);
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroup not found"));

        repository.deleteById(id);
    }

    private AccountGroupDto convertToDTO(AccountGroup entity) {
        if (entity == null) return null;
        return AccountGroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private AccountGroup convertToEntity(AccountGroupDto dto) {
        if (dto == null) return null;
        return AccountGroup.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

}
