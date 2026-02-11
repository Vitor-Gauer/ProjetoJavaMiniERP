package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.ProdutoDTO;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Produto;
import ProjetoJava.DonodoNegocio.repository.EstoqueRepository;
import ProjetoJava.DonodoNegocio.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Produto toEntity(ProdutoDTO dto) {
        if (dto == null) {
            return null;
        }
        Produto entity = new Produto();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setNome(dto.getNome());
        entity.setMarca(dto.getMarca());
        entity.setSubmarca(dto.getSubmarca());
        entity.setValorUni(dto.getValorUni());
        entity.setQuantidade(dto.getQuantidade());
        entity.setSku(dto.getSku());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);

            // Resolve relacionamentos baseados em ID Local e Empresa ID
            if (dto.getEstoqueId() != null) {
                estoqueRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getEstoqueId().intValue())
                        .ifPresent(entity::setEstoque);
            }
            if (dto.getFornecedorId() != null) {
                fornecedorRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getFornecedorId().intValue())
                        .ifPresent(entity::setFornecedor);
            }
        }

        return entity;
    }

    public ProdutoDTO toDTO(Produto entity) {
        if (entity == null) {
            return null;
        }
        ProdutoDTO dto = new ProdutoDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setNome(entity.getNome());
        dto.setMarca(entity.getMarca());
        dto.setSubmarca(entity.getSubmarca());
        dto.setValorUni(entity.getValorUni());
        dto.setQuantidade(entity.getQuantidade());
        dto.setSku(entity.getSku());

        if (entity.getEstoque() != null && entity.getEstoque().getIdLocalEmpresa() != null) {
            dto.setEstoqueId(entity.getEstoque().getIdLocalEmpresa().longValue());
        }
        if (entity.getFornecedor() != null && entity.getFornecedor().getIdLocalEmpresa() != null) {
            dto.setFornecedorId(entity.getFornecedor().getIdLocalEmpresa().longValue());
        }

        return dto;
    }

    public void updateEntityFromDTO(ProdutoDTO dto, Produto entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setNome(dto.getNome());
        entity.setMarca(dto.getMarca());
        entity.setSubmarca(dto.getSubmarca());
        entity.setValorUni(dto.getValorUni());
        entity.setQuantidade(dto.getQuantidade());
        entity.setSku(dto.getSku());

        // Atualiza relacionamentos se fornecidos
        if (dto.getEmpresaId() != null) {
            // Se a empresa mudar (raro), atualiza
            if (entity.getEmpresa() == null || !entity.getEmpresa().getId().equals(dto.getEmpresaId())) {
                Empresa empresa = new Empresa();
                empresa.setId(dto.getEmpresaId());
                entity.setEmpresa(empresa);
            }
            
            if (dto.getEstoqueId() != null) {
                estoqueRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getEstoqueId().intValue())
                        .ifPresent(entity::setEstoque);
            }
            if (dto.getFornecedorId() != null) {
                fornecedorRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getFornecedorId().intValue())
                        .ifPresent(entity::setFornecedor);
            }
        }
    }
}