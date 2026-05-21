package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dicionario_totem")
public class DicionarioTotem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "idioma_id", nullable = false)
    private Idioma idioma;

    @Column(name = "chave_componente", nullable = false, length = 100)
    private String chaveComponente;

    @Column(name = "texto_traduzido", nullable = false, length = 500)
    private String textoTraduzido;
}
