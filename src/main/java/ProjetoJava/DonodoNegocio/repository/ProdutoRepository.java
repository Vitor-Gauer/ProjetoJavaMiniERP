package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Listar todos as entidades pertencentes a uma empresa
    List<Produto> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    // Buscar item específico da empresa (para alteração ou consulta)
    Optional<Produto> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Integer idLocalEmpresa);

    // Query customizada para achar o maior ID local usado (para gerar o próximo)
    @Query("SELECT MAX(p.idLocalEmpresa) FROM Produto p WHERE p.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(Long empresaId);
}