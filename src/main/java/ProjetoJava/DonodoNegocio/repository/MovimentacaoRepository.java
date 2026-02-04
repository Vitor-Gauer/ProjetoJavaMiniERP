package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Movimentacao> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(m.idLocalEmpresa) FROM Movimentacao m WHERE m.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(Long empresaId);
}