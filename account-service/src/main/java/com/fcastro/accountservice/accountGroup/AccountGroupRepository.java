package com.fcastro.accountservice.accountGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long>, CustomAccountGroupRepository<Long> {

    @Query("select ag from accountGroup ag, accountGroupMember aga, account a " +
            "where ag.id = aga.accountGroupId  " +
            "and aga.accountId = a.id  " +
            "and a.email = :email " +
            "order by ag.name")
    List<AccountGroup> findAllStrict(String email);

    @Query("select ag from accountGroup ag, accountGroupMember aga, account a " +
            "where ag.parentAccountGroup.id is null " +
            "and ag.id = aga.accountGroupId  " +
            "and aga.accountId = a.id  " +
            "and a.email = :email " +
            "order by ag.name")
    Optional<AccountGroup> findParentAccountGroup(String email);
}
