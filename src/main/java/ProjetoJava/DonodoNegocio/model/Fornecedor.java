package ProjetoJava.DonodoNegocio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Fornecedor")
@Getter
@Setter
public class Fornecedor extends BaseEmpresaEntity {

    @Column(nullable = false)
    private String nome;

    @Column(length = 20)
    private String documento;
}