package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.AuthResponseDTO;
import org.example.backend.dto.User.UserLoginDTO;
import org.example.backend.dto.User.UserRegisterDTO;
import org.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/app/auth") // Base url, following the http://localhost:8080/
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Method specific url, for a specific method
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO loginRequestDTO) {
        Optional<AuthResponseDTO> optionalAuthResponseDTO = this.authService.login(loginRequestDTO);
        return optionalAuthResponseDTO.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(optionalAuthResponseDTO.get()) :
                ResponseEntity.badRequest().body(Map.of("message", "Credenciais inválidas!"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        Optional<AuthResponseDTO> optionalAuthResponseDTO = this.authService.register(userRegisterDTO);
        return optionalAuthResponseDTO.map(authResponseDTO -> ResponseEntity.status(HttpStatus.CREATED).body(authResponseDTO)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
