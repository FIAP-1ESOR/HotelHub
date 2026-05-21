package com.hotelhub.fiap.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios_portal")
public class UsuarioPortal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String cargo;
}