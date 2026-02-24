package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.EstoqueDTO;
import ProjetoJava.DonodoNegocio.mapper.EstoqueMapper;
import ProjetoJava.DonodoNegocio.model.Estoque;
import ProjetoJava.DonodoNegocio.repository.EstoqueRepository;
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
        Estoque entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Estoque> existing = estoqueRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                estoqueMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = estoqueMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = estoqueMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = estoqueRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        entity = estoqueRepository.save(entity);
        return estoqueMapper.toDTO(entity);
    }
}