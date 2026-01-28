package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
public class Usuario extends BaseEmpresaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_usuario_id", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(nullable = false, length = 50)
    private String login;

    @Column(name = "senha_hash", nullable = false, columnDefinition = "TEXT")
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativo;

    @PrePersist
    protected void onCreate() {
        ativo = true;
    }
}