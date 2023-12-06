package com.fcastro.pantry.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from product p where lower(p.code) like %:code%")
    List<Product> findAllByCode(String code);

    @Query("select p from product p where lower(p.description) like %:description%")
    List<Product> findAllByDescription(String description);
}
