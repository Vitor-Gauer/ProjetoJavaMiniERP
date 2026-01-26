package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private Long empresaId;
    private Long tipoUsuarioId;
    private String login;
    private String senhaHash;
    private boolean ativo;
}