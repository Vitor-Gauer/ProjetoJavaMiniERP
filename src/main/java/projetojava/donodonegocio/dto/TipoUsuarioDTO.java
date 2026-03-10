package projetojava.donodonegocio.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TipoUsuarioDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome do cargo é obrigatório")
    private String cargo;
}