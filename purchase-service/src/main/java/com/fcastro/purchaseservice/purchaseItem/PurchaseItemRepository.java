package com.fcastro.purchaseservice.purchaseItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Query("select i, p from purchaseItem i, product p " +
            "where i.product.id = p.id and i.purchase.id is null " +
            "and i.pantryId in :pantryIds " +
            "order by p.category, p.code")
    List<PurchaseItem> listPendingPurchase(Set<Long> pantryIds);

    @Modifying
    @Query("update purchaseItem set purchase.id = :purchaseId where purchase.id is null and pantryId in :pantryIds")
    void updatePendingPurchaseItems(@Param("purchaseId") Long purchaseId, @Param("pantryIds") Set<Long> pantryIds);

    @Query("select i, p from purchaseItem i, product p where i.purchase.id = :purchaseId and i.product.id = p.id order by p.category, p.code")
    List<PurchaseItem> findAllByPurchaseId(@Param("purchaseId") Long purchaseId);

    @Query("select p from purchaseItem p where p.pantryId = :pantryId and p.product.id = :productId and p.purchase.id is null")
    PurchaseItem findByPantryIdAndProductIdAndNoPurchaseOrder(Long pantryId, Long productId);

    @Query("select p from purchaseItem p, purchase o where p.pantryId = :pantryId and p.product.id = :productId and p.purchase.id = o.id and o.processedAt is null")
    PurchaseItem findByPantryIdAndProductIdAndOpenPurchaseOrder(Long pantryId, Long productId);

    @Query("select p from purchaseItem p where p.id = :id and p.purchase.id = :purchaseId")
    Optional<PurchaseItem> findByIdAndPurchaseId(Long id, Long purchaseId);

    @Modifying
    @Query("delete from purchaseItem i " +
            "where i.pantryId in :pantryIds ")
    int deleteAllByPantryIds(List<Long> pantryIds);
}
