package org.example.backend.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserLoginDTO(
        @Email(message = "Formato de email inválido")
        @NotBlank(message = "O email é obrigatório")
        String email,
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-_#@$%^&+=]).{8,}$",
                message = "A senha deve ter ao menos uma letra maiúscula, uma minúscula, um número, e um caractere especial (-, _, #, @, $, etc.)"
        )
        String password
) { }
