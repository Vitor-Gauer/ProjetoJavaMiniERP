package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Estoque")
@Getter
@Setter
@AssociationOverride(name = "empresa", joinColumns = @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_estoque_empresa")))
public class Estoque extends BaseEmpresaEntity {

    @Column(nullable = false, length = 80)
    private String nome;
}