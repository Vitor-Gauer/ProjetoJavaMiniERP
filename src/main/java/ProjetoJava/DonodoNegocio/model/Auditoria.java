package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Auditoria")
@Getter
@Setter
@AssociationOverride(name = "empresa", joinColumns = @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_auditoria_empresa")))
public class Auditoria extends BaseEmpresaEntity {

    @Column(name = "login_id", nullable = false)
    private Long loginId;

    @Column(name = "eh_admin", nullable = false)
    private boolean ehAdmin;

    @Column(name = "tipo_operacao", nullable = false, length = 30) // Aumentado para caber "POSSIVEL_TENTATIVA_INVASAO"
    private String tipoOperacao;

    @Column(name = "tabela_afetada", nullable = false, length = 50)
    private String tabelaAfetada;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @PrePersist
    protected void onCreate() {
        dataHora = LocalDateTime.now();
    }
}