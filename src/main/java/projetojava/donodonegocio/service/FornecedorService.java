package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.FornecedorDTO;
import projetojava.donodonegocio.mapper.FornecedorMapper;
import projetojava.donodonegocio.model.Fornecedor;
import projetojava.donodonegocio.repository.FornecedorRepository;
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
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovoFornecedor(dto);
        }

        Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return fornecedorOpt
                .map(fornecedor -> atualizarFornecedorExistente(dto, fornecedor))
                .orElseGet(() -> criarNovoFornecedor(dto));
    }

    private FornecedorDTO criarNovoFornecedor(FornecedorDTO dto) {
        Fornecedor novoFornecedor = fornecedorMapper.toEntity(dto);
        
        Integer maxId = fornecedorRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
        int proximoId = (maxId == null) ? 1 : maxId + 1;
        novoFornecedor.setIdLocalEmpresa(proximoId);
        
        Fornecedor fornecedorSalvo = fornecedorRepository.save(novoFornecedor);
        return fornecedorMapper.toDTO(fornecedorSalvo);
    }

    private FornecedorDTO atualizarFornecedorExistente(FornecedorDTO dto, Fornecedor fornecedor) {
        fornecedorMapper.updateEntityFromDTO(dto, fornecedor);
        Fornecedor fornecedorAtualizado = fornecedorRepository.save(fornecedor);
        return fornecedorMapper.toDTO(fornecedorAtualizado);
    }
}