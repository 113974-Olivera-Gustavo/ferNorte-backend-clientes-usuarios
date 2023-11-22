package com.example.micro_b.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "tipo_documento")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoDocumentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipo_documento;
    @Column
    private String descripcion;
}
