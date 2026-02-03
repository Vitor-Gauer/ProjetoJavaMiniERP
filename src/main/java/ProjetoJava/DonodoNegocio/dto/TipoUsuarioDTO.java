package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TipoUsuarioDTO {
    private Long id;
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "A descrição do cargo é obrigatória")
    private String cargo;
}