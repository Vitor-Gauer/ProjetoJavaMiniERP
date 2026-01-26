package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;

@Data
public class EmpresaDTO {
    private Long id;
    private String nome;
    private String loginMaster;
    private String senhaHashAdmin;
    private String senhaHashPublica;
}