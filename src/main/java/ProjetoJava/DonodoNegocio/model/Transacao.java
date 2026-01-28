package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transacao")
@Getter
@Setter
public class Transacao extends BaseEmpresaUsuarioEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoTransacao tipoTransacao;

    @Column(name = "tabela_responsavel", nullable = false, length = 20)
    private String tabelaResponsavel;

    @Column(name = "responsavel_id", nullable = false)
    private Integer responsavelId;

    @Column(name = "foi_resolvido", nullable = false)
    private boolean foiResolvido;

    @Column(name = "eh_valida", nullable = false)
    private boolean ehValida;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "intervalo_cobranca", nullable = false)
    private Short intervaloCobranca;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        foiResolvido = false;
        ehValida = true;
    }
}