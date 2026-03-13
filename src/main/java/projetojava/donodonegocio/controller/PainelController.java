package com.projetojava.donodonegocio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PainelController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/painel")
    public String painel() {
        return "painel";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
