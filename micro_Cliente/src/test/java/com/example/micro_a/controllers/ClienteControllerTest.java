package com.example.micro_a.controllers;

import com.example.micro_a.entities.TipoDocumentoEntity;
import com.example.micro_a.models.*;
import com.example.micro_a.services.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Mock
    private ClienteController clienteController;

    @Mock
    private Cliente cliente;

    @Mock
    private TipoDocumento tipoDocumento;

    @BeforeEach
    public void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("John");
        cliente.setApellido("Doe");
        cliente.setEmail("john.doe@example.com");
        cliente.setTelefono("1234567890");
        cliente.setNroDoc(123456789L);
        cliente.setDomicilio("Calle Falsa 123");
        cliente.setCant_puntos(0.0);
        cliente.setId_tipo_doc(new TipoDocumento());
        cliente.setId_tipo_cliente(new TipoCliente());
        TP_Categoria_Fiscal tp_categoria_fiscal = new TP_Categoria_Fiscal();
        tp_categoria_fiscal.setId_categoria(1L);
        cliente.setId_categoria_fiscal(tp_categoria_fiscal);
        cliente.setId_clasificacion(new Clasificacion());

        tipoDocumento = new TipoDocumento();
        tipoDocumento.setId_tipo_doc(1L);
        tipoDocumento.setTipo_documento("DNI");
    }

    @Test
    public void testGetClienteByDniWhenValidNroDocThenReturnClientes() throws Exception {
        List<Cliente> mockClientes = Arrays.asList(cliente);

        when(clienteService.getClienteByDni(any(Long.class))).thenReturn(mockClientes);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clientes/getClienteByDni/{nroDoc}", cliente.getNroDoc())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value(cliente.getId()))
                .andExpect(jsonPath("$[0].nombre").value(cliente.getNombre()));
    }

    @Test
    public void testGetClienteByDniWhenInvalidNroDocThenReturnEmptyList() throws Exception {
        when(clienteService.getClienteByDni(any(Long.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clientes/getClienteByDni/{nroDoc}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.empty()));
    }

    @Test
    public void testGetClasificacionWhenExceptionThenReturnInternalServerError() throws Exception {
        when(clienteService.getClasificacion()).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clientes/getAllClasificacion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
    @Test
    public void testGetClasificacionWhenServiceReturnsEmptyListThenReturnEmptyList() throws Exception {
        when(clienteService.getClasificacion()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clientes/getAllClasificacion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.empty()));
    }

    @Test
    public void testGetAllClientesWhenClientsExistThenReturnClients() throws Exception {
        Cliente mockCliente1 = new Cliente();
        mockCliente1.setId(1L);
        mockCliente1.setNombre("Test1");

        Cliente mockCliente2 = new Cliente();
        mockCliente2.setId(2L);
        mockCliente2.setNombre("Test2");

        List<Cliente> mockClientes = Arrays.asList(mockCliente1, mockCliente2);

        when(clienteService.getAllClients()).thenReturn(mockClientes);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clientes/getAllClientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value("Test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nombre").value("Test2"));
    }

    @Test
    public void testCreatePersonaWhenValidClienteThenReturn200AndCliente() throws Exception {
        Cliente mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setNombre("Test");

        when(clienteService.createClient(any(Cliente.class))).thenReturn(mockCliente);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/clientes/postCliente")
                        .content(new ObjectMapper().writeValueAsString(mockCliente))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Test"));
    }

    @Test
    public void testCreatePersonaWhenInvalidClienteThenReturn500() throws Exception {
        Cliente invalidCliente = new Cliente();
        invalidCliente.setId(-1L);
        invalidCliente.setNombre("");

        when(clienteService.createClient(any(Cliente.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/clientes/postCliente")
                        .content(new ObjectMapper().writeValueAsString(invalidCliente))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testGetAllClientes() throws Exception {
        Cliente mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setNombre("Test");

        List<Cliente> mockClientes = Arrays.asList(mockCliente);

        when(clienteService.getAllClients()).thenReturn(mockClientes);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clientes/getAllClientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value("Test"));
    }

    @Test
    public void testUpdateClienteWhenValidClienteThenReturnOk() throws Exception {
        Cliente mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setNombre("Test");

        when(clienteService.updateClient(any(Cliente.class))).thenReturn(mockCliente);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clientes/updateCliente")
                        .content(new ObjectMapper().writeValueAsString(mockCliente))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Test"));
    }

    @Test
    public void testUpdateClienteWhenInvalidClienteThenReturnInternalServerError() throws Exception {
        Cliente invalidCliente = new Cliente();
        invalidCliente.setId(-1L);
        invalidCliente.setNombre("");

        when(clienteService.updateClient(any(Cliente.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clientes/updateCliente")
                        .content(new ObjectMapper().writeValueAsString(invalidCliente))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}