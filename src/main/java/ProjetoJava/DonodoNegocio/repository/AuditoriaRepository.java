package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
}