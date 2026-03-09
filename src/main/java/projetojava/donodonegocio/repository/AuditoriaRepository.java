package projetojava.donodonegocio.repository;

import projetojava.donodonegocio.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Auditoria> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(a.idLocalEmpresa) FROM Auditoria a WHERE a.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(@Param("empresaId") Long empresaId);
}