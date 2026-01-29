package ProjetoJava.DonodoNegocio;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.TipoUsuario;
import ProjetoJava.DonodoNegocio.model.Usuario;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.TipoUsuarioRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional // Garante que cada teste rode em uma transação e dê rollback no final
public class TesteDatabase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Test
    void deveConectarAoBDeRealizarOperacoesCRUD() {
        // --- Setup: Criar dependências (Empresa e TipoUsuario) ---
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Teste DB");
        empresa.setLoginMaster("master_teste_db");
        empresa.setSenhaHashAdmin("admin_hash");
        empresa.setSenhaHashPublica("public_hash");
        Empresa empresaSalva = empresaRepository.save(empresa);

        TipoUsuario tipo = new TipoUsuario();
        tipo.setEmpresa(empresaSalva);
        tipo.setCargo("Admin Teste DB");
        tipo.setIdLocalEmpresa(1); // <-- CORREÇÃO: Definindo o valor obrigatório
        TipoUsuario tipoSalvo = tipoUsuarioRepository.save(tipo);

        // --- 1. Insert ---
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin("db_user");
        novoUsuario.setSenhaHash("hash_inicial_db");
        novoUsuario.setEmpresa(empresaSalva);
        novoUsuario.setTipoUsuario(tipoSalvo);
        novoUsuario.setIdLocalEmpresa(2); // <-- CORREÇÃO: Definindo o valor obrigatório
        Usuario usuarioInserido = usuarioRepository.save(novoUsuario);

        assertNotNull(usuarioInserido.getId(), "O ID do usuário não deveria ser nulo após o insert.");

        // --- 2. Select (após Insert) ---
        Usuario usuarioSelecionado1 = usuarioRepository.findById(usuarioInserido.getId()).orElse(null);

        // --- 3. Verificação (após Insert) ---
        assertNotNull(usuarioSelecionado1, "Usuário deveria ser encontrado no BD após o insert.");
        assertEquals("db_user", usuarioSelecionado1.getLogin(), "O login do usuário selecionado não corresponde ao que foi inserido.");
        assertEquals(2, usuarioSelecionado1.getIdLocalEmpresa(), "O idLocalEmpresa não corresponde.");

        // --- 4. Update ---
        usuarioSelecionado1.setLogin("db_user_atualizado");
        usuarioRepository.save(usuarioSelecionado1);

        // --- 5. Select (após Update) ---
        Usuario usuarioSelecionado2 = usuarioRepository.findById(usuarioInserido.getId()).orElse(null);

        // --- 6. Verificação (após Update) ---
        assertNotNull(usuarioSelecionado2, "Usuário deveria ser encontrado no BD após o update.");
        assertEquals("db_user_atualizado", usuarioSelecionado2.getLogin(), "O login do usuário não foi atualizado corretamente no BD.");
    }
}