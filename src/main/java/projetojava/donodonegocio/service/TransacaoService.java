package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.TransacaoDTO;
import projetojava.donodonegocio.mapper.TransacaoMapper;
import projetojava.donodonegocio.model.Transacao;
import projetojava.donodonegocio.repository.TipoTransacaoRepository;
import projetojava.donodonegocio.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;
    private final TipoTransacaoRepository tipoTransacaoRepository;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public List<TransacaoDTO> criarGrupo(String tipoNome,
                                        Long empresaId,
                                        Integer usuarioResponsavelId,
                                        Integer clienteId,
                                        Integer produtoId,
                                        Integer tesouroId,
                                        Short intervaloCobranca) {
        if (empresaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa inválida");
        }
        if (usuarioResponsavelId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário inválido");
        }

        var tipoOpt = tipoTransacaoRepository.findByEmpresaIdAndNomeIgnoreCase(empresaId, tipoNome);
        var tipo = tipoOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de transação indisponível"));

        String grupoId = java.util.UUID.randomUUID().toString();
        List<Transacao> entities = new ArrayList<>();

        // Etapa Usuário (quem iniciou)
        entities.add(novaEtapa(empresaId, usuarioResponsavelId, tipo.getIdLocalEmpresa(), grupoId, "Usuario", usuarioResponsavelId, intervaloCobranca));

        // Etapa Cliente
        if (clienteId != null) {
            entities.add(novaEtapa(empresaId, usuarioResponsavelId, tipo.getIdLocalEmpresa(), grupoId, "Cliente", clienteId, intervaloCobranca));
        }

        // Etapa Produto
        if (produtoId != null) {
            entities.add(novaEtapa(empresaId, usuarioResponsavelId, tipo.getIdLocalEmpresa(), grupoId, "Produto", produtoId, intervaloCobranca));
        }

        // Etapa Tesouro
        if (tesouroId != null) {
            entities.add(novaEtapa(empresaId, usuarioResponsavelId, tipo.getIdLocalEmpresa(), grupoId, "Tesouro", tesouroId, intervaloCobranca));
        }

        transacaoRepository.saveAll(entities);
        return entities.stream().map(transacaoMapper::toDTO).toList();
    }

    private Transacao novaEtapa(Long empresaId,
                               Integer usuarioIdLocal,
                               Integer tipoIdLocal,
                               String grupoId,
                               String tabelaResponsavel,
                               Integer responsavelId,
                               Short intervaloCobranca) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setEmpresaId(empresaId);
        dto.setUsuarioId(usuarioIdLocal.longValue());
        dto.setTipoId(tipoIdLocal.longValue());
        dto.setGrupoId(grupoId);
        dto.setTabelaResponsavel(tabelaResponsavel);
        dto.setResponsavelId(responsavelId);
        dto.setIntervaloCobranca(intervaloCobranca != null ? intervaloCobranca : 1);
        dto.setFoiResolvido(false);
        dto.setEhValida(true);

        Transacao entity = transacaoMapper.toEntity(dto);
        entity.setIdLocalEmpresa(gerarProximoIdLocal(empresaId));
        return entity;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public void toggleValidadeGrupo(Long empresaId, String grupoId) {
        if (grupoId == null || grupoId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grupo inválido");
        }
        List<Transacao> grupo = transacaoRepository.findByEmpresaIdAndGrupoIdOrderByIdLocalEmpresaAsc(empresaId, grupoId);
        if (grupo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado");
        }
        boolean novoStatus = !grupo.get(0).isEhValida();
        transacaoRepository.updateEhValidaByGrupo(empresaId, grupoId, novoStatus);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public void resolver(Long empresaId, Integer idLocalEmpresa) {
        Transacao transacao = buscarTransacaoOuLancarExcecao(empresaId, idLocalEmpresa);

        if (!transacao.isEhValida()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido quitar uma transação inválida.");
        }

        if (transacao.isFoiResolvido()) {
            return;
        }

        marcarComoResolvida(transacao);
        verificarEGerarRecorrencia(transacao);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public void quitarGrupo(Long empresaId, String grupoId) {
        if (grupoId == null || grupoId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grupo inválido");
        }

        List<Transacao> grupo = transacaoRepository.findByEmpresaIdAndGrupoIdOrderByIdLocalEmpresaAsc(empresaId, grupoId);
        if (grupo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado");
        }

        boolean existeInvalida = grupo.stream().anyMatch(t -> !t.isEhValida());
        if (existeInvalida) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido quitar um grupo com etapas inválidas.");
        }

        for (Transacao t : grupo) {
            if (!t.isFoiResolvido()) {
                marcarComoResolvida(t);
                verificarEGerarRecorrencia(t);
            }
        }
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