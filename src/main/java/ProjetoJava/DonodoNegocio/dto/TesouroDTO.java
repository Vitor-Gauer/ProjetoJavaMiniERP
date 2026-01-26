package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TesouroDTO {
    private Long id;
    private Long empresaId;
    private String nomeConta;
    private BigDecimal saldoAtual;
}