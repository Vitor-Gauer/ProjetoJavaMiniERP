package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ClienteDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String nome;
    private String endereco;

    @Size(max = 12, message = "O telefone deve ter no máximo 12 caracteres")
    private String telefone;

    @NotNull(message = "O saldo tem que ter um valor")
    private BigDecimal saldo;
}