package com.fcastro.account.accountGroupMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountGroupMemberRepository extends JpaRepository<AccountGroupMember, AccountGroupMemberKey> {

    @Query("select aga, ac from account ac, accountGroupAccount aga, account a " +
            "where aga.accountId = ac.id  " +
            "and aga.accountGroupId  = :groupId  " +
            "and aga.accountId = a.id  " +
            "and a.email = :email " +
            "order by ac.name")
    List<AccountGroupMember> findAllByAccountIdAndGroupId(String email, long groupId);

    @Modifying
    @Query("delete from accountGroupAccount aga where aga.accountGroupId = :groupId")
    void deleteAllByAccountGroupId(long groupId);


    List<AccountGroupMember> findAllByAccountGroupId(long groupId);
}
