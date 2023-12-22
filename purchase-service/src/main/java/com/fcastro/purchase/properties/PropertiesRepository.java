package com.fcastro.purchase.properties;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertiesRepository extends JpaRepository<Properties, String> {

//    @Query("select p.propertyKey, to_json(p.propertyValue) from properties p where propertyKey = :key")
//    Optional<Properties> findById(String key);
}
