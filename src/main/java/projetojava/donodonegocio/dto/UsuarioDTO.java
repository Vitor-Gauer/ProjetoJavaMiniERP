package projetojava.donodonegocio.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UsuarioDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotNull(message = "O tipo de usuário é obrigatório")
    private Long tipoUsuarioId;

    @NotBlank(message = "O login é obrigatório")
    private String login;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    private boolean ativo;
}