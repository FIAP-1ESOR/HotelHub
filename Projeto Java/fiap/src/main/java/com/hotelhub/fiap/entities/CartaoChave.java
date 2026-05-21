package com.hotelhub.fiap.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cartoes_chave")
public class CartaoChave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaHospede reserva;

    @Column(name = "numero_cartao_chave", nullable = false, length = 50)
    private String numeroCartaoChave;

    @Column(name = "status_cartao", nullable = false, length = 20)
    private String statusCartao;
}