package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.TransacaoDTO;
import ProjetoJava.DonodoNegocio.mapper.TransacaoMapper;
import ProjetoJava.DonodoNegocio.repository.MovimentacaoRepository;
import ProjetoJava.DonodoNegocio.repository.TransacaoRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final TransacaoRepository transacaoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Getter
    @RequiredArgsConstructor
    public enum TipoRelatorio {
        DESPESA("Despesa"),
        RECEITA("Receita"),
        DEVENDO("Devendo"),
        QUITADO("Quitado"),
        ENTRADA_ESTOQUE("Entrada Estoque"),
        SAIDA_ESTOQUE("Saida Estoque"),
        VENDA("Venda"),
        COMPRA("Compra"),
        TRANSFERENCIA("Transferencia"),
        AJUSTE("Ajuste");

        private final String nome;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getRelatorioTransacoes(Long empresaId, TipoRelatorio tipo) {
        return transacaoRepository.findByEmpresaIdAndTipoTransacaoNome(empresaId, tipo.getNome()).stream()
                .map(transacaoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')") // Apenas Admin vê Lucro
    public BigDecimal getLucro(Long empresaId) {
        BigDecimal saldo = movimentacaoRepository.calcularSaldoTesouro(empresaId);
        return saldo != null ? saldo : BigDecimal.ZERO;
    }
}