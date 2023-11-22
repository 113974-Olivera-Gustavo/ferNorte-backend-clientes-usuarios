package com.example.micro_a.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_cliente")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipo_cliente;

    @Column
    private String tipo_cliente;
}
