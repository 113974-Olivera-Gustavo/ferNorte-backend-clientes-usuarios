package com.example.micro_a.dtos;

import com.example.micro_a.models.TP_Categoria_Fiscal;
import com.example.micro_a.models.TipoCliente;
import com.example.micro_a.models.TipoDocumento;

import java.math.BigInteger;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private TipoDocumento id_tipo_doc;
    private BigInteger nro_doc;
    private TP_Categoria_Fiscal id_categoria_fiscal;
    private TipoCliente id_tipo_cliente;
    private String categoria;
    private Integer cant_puntos;
}
