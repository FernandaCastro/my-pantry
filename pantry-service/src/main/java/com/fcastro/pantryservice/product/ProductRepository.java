package com.fcastro.pantryservice.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select new com.fcastro.pantryservice.product.Product(p.id, p.code, p.description, p.size, p.category, ac.accountGroupId) as account_group_id " +
            "from product p, accessControl ac " +
            "where p.code = :code " +
            "and p.id = ac.clazzId " +
            "and ac.clazz = 'Product' ")
    Optional<Product> findByCode(String code);

    @Query("select new com.fcastro.pantryservice.product.Product(p.id, p.code, p.description, p.size, p.category, ac.accountGroupId) as account_group_id " +
            "from product p, accessControl ac " +
            "where p.id = :id " +
            "and p.id = ac.clazzId " +
            "and ac.clazz = 'Product' ")
    Optional<Product> findById(Long id);

    @Query("select new com.fcastro.pantryservice.product.Product(p.id, p.code, p.description, p.size, p.category, ac.accountGroupId) as account_group_id " +
            "from product p, accessControl ac " +
            "where p.id = ac.clazzId " +
            "and ac.clazz = 'Product' " +
            "and ac.accountGroupId in :accountGroups " +
            "order by p.code")
    List<Product> findAll(Set<Long> accountGroups);

    @Query("select new com.fcastro.pantryservice.product.Product(p.id, p.code, p.description, p.size, p.category, ac.accountGroupId) as account_group_id " +
            "from product p, accessControl ac " +
            "where ( lower(p.code) like %:searchParam% " +
            "or lower(p.description) like %:searchParam% ) " +
            "and p.id = ac.clazzId " +
            "and ac.clazz = 'Product' " +
            "and ac.accountGroupId = :accountGroupId " +
            "order by p.code")
    List<Product> findAllByCodeOrDescription(String searchParam, Long accountGroupId);
}
