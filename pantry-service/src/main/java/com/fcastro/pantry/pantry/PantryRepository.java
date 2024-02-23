package com.fcastro.pantry.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PantryRepository extends JpaRepository<Pantry, Long> {
    Optional<Pantry> findByName(String name);
}
