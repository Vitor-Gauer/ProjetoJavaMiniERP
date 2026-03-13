package com.projetojava.donodonegocio.service;

import com.projetojava.donodonegocio.dto.ClienteRequestDTO;
import com.projetojava.donodonegocio.dto.ClienteResponseDTO;
import com.projetojava.donodonegocio.model.Cliente;
import com.projetojava.donodonegocio.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public void salvar(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setCep(dto.cep());
        cliente.setLogradouro(dto.logradouro());
        cliente.setBairro(dto.bairro());
        cliente.setLocalidade(dto.localidade());
        cliente.setUf(dto.uf());
        
        // Aqui entrariam outras regras de negócio antes de salvar
        repository.save(cliente);
    }

    public List<ClienteResponseDTO> listarTodos() {
        return repository.findAll().stream()
            .map(c -> new ClienteResponseDTO(
                c.getId(), 
                c.getNome(), 
                c.getEmail(), 
                c.getLocalidade() + "/" + c.getUf()
            ))
            .collect(Collectors.toList());
    }
}
