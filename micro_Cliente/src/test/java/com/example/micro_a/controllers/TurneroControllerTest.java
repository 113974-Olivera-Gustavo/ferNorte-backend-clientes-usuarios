package com.example.micro_a.controllers;

import com.example.micro_a.entities.SolicitudEntity;
import com.example.micro_a.models.*;
import com.example.micro_a.services.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(TurneroController.class)
public class TurneroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerarTurnoClienteRegistradoWhenValidRequestThenReturnSolicitud() throws Exception {
        Solicitud solicitud = new Solicitud();
        solicitud.setId_solicitud(1L);
        when(clienteService.generarSolicitudCliente(any(Long.class))).thenReturn(solicitud);

        mockMvc.perform(MockMvcRequestBuilders.post("/turnero/generarTurnoClienteRegistrado")
                        .param("nroDoc", "12345678"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id_solicitud").value(1L));
    }

    @Test
    public void testGenerarTurnoClienteTemporalWhenValidRequestThenReturnSolicitud() throws Exception {
        ClienteTemporal clienteTemporal = new ClienteTemporal();
        clienteTemporal.setId(1L);
        clienteTemporal.setNombre("John");
        clienteTemporal.setApellido("Doe");
        clienteTemporal.setNroDoc(123456789L);

        Solicitud solicitud = new Solicitud();
        solicitud.setId_solicitud(1L);
        when(clienteService.generarSolicitudClienteTemporal(any(ClienteTemporal.class))).thenReturn(solicitud);

        mockMvc.perform(MockMvcRequestBuilders.post("/turnero/generarTurnoClienteTemporal")
                        .content(objectMapper.writeValueAsString(clienteTemporal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id_solicitud").value(1L));
    }

    @Test
    public void testGetClientesRegitradosWhenValidRequestThenReturnList() throws Exception {
        SolicitudEntity solicitudEntity = new SolicitudEntity();
        solicitudEntity.setId_solicitud(1L);
        when(clienteService.getSolicitudClienteRegistrado()).thenReturn(Collections.singletonList(solicitudEntity));

        mockMvc.perform(MockMvcRequestBuilders.get("/turnero/getClientesSinAtender"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id_solicitud").value(1L));
    }

    @Test
    public void testGetClientesAtendidosWhenValidRequestThenReturnList() throws Exception {
        SolicitudEntity solicitudEntity = new SolicitudEntity();
        solicitudEntity.setId_solicitud(1L);
        when(clienteService.getSolicitudClienteAtendido()).thenReturn(Collections.singletonList(solicitudEntity));

        mockMvc.perform(MockMvcRequestBuilders.get("/turnero/getClientesAtendidos"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id_solicitud").value(1L));
    }

    @Test
    public void testDarBajaLogicaWhenValidRequestThenReturnSuccessMessage() throws Exception {
        when(clienteService.atenderCliente(anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/turnero/atenderCliente/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Cliente atendido"));
    }

    @Test
    public void testDarBajaLogicaWhenInvalidRequestThenReturnErrorMessage() throws Exception {
        when(clienteService.atenderCliente(anyLong())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/turnero/atenderCliente/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("No se pudo atender al cliente"));
    }
}