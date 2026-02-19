package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.ClienteDTO;
import ProjetoJava.DonodoNegocio.mapper.ClienteMapper;
import ProjetoJava.DonodoNegocio.model.Cliente;
import ProjetoJava.DonodoNegocio.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ClienteDTO salvar(ClienteDTO dto) {
        Cliente entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Cliente> existing = clienteRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                clienteMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = clienteMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = clienteMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = clienteRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        entity = clienteRepository.save(entity);
        return clienteMapper.toDTO(entity);
    }
}