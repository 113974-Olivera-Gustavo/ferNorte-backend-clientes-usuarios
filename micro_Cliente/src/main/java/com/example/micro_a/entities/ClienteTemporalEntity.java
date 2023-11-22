package com.example.micro_a.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name = "ClienteTemporal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteTemporalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cliente_temporal;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @Column
    private BigInteger nroDoc;
}
