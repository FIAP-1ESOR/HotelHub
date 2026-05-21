package com.hotelhub.fiap.entities.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NovaReservaDTO {
    private String nomeHospede;
    private String documentoCpfPassaporte;
    private Integer quantidadePessoas;
    private LocalDateTime dataEntradaPrevista;
    private LocalDateTime dataSaidaPrevista;
}