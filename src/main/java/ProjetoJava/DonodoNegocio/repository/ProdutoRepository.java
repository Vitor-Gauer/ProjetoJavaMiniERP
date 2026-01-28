package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}