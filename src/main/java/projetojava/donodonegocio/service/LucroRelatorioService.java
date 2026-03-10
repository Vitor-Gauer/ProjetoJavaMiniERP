package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.LucroRelatorioDTO;
import projetojava.donodonegocio.model.Transacao;
import projetojava.donodonegocio.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LucroRelatorioService {
    
    private final TransacaoRepository transacaoRepository;
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<LucroRelatorioDTO> gerarRelatorioLucro(Long empresaId, 
                                                         Integer anoInicial, 
                                                         Integer anoFinal, 
                                                         List<Integer> meses) {
        
        if (anoInicial > anoFinal) {
            throw new IllegalArgumentException("Ano inicial não pode ser maior que ano final");
        }
        
        List<Transacao> transacoes = transacaoRepository.findByEmpresaIdAndEhValidaTrueAndFoiResolvidoTrue(empresaId);
        
        Map<String, List<Transacao>> transacoesPorProduto = transacoes.stream()
                .filter(t -> t.getDataResolucao() != null)
                .collect(Collectors.groupingBy(this::getProdutoNome));
        
        List<LucroRelatorioDTO> relatorio = new ArrayList<>();
        
        for (Map.Entry<String, List<Transacao>> entry : transacoesPorProduto.entrySet()) {
            String produtoNome = entry.getKey();
            List<Transacao> transacoesProduto = entry.getValue();
            
            LucroRelatorioDTO dto = calcularLucroProduto(produtoNome, transacoesProduto, anoFinal, meses);
            relatorio.add(dto);
        }
        
        return relatorio.stream()
                .sorted(Comparator.comparing(LucroRelatorioDTO::getProdutoNome))
                .collect(Collectors.toList());
    }
    
    private LucroRelatorioDTO calcularLucroProduto(String produtoNome, List<Transacao> transacoes, Integer anoFinal, List<Integer> meses) {
        
        List<Transacao> periodoAtual = filtrarPorPeriodo(transacoes, anoFinal, meses);
        List<Transacao> periodoPassado = filtrarPorPeriodo(transacoes, anoFinal - 1, meses);
        
        long qtdVendasAtual = contarPorTipo(periodoAtual, "Venda");
        long qtdComprasAtual = contarPorTipo(periodoAtual, "Compra");
        
        BigDecimal lucroAtual = calcularLucroLiquido(periodoAtual);
        BigDecimal lucroPassado = calcularLucroLiquido(periodoPassado);
        
        BigDecimal crescimentoAnual = calcularCrescimento(lucroPassado, lucroAtual);
        
        List<LucroRelatorioDTO.MesLucroDTO> lucroMensal = calcularLucroMensal(
                transacoes, anoFinal, meses, anoFinal - 1
        );
        
        return new LucroRelatorioDTO(
                produtoNome,
                qtdVendasAtual,
                qtdComprasAtual,
                lucroAtual,
                lucroPassado,
                crescimentoAnual,
                lucroMensal
        );
    }
    
    private List<Transacao> filtrarPorPeriodo(List<Transacao> transacoes, int ano, List<Integer> meses) {
        return transacoes.stream()
                .filter(t -> {
                    LocalDate data = t.getDataResolucao().toLocalDate();
                    return data.getYear() == ano && meses.contains(data.getMonthValue() + 1);
                })
                .collect(Collectors.toList());
    }
    
    private long contarPorTipo(List<Transacao> transacoes, String tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipoTransacao() != null && tipo.equals(t.getTipoTransacao().getNome()))
                .count();
    }
    
    private BigDecimal calcularLucroLiquido(List<Transacao> transacoes) {
        BigDecimal vendas = transacoes.stream()
                .filter(t -> "Venda".equals(t.getTipoTransacao() != null ? t.getTipoTransacao().getNome() : ""))
                .map(t -> BigDecimal.valueOf(100.0)) // Simulação - calcular valor real
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal compras = transacoes.stream()
                .filter(t -> "Compra".equals(t.getTipoTransacao() != null ? t.getTipoTransacao().getNome() : ""))
                .map(t -> BigDecimal.valueOf(60.0)) // Simulação - calcular valor real
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return vendas.subtract(compras);
    }
    
    private BigDecimal calcularCrescimento(BigDecimal passado, BigDecimal atual) {
        if (passado.compareTo(BigDecimal.ZERO) == 0) {
            return atual.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        
        return atual.subtract(passado)
                .divide(passado.abs(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    private List<LucroRelatorioDTO.MesLucroDTO> calcularLucroMensal(List<Transacao> transacoes, 
                                                                   int anoAtual, 
                                                                   List<Integer> meses, 
                                                                   int anoPassado) {
        return meses.stream()
                .map(mes -> {
                    List<Transacao> mesAtual = filtrarPorMes(transacoes, anoAtual, mes);
                    List<Transacao> mesPassado = filtrarPorMes(transacoes, anoPassado, mes);
                    
                    BigDecimal lucroMesAtual = calcularLucroLiquido(mesAtual);
                    BigDecimal lucroMesPassado = calcularLucroLiquido(mesPassado);
                    BigDecimal crescimento = calcularCrescimento(lucroMesPassado, lucroMesAtual);
                    
                    String nomeMes = YearMonth.of(anoAtual, mes).getMonth().toString();
                    
                    return new LucroRelatorioDTO.MesLucroDTO(nomeMes, lucroMesAtual, crescimento);
                })
                .collect(Collectors.toList());
    }
    
    private List<Transacao> filtrarPorMes(List<Transacao> transacoes, int ano, int mes) {
        return transacoes.stream()
                .filter(t -> {
                    LocalDate data = t.getDataResolucao().toLocalDate();
                    return data.getYear() == ano && data.getMonthValue() == mes;
                })
                .collect(Collectors.toList());
    }
    
    private String getProdutoNome(Transacao transacao) {
        if ("Produto".equals(transacao.getTabelaResponsavel()) && transacao.getResponsavelId() != null) {
            return "Produto " + transacao.getResponsavelId();
        }
        return "Produto Geral";
    }
}
