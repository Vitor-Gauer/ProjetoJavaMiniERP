package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "Tipo_Transacao")
@Getter
@Setter
public class TipoTransacao extends BaseEmpresaEntity {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "prct_juros", nullable = false, precision = 5, scale = 2)
    private BigDecimal prctJuros;

    @Column(name = "eh_recebimento", nullable = false)
    private boolean ehRecebimento;

    @Column(name = "eh_recorrente", nullable = false)
    private boolean ehRecorrente;

    @PrePersist
    protected void onCreate() {
        if (prctJuros == null) {
            prctJuros = BigDecimal.ZERO;
        }
        ehRecorrente = false;
    }
}