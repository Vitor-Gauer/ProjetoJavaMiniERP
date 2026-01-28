package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
}