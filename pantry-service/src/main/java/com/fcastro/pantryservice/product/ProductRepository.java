package com.fcastro.pantryservice.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    @Query("select p " +
            "from product p " +
            "where ( lower(p.code) like %:searchParam% " +
            "or lower(p.description) like %:searchParam% ) " +
            "and p.id in :productIds " +
            "order by p.code")
    List<Product> findAllByCodeOrDescription(String searchParam, Set<Long> productIds);


    @Query("select p from product p where p.id in :productIds")
    List<Product> findAllByIds(Set<Long> productIds);
}
