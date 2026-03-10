package projetojava.donodonegocio.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class EstoqueDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome do estoque é obrigatório")
    private String nome;
}