package com.projetojava.donodonegocio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String painel() {
        return "painel"; 
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
