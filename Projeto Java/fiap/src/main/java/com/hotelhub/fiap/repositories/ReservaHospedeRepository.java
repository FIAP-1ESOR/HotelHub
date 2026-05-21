package com.hotelhub.fiap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelhub.fiap.entities.ReservaHospede;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaHospedeRepository extends JpaRepository<ReservaHospede, Integer> {

    Optional<ReservaHospede> findByHotelIdAndCodigoReserva(Integer hotelId, String codigoReserva);
    
    List<ReservaHospede> findByHotelIdAndDocumentoCpfPassaporte(Integer hotelId, String documentoCpfPassaporte);
    
    Optional<ReservaHospede> findByHotelIdAndCodigoReservaAndDocumentoCpfPassaporte(
            Integer hotelId, String codigoReserva, String documentoCpfPassaporte);
}