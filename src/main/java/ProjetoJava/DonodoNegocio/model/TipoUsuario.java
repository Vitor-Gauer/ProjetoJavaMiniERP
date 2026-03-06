package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Tipo_Usuario")
@Getter
@Setter
@AssociationOverride(name = "empresa", joinColumns = @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tipo_usuario_empresa")))
public class TipoUsuario extends BaseEmpresaEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String cargo;
}