package com.fcastro.pantry.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PantryRepository extends JpaRepository<Pantry, Long> {

    @Query("select new com.fcastro.pantry.pantry.Pantry(p.id, p.name, p.type, p.isActive, ac.accountGroupId) as account_group_id from pantry p, accessControl ac " +
            "where p.id = :id " +
            "and p.id = ac.clazzId " +
            "and ac.clazz = 'Pantry' ")
    Optional<Pantry> findById(long id);

    @Query("select new com.fcastro.pantry.pantry.Pantry(p.id, p.name, p.type, p.isActive, ac.accountGroupId) as account_group_id from pantry p, accessControl ac " +
            "where p.name = :name " +
            "and p.id = ac.clazzId " +
            "and ac.clazz = 'Pantry' ")
    Optional<Pantry> findByName(String name);

    @Query("select new com.fcastro.pantry.pantry.Pantry(p.id, p.name, p.type, p.isActive, ac.accountGroupId) as account_group_id " +
            "from pantry p, accessControl ac " +
            "where p.id = ac.clazzId " +
            "and ac.clazz = 'Pantry' " +
            "and ac.accountGroupId in :accountGroups " +
            "order by p.name")
    List<Pantry> findAllByAccountGroup(Set<Long> accountGroups);
}
