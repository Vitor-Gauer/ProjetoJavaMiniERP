package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovimentacaoDTO {
    private Long id;
    private Long empresaId;
    private Long transacaoPaiId;
    private String tabelaMovimentada;
    private Integer movimentadoId;
    private BigDecimal quantidade;
    private boolean ehEntrada;
    private boolean foiResolvida;
}