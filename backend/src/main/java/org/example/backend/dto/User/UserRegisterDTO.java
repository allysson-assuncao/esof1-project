package org.example.backend.dto.User;

import jakarta.validation.constraints.*;
import org.example.backend.model.enums.UserRole;

public record UserRegisterDTO(
        @Size(min = 4, message = "O nome de usuário deve ter ao menos 4 caracteres") @NotBlank(message = "O nome de usuário é obrigatório")
        String username,
        @Email(message = "Formato de email inválido")
        @NotBlank(message = "O email é obrigatório")
        String email,
        @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-_#@$%^&+=]).{8,}$",
                message = "A senha deve ter ao menos uma letra maiúscula, uma minúscula, um número, e um caractere especial (-, _, #, @, $, etc.)"
        )
        String password,
        @NotBlank(message = "O nome é obrigatório")
        String name,
        @NotNull(message = "O cargo do usuário é obrigatorio") UserRole role
) {
}