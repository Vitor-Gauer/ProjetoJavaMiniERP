package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoTransacaoRepository extends JpaRepository<TipoTransacao, Long> {

    List<TipoTransacao> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<TipoTransacao> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(t.idLocalEmpresa) FROM TipoTransacao t WHERE t.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(@Param("empresaId") Long empresaId);
}