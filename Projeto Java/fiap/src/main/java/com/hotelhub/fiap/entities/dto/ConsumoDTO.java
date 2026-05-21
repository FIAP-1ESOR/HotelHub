package com.hotelhub.fiap.entities.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConsumoDTO {
    private Integer reservaId;
    private String tipoConsumo; // FRIGOBAR, ROOM_SERVICE, LAVANDERIA
    private String descricaoItem;
    private Integer quantidade;
    private BigDecimal precoUnitarioMomento;
}