package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.ProdutoFrigobar;

import java.util.List;

@Repository
public interface ProdutoFrigobarRepository extends JpaRepository<ProdutoFrigobar, Integer> {
    List<ProdutoFrigobar> findByHotelId(Integer hotelId);
}