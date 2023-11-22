package com.example.micro_b.dtos;

import com.example.micro_b.entities.CargoEntity;
import com.example.micro_b.entities.TipoDocumentoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistroResponse {
    private String nombre;
    private String apellido;
    private String username;
    private String password;
    private String email;
    private String telefono;
    private TipoDocumentoEntity id_tipo_documento;
    private String numero_documento;
    private CargoEntity id_cargo;
    private Boolean activo;
}
