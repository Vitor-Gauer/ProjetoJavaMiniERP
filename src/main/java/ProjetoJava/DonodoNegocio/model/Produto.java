package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "Produto")
@Getter
@Setter
@AssociationOverride(name = "empresa", joinColumns = @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_produto_empresa")))
public class Produto extends BaseEmpresaEntity {

    @Column(nullable = false)
    private String nome;

    @Column
    private String marca;

    @Column
    private String submarca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false, foreignKey = @ForeignKey(name = "fk_produto_estoque"))
    private Estoque estoque;

    @Column(name = "valor_uni", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorUni;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_produto_fornecedor"))
    private Fornecedor fornecedor;

    @Column(length = 50)
    private String sku;
}