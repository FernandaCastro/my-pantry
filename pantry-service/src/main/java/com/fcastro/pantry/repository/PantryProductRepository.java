package com.fcastro.pantry.repository;

import com.fcastro.pantry.model.PantryProductEntity;
import com.fcastro.pantry.model.PantryProductPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PantryProductRepository extends JpaRepository<PantryProductEntity, PantryProductPk> {

    @Query("select t from PantryProduct t where t.pantryId = :pantryId")
    List<PantryProductEntity> findAllByPantryId(@Param("pantryId") long pantryId);
}
