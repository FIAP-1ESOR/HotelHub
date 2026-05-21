package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.CartaoChave;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartaoChaveRepository extends JpaRepository<CartaoChave, Integer> {
    List<CartaoChave> findByReservaId(Integer reservaId);
    Optional<CartaoChave> findByNumeroCartaoChave(String numeroCartaoChave);
}