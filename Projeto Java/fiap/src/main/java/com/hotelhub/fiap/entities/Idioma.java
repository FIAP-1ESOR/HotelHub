package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "idiomas")
public class Idioma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "codigo_iso", nullable = false, length = 5)
    private String codigoIso;

    @Column(name = "nome_idioma", nullable = false, length = 50)
    private String nomeIdioma;

    @Column(nullable = false)
    private Boolean ativo;
}