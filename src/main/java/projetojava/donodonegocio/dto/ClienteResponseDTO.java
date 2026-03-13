package com.projetojava.donodonegocio.dto;

public record ClienteResponseDTO(
    Long id,
    String nome,
    String email,
    String cidadeEstado
) {}
