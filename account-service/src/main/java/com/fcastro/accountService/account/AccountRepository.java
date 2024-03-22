package com.fcastro.accountService.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select p from account p where p.email = :email")
    Optional<Account> findByEmail(String email);

    @Query("select p from account p where lower(p.name) = :searchParam or lower(p.email) = :searchParam order by p.name")
    List<Account> findAllByNameOrEmail(String searchParam);
}
