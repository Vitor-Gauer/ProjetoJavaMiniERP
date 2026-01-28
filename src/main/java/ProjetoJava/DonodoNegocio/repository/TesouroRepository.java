package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Tesouro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TesouroRepository extends JpaRepository<Tesouro, Long> {
}