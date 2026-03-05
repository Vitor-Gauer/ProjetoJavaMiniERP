package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.TransacaoDTO;
import ProjetoJava.DonodoNegocio.mapper.TransacaoMapper;
import ProjetoJava.DonodoNegocio.model.Transacao;
import ProjetoJava.DonodoNegocio.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public TransacaoDTO salvar(TransacaoDTO dto) {
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovaTransacao(dto);
        }

        Optional<Transacao> transacaoOpt = transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return transacaoOpt
                .map(transacao -> atualizarTransacaoExistente(dto, transacao))
                .orElseGet(() -> criarNovaTransacao(dto));
    }

    private TransacaoDTO criarNovaTransacao(TransacaoDTO dto) {
        Transacao novaTransacao = transacaoMapper.toEntity(dto);
        novaTransacao.setIdLocalEmpresa(gerarProximoIdLocal(dto.getEmpresaId()));
        
        Transacao transacaoSalva = transacaoRepository.save(novaTransacao);
        return transacaoMapper.toDTO(transacaoSalva);
    }

    private TransacaoDTO atualizarTransacaoExistente(TransacaoDTO dto, Transacao transacao) {
        transacaoMapper.updateEntityFromDTO(dto, transacao);
        Transacao transacaoAtualizada = transacaoRepository.save(transacao);
        return transacaoMapper.toDTO(transacaoAtualizada);
    }

    private Integer gerarProximoIdLocal(Long empresaId) {
        Integer maxId = transacaoRepository.findMaxIdLocalByEmpresaId(empresaId);
        return (maxId == null) ? 1 : maxId + 1;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public void resolver(Long empresaId, Integer idLocalEmpresa) {
        Transacao transacao = buscarTransacaoOuLancarExcecao(empresaId, idLocalEmpresa);

        if (transacao.isFoiResolvido()) {
            return;
        }

        marcarComoResolvida(transacao);
        verificarEGerarRecorrencia(transacao);
    }

    private Transacao buscarTransacaoOuLancarExcecao(Long empresaId, Integer idLocalEmpresa) {
        return transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(empresaId, idLocalEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    String.format("Transação não encontrada para empresa %d e ID local %d", empresaId, idLocalEmpresa)));
    }

    private void marcarComoResolvida(Transacao transacao) {
        transacao.setFoiResolvido(true);
        transacao.setDataResolucao(LocalDateTime.now());
        transacaoRepository.save(transacao);
    }

    private void verificarEGerarRecorrencia(Transacao transacao) {
        if (transacao.getTipoTransacao().isEhRecorrente() && 
            transacao.getIntervaloCobranca() != null && 
            transacao.getIntervaloCobranca() > 0) {
            gerarProximaTransacaoRecorrente(transacao);
        }
    }

    private void gerarProximaTransacaoRecorrente(Transacao original) {
        Transacao nova = new Transacao();
        nova.setEmpresa(original.getEmpresa());
        nova.setUsuario(original.getUsuario());
        nova.setTipoTransacao(original.getTipoTransacao());
        nova.setTabelaResponsavel(original.getTabelaResponsavel());
        nova.setResponsavelId(original.getResponsavelId());
        nova.setEhValida(original.isEhValida());
        nova.setIntervaloCobranca(original.getIntervaloCobranca());
        
        nova.setDataCriacao(original.getDataCriacao().plusDays(original.getIntervaloCobranca()));
        nova.setFoiResolvido(false);
        nova.setDataResolucao(null);
        nova.setIdLocalEmpresa(gerarProximoIdLocal(original.getEmpresa().getId()));

        transacaoRepository.save(nova);
    }
}