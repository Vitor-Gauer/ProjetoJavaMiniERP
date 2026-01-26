package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClienteDTO {
    private Long id;
    private Long empresaId;
    private String nome;
    private String endereco;
    private String telefone;
    private BigDecimal saldo;
}