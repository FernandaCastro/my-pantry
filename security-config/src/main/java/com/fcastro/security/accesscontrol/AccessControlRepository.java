package com.fcastro.security.accesscontrol;

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
            "and a.accountGroupId in :groupIds")
    List<AccessControl> findAllByGroupIds(String clazz, Long clazzId, List<Long> groupIds);

    @Query("select a from accessControl a " +
            "where a.clazz = :clazz " +
            "and a.accountGroupId in :groupIds")
    List<AccessControl> findAllByClazzAndGroupIds(String clazz, List<Long> groupIds);

    @Query("select a from accessControl a " +
            "where a.clazz = :clazz " +
            "and a.clazzId = :clazzId")
    Optional<AccessControl> findByClazzAndClazzId(String clazz, Long clazzId);

    @Query("delete from accessControl a " +
            "where a.clazz = :clazz " +
            "and a.clazzId = :clazzId")
    @Modifying
    void deleteAllByClazzAndClazzId(String clazz, Long clazzId);

    List<AccessControl> findAllByAccountGroupId(Long accountGroupId);
}
