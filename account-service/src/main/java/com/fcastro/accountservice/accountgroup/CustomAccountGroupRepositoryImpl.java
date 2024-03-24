package com.fcastro.accountservice.accountgroup;

import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberRepository;
import jakarta.persistence.EntityManager;

public class CustomAccountGroupRepositoryImpl implements CustomAccountGroupRepository<Long> {

    private final AccountGroupMemberRepository accountGroupMemberRepository;
    private final EntityManager entityManager;

    public CustomAccountGroupRepositoryImpl(AccountGroupMemberRepository accountGroupMemberRepository,
                                            EntityManager entityManager) {
        this.accountGroupMemberRepository = accountGroupMemberRepository;
        this.entityManager = entityManager;
    }

    @Override
    public void deleteById(Long id) {
        accountGroupMemberRepository.deleteAllByAccountGroupId(id);

        entityManager.createQuery("delete from accountGroup where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
