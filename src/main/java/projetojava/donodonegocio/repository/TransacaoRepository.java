package projetojava.donodonegocio.repository;

import projetojava.donodonegocio.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    Optional<Transacao> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    @Query("SELECT MAX(t.idLocalEmpresa) FROM Transacao t WHERE t.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(Long empresaId);
    
    List<Transacao> findByEmpresaIdAndTipoTransacaoNome(Long empresaId, String nomeTipo);

    List<Transacao> findByEmpresaIdAndGrupoIdOrderByIdLocalEmpresaAsc(Long empresaId, String grupoId);

    @Modifying
    @Query("""
            UPDATE Transacao t
               SET t.ehValida = :novoStatus
             WHERE t.empresa.id = :empresaId
               AND t.grupoId = :grupoId
            """)
    void updateEhValidaByGrupo(@Param("empresaId") Long empresaId,
                             @Param("grupoId") String grupoId,
                             @Param("novoStatus") boolean novoStatus);

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM Transacao t
            WHERE t.empresa.id = :empresaId
              AND t.ehValida = true
              AND t.foiResolvido = false
              AND t.intervaloCobranca IS NOT NULL
              AND t.intervaloCobranca > 0
              AND FUNCTION('timestampadd', DAY, t.intervaloCobranca, t.dataCriacao) <= :agora
            """)
    boolean existsCobrancaVencidaNaoResolvida(@Param("empresaId") Long empresaId, @Param("agora") LocalDateTime agora);

    List<Transacao> findByEmpresaIdAndEhValidaTrueAndFoiResolvidoFalse(Long empresaId);

    List<Transacao> findByEmpresaIdAndEhValidaTrueAndFoiResolvidoTrue(Long empresaId);
}