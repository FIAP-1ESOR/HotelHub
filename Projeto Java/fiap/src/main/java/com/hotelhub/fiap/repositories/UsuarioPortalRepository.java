package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.UsuarioPortal;

import java.util.Optional;

@Repository
public interface UsuarioPortalRepository extends JpaRepository<UsuarioPortal, Integer> {
    Optional<UsuarioPortal> findByUsername(String username);
}
