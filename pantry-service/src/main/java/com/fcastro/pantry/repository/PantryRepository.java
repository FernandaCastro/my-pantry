package com.fcastro.pantry.repository;

import com.fcastro.pantry.model.PantryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PantryRepository extends JpaRepository<PantryEntity, Long> {

}
