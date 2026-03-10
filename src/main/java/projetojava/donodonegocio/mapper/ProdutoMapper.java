package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.ProdutoDTO;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.model.Produto;
import projetojava.donodonegocio.repository.EstoqueRepository;
import projetojava.donodonegocio.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoMapper {

    private final EstoqueRepository estoqueRepository;
    private final FornecedorRepository fornecedorRepository;

    public Produto toEntity(ProdutoDTO dto) {
        if (dto.getIdLocalEmpresa() == null || dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("Empresa ID ou Local ID faltam na request.");
        }
        Produto entity = new Produto();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        entity.setNome(dto.getNome());
        entity.setMarca(dto.getMarca());
        entity.setSubmarca(dto.getSubmarca());
        entity.setValorUni(dto.getValorUni());
        entity.setQuantidade(dto.getQuantidade());
        entity.setSku(dto.getSku());

        Empresa empresa = new Empresa();
        empresa.setId(dto.getEmpresaId());
        entity.setEmpresa(empresa);

        // Devido a esses dois campos serem Longs -> intValue e, principalmente, estarmos no mapper, é interessante lidar com NPE
        // A unica forma de atingir esse ponto deverá ser após passar por um rest controller (ainda não implementado) ou *fazer um teste sem passar por endpoint*
        // Exemplo: testar se o banco realmente vai retornar sqlexception ao em vez de salvar um valor com regra de negócio errada
        if (dto.getEstoqueId() != null) {
            estoqueRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getEstoqueId().intValue())
                    .ifPresent(entity::setEstoque);
        }
        if (dto.getFornecedorId() != null) {
            fornecedorRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getFornecedorId().intValue())
                    .ifPresent(entity::setFornecedor);
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