package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoTransacaoRepository extends JpaRepository<TipoTransacao, Long> {
}