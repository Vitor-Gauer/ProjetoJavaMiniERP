package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.TransacaoDTO;
import ProjetoJava.DonodoNegocio.mapper.TransacaoMapper;
import ProjetoJava.DonodoNegocio.repository.MovimentacaoRepository;
import ProjetoJava.DonodoNegocio.repository.TransacaoRepository;
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

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getDespesas(Long empresaId) {
        return getTransacoesPorTipo(empresaId, "Despesa");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getReceitas(Long empresaId) {
        return getTransacoesPorTipo(empresaId, "Receita");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getDevendo(Long empresaId) {
        return getTransacoesPorTipo(empresaId, "Devendo");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getQuitado(Long empresaId) {
        return getTransacoesPorTipo(empresaId, "Quitado");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')") // Apenas Admin vê Lucro
    public BigDecimal getLucro(Long empresaId) {
        BigDecimal saldo = movimentacaoRepository.calcularSaldoTesouro(empresaId);
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getEntradasEstoque(Long empresaId) {
        return getTransacoesPorTipo(empresaId, "Entrada Estoque");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getSaidasEstoque(Long empresaId) {
        return getTransacoesPorTipo(empresaId, "Saida Estoque");
    }

    private List<TransacaoDTO> getTransacoesPorTipo(Long empresaId, String tipo) {
        return transacaoRepository.findByEmpresaIdAndTipoTransacaoNome(empresaId, tipo).stream()
                .map(transacaoMapper::toDTO)
                .collect(Collectors.toList());
    }
}