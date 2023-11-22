package com.example.micro_a.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteTemporal {
    private Long id;
    private String nombre;
    private String apellido;
    private Long nroDoc;
}
