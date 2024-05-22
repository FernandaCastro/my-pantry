package com.fcastro.accountservice.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r, count(p.id) priority " +
            "FROM role r " +
            "JOIN r.permissions p " +
            "GROUP BY r.id " +
            "ORDER BY priority DESC")
    List<Role> findAllOrderByAccessLevel();

    Optional<Role> findByName(String name);
}
