package com.fcastro.purchaseService.purchase;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @EntityGraph(attributePaths = {"items"})
    @Query("select p from Purchase p where p.processedAt is null")
    Purchase getPending();

}
