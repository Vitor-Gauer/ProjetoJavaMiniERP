package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}