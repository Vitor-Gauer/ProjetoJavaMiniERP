package projetojava.donodonegocio.controller;

import projetojava.donodonegocio.model.Cliente;
import projetojava.donodonegocio.service.ClienteService;
import projetojava.donodonegocio.config.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return AppConstants.VIEW_CLIENTE_FORM;
    }
    
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
                return AppConstants.REDIRECT_TABELAS;
            }
            
            // Separar endereço em campos individuais
            if (cliente.getEndereco() != null && cliente.getEndereco().contains(",")) {
                String[] partes = cliente.getEndereco().split(",", 4);
                if (partes.length >= 1) cliente.setCep(partes[0]);
                if (partes.length >= 2) cliente.setRua(partes[1]);
                if (partes.length >= 3) cliente.setNumero(partes[2]);
                if (partes.length >= 4) cliente.setComplemento(partes[3]);
            }
            
            model.addAttribute("cliente", cliente);
            return AppConstants.VIEW_CLIENTE_FORM;
            
        } catch (Exception e) {
            log.error("Erro ao carregar cliente para edição: {}", id, e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao carregar cliente");
            return AppConstants.REDIRECT_TABELAS;
        }
    }
    
    @PostMapping("/salvar")
    public String salvarCliente(@Valid @ModelAttribute Cliente cliente, 
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        try {
            if (bindingResult.hasErrors()) {
                return AppConstants.VIEW_CLIENTE_FORM;
            }
            
            // Combinar campos de endereço
            String enderecoCompleto = formatarEnderecoCompleto(cliente);
            cliente.setEndereco(enderecoCompleto);
            
            clienteService.salvar(cliente);
            
            redirectAttributes.addFlashAttribute("sucesso", "Cliente salvo com sucesso!");
            return AppConstants.REDIRECT_TABELAS;
            
        } catch (Exception e) {
            log.error("Erro ao salvar cliente", e);
            model.addAttribute("erro", "Erro ao salvar cliente: " + e.getMessage());
            return AppConstants.VIEW_CLIENTE_FORM;
        }
    }
    
    @PostMapping("/excluir/{id}")
    public void excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao excluir cliente: {}", id, e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir cliente");
        }
    }
    
    private String formatarEnderecoCompleto(Cliente cliente) {
        StringBuilder endereco = new StringBuilder();
        
        if (cliente.getCep() != null && !cliente.getCep().trim().isEmpty()) {
            endereco.append(cliente.getCep().trim());
        }
        
        if (cliente.getRua() != null && !cliente.getRua().trim().isEmpty()) {
            if (!endereco.isEmpty()) endereco.append(",");
            endereco.append(cliente.getRua().trim());
        }
        
        if (cliente.getNumero() != null && !cliente.getNumero().trim().isEmpty()) {
            if (!endereco.isEmpty()) endereco.append(",");
            endereco.append(cliente.getNumero().trim());
        }
        
        if (cliente.getComplemento() != null && !cliente.getComplemento().trim().isEmpty()) {
            if (!endereco.isEmpty()) endereco.append(",");
            endereco.append(cliente.getComplemento().trim());
        }
        
        return endereco.toString();
    }
}
