package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.DicionarioTotem;

import java.util.List;

@Repository
public interface DicionarioTotemRepository extends JpaRepository<DicionarioTotem, Integer> {
    List<DicionarioTotem> findByHotelIdAndIdiomaId(Integer hotelId, Integer idiomaId);
}