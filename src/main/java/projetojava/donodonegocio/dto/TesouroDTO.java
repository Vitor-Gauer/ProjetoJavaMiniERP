package projetojava.donodonegocio.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Data
public class TesouroDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome da conta é obrigatório")
    private String nomeConta;

    @NotNull(message = "O saldo não pode ser nulo")
    @PositiveOrZero(message = "O saldo deve ser zero ou maior")
    private BigDecimal saldoAtual;
}