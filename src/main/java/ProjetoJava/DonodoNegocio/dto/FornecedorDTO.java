package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;

@Data
public class FornecedorDTO {
    private Long id;
    private Long empresaId;
    private Long idLocalEmpresa;
    private String nome;
    private String documento;
}