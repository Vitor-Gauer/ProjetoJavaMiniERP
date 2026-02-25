package ProjetoJava.DonodoNegocio;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.TipoUsuario;
import ProjetoJava.DonodoNegocio.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("dev")
@DisplayName("Testes de Persistência para a Entidade Usuario")
class UsuarioPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    private Empresa empresa;
    private TipoUsuario tipoUsuario;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setNome("Empresa Teste");
        empresa.setLoginMaster("master_teste");
        empresa.setSenhaHashAdmin("admin_hash");
        empresa.setSenhaHashPublica("public_hash");
        entityManager.persist(empresa);

        tipoUsuario = new TipoUsuario();
        tipoUsuario.setEmpresa(empresa);
        tipoUsuario.setCargo("Admin Teste");
        tipoUsuario.setIdLocalEmpresa(1);
        entityManager.persist(tipoUsuario);
    }

    private Usuario criarUsuarioPadrao() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin("user_test");
        novoUsuario.setSenhaHash("some_hash");
        novoUsuario.setEmpresa(empresa);
        novoUsuario.setTipoUsuario(tipoUsuario);
        novoUsuario.setIdLocalEmpresa(2);
        return novoUsuario;
    }

    @Test
    @DisplayName("Deve salvar um novo usuário no banco de dados")
    void deveSalvarUsuarioComSucesso() {
        Usuario novoUsuario = criarUsuarioPadrao();

        Usuario usuarioSalvo = entityManager.persistAndFlush(novoUsuario);

        assertNotNull(usuarioSalvo.getId(), "O ID do usuário não deveria ser nulo após a persistência.");
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo seu ID")
    void deveEncontrarUsuarioPorId() {
        Usuario novoUsuario = criarUsuarioPadrao();
        Long usuarioId = entityManager.persistAndGetId(novoUsuario, Long.class);
        entityManager.clear();

        Usuario usuarioEncontrado = entityManager.find(Usuario.class, usuarioId);

        assertNotNull(usuarioEncontrado, "O usuário deveria ser encontrado pelo ID.");
        assertEquals("user_test", usuarioEncontrado.getLogin());
    }

    @Test
    @DisplayName("Deve atualizar o login de um usuário existente")
    void deveAtualizarLoginDoUsuario() {
        Usuario novoUsuario = criarUsuarioPadrao();
        Long usuarioId = entityManager.persistAndGetId(novoUsuario, Long.class);
        entityManager.detach(novoUsuario); // Garante que estamos trabalhando com uma entidade gerenciada

        Usuario usuarioParaAtualizar = entityManager.find(Usuario.class, usuarioId);
        assertNotNull(usuarioParaAtualizar);

        usuarioParaAtualizar.setLogin("user_test_updated");
        entityManager.flush();
        entityManager.clear();

        Usuario usuarioAtualizado = entityManager.find(Usuario.class, usuarioId);

        assertNotNull(usuarioAtualizado, "O usuário atualizado não deveria ser nulo.");
        assertEquals("user_test_updated", usuarioAtualizado.getLogin(), "O login do usuário deveria ter sido atualizado.");
    }
}