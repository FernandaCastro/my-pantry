package com.fcastro.accountService.accountGroup;

import com.fcastro.accountService.accountGroupMember.AccountGroupMemberService;
import com.fcastro.accountService.exception.DeletionNotAllowedException;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.auth.AuthorizationUtils;
import com.fcastro.security.model.AccountGroupDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountGroupService {

    private final AccountGroupRepository repository;
    private final AccountGroupMemberService groupMemberService;
    private final AuthorizationUtils authorizationUtils;

    public AccountGroupService(AccountGroupRepository repository, AccountGroupMemberService groupMemberService, AuthorizationUtils authorizationUtils) {
        this.repository = repository;
        this.groupMemberService = groupMemberService;
        this.authorizationUtils = authorizationUtils;
    }

    public Optional<AccountGroupDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public List<AccountGroupDto> getAll() {
        List<AccountGroup> listEntity = repository.findAllStrict(authorizationUtils.getUserEmail());
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //TODO: Pageable
    public List<AccountGroupDto> getAll(String email) {
        var listEntity = repository.findAllStrict(email);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AccountGroupDto createParentGroup(long accountId) {
        var accountGroup = AccountGroup.builder()
                .name("Default Group")
                .build();

        accountGroup = repository.save(accountGroup);
        groupMemberService.createParentGroupMember(accountId, accountGroup.getId());

        return convertToDTO(accountGroup);
    }

    public AccountGroupDto save(AccountGroupDto dto) {

        //update
        if (dto.getId() != null && repository.findById(dto.getId()).isPresent()) {
            var accountGroup = repository.save(convertToEntity(dto));
            return convertToDTO(accountGroup);
        }

        //new a child group, so find  and associate the parent
        var parentGroup = repository.findParentAccountGroup(authorizationUtils.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Parent Account Group not found"));
        ;

        var accountGroup = convertToEntity(dto);
        accountGroup.setParentAccountGroup(parentGroup);
        accountGroup = repository.save(accountGroup);

        groupMemberService.createChildGroupMember(authorizationUtils.getUserEmail(), accountGroup.getId());

        return convertToDTO(accountGroup);
    }

    public void delete(long accountGroupId) {
        //TODO: Guarantee that groups associated to objects (pantries ie) should not be deleted
        var accountGroup = repository.findById(accountGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroup not found"));

        if (accountGroup.getParentAccountGroup() == null)
            throw new DeletionNotAllowedException("This is your main Account Group. It cannot be deleted.");

        repository.deleteById(accountGroupId);
    }

    private AccountGroupDto convertToDTO(AccountGroup entity) {
        if (entity == null) return null;

        AccountGroupDto parentAccountGroup = null;
        if (entity.getParentAccountGroup() != null) {
            parentAccountGroup = AccountGroupDto.builder()
                    .id(entity.getParentAccountGroup().getId())
                    .build();
        }

        return AccountGroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentAccountGroup(parentAccountGroup)
                .build();
    }

    private AccountGroup convertToEntity(AccountGroupDto dto) {
        if (dto == null) return null;

        AccountGroup parentAccountGroup = null;
        if (dto.getParentAccountGroup() != null) {
            parentAccountGroup = AccountGroup.builder()
                    .id(dto.getParentAccountGroup().getId())
                    .build();
        }
        return AccountGroup.builder()
                .id(dto.getId())
                .name(dto.getName())
                .parentAccountGroup(parentAccountGroup)
                .build();
    }

}
