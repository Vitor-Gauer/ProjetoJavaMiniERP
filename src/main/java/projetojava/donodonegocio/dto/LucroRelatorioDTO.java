package projetojava.donodonegocio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LucroRelatorioDTO {
    private String produtoNome;
    private Long quantidadeVendas;
    private Long quantidadeCompras;
    private BigDecimal lucroAtual;
    private BigDecimal lucroPassado;
    private BigDecimal crescimentoAnual;
    private List<MesLucroDTO> lucroMensal;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MesLucroDTO {
        private String mes;
        private BigDecimal lucro;
        private BigDecimal crescimento;
    }
}
