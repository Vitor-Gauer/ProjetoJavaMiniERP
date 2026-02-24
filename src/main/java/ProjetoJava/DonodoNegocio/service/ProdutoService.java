package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.ProdutoDTO;
import ProjetoJava.DonodoNegocio.mapper.ProdutoMapper;
import ProjetoJava.DonodoNegocio.model.Produto;
import ProjetoJava.DonodoNegocio.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProdutoDTO salvar(ProdutoDTO dto) {
        Produto entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Produto> existing = produtoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                produtoMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = produtoMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = produtoMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = produtoRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        entity = produtoRepository.save(entity);
        return produtoMapper.toDTO(entity);
    }
}