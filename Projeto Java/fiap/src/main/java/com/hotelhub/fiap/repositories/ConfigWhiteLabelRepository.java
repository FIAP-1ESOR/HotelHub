package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.ConfigWhiteLabel;

import java.util.Optional;

@Repository
public interface ConfigWhiteLabelRepository extends JpaRepository<ConfigWhiteLabel, Integer> {
    Optional<ConfigWhiteLabel> findByHotelId(Integer hotelId);
}