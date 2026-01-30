package ProjetoJava.DonodoNegocio.repository;

import ProjetoJava.DonodoNegocio.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // O Spring monta o SQL automaticamente baseado no nome do método:
    // SELECT * FROM Produto WHERE empresa_id = ? ORDER BY id_local_empresa ASC
    List<Produto> findByEmpresaIdOrderByIdLocalEmpresaAsc(Long empresaId);

    // Busca item específico da empresa
    Optional<Produto> findByEmpresaIdAndIdLocalEmpresa(Long empresaId, Long idLocalEmpresa);

    // Query customizada para achar o maior ID local usado (para gerar o próximo)
    @Query("SELECT MAX(p.idLocalEmpresa) FROM Produto p WHERE p.empresa.id = :empresaId")
    Integer findMaxIdLocalByEmpresaId(Long empresaId);
}