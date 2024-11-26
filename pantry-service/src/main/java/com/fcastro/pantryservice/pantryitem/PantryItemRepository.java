package com.fcastro.pantryservice.pantryitem;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, PantryItemKey> {

    @Query("select p from pantryItem p where p.pantry.id = :pantryId order by product.code")
    @EntityGraph(attributePaths = {"product"})
    List<PantryItem> findAllByPantryId(@Param("pantryId") Long pantryId);

    @Deprecated
    @Query("select p from pantryItem p where p.pantry.id = :pantryId and p.currentQty > 0 order by product.code")
    @EntityGraph(attributePaths = {"product"})
    List<PantryItem> findAllToConsumeByPantryId(@Param("pantryId") Long pantryId);

    @Query("select p from pantryItem p where p.pantry.id in :pantryIds and p.currentQty > 0 order by product.code")
    @EntityGraph(attributePaths = {"pantry", "product"})
    List<PantryItem> findAllToConsumeByPantryId(@Param("pantryIds") List<Long> pantryIds);

    @EntityGraph(attributePaths = {"pantry", "product"})
    Optional<PantryItem> findEagerByPantryIdAndProductId(Long pantryId, Long productId);

    @Query("select count(p) from pantryItem p where p.product.id = :productId")
    Integer countPantryItem(Long productId);

    @Modifying
    @Query("delete from pantryItem p where p.pantry.id = :pantryId")
    void deleteByPantryId(@Param("pantryId") Long pantryId);
}
