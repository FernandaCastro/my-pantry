package com.fcastro.accountservice.accountgroupmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountGroupMemberRepository extends JpaRepository<AccountGroupMember, AccountGroupMemberKey> {

    @Query("SELECT gm, g, a, r FROM accountGroupMember gm " +
            "JOIN FETCH gm.accountGroup g " +
            "JOIN gm.account a " +
            "JOIN gm.role r " +
            "WHERE a.email = :email ")
    List<AccountGroupMember> findAllByEmail(String email);

    @Query("select aga, a, r " +
            "from account a, accountGroupMember aga, role r " +
            "where aga.accountId = a.id  " +
            "and r.id = aga.role.id " +
            "and a.email = :email " +
            "and aga.accountGroupId = :groupId")
    Optional<AccountGroupMember> findByGroupIdAndEmail(Long groupId, String email);

    @Modifying
    @Query("delete from accountGroupMember aga where aga.accountGroupId = :groupId")
    void deleteAllByAccountGroupId(long groupId);

    List<AccountGroupMember> findAllByAccountId(long accountId);

    List<AccountGroupMember> findAllByAccountGroupId(long groupId);

    @Query("select distinct gm " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "role role " +
            "JOIN role.permissions per " +
            "where ac.id = gm.account.id " +
            "and role.id = gm.role.id " +
            "and ac.email = :email " +
            "and lower(per.id) = lower(:permission)")
    List<AccountGroupMember> hasPermissionInAnyGroup(String email, String permission);

    @Query("select distinct gm " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "role role " +
            "JOIN role.permissions per " +
            "where ac.id = gm.account.id " +
            "and role.id = gm.role.id " +
            "and ac.email = :email " +
            "and lower(per.id) = lower(:permission) " +
            "and gm.accountGroup.id = :accountGroupId")
    AccountGroupMember hasPermissionInGroup(String email, String permission, Long accountGroupId);
}
