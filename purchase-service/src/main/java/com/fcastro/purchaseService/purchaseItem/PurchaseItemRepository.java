package com.fcastro.purchaseService.purchaseItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Query("select count(p) from purchaseItem p where p.purchase.id is null")
    int countPendingPurchase();

    @Modifying
    @Query("update purchaseItem p set p.purchase.id = :purchaseId where p.purchase.id is null")
    int updatePendingPurchase(@Param("purchaseId") Long purchaseId);

    @Query("select p from purchaseItem p where p.purchase.id = :purchaseId")
    List<PurchaseItem> findAllByPurchaseId(@Param("purchaseId") Long purchaseId);
}
