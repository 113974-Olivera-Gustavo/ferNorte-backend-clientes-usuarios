package com.example.micro_a.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigInteger;

@Table(name = "Cliente")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cliente;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @ManyToOne
    @JoinColumn(name = "id_tipo_doc")
    private TipoDocumentoEntity id_tipo_doc;

    @Column
    private Long nroDoc;

    @Column
    private String email;

    @Column
    private String telefono;
    @Column
    private String domicilio;

    @ManyToOne
    @JoinColumn(name = "id_tipo_cliente")
    private TipoClienteEntity id_tipo_cliente;

    @ManyToOne
    @JoinColumn(name = "id_categoria_fiscal")
    private TP_Categoria_FiscalEntity id_categoria_fiscal;


    @ManyToOne
    @JoinColumn(name = "id_clasificacion")
    private ClasificacionEntity id_clasificacion;

    @Column
    @Min(0)
    @Max(5000)
    private Double cant_puntos;
}
