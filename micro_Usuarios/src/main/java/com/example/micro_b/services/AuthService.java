package com.example.micro_b.services;

import com.example.micro_b.dtos.*;
import com.example.micro_b.entities.UsuarioEntity;
import com.example.micro_b.jwt.JwtService;
import com.example.micro_b.repositories.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private final UsuarioRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;

    public AuthService(UsuarioRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
    }
    public AuthResponse register(RegistroRequest request) {
        Optional<UsuarioEntity> existingUserEmail = userRepository.findByEmail(request.getEmail());
        if(existingUserEmail.isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        Optional<UsuarioEntity> existingUserDocNumber = userRepository.findByNumeroDocumento(request.getNumero_documento());
        if(existingUserDocNumber.isPresent()) {
            throw new RuntimeException("Document number already in use");
        }
        Optional<UsuarioEntity> existingUsername = userRepository.findByUsername(request.getUsername());
        if(existingUsername.isPresent()) {
            throw new RuntimeException("Username already in use");
        }

        UsuarioEntity user = UsuarioEntity.builder()
                .nombre(request.getNombre())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .apellido(request.getApellido())
                .cargo(request.getId_cargo())
                .id_tipo_documento(request.getId_tipo_documento())
                .numeroDocumento(request.getNumero_documento())
                .telefono(request.getTelefono())
                .activo(true)
                .build();
        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken((UserDetails) user))
                .build();
    }
    public UsuarioEntity updateUser(UpdateUserRequest request){
        if(request == null){
            throw new IllegalArgumentException("The update request must not be null");
        }
        UsuarioEntity user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setId_tipo_documento(request.getId_tipo_documento());
        user.setNumeroDocumento(request.getNumero_documento());
        user.setCargo(request.getId_cargo());
        user.setActivo(request.getActivo());
        return userRepository.save(user);
    }
    public AuthResponse login(LoginRequest request) {
        Boolean active = userRepository.findByEmail(request.getUsername())
                .map(UsuarioEntity::getActivo)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!active){
            throw new RuntimeException("User not active");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public boolean updatePasswordUsingToken(String token, String newPassword) {
        String username = jwtService.getUsernameFromToken(token);

        if (username != null) {
            UsuarioEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            userRepository.save(user);

            return true; // Contraseña actualizada con éxito
        }

        return false;
    }

    public boolean verifyPasswordResetCode(String email, String verificationCode) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    String storedVerificationCode = user.getVerificationCode();
                    return storedVerificationCode != null && storedVerificationCode.equals(verificationCode);
                })
                .orElse(false);
    }

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
    public void storeVerificationCodeInDatabase(String email, String verificationCode) {
        UsuarioEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerificationCode(verificationCode);
        userRepository.save(user);
    }
    public void sendVerificationCode(String email, String verificationCode) {
        emailService.sendVerificationCode(email, verificationCode);
    }


public String generateTokenForPasswordReset(String email, String verificationCode) {
    // Verifica el código de verificación para el usuario con el correo electrónico dado
    if (verifyPasswordResetCode(email, verificationCode)) {
        // Genera un token para permitir la modificación de las credenciales
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtService.getToken(userDetails);
    } else {
        throw new RuntimeException("Código de verificación no válido");
    }

}



}
