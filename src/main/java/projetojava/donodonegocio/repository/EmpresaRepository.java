package projetojava.donodonegocio.repository;

import projetojava.donodonegocio.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByLoginMaster(String loginMaster);
    
    Optional<Empresa> findByLoginPublico(String loginPublico);
}