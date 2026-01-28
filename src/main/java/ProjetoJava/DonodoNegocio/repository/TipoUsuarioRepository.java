package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {
}