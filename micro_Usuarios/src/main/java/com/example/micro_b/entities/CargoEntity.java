package com.example.micro_b.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "cargo")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CargoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cargo;
    @Column
    private String descripcion;
}
