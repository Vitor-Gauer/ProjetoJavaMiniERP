package ProjetoJava.DonodoNegocio;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.TipoUsuario;
import ProjetoJava.DonodoNegocio.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJPATest
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@AutoConfigureTestEntityManager
class TesteDatabase {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveConectarAoBDeRealizarOperacoesCRUD() {
        // --- Setup: Criar dependências ---
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Teste DB");
        empresa.setLoginMaster("master_teste_db");
        empresa.setSenhaHashAdmin("admin_hash");
        empresa.setSenhaHashPublica("public_hash");
        entityManager.persist(empresa);

        TipoUsuario tipo = new TipoUsuario();
        tipo.setEmpresa(empresa);
        tipo.setCargo("Admin Teste DB");
        tipo.setIdLocalEmpresa(1);
        entityManager.persist(tipo);

        // --- 1. Insert ---
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin("db_user");
        novoUsuario.setSenhaHash("hash_inicial_db");
        novoUsuario.setEmpresa(empresa);
        novoUsuario.setTipoUsuario(tipo);
        novoUsuario.setIdLocalEmpresa(2);
        
        // persistAndFlush: Persiste e sincroniza com o banco imediatamente
        Usuario usuarioInserido = entityManager.persistAndFlush(novoUsuario);
        
        // Limpa o contexto para garantir que o próximo find faça um SELECT real
        entityManager.clear();

        assertNotNull(usuarioInserido.getId(), "O ID do usuário não deveria ser nulo após o insert.");

        // --- 2. Select (após Insert) ---
        Usuario usuarioSelecionado1 = entityManager.find(Usuario.class, usuarioInserido.getId());

        // --- 3. Verificação (após Insert) ---
        assertNotNull(usuarioSelecionado1, "Usuário deveria ser encontrado no BD após o insert.");
        assertEquals("db_user", usuarioSelecionado1.getLogin());
        assertEquals(2, usuarioSelecionado1.getIdLocalEmpresa());

        // --- 4. Update ---
        usuarioSelecionado1.setLogin("db_user_atualizado");
        
        // Flush força o update no banco
        entityManager.flush();
        entityManager.clear();

        // --- 5. Select (após Update) ---
        Usuario usuarioSelecionado2 = entityManager.find(Usuario.class, usuarioInserido.getId());

        // --- 6. Verificação (após Update) ---
        assertNotNull(usuarioSelecionado2, "Usuário deveria ser encontrado no BD após o update.");
        assertEquals("db_user_atualizado", usuarioSelecionado2.getLogin(), "O login do usuário não foi atualizado corretamente no BD.");
    }
}