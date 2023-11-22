package com.example.micro_a.controllers;

import com.example.micro_a.dtos.FacturacionRequest;
import com.example.micro_a.dtos.catalogo.CompraRequest;
import com.example.micro_a.dtos.catalogo.ProductoRequest;
import com.example.micro_a.entities.ClienteEntity;
import com.example.micro_a.models.Cliente;
import com.example.micro_a.services.ClienteService;
import com.example.micro_a.services.EmailService;
import com.example.micro_a.templates.FidelizacionClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fidelizacion")
public class FidelizacionController {
    private final ClienteService clienteService;
    private final EmailService emailService;

    public FidelizacionController(ClienteService clienteService, EmailService emailService) {
        this.clienteService = clienteService;
        this.emailService = emailService;
    }

    @PostMapping("/actualizar-puntos")
    public ResponseEntity<Cliente> actualizarPuntos(@RequestBody FacturacionRequest facturacionRequest) {
        Cliente cliente = clienteService.setearPuntos(facturacionRequest);
        return ResponseEntity.ok(cliente);
    }
    @GetMapping("/getAllProductos")
    public ResponseEntity<List<ProductoRequest>> getAllProductos(){
        List<ProductoRequest> productos = clienteService.getProductosDescuento();
        return ResponseEntity.ok(productos);
    }
    @PostMapping("/sendProductCatalogToAllClients")
    public ResponseEntity<String> sendProductCatalogToAllClients() {
        try {
            List<Cliente> clientes = clienteService.getAllClients();

            for (Cliente cliente : clientes) {
                List<ProductoRequest> productos = clienteService.getProductosDescuento();
                emailService.sendProductCatalog(cliente.getEmail(), productos);
            }

            return ResponseEntity.ok("Catálogo de productos enviado exitosamente a todos los clientes.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el catálogo a todos los clientes: " + e.getMessage());
        }
    }
    @PostMapping("/sendProductCatalogToAllClientsFilter")
    public ResponseEntity<String> sendProductCatalogToAllClientsFilter() {
        try {
            List<Cliente> clientes = clienteService.getAllClients();

            for (Cliente cliente : clientes) {
                Double puntosCliente = cliente.getCant_puntos();
                // Obtener los productos que aplican descuento para este cliente
                List<ProductoRequest> productos = clienteService.getProductosDescuento();
                // Envía el catálogo de productos filtrado por puntos al cliente
                emailService.sendProductCatalogFilter(cliente.getEmail(), productos, puntosCliente, cliente.nombreCompleto());
            }

            return ResponseEntity.ok("Catálogo de productos enviado exitosamente a todos los clientes.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el catálogo a todos los clientes: " + e.getMessage());
        }
    }
    //Metodo que resta la cantidad de puntos al cliente
    @PostMapping("/procesar-compra")
    public ResponseEntity<String> procesarCompra(@RequestBody CompraRequest compraRequest) {
        try {
            // Procesar la compra y restar los puntos al cliente
            clienteService.procesarCompra(compraRequest);

            return ResponseEntity.ok("Compra procesada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la compra: " + e.getMessage());
        }
    }

}
