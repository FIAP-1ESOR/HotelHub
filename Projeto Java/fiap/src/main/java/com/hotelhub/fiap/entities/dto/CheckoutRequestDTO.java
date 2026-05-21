package com.hotelhub.fiap.entities.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CheckoutRequestDTO {
    private Integer reservaId;
    
    // Dados do pagamento simulado na "maquininha" do totem
    private String metodoPagamento; // PIX, CARTAO_CREDITO, CARTAO_DEBITO
    private BigDecimal valorPago;
    
}