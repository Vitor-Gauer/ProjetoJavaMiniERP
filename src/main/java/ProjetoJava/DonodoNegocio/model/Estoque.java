package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Estoque")
@Getter
@Setter
public class Estoque extends BaseEmpresaEntity {

    @Column(nullable = false, length = 80)
    private String nome;
}