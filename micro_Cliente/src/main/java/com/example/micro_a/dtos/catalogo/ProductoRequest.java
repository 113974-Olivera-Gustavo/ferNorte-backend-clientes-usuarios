package com.example.micro_a.dtos.catalogo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {
    private String codigo;
    private String nombre;
    private String descripcion;
    private Double precio_compra;
    private Double precio_mayorista;
    private String imageURL;
    private String dimensiones;
    private String peso;
    private String material;
    private String color;
    private String paisOrigen;
    private Marca marca;
    private List<Categoria> categorias;
    private List<Oferta> ofertas;
    private List<Descuento> descuentos;

}
