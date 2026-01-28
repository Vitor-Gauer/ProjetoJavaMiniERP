package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TipoTransacaoDTO {
    private Long id;
    private Long empresaId;
    private Long idLocalEmpresa;
    private String nome;
    private BigDecimal prctJuros;
    private boolean ehRecebimento;
    private boolean ehRecorrente;
}