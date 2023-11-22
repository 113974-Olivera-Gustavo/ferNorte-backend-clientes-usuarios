package com.example.micro_a.controllers;

import com.example.micro_a.entities.SolicitudEntity;
import com.example.micro_a.models.Cliente;
import com.example.micro_a.models.ClienteTemporal;
import com.example.micro_a.models.Solicitud;
import com.example.micro_a.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnero")
public class TurneroController {

    private final ClienteService clienteService;

    @Autowired
    public TurneroController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/generarTurnoClienteRegistrado")
    public ResponseEntity<Solicitud> generarTurnoClienteRegistrado(@RequestParam Long nroDoc) {
        Solicitud solicitud = clienteService.generarSolicitudCliente(nroDoc);
        return ResponseEntity.ok(solicitud);
    }
    @PostMapping("/generarTurnoClienteTemporal")
    public ResponseEntity<Solicitud> generarTurnoClienteTemporal(@RequestBody ClienteTemporal request) {
        Solicitud solicitud = clienteService.generarSolicitudClienteTemporal(request);
        return ResponseEntity.ok(solicitud);
    }
    @GetMapping("/getClientesSinAtender")
    @Operation(summary = "Obtener todas los clientes sin atender", description = "Obtiene todas los clientes sin atender")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<SolicitudEntity>> getClientesRegitrados() {
        List<SolicitudEntity> clientes = clienteService.getSolicitudClienteRegistrado();
        return ResponseEntity.ok(clientes);
    }
    @GetMapping("/getClientesAtendidos")
    @Operation(summary = "Obtener todas los clientes atendidos", description = "Obtiene todas los clientes atendidos")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<SolicitudEntity>> getClientesAtendidos() {
        List<SolicitudEntity> clientes = clienteService.getSolicitudClienteAtendido();
        return ResponseEntity.ok(clientes);
    }
    @PutMapping("/atenderCliente/{id}")
    public ResponseEntity<String> darBajaLogica(@PathVariable Long id) {
        boolean bajaExitosa = clienteService.atenderCliente(id);
        if (bajaExitosa) {
            return ResponseEntity.ok("Cliente atendido");
        } else {
            return ResponseEntity.badRequest().body("No se pudo atender al cliente");
        }
    }
}
