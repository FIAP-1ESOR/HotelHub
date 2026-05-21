package com.hotelhub.fiap.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "hoteis")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_estabelecimento", nullable = false, length = 150)
    private String nomeEstabelecimento;

    @Column(nullable = false, length = 18)
    private String cnpj;

    @Column(name = "status_licenca", nullable = false, length = 20)
    private String statusLicenca;

    @Column(name = "email_suporte_mock", length = 100)
    private String emailSuporteMock;
}