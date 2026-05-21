package com.hotelhub.fiap.entities.dto;

import lombok.Data;

@Data
public class CheckinManualDTO {
    private Integer reservaId;
    private Integer quartoId; // Opcional: Se for null, o sistema escolhe o primeiro livre
}