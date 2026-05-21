package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transacoes_pagamento")
public class TransacaoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaHospede reserva;

    @Column(name = "metodo_pagamento", nullable = false, length = 30)
    private String metodoPagamento;

    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "data_hora_pagamento", nullable = false)
    private LocalDateTime dataHoraPagamento;
}