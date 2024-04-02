package com.fcastro.accountservice.accountgroupmember;

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

    @Query("select distinct gm.* " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "accessControl acc, " +
            "role role, " +
            "rolePermission rp, " +
            "permission per " +
            "where ac.id = gm.accountId " +
            "and acc.accountGroupId = gm.accountGroupId " +
            "and role.id = gm.roleId " +
            "and rp.roleId = role.id " +
            "and per.id = rp.permissionId " +
            "and ac.email = :email " +
            "and per.name = :permission")
    List<AccountGroupMember> hasPermissionInAnyGroup(String email, String permission);

    @Query("select distinct gm.* " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "accessControl acc, " +
            "role role, " +
            "rolePermission rp, " +
            "permission per " +
            "where ac.id = gm.accountId " +
            "and acc.accountGroupId = gm.accountGroupId " +
            "and role.id = gm.roleId " +
            "and rp.roleId = role.id " +
            "and per.id = rp.permissionId " +
            "and ac.email = :email " +
            "and per.name = :permission " +
            "and gm.accountGroupId = :accountGroupId")
    List<AccountGroupMember> hasPermissionInGroup(String email, String permission, Long accountGroupId);

    @Query("select distinct gm.* " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "accessControl acc, " +
            "role role, " +
            "rolePermission rp, " +
            "permission per " +
            "where ac.id = gm.accountId " +
            "and acc.accountGroupId = gm.accountGroupId " +
            "and role.id = gm.roleId " +
            "and rp.roleId = role.id " +
            "and per.id = rp.permissionId " +
            "and ac.email = :email " +
            "and per.name = :permission " +
            "and acc.clazz = clazz " +
            "and acc.clazzId = clazzId")
    List<AccountGroupMember> hasPermissionInObject(String email, String permission, String clazz, String clazzId);
}
