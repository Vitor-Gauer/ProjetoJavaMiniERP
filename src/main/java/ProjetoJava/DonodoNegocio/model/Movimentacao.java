package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "Movimentacao")
@Getter
@Setter
public class Movimentacao extends BaseEmpresaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transacao_pai_id", nullable = false)
    private Transacao transacaoPai;

    @Column(name = "tabela_movimentada", nullable = false, length = 20)
    private String tabelaMovimentada;

    @Column(name = "movimentado_id", nullable = false)
    private Integer movimentadoId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal quantidade;

    @Column(name = "eh_entrada", nullable = false)
    private boolean ehEntrada;

    @Column(name = "foi_resolvida", nullable = false)
    private boolean foiResolvida;
}