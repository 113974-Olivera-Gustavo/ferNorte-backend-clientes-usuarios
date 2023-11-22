package com.example.micro_a.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TP_Categoria_Fiscal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TP_Categoria_FiscalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_categoria_fiscal;

    @Column
    private String descripcion;


}
