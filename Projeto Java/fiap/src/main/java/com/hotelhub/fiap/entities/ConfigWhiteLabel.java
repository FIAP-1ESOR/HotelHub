package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "config_white_label")
public class ConfigWhiteLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "cor_primaria_hex", nullable = false, length = 7)
    private String corPrimariaHex;

    @Column(name = "cor_secundaria_hex", nullable = false, length = 7)
    private String corSecundariaHex;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "splash_image_path")
    private String splashImagePath;

    @Column(name = "info_hotel_texto", columnDefinition = "TEXT")
    private String infoHotelTexto;

    @Column(name = "info_hotel_image_path")
    private String infoHotelImagePath;
}