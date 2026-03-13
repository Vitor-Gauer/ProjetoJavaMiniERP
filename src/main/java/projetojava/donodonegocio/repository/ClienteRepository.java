package com.projetojava.donodonegocio.repository;

import com.projetojava.donodonegocio.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
