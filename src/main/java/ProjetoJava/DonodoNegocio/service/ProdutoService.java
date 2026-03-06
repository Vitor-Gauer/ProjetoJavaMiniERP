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
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovoProduto(dto);
        }

        Optional<Produto> produtoOpt = produtoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return produtoOpt
                .map(produto -> atualizarProdutoExistente(dto, produto))
                .orElseGet(() -> criarNovoProduto(dto));
    }

    private ProdutoDTO criarNovoProduto(ProdutoDTO dto) {
        Produto novoProduto = produtoMapper.toEntity(dto);
        
        Integer maxId = produtoRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
        int proximoId = (maxId == null) ? 1 : maxId + 1;
        novoProduto.setIdLocalEmpresa(proximoId);
        
        Produto produtoSalvo = produtoRepository.save(novoProduto);
        return produtoMapper.toDTO(produtoSalvo);
    }

    private ProdutoDTO atualizarProdutoExistente(ProdutoDTO dto, Produto produto) {
        produtoMapper.updateEntityFromDTO(dto, produto);
        Produto produtoAtualizado = produtoRepository.save(produto);
        return produtoMapper.toDTO(produtoAtualizado);
    }
}