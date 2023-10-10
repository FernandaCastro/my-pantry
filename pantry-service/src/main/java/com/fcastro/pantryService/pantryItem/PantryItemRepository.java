package com.fcastro.pantryService.pantryItem;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, PantryItemKey> {

    @Query("select p from pantryItem p where p.pantryId = :pantryId")
    List<PantryItem> findAllByPantryId(@Param("pantryId") Long pantryId);

    @EntityGraph(attributePaths = {"pantry"})
    Optional<PantryItem> findEagerByPantryIdAndProductId(Long pantryId, Long productId);

}
