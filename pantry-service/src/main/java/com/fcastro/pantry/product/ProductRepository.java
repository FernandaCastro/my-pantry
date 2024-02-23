package com.fcastro.pantry.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from product p where lower(p.code) like %:searchParam% or lower(p.description) like %:searchParam% order by p.code")
    List<Product> findAllByCodeOrDescription(String searchParam);

    Optional<Product> findByCode(String code);
}
