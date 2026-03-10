package projetojava.donodonegocio.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditoriaDTO {
    private Long empresaId;
    private Integer idLocalEmpresa;
    private Long loginId;
    private boolean ehAdmin;
    private String tipoOperacao;
    private String tabelaAfetada;
    private LocalDateTime dataHora;
}