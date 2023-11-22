package com.example.micro_a.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Clasificacion")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClasificacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_clasificacion;

    @Column
    private String descripcion;
}
