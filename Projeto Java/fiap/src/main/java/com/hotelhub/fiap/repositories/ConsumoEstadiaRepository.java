package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.ConsumoEstadia;

import java.util.List;

@Repository
public interface ConsumoEstadiaRepository extends JpaRepository<ConsumoEstadia, Integer> {
    List<ConsumoEstadia> findByReservaId(Integer reservaId);
}