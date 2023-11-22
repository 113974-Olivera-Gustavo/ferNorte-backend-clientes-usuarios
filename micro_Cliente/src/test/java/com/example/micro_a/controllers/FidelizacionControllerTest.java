package com.example.micro_a.controllers;

import com.example.micro_a.dtos.FacturacionRequest;
import com.example.micro_a.dtos.catalogo.CompraRequest;
import com.example.micro_a.dtos.catalogo.ProductoRequest;
import com.example.micro_a.models.Cliente;
import com.example.micro_a.services.ClienteService;
import com.example.micro_a.services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest
public class FidelizacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private EmailService emailService;

    @Test
    public void testGetAllProductosWhenValidRequestThenReturnOk() throws Exception {
        List<ProductoRequest> productos = Arrays.asList(new ProductoRequest(), new ProductoRequest());
        when(clienteService.getProductosDescuento()).thenReturn(productos);

        mockMvc.perform(MockMvcRequestBuilders.get("/fidelizacion/getAllProductos"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists());
    }

    @Test
    public void testSendProductCatalogToAllClients() throws Exception {
        List<Cliente> clientes = Arrays.asList(new Cliente(), new Cliente());
        when(clienteService.getAllClients()).thenReturn(clientes);

        mockMvc.perform(MockMvcRequestBuilders.post("/fidelizacion/sendProductCatalogToAllClients"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Catálogo de productos enviado exitosamente a todos los clientes."));
    }

    @Test
    public void testSendProductCatalogToAllClientsFilter() throws Exception {
        List<Cliente> clientes = Arrays.asList(new Cliente(), new Cliente());
        when(clienteService.getAllClients()).thenReturn(clientes);

        mockMvc.perform(MockMvcRequestBuilders.post("/fidelizacion/sendProductCatalogToAllClientsFilter"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Catálogo de productos enviado exitosamente a todos los clientes."));
    }

    @Test
    public void testProcesarCompra() throws Exception {
        CompraRequest compraRequest = new CompraRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/fidelizacion/procesar-compra")
                        .content(new ObjectMapper().writeValueAsString(compraRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Compra procesada exitosamente."));
    }

    @Test
    public void testActualizarPuntosWhenValidRequestThenReturnOk() throws Exception {
        FacturacionRequest facturacionRequest = new FacturacionRequest(12345678L);
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("John");
        cliente.setApellido("Doe");
        cliente.setEmail("john.doe@example.com");
        cliente.setTelefono("1234567890");
        cliente.setNroDoc(12345678L);
        cliente.setCant_puntos(1000.0);

        when(clienteService.setearPuntos(any(FacturacionRequest.class))).thenReturn(cliente);

        mockMvc.perform(MockMvcRequestBuilders.post("/fidelizacion/actualizar-puntos")
                        .content(new ObjectMapper().writeValueAsString(facturacionRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(cliente.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(cliente.getNombre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.apellido").value(cliente.getApellido()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(cliente.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telefono").value(cliente.getTelefono()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nroDoc").value(cliente.getNroDoc()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cant_puntos").value(cliente.getCant_puntos()));
    }
}