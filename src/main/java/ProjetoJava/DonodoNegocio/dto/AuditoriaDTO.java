package ProjetoJava.DonodoNegocio.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditoriaDTO {
    private Long id;
    private Long empresaId;
    private Long idLocalEmpresa;
    private Long usuarioId;
    private String tipoOperacao;
    private String tabelaAfetada;
    private LocalDateTime dataHora;
}