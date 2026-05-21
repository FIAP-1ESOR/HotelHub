package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.Quarto;

import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Integer> {
    List<Quarto> findByHotelId(Integer hotelId);
    
    // Busca quartos de um hotel filtrando pelo status (ex: buscar apenas 'LIVRE' na hora do check-in)
    List<Quarto> findByHotelIdAndStatus(Integer hotelId, String status);
}