package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.EstoqueDTO;
import projetojava.donodonegocio.mapper.EstoqueMapper;
import projetojava.donodonegocio.model.Estoque;
import projetojava.donodonegocio.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EstoqueDTO salvar(EstoqueDTO dto) {
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovoEstoque(dto);
        }

        Optional<Estoque> estoqueOpt = estoqueRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return estoqueOpt
                .map(estoque -> atualizarEstoqueExistente(dto, estoque))
                .orElseGet(() -> criarNovoEstoque(dto));
    }

    private EstoqueDTO criarNovoEstoque(EstoqueDTO dto) {
        Estoque novoEstoque = estoqueMapper.toEntity(dto);
        
        Integer maxId = estoqueRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
        int proximoId = (maxId == null) ? 1 : maxId + 1;
        novoEstoque.setIdLocalEmpresa(proximoId);
        
        Estoque estoqueSalvo = estoqueRepository.save(novoEstoque);
        return estoqueMapper.toDTO(estoqueSalvo);
    }

    private EstoqueDTO atualizarEstoqueExistente(EstoqueDTO dto, Estoque estoque) {
        estoqueMapper.updateEntityFromDTO(dto, estoque);
        Estoque estoqueAtualizado = estoqueRepository.save(estoque);
        return estoqueMapper.toDTO(estoqueAtualizado);
    }
}