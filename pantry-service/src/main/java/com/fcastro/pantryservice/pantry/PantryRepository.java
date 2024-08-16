package com.fcastro.pantryservice.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PantryRepository extends JpaRepository<Pantry, Long> {


//    @Query("select new com.fcastro.pantryservice.pantry.Pantry(p.id, p.name, p.type, p.isActive, ac.accountGroupId) as account_group_id from pantry p, accessControl ac " +
//            "where p.id = :id " +
//            "and p.id = ac.clazzId " +
//            "and ac.clazz = 'Pantry' ")
//    Optional<Pantry> findById(long id);
//
//
//    @Query("select new com.fcastro.pantryservice.pantry.Pantry(p.id, p.name, p.type, p.isActive, ac.accountGroupId) as account_group_id from pantry p, accessControl ac " +
//            "where p.name = :name " +
//            "and p.id = ac.clazzId " +
//            "and ac.clazz = 'Pantry' ")
//    Optional<Pantry> findByName(String name);

    Optional<Pantry> findByName(String name);

    ///
    @Query("select p from pantry p where p.id in :pantryIds order by p.isActive desc, p.name")
    List<Pantry> findAllByIds(Set<Long> pantryIds);

}
