package com.example.micro_a.controllers;

import com.example.micro_a.entities.*;
import com.example.micro_a.models.*;
import com.example.micro_a.services.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;

    }

    @PostMapping("/postCliente")
    @Operation(summary = "Crear una persona", description = "Crea una nueva persona.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<Cliente> createPersona(@RequestBody Cliente cliente) {
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Cliente createdCliente = clienteService.createClient(cliente);
        return ResponseEntity.ok(createdCliente);
    }
    @GetMapping("/getAllClientes")
    @Operation(summary = "Obtener todas las personas", description = "Obtiene todas las personas.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteService.getAllClients();
        return ResponseEntity.ok(clientes);
    }
    @GetMapping("/getAllTipoCliente")
    @Operation(summary = "Obtener todos los tipos de clientes", description = "Obtiene todos los tipos de cliente.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    @ApiResponse(responseCode = "400", description = "No se pudieron obtener los datos.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<TipoCliente>> getTipoCliente() {
        List<TipoCliente> tipoCliente = clienteService.getTipoCliente();
        return ResponseEntity.ok(tipoCliente);
    }
    @GetMapping("/getAllClasificacion")
    @Operation(summary = "Obtener todas las clasificaciones", description = "Obtiene todas las clasificaciones.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<Clasificacion>> getClasificacion() {
        List<Clasificacion> clasificacion = clienteService.getClasificacion();
        return ResponseEntity.ok(clasificacion);
    }
    @GetMapping("/getAllTPCategoriaFiscal")
    @Operation(summary = "Obtener todas los tipos categoria fiscal", description = "Obtiene todos los tipos categoria fiscal.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<TP_Categoria_Fiscal>> getCategoriaFiscal() {
        List<TP_Categoria_Fiscal> categoriaFiscal = clienteService.getTPCategoriaFiscal();
        return ResponseEntity.ok(categoriaFiscal);
    }
    @GetMapping("/getAllTipoDocumento")
    @Operation(summary = "Obtener todos los tipos de documento", description = "Obtiene todos los tipos de documento.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<List<TipoDocumento>> getTipoDocumento() {
        List<TipoDocumento> tipoDocumento = clienteService.getTipoDocumento();
        return ResponseEntity.ok(tipoDocumento);
    }

    @PutMapping("/updateCliente")
    @Operation(summary = "Actualizar una persona", description = "Actualiza una persona.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<Cliente> updateCliente(@RequestBody Cliente cliente) {
        Cliente updatedCliente = clienteService.updateClient(cliente);
        return ResponseEntity.ok(updatedCliente);
    }

    @PostMapping("/createTipoDocumento")
    @Operation(summary = "Crear un tipo de documento", description = "Crea un nuevo tipo de documento.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    @ApiResponse(responseCode = "400", description = "No se pudo crear el Tipo de Documento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<TipoDocumentoEntity> createTipoDocumento(@RequestBody TipoDocumento tipoDocumento) {
        TipoDocumentoEntity createdCliente = clienteService.createTipoDoc(tipoDocumento);
        return ResponseEntity.ok(createdCliente);
    }

    @PostMapping("/createTipoCliente")
    @Operation(summary = "Crear un tipo de cliente", description = "Crea un nuevo tipo de cliente.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<TipoClienteEntity> createTipoCliente(@RequestBody TipoCliente tipoCliente) {
        TipoClienteEntity createdCliente = clienteService.createTipoCliente(tipoCliente);
        return ResponseEntity.ok(createdCliente);
    }

    @PostMapping("/createClasificacion")
    @Operation(summary = "Crear una clasificacion", description = "Crea una nueva clasificacion.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<ClasificacionEntity> createClasificacion(@RequestBody Clasificacion clasificacion) {
        ClasificacionEntity createdCliente = clienteService.createClasificacion(clasificacion);
        return ResponseEntity.ok(createdCliente);
    }

    @PostMapping("/createTP_Categoria_Fiscal")
    @Operation(summary = "Crear una categoria fiscal", description = "Crea una nueva categoria fiscal.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class)))
    public ResponseEntity<TP_Categoria_FiscalEntity> createTP_Categoria_Fiscal(@RequestBody TP_Categoria_Fiscal tpCategoriaFiscal) {
        TP_Categoria_FiscalEntity createdCliente = clienteService.createTP_Categoria_Fiscal(tpCategoriaFiscal);
        return ResponseEntity.ok(createdCliente);
    }
    @GetMapping("/getClienteByDni/{nroDoc}")
    public ResponseEntity<List<Cliente>> getClienteByDni(@PathVariable Long nroDoc) {
        List<Cliente> cliente = clienteService.getClienteByDni(nroDoc);
        return ResponseEntity.ok(cliente);
    }


}
