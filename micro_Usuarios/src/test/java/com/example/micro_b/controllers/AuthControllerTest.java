package com.example.micro_b.controllers;

import com.example.micro_b.dtos.*;
import com.example.micro_b.entities.CargoEntity;
import com.example.micro_b.entities.TipoDocumentoEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.micro_b.services.AuthService;
import com.example.micro_b.services.UserServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserServices userServices;
    @InjectMocks
    private AuthController authController;

    private List<CargoRequest> cargos;

    @BeforeEach
    public void setup() {
        cargos = Arrays.asList(
                new CargoRequest(1L, "Cargo 1"),
                new CargoRequest(2L, "Cargo 2")
        );
    }

    @Test
    public void testUpdatePassword() throws Exception {
        when(authService.updatePasswordUsingToken("mockedToken", "newPassword")).thenReturn(true);

        UpdateCredentialResponse updateCredentialResponse = new UpdateCredentialResponse();
        updateCredentialResponse.setToken("mockedToken");
        updateCredentialResponse.setNewPassword("newPassword");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateCredentialResponse)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String expectedMessage = "Contraseña actualizada con éxito.";
        String responseMessage = result.getResponse().getContentAsString();
        assertEquals(expectedMessage, responseMessage);

        verify(authService, times(1)).updatePasswordUsingToken("mockedToken", "newPassword");
    }
    @Test
    public void testResetPassword() throws Exception {

        when(authService.generateTokenForPasswordReset("test@example.com", "123456")).thenReturn("mockedToken");

        UpdateCredentialsRequest resetPasswordRequest = new UpdateCredentialsRequest();
        resetPasswordRequest.setEmail("test@example.com");
        resetPasswordRequest.setVerificationCode("123456");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(resetPasswordRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String expectedToken = "mockedToken";
        String responseJson = result.getResponse().getContentAsString();
        AuthResponse authResponse = new ObjectMapper().readValue(responseJson, AuthResponse.class);
        assertEquals(expectedToken, authResponse.getToken());

        verify(authService, times(1)).generateTokenForPasswordReset("test@example.com", "123456");
    }
    @Test
    public void testForgotPassword() throws Exception {

        when(authService.generateVerificationCode()).thenReturn("123456");

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestMap)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        String expectedResponse = "Se ha enviado un código de verificación al email del usuario.";
        assertEquals(expectedResponse, result.getResponse().getContentAsString());

        verify(authService, times(1)).generateVerificationCode();
        verify(authService, times(1)).sendVerificationCode("test@example.com", "123456");
        verify(authService, times(1)).storeVerificationCodeInDatabase("test@example.com", "123456");
    }
    @Test
    public void testRegister() throws Exception {
        RegistroRequest registroRequest = RegistroRequest.builder()
                .nombre("Nombre")
                .apellido("Apellido")
                .username("usuario")
                .password("contrasena")
                .email("correo@ejemplo.com")
                .telefono("123456789")
                .id_tipo_documento(new TipoDocumentoEntity())
                .numero_documento("12345678")
                .id_cargo(new CargoEntity())
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("token_de_prueba")
                .build();

        when(authService.register(eq(registroRequest))).thenReturn(authResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registroRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        AuthResponse actualAuthResponse = new ObjectMapper().readValue(responseJson, AuthResponse.class);

        assertEquals(authResponse, actualAuthResponse);
    }
    @Test
    public void testLogin() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .username("usuario")
                .password("contrasena")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("token_de_prueba")
                .build();

        when(authService.login(eq(loginRequest))).thenReturn(authResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        AuthResponse actualAuthResponse = new ObjectMapper().readValue(responseJson, AuthResponse.class);

        assertEquals(authResponse, actualAuthResponse);
    }
    @Test
    public void testFindUserByCargo() throws Exception {
        String cargo = "Cargo de Prueba";


        RegistroResponse registroResponse1 = new RegistroResponse();
        RegistroResponse registroResponse2 = new RegistroResponse();
        List<RegistroResponse> registroResponses = Arrays.asList(registroResponse1, registroResponse2);

        when(userServices.findUsersByCargo(eq(cargo))).thenReturn(registroResponses);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/auth/getUser/ByCargo")
                        .param("cargo", cargo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<RegistroResponse> actualRegistroResponses = new ObjectMapper().readValue(responseJson, new TypeReference<List<RegistroResponse>>() {});

        assertEquals(registroResponses, actualRegistroResponses);
    }
    @Test
    public void testCreateCargo() throws Exception {
        CargoRequest cargoRequest = new CargoRequest(1L, "Cargo de Prueba");

        CargoEntity cargoEntity = new CargoEntity(1L, "Cargo de Prueba");
        when(userServices.createCargo(any(CargoRequest.class))).thenReturn(cargoEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/createCargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cargoRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        CargoEntity actualCargoEntity = new ObjectMapper().readValue(responseJson, CargoEntity.class);

        assertEquals(cargoEntity, actualCargoEntity);
    }


    @Test
    public void testCreateTipoDoc() throws Exception {
        TipoDocumentoRequest tipoDocumentoRequest = new TipoDocumentoRequest(1L, "Documento de Identidad");

        TipoDocumentoEntity tipoDocumentoEntity = new TipoDocumentoEntity(1L, "Documento de Identidad");
        when(userServices.createTipoDoc(any(TipoDocumentoRequest.class))).thenReturn(tipoDocumentoEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/create/TipoDoc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tipoDocumentoRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TipoDocumentoEntity actualTipoDocumentoEntity = new ObjectMapper().readValue(responseJson, TipoDocumentoEntity.class);

        assertEquals(tipoDocumentoEntity, actualTipoDocumentoEntity);
    }

    @Test
    public void testDarBajaLogicaUsuarioNoEncontrado() throws Exception {
        String numeroDocumento = "123456789";
        when(userServices.bajaUser(numeroDocumento)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/auth/baja-logica/{numeroDocumento}", numeroDocumento)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDarBajaLogica() throws Exception {
        String numeroDocumento = "123456789";
        when(userServices.bajaUser(numeroDocumento)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/auth/baja-logica/{numeroDocumento}", numeroDocumento)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Usuario dado de baja exitosamente"));
    }

    @Test
    public void testGetCargos() throws Exception {
        CargoRequest cargo1 = new CargoRequest(1L, "Cargo 1");
        CargoRequest cargo2 = new CargoRequest(2L, "Cargo 2");
        List<CargoRequest> cargos = Arrays.asList(cargo1, cargo2);

        Mockito.when(userServices.getCargos()).thenReturn(cargos);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/auth/getCargos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<CargoRequest> actualCargos = objectMapper.readValue(responseJson, new TypeReference<List<CargoRequest>>() {});


        assertEquals(cargos, actualCargos);
    }

}