package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.Idioma;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdiomaRepository extends JpaRepository<Idioma, Integer> {
    List<Idioma> findByHotelIdAndAtivoTrue(Integer hotelId);
    Optional<Idioma> findByHotelIdAndCodigoIso(Integer hotelId, String codigoIso);
}