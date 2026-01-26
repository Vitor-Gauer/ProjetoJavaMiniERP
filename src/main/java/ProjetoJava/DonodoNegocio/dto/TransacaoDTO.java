package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransacaoDTO {
    private Long id;
    private Long empresaId;
    private Long usuarioId;
    private Long tipoId;
    private String tabelaResponsavel;
    private Integer responsavelId;
    private boolean foiResolvido;
    private boolean ehValida;
    private LocalDateTime dataCriacao;
    private Short intervaloCobranca;
    private LocalDateTime dataResolucao;
}