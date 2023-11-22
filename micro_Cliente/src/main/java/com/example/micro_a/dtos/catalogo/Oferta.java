package com.example.micro_a.dtos.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Oferta {
    private String codigo;
    private String nombre;
    private String descripcion;
    private Double precio_oferta;;
    private Double puntos;
    private Boolean activo;
}
