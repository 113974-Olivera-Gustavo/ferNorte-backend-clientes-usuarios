package com.example.micro_a.dtos.catalogo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductosCanjeados {
    private String codigo;
    private Integer cantidad;

}
