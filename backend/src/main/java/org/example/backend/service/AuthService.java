package org.example.backend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.AuthResponseDTO;
import org.example.backend.dto.User.UserLoginDTO;
import org.example.backend.dto.User.UserRegisterDTO;
import org.example.backend.infra.security.TokenService;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public Optional<AuthResponseDTO> login(UserLoginDTO userLoginDTO) {
        User user = this.userRepository.findByEmail(userLoginDTO.email()).orElseThrow(() -> new EntityNotFoundException("Usuário com email " + userLoginDTO.email() + " não encontrado"));
        if (this.passwordEncoder.matches(userLoginDTO.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return Optional.of(AuthResponseDTO.builder()
                    .username(userLoginDTO.email())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .token(token).build());
        }
        return Optional.empty();
    }

    public Optional<AuthResponseDTO> register(UserRegisterDTO userRegisterDTO) {
        Optional<User> user = this.userRepository.findByEmail(userRegisterDTO.email());

        if (user.isPresent()) {
            throw new EntityExistsException("Usuário com email '" + userRegisterDTO.email() + "' já esta cadastrado");
        }

        User newUser = User.builder()
                .email(userRegisterDTO.email())
                .username(userRegisterDTO.username())
                .password(passwordEncoder.encode(userRegisterDTO.password()))
                .name(userRegisterDTO.name())
                .role(userRegisterDTO.role())
                .build();

        this.userRepository.save(newUser);

        String token = this.tokenService.generateToken(newUser);
        return Optional.of(AuthResponseDTO.builder()
                    .username(userRegisterDTO.email())
                    .email(newUser.getEmail())
                    .role(newUser.getRole())
                    .token(token).build());
    }

}
