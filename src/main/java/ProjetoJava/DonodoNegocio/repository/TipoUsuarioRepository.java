package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    List<TipoUsuario> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<TipoUsuario> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(t.idLocalEmpresa) FROM TipoUsuario t WHERE t.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(@Param("empresaId") Long empresaId);
}