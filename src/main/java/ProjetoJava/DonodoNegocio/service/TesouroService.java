package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.TesouroDTO;
import ProjetoJava.DonodoNegocio.mapper.TesouroMapper;
import ProjetoJava.DonodoNegocio.model.Tesouro;
import ProjetoJava.DonodoNegocio.repository.TesouroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TesouroService {

    private final TesouroRepository tesouroRepository;
    private final TesouroMapper tesouroMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TesouroDTO salvar(TesouroDTO dto) {
        Tesouro entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Tesouro> existing = tesouroRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                tesouroMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = tesouroMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = tesouroMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = tesouroRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        entity = tesouroRepository.save(entity);
        return tesouroMapper.toDTO(entity);
    }
}