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
        Transacao entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Transacao> existing = transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                transacaoMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = transacaoMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = transacaoMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = transacaoRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        entity = transacaoRepository.save(entity);
        return transacaoMapper.toDTO(entity);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public void resolver(Long empresaId, Integer idLocalEmpresa) {
        Transacao transacao = transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(empresaId, idLocalEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada"));

        if (transacao.isFoiResolvido()) {
            return;
        }

        transacao.setFoiResolvido(true);
        transacao.setDataResolucao(LocalDateTime.now());
        transacaoRepository.save(transacao);

        if (transacao.getTipoTransacao().isEhRecorrente() && transacao.getIntervaloCobranca() != null && transacao.getIntervaloCobranca() > 0) {
            criarProximaRecorrencia(transacao);
        }
    }

    private void criarProximaRecorrencia(Transacao original) {
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

        Integer maxId = transacaoRepository.findMaxIdLocalByEmpresaId(original.getEmpresa().getId());
        int nextId = (maxId == null) ? 1 : maxId + 1;
        nova.setIdLocalEmpresa(nextId);

        transacaoRepository.save(nova);
    }
}