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
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovoCliente(dto);
        }

        Optional<Cliente> clienteOpt = clienteRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return clienteOpt
                .map(cliente -> atualizarClienteExistente(dto, cliente))
                .orElseGet(() -> criarNovoCliente(dto));
    }

    private ClienteDTO criarNovoCliente(ClienteDTO dto) {
        Cliente novoCliente = clienteMapper.toEntity(dto);
        
        Integer maxId = clienteRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
        int proximoId = (maxId == null) ? 1 : maxId + 1;
        novoCliente.setIdLocalEmpresa(proximoId);
        
        Cliente clienteSalvo = clienteRepository.save(novoCliente);
        return clienteMapper.toDTO(clienteSalvo);
    }

    private ClienteDTO atualizarClienteExistente(ClienteDTO dto, Cliente cliente) {
        clienteMapper.updateEntityFromDTO(dto, cliente);
        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteAtualizado);
    }
}