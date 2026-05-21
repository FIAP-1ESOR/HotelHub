package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "consumos_estadia")
public class ConsumoEstadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaHospede reserva;

    @Column(name = "tipo_consumo", nullable = false, length = 30)
    private String tipoConsumo;

    @Column(name = "descricao_item", nullable = false, length = 150)
    private String descricaoItem;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario_momento", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioMomento;

    @Column(name = "valor_total_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotalItem;
}