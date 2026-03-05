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
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovoTesouro(dto);
        }

        Optional<Tesouro> tesouroOpt = tesouroRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return tesouroOpt
                .map(tesouro -> atualizarTesouroExistente(dto, tesouro))
                .orElseGet(() -> criarNovoTesouro(dto));
    }

    private TesouroDTO criarNovoTesouro(TesouroDTO dto) {
        Tesouro novoTesouro = tesouroMapper.toEntity(dto);
        
        Integer maxId = tesouroRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
        int proximoId = (maxId == null) ? 1 : maxId + 1;
        novoTesouro.setIdLocalEmpresa(proximoId);
        
        Tesouro tesouroSalvo = tesouroRepository.save(novoTesouro);
        return tesouroMapper.toDTO(tesouroSalvo);
    }

    private TesouroDTO atualizarTesouroExistente(TesouroDTO dto, Tesouro tesouro) {
        tesouroMapper.updateEntityFromDTO(dto, tesouro);
        Tesouro tesouroAtualizado = tesouroRepository.save(tesouro);
        return tesouroMapper.toDTO(tesouroAtualizado);
    }
}