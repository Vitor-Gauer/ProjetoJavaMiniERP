package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Data
public class ProdutoDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;
    private String marca;
    private String submarca;

    @NotNull(message = "O estoque é obrigatório")
    private Long estoqueId;

    @NotNull(message = "O valor unitário é obrigatório")
    @PositiveOrZero
    private BigDecimal valorUni;

    @NotNull(message = "A quantidade presente no estoque é obrigatória")
    @PositiveOrZero
    private BigDecimal quantidade;

    @NotNull(message = "O fornecedor é obrigatório")
    private Long fornecedorId;
    private String sku;
}