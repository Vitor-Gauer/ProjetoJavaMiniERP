package com.projetojava.donodonegocio.controller;

import com.projetojava.donodonegocio.dto.ClienteRequestDTO;
import com.projetojava.donodonegocio.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listarTodos());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String formNovo() {
        return "clientes/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ClienteRequestDTO dto) {
        service.salvar(dto);
        return "redirect:/clientes";
    }
}
