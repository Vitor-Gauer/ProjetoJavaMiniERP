package ProjetoJava.DonodoNegocio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "Login obrigatório")
    private String login;

    @NotBlank(message = "Senha obrigatória")
    @Size(min = 10, message = "Senha deve ter no mínimo 10 caracteres")
    private String senha;
}