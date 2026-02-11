package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Tesouro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TesouroRepository extends JpaRepository<Tesouro, Long> {

    List<Tesouro> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Tesouro> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(t.idLocalEmpresa) FROM Tesouro t WHERE t.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(@Param("empresaId") Long empresaId);
}