package com.example.micro_a.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    private Long id_solicitud;
    private Cliente cliente;
    private ClienteTemporal clienteTemporal;
    private Boolean atendido;
    private LocalDateTime fecha;
    private String nro_turno;
}
