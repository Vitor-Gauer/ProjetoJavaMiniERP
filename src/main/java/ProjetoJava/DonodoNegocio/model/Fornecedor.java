package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Fornecedor")
@Getter
@Setter
@AssociationOverride(name = "empresa", joinColumns = @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fornecedor_empresa")))
public class Fornecedor extends BaseEmpresaEntity {

    @Column(nullable = false)
    private String nome;

    @Column(length = 20)
    private String documento;
}