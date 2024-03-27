package com.fcastro.purchaseservice.properties;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertiesRepository extends JpaRepository<Properties, String> {

    @Query("select p from properties p where p.propertyKey like :key")
    List<Properties> findAllLikeId(String key);
}
