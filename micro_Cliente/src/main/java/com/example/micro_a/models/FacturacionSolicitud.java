package com.example.micro_a.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturacionSolicitud {
    private Long nroDoc;
    private BigDecimal totalAmountBilled;
}
