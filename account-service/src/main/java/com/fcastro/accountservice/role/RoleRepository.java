package com.fcastro.accountservice.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    @Query("SELECT r, count(p.id) priority " +
            "FROM role r " +
            "JOIN r.permissions p " +
            "GROUP BY r.id " +
            "ORDER BY priority DESC")
    List<Role> findAllOrderByAccessLevel();
}
