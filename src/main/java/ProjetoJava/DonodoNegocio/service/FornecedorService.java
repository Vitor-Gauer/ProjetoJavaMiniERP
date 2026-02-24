package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.FornecedorDTO;
import ProjetoJava.DonodoNegocio.mapper.FornecedorMapper;
import ProjetoJava.DonodoNegocio.model.Fornecedor;
import ProjetoJava.DonodoNegocio.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public FornecedorDTO salvar(FornecedorDTO dto) {
        Fornecedor entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Fornecedor> existing = fornecedorRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                fornecedorMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = fornecedorMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = fornecedorMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = fornecedorRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        entity = fornecedorRepository.save(entity);
        return fornecedorMapper.toDTO(entity);
    }
}