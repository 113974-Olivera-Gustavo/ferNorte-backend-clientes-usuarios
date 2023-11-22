package com.example.micro_a.models;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private TipoDocumento id_tipo_doc;
    private Long nroDoc;
    private String domicilio;
    private TP_Categoria_Fiscal id_categoria_fiscal;
    private TipoCliente id_tipo_cliente;
    private Clasificacion id_clasificacion;
    private Double cant_puntos;

    public String nombreCompleto(){
        return this.getNombre()+" "+this.getApellido();
    }
}
