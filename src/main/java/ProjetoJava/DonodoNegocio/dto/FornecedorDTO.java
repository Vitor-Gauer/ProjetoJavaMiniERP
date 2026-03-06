package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class FornecedorDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome do fornecedor é obrigatório")
    private String nome;

    private String documento;
}