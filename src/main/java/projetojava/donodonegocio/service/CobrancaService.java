package projetojava.donodonegocio.service;

import projetojava.donodonegocio.model.Transacao;
import projetojava.donodonegocio.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CobrancaService {
    
    private final TransacaoRepository transacaoRepository;
    
    private final ConcurrentMap<Long, LocalDateTime> ultimaNotificacaoPorUsuario = new ConcurrentHashMap<>();
    
    public boolean verificarCobrancasPendentes(Long empresaId, Long usuarioId) {
        try {
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime ultimaNotificacao = ultimaNotificacaoPorUsuario.get(usuarioId);
            
            if (ultimaNotificacao != null && ultimaNotificacao.plusHours(24).isAfter(agora)) {
                return false;
            }
            
            List<Transacao> cobrancasPendentes = transacaoRepository
                    .findByEmpresaIdAndEhValidaTrueAndFoiResolvidoFalse(empresaId)
                    .stream()
                    .filter(t -> t.getIntervaloCobranca() != null && t.getIntervaloCobranca() > 0)
                    .filter(t -> t.getDataCriacao() != null)
                    .filter(t -> t.getDataCriacao().plusDays(t.getIntervaloCobranca()).isBefore(agora)
                            || t.getDataCriacao().plusDays(t.getIntervaloCobranca()).isEqual(agora))
                    .toList();
            
            if (!cobrancasPendentes.isEmpty()) {
                log.info("Usuário {} da empresa {} tem {} cobranças pendentes", usuarioId, empresaId, cobrancasPendentes.size());
                ultimaNotificacaoPorUsuario.put(usuarioId, agora);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar cobranças para usuário {} da empresa {}: {}", usuarioId, empresaId, e.getMessage());
            return false;
        }
    }
    
    public void limparNotificacaoUsuario(Long usuarioId) {
        ultimaNotificacaoPorUsuario.remove(usuarioId);
    }
    
    public boolean temNotificacaoPendente(Long usuarioId) {
        LocalDateTime ultimaNotificacao = ultimaNotificacaoPorUsuario.get(usuarioId);
        if (ultimaNotificacao == null) return false;
        
        return ultimaNotificacao.plusHours(24).isAfter(LocalDateTime.now());
    }
}
