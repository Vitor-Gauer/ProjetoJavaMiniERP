package projetojava.donodonegocio.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Data
public class TipoTransacaoDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome do tipo de transação é obrigatório")
    private String nome;

    @NotNull(message = "O percentual de juros é obrigatório ter um valor")
    @PositiveOrZero(message = "O juros deve ser zero ou maior")
    private BigDecimal prctJuros;

    private boolean ehRecebimento;
    private boolean ehRecorrente;
}