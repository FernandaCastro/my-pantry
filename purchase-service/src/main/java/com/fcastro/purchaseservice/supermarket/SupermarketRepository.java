package com.fcastro.purchaseservice.supermarket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SupermarketRepository extends JpaRepository<Supermarket, Long> {

    @Query("select p from supermarket p where p.id in :supermarketIds")
    List<Supermarket> findAllByIds(Set<Long> supermarketIds);
}
