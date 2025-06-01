package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.AuthResponseDTO;
import org.example.backend.dto.UserLoginDTO;
import org.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
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

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody String message) {
        return this.authService.test(message) ?
                ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Mensagem de teste recebida com sucesso")) :
                ResponseEntity.badRequest().body(Map.of("message","Mensagem nao continha teste"));
    }

}
