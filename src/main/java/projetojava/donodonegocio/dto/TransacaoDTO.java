package projetojava.donodonegocio.dto;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TransacaoDTO {
    private Long empresaId;
    private Long idLocalEmpresa;

    private String grupoId;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "O tipo da transação é obrigatório")
    private Long tipoId;

    @NotBlank(message = "A tabela responsável é obrigatória")
    private String tabelaResponsavel;

    @NotNull(message = "O ID do responsável é obrigatório")
    private Integer responsavelId;

    private boolean foiResolvido;
    private boolean ehValida;
    private LocalDateTime dataCriacao;

    @NotNull(message = "O intervalo de cobrança é obrigatório")
    private Short intervaloCobranca;
    private LocalDateTime dataResolucao;
}