package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "Cliente")
@Getter
@Setter
public class Cliente extends BaseEmpresaEntity {

    @Column(nullable = false)
    private String nome;

    @Column
    private String endereco;

    @Column(length = 12)
    private String telefone;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @PrePersist
    protected void onCreate() {
        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }
    }
}