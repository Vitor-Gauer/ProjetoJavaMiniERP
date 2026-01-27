package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;

@Data
public class TipoUsuarioDTO {
    private Long id;
    private Long empresaId;
    private Long idLocalEmpresa;
    private String cargo;
}