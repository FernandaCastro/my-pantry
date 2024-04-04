package com.fcastro.purchaseservice.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {


    @Query("select p from purchase p " +
            "where p.processedAt is null " +
            "and NOT EXISTS ( " +
            "select 1 " +
            "from purchaseItem i " +
            "where i.purchase.id = p.id " +
            "and i.pantryId not in :pantryIds ) ")
        //@EntityGraph(attributePaths = {"items"})
    Purchase getPending(Set<Long> pantryIds);

    @Query("select p from purchase p " +
            "where NOT EXISTS ( " +
            "select 1 " +
            "from purchaseItem i " +
            "where i.purchase.id = p.id " +
            "and i.pantryId not in :pantryIds ) " +
            "order by p.createdAt desc")
    List<Purchase> findAllOrderByDescCreateAt(Set<Long> pantryIds);

}
