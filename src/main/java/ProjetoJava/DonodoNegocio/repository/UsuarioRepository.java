package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Usuario> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    Optional<Usuario> findByLoginAndEmpresaId(String login, Long empresaId);

    @Query("SELECT MAX(u.idLocalEmpresa) FROM Usuario u WHERE u.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(@Param("empresaId") Long empresaId);
}