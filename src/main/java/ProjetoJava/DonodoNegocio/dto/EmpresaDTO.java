package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class EmpresaDTO {

    @NotBlank(message = "O nome da empresa é obrigatório")
    private String nome;

    @NotBlank(message = "O login master é obrigatório")
    private String loginMaster;

    @NotBlank(message = "O login público é obrigatório")
    private String loginPublico;

    @NotBlank(message = "A senha de admin é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senhaAdmin;

    @NotBlank(message = "A senha pública é obrigatória")
    private String senhaPublica;
}