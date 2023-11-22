package com.example.micro_b.controllers;

import com.example.micro_b.dtos.*;
import com.example.micro_b.entities.CargoEntity;
import com.example.micro_b.entities.TipoDocumentoEntity;
import com.example.micro_b.entities.UsuarioEntity;
import com.example.micro_b.services.AuthService;
import com.example.micro_b.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserServices userServices;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/update")
    public ResponseEntity<UsuarioEntity> updateBasicData(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(authService.updateUser(request));
    }
    //Metodo para enviar codigo de verificacion al mail del usuario interno
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email != null && !email.isEmpty()) {
            String verificationCode = authService.generateVerificationCode();
            authService.sendVerificationCode(email, verificationCode);
            authService.storeVerificationCodeInDatabase(email, verificationCode);
            return ResponseEntity.ok("Se ha enviado un código de verificación al email del usuario.");
        } else {
            return ResponseEntity.badRequest().body("El email no puede estar vacío.");
        }
    }
    //Metodo para usar codigo de verificacion para generar token para modificar password
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody UpdateCredentialsRequest resetPasswordRequest) {
        String token = authService.generateTokenForPasswordReset(resetPasswordRequest.getEmail(), resetPasswordRequest.getVerificationCode());
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }
    //Metodo para actualizar password mediante token obtenido en el metodo anterior
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdateCredentialResponse request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        boolean passwordUpdated = authService.updatePasswordUsingToken(token, newPassword);

        if (passwordUpdated) {
            return ResponseEntity.ok("Contraseña actualizada con éxito.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar la contraseña. Verifique el token.");
        }
    }

    //User
    @GetMapping("/getUser/ByCargo")
    public ResponseEntity<List<RegistroResponse>> findUserByCargo(@RequestParam String cargo){
        List<RegistroResponse> registroResponses = userServices.findUsersByCargo(cargo);
        return ResponseEntity.ok(registroResponses);
    }


    @PostMapping("/createCargo")
    public ResponseEntity<CargoEntity> createCargo(@RequestBody CargoRequest cargo) {
        return ResponseEntity.ok(userServices.createCargo(cargo));
    }

    @PostMapping("/create/TipoDoc")
    public ResponseEntity<TipoDocumentoEntity> createTipoDoc(@RequestBody TipoDocumentoRequest tipoDocumentoRequest) {
        return ResponseEntity.ok(userServices.createTipoDoc(tipoDocumentoRequest));
    }


    @PutMapping("/baja-logica/{numeroDocumento}")
    public ResponseEntity<String> darBajaLogica(@PathVariable String numeroDocumento) {
        boolean bajaExitosa = userServices.bajaUser(numeroDocumento);
        if (bajaExitosa) {
            return ResponseEntity.ok("Usuario dado de baja exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getCargos")
    public ResponseEntity<List<CargoRequest>> getCargos(){
        List<CargoRequest> cargoRequests = userServices.getCargos();
        return ResponseEntity.ok(cargoRequests);
    }

}
