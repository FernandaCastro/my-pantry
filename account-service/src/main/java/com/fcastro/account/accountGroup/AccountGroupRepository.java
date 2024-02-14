package com.fcastro.account.accountGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long>, CustomAccountGroupRepository<Long> {

    @Query("select ag from accountGroup ag, accountGroupAccount aga " +
            "where ag.id = aga.accountGroupId  " +
            "and aga.accountId = :accountId  " +
            "order by ag.name")
    List<AccountGroup> findAllStrict(long accountId);
}
