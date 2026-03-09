package projetojava.donodonegocio.repository;

import projetojava.donodonegocio.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    List<Estoque> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Estoque> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(e.idLocalEmpresa) FROM Estoque e WHERE e.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(@Param("empresaId") Long empresaId);
}