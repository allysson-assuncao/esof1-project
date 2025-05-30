package org.example.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.AuthResponseDTO;
import org.example.backend.dto.UserLoginDTO;
import org.example.backend.infra.security.TokenService;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public Optional<AuthResponseDTO> login(UserLoginDTO userLoginDTO) {
        User user = this.userRepository.findByEmail(userLoginDTO.email()).orElseThrow(() -> new EntityNotFoundException("Usuário com email " + userLoginDTO.email() + " não encontrado"));
        if (this.passwordEncoder.matches(userLoginDTO.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return Optional.of(new AuthResponseDTO(user.getUsername(), token, user.getRole()));
        }
        return Optional.empty();
    }

    public boolean test(String message) {
        System.out.println(message);
        return message.contains("teste");
    }

}
