package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservas_hospedes")
public class ReservaHospede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "codigo_reserva", nullable = false, length = 20)
    private String codigoReserva;

    @Column(name = "nome_hospede", nullable = false, length = 150)
    private String nomeHospede;

    @Column(name = "documento_cpf_passaporte", nullable = false, length = 30)
    private String documentoCpfPassaporte;

    @Column(name = "quantidade_pessoas", nullable = false)
    private Integer quantidadePessoas;

    @Column(name = "data_entrada_prevista", nullable = false)
    private LocalDateTime dataEntradaPrevista;

    @Column(name = "data_saida_prevista", nullable = false)
    private LocalDateTime dataSaidaPrevista;

    @Column(name = "data_entrada_real")
    private LocalDateTime dataEntradaReal;

    @Column(name = "data_saida_real")
    private LocalDateTime dataSaidaReal;

    @Column(name = "termo_consentimento_aceito", nullable = false)
    private Boolean termoConsentimentoAceito;

    @ManyToOne
    @JoinColumn(name = "quarto_id") // Pode ser nulo
    private Quarto quarto;

    @Column(name = "status_reserva", nullable = false, length = 25)
    private String statusReserva;
}