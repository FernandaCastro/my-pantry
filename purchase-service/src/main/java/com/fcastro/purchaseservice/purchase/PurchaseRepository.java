package com.fcastro.purchaseservice.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {


    @Query("select p from purchase p where p.processedAt is null")
    //@EntityGraph(attributePaths = {"items"})
    Purchase getPending();

    @Query("select p from purchase p order by p.createdAt desc")
    List<Purchase> findAllOrderByDescCreateAt();

}
