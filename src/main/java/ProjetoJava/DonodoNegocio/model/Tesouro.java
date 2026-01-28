package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "Tesouro")
@Getter
@Setter
public class Tesouro extends BaseEmpresaEntity {

    @Column(name = "nome_conta", nullable = false, length = 100)
    private String nomeConta;

    @Column(name = "saldo_atual", precision = 15, scale = 2)
    private BigDecimal saldoAtual;

    @PrePersist
    protected void onCreate() {
        if (saldoAtual == null) {
            saldoAtual = BigDecimal.ZERO;
        }
    }
}