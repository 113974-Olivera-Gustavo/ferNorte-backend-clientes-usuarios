package com.example.micro_b.services;

import com.example.micro_b.dtos.AuthResponse;
import com.example.micro_b.dtos.LoginRequest;
import com.example.micro_b.dtos.RegistroRequest;
import com.example.micro_b.dtos.UpdateUserRequest;
import com.example.micro_b.entities.CargoEntity;
import com.example.micro_b.entities.TipoDocumentoEntity;
import com.example.micro_b.entities.UsuarioEntity;
import com.example.micro_b.jwt.JwtService;
import com.example.micro_b.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private String email;
    private String verificationCode;
    private String token;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        email = "test@example.com";
        verificationCode = "123456";
        token = "token";
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest request = new LoginRequest("username", "password");
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setActivo(true);
        when(userRepository.findByEmail("username")).thenReturn(java.util.Optional.of(usuario));
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("dummyToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("dummyToken", response.getToken());
    }

    @Test
    public void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest("nonExistingUser", "password");
        when(userRepository.findByEmail("nonExistingUser")).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    public void testLoginUserNotActive() {
        LoginRequest request = new LoginRequest("inactiveUser", "password");
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setActivo(false);
        when(userRepository.findByEmail("inactiveUser")).thenReturn(java.util.Optional.of(usuario));

        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
    }


    @Test
    public void testRegister() {
        RegistroRequest registrationRequest = new RegistroRequest();
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());

        when(userRepository.findByNumeroDocumento(registrationRequest.getNumero_documento())).thenReturn(Optional.empty());

        when(userRepository.findByUsername(registrationRequest.getUsername())).thenReturn(Optional.empty());

        UsuarioEntity savedUser = UsuarioEntity.builder()
                .nombre(registrationRequest.getNombre())
                .username(registrationRequest.getUsername())
                .password("encodedPassword")
                .email(registrationRequest.getEmail())
                .apellido(registrationRequest.getApellido())
                .cargo(new CargoEntity())
                .id_tipo_documento(new TipoDocumentoEntity())
                .numeroDocumento(registrationRequest.getNumero_documento())
                .telefono(registrationRequest.getTelefono())
                .activo(true)
                .build();
        when(userRepository.save(any(UsuarioEntity.class))).thenReturn(savedUser);

        when(jwtService.getToken(any(UserDetails.class))).thenReturn("token");

        AuthResponse authResponse = authService.register(registrationRequest);

        verify(userRepository, times(1)).findByEmail(registrationRequest.getEmail());

        verify(userRepository, times(1)).findByNumeroDocumento(registrationRequest.getNumero_documento());

        verify(userRepository, times(1)).findByUsername(registrationRequest.getUsername());

        verify(userRepository, times(1)).save(any(UsuarioEntity.class));

        verify(jwtService, times(1)).getToken(any(UserDetails.class));

        assertNotNull(authResponse);
        assertEquals("token", authResponse.getToken());
    }
    @Test
    public void testUpdateUser() {
        // Datos de prueba
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(1L);
        updateUserRequest.setNombre("John");
        updateUserRequest.setApellido("Doe");
        updateUserRequest.setTelefono("123456789");

        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setId_cargo(1L);
        cargoEntity.setDescripcion("Vendedor");

        TipoDocumentoEntity tipoDocumentoEntity = new TipoDocumentoEntity();
        tipoDocumentoEntity.setId_tipo_documento(1L);
        tipoDocumentoEntity.setDescripcion("DNI");

        updateUserRequest.setId_cargo(cargoEntity);
        updateUserRequest.setId_tipo_documento(tipoDocumentoEntity);
        updateUserRequest.setActivo(true);

        UsuarioEntity existingUser = UsuarioEntity.builder().id(1L).build();
        when(userRepository.findById(updateUserRequest.getId())).thenReturn(Optional.of(existingUser));

        UsuarioEntity savedUser = UsuarioEntity.builder()
                .id(updateUserRequest.getId())
                .nombre(updateUserRequest.getNombre())
                .apellido(updateUserRequest.getApellido())
                .telefono(updateUserRequest.getTelefono())
                .id_tipo_documento(updateUserRequest.getId_tipo_documento())
                .numeroDocumento(updateUserRequest.getNumero_documento())
                .cargo(cargoEntity)
                .activo(updateUserRequest.getActivo())
                .build();
        when(userRepository.save(any(UsuarioEntity.class))).thenReturn(savedUser);

        UsuarioEntity updatedUser = authService.updateUser(updateUserRequest);

        verify(userRepository, times(1)).findById(updateUserRequest.getId());

        verify(userRepository, times(1)).save(any(UsuarioEntity.class));

        assertNotNull(updatedUser);
        assertEquals(updateUserRequest.getId(), updatedUser.getId());
        assertEquals(updateUserRequest.getNombre(), updatedUser.getNombre());
        assertEquals(updateUserRequest.getApellido(), updatedUser.getApellido());
        assertEquals(updateUserRequest.getTelefono(), updatedUser.getTelefono());
        assertEquals(updateUserRequest.getId_tipo_documento(), updatedUser.getId_tipo_documento());
        assertEquals(updateUserRequest.getNumero_documento(), updatedUser.getNumeroDocumento());

        assertNotNull(updatedUser.getCargo());
        assertEquals(cargoEntity.getId_cargo(), updatedUser.getCargo().getId_cargo());
        assertEquals(cargoEntity.getDescripcion(), updatedUser.getCargo().getDescripcion());

        assertEquals(updateUserRequest.getActivo(), updatedUser.getActivo());
    }

    @Test
    public void testGenerateTokenForPasswordResetWhenVerificationCodeIsInvalidThenThrowException() {
        assertThrows(RuntimeException.class, () -> authService.generateTokenForPasswordReset(email, verificationCode));
    }

    @Test
    public void testRegisterWhenEmailIsAlreadyInUseThenThrowException() {
        RegistroRequest request = new RegistroRequest();
        request.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UsuarioEntity()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    public void testUpdateUserWhenUserNotFoundThenThrowException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L);
        when(userRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.updateUser(request));
    }

    @Test
    public void testLoginWhenUserNotFoundThenThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername(email);
        when(userRepository.findByEmail(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    public void testUpdatePasswordUsingTokenWhenUsernameIsNullThenReturnFalse() {
        when(jwtService.getUsernameFromToken(token)).thenReturn(null);

        assertFalse(authService.updatePasswordUsingToken(token, "newPassword"));
    }

    @Test
    public void testStoreVerificationCodeInDatabaseWhenUserNotFoundThenThrowException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.storeVerificationCodeInDatabase(email, verificationCode));
    }
    @Test
    public void testVerifyPasswordResetCode() {
        String email = "test@example.com";
        String verificationCode = "123456";
        UsuarioEntity user = new UsuarioEntity();
        user.setVerificationCode(verificationCode);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean result = authService.verifyPasswordResetCode(email, verificationCode);

        assertTrue(result);
    }
    @Test
    public void testGenerateVerificationCode() {
        String verificationCode = authService.generateVerificationCode();
        assertEquals(6, verificationCode.length());
        assertTrue(verificationCode.matches("\\d+"));
    }
    @Test
    public void testStoreVerificationCodeInDatabase() {
        String email = "test@example.com";
        String verificationCode = "123456";
        UsuarioEntity user = new UsuarioEntity();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        authService.storeVerificationCodeInDatabase(email, verificationCode);

        assertEquals(verificationCode, user.getVerificationCode());

        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void testSendVerificationCode() {
        String email = "test@example.com";
        String verificationCode = "123456";

        authService.sendVerificationCode(email, verificationCode);

        verify(emailService, times(1)).sendVerificationCode(email, verificationCode);
    }
    @Test
    public void testGenerateTokenForPasswordReset_InvalidVerificationCode() {
        String email = "test@example.com";
        String verificationCode = "123456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.generateTokenForPasswordReset(email, verificationCode));
    }

}