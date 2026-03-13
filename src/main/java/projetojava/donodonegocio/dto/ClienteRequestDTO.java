package com.projetojava.donodonegocio.dto;

public record ClienteRequestDTO(
    String nome,
    String email,
    String cep,
    String logradouro,
    String bairro,
    String localidade,
    String uf
) {}
