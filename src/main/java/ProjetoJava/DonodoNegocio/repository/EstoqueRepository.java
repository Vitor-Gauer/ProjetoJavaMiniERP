package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
}