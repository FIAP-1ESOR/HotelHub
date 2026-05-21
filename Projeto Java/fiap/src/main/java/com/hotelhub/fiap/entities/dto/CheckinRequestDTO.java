package com.hotelhub.fiap.entities.dto;

import lombok.Data;

@Data
public class CheckinRequestDTO {
    private String codigoReserva;
    private String documentoCpfPassaporte;
    
    private Boolean termoConsentimentoAceito;
}