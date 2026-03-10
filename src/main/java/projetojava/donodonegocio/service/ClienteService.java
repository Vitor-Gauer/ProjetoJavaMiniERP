package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.ClienteDTO;
import projetojava.donodonegocio.mapper.ClienteMapper;
import projetojava.donodonegocio.model.Cliente;
import projetojava.donodonegocio.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
    
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public void salvar(Cliente cliente) {
        if (cliente.getIdLocalEmpresa() == null) {
            Integer maxId = clienteRepository.findMaxIdLocalByEmpresaId(cliente.getEmpresa().getId());
            int proximoId = (maxId == null) ? 1 : maxId + 1;
            cliente.setIdLocalEmpresa(proximoId);
        }
        
        clienteRepository.save(cliente);
    }
    
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }
    
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
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