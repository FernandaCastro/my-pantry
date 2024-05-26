package com.fcastro.accountservice.accesscontrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessControlRepository extends JpaRepository<AccessControl, AccessControlKey> {

    @Query("select a from accessControl a " +
            "where a.clazz = :clazz " +
            "and a.clazzId = :clazzId " +
            "and a.accountGroup.id in :groupIds")
    List<AccessControl> findAllByGroupIds(String clazz, Long clazzId, List<Long> groupIds);

    @Query("select a from accessControl a " +
            "where a.clazz = :clazz " +
            "and a.accountGroup.id in :groupIds")
    List<AccessControl> findAllByClazzAndGroupIds(String clazz, List<Long> groupIds);

    @Query("select a from accessControl a " +
            "where a.clazz = :clazz " +
            "and a.clazzId = :clazzId")
    Optional<AccessControl> findByClazzAndClazzId(String clazz, Long clazzId);

    @Modifying
    @Query("delete accessControl a " +
            "where a.clazz = :clazz " +
            "and a.clazzId = :clazzId")
    void deleteAllByClazzAndClazzId(String clazz, Long clazzId);

    List<AccessControl> findAllByAccountGroupId(Long accountGroupId);

    @Query("select distinct acc " +
            "from accountGroupMember gm," +
            "account ac, " +
            "accessControl acc " +
            "where ac.id = gm.account.id " +
            "and acc.accountGroup.id = gm.accountGroup.id " +
            "and ac.email = :email " +
            "and acc.clazz = :clazz ")
    List<AccessControl> findAllByEmailAndClazz(String email, String clazz);

    @Query("select distinct acc " +
            "from accountGroupMember gm," +
            "account ac, " +
            "accessControl acc " +
            "where ac.id = gm.accountId " +
            "and acc.accountGroup.id = gm.accountGroup.id " +
            "and ac.email = :email " +
            "and acc.clazz = :clazz " +
            "and acc.clazzId = :clazzId ")
    List<AccessControl> findAllByEmailAndClazzAndClazzId(String email, String clazz, Long clazzId);

    @Query("select distinct acc " +
            "from accountGroupMember gm," +
            "account ac, " +
            "accountGroup ag, " +
            "accessControl acc " +
            "where ac.id = gm.account.id " +
            "and ag.id = gm.accountGroup.id " +
            "and ac.email = :email " +
            "and acc.clazz = :clazz " +
            "and (( acc.accountGroup.id = :accountGroupId and acc.accountGroup.id = ag.id ) " +
            "or ( ag.id = :accountGroupId and acc.accountGroup.id = ag.parentAccountGroup.id))")
    List<AccessControl> findAllByEmailAndClazzAndAccountGroupId(String email, String clazz, Long accountGroupId);

    @Query("select distinct acc " +
            "from accountGroupMember gm," +
            "account ac, " +
            "accessControl acc, " +
            "role role " +
            "JOIN role.permissions per " +
            "where ac.id = gm.accountId " +
            "and acc.accountGroup.id = gm.accountGroup.id " +
            "and role.id = gm.role.id " +
            "and ac.email = :email " +
            "and acc.clazz = :clazz " +
            "and lower(per.name) = lower(:permission) ")
    List<AccessControl> findAllByEmailAndClazzAndPermission(String email, String clazz, String permission);


    @Query("select acc " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "accessControl acc, " +
            "role role " +
            "JOIN role.permissions per " +
            "where ac.id = gm.account.id " +
            "and acc.accountGroup.id = gm.accountGroup.id " +
            "and role.id = gm.role.id " +
            "and ac.email = :email " +
            "and lower(per.name) = lower(:permission) " +
            "and acc.clazz = :clazz " +
            "and acc.clazzId = :clazzId")
    AccessControl hasPermissionInObject(String email, String permission, String clazz, Long clazzId);

    @Query("select acc " +
            "from accountGroupMember gm, " +
            "account ac, " +
            "accessControl acc, " +
            "role role " +
            "JOIN role.permissions per " +
            "where ac.id = gm.account.id " +
            "and acc.accountGroup.id = gm.accountGroup.id " +
            "and role.id = gm.role.id " +
            "and ac.email = :email " +
            "and lower(per.name) = lower(:permission) " +
            "and acc.clazz = :clazz " +
            "and acc.clazzId in :clazzIds")
    List<AccessControl> hasPermissionInObjectList(String email, String permission, String clazz, List<Long> clazzIds);
}
