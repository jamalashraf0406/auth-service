package com.example.authservice.repositories;

import com.example.authservice.entities.JwkKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwkKeyRepository extends JpaRepository<JwkKeyEntity, String> {

    Optional<JwkKeyEntity> findByActiveTrue();
}