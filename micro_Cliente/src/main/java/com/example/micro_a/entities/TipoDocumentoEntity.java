package com.example.micro_a.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TipoDocumento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoDocumentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipo_doc;

    @Column
    private String tipo_documento;
}

