package com.fcastro.purchase.purchaseItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Query("select count(p) from PurchaseItem p where p.purchase.id is null")
    int countPendingPurchase();

    @Modifying
    @Query("update PurchaseItem p set p.purchase.id = :purchaseId where p.purchase.id is null")
    int updatePendingPurchaseItems(@Param("purchaseId") Long purchaseId);

    @Query("select p from PurchaseItem p where p.purchase.id = :purchaseId")
    List<PurchaseItem> findAllByPurchaseId(@Param("purchaseId") Long purchaseId);

    @Query("select p from PurchaseItem p where p.pantryId = :pantryId and p.productId = :productId and p.purchase.id is null")
    PurchaseItem findByPantryIdAndProductIdAndPurchaseIdIsNull(Long pantryId, Long productId);

    @Query("select p from PurchaseItem p where p.id = :id and p.purchase.id = :purchaseId")
    Optional<PurchaseItem> findByIdAndPurchaseId(Long id, Long purchaseId);
}
