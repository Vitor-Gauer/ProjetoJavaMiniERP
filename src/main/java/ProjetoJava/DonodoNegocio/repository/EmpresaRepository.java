package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}