package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Transacao> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(t.idLocalEmpresa) FROM Transacao t WHERE t.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(Long empresaId);
    
    List<Transacao> findByEmpresaIdAndTipoTransacaoNome(Long empresaId, String nomeTipo);
}