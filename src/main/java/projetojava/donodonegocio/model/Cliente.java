package com.projetojava.donodonegocio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
}
