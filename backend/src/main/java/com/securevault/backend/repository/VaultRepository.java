package com.securevault.backend.repository;

import com.securevault.backend.entity.Vault;
import com.securevault.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaultRepository extends JpaRepository<Vault, Long> {

    Optional<Vault> findByUser(User user);

    boolean existsByUser(User user);
}