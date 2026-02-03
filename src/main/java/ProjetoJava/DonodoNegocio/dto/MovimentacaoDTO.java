package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MovimentacaoDTO {
    private Long id;
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotNull(message = "A transação pai é obrigatória")
    private Long transacaoPaiId;

    @NotBlank(message = "A tabela movimentada é obrigatória")
    private String tabelaMovimentada;

    @NotNull(message = "O ID do item movimentado é obrigatório")
    private Integer movimentadoId;

    @NotNull(message = "A quantidade é obrigatória")
    private BigDecimal quantidade;

    private boolean ehEntrada;
    private boolean foiResolvida;
}