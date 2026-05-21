package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.TransacaoPagamento;

import java.util.List;

@Repository
public interface TransacaoPagamentoRepository extends JpaRepository<TransacaoPagamento, Integer> {
    List<TransacaoPagamento> findByReservaId(Integer reservaId);
    
    List<TransacaoPagamento> findByHotelId(Integer hotelId);
}
