package com.fcastro.accountService.accountGroupMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountGroupMemberRepository extends JpaRepository<AccountGroupMember, AccountGroupMemberKey> {

    @Query("select aga, a from account a, accountGroupMember aga " +
            "where aga.accountId = a.id  " +
            "and aga.accountGroupId  = :groupId  " +
            "and a.email = :email ")
    List<AccountGroupMember> findAllByAccountIdAndGroupId(String email, long groupId);

    @Query("select aga, a from account a, accountGroupMember aga " +
            "where aga.accountId = a.id  " +
            "and a.email = :email ")
    List<AccountGroupMember> findAllByEmail(String email);

    @Query("select aga, a " +
            "from account a, accountGroupMember aga " +
            "where aga.accountId = a.id  " +
            "and a.email = :email " +
            "and aga.accountGroupId = :groupId")
    Optional<AccountGroupMember> findByGroupIdAndEmail(Long groupId, String email);

    @Modifying
    @Query("delete from accountGroupMember aga where aga.accountGroupId = :groupId")
    void deleteAllByAccountGroupId(long groupId);

    List<AccountGroupMember> findAllByAccountId(long accountId);

    List<AccountGroupMember> findAllByAccountGroupId(long groupId);

    List<AccountGroupMember> findAllByRoleIdAndAccountGroupId(long roleId, long groupId);
}
