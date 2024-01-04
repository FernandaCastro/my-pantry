package com.fcastro.pantry.pantryItem;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, PantryItemKey> {

    @Query("select p from pantryItem p where p.pantryId = :pantryId order by product.code")
    @EntityGraph(attributePaths = {"product"})
    List<PantryItem> findAllByPantryId(@Param("pantryId") Long pantryId);

    @Query("select p from pantryItem p where p.pantryId = :pantryId and p.currentQty > 0 order by product.code")
    @EntityGraph(attributePaths = {"product"})
    List<PantryItem> findAllToConsumeByPantryId(@Param("pantryId") Long pantryId);

    @EntityGraph(attributePaths = {"pantry", "product"})
    Optional<PantryItem> findEagerByPantryIdAndProductId(Long pantryId, Long productId);

    @Query("select count(p) from pantryItem p where p.productId = :productId")
    Integer countPantryItem(Long productId);
}
