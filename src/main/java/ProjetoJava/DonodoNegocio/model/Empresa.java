package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Empresa")
@Getter
@Setter
public class Empresa extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Column(name = "login_master", nullable = false, unique = true, length = 50)
    private String loginMaster;

    @Column(name = "senha_hash_admin", nullable = false, columnDefinition = "TEXT")
    private String senhaHashAdmin;

    @Column(name = "senha_hash_publica", nullable = false, columnDefinition = "TEXT")
    private String senhaHashPublica;
}