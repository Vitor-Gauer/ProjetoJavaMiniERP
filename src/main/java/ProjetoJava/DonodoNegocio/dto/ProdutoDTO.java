package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoDTO {
    private Long id;
    private Long empresaId;
    private String nome;
    private String marca;
    private String submarca;
    private Long estoqueId;
    private BigDecimal valorUni;
    private BigDecimal quantidade;
    private Long fornecedorId;
    private String sku;
}