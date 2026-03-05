package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.TipoTransacao;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.TipoTransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final TipoTransacaoRepository tipoTransacaoRepository;

    private record ConfiguracaoTipoTransacao(String nome, boolean recorrente, boolean recebimento) {}

    private static final List<ConfiguracaoTipoTransacao> CONFIGURACOES_INICIAIS = List.of(
            // Financeiro
            new ConfiguracaoTipoTransacao("Despesa", true, false),
            new ConfiguracaoTipoTransacao("Receita", true, true),
            new ConfiguracaoTipoTransacao("Devendo", false, false),
            new ConfiguracaoTipoTransacao("Quitado", false, true),
            new ConfiguracaoTipoTransacao("Compra", false, false),
            new ConfiguracaoTipoTransacao("Venda", false, true),
            // Estoque
            new ConfiguracaoTipoTransacao("Entrada Estoque", true, true),
            new ConfiguracaoTipoTransacao("Saida Estoque", true, false),
            new ConfiguracaoTipoTransacao("Compra Estoque", false, true),
            new ConfiguracaoTipoTransacao("Venda Estoque", false, false)
    );

    @Transactional
    public void inicializarTiposTransacao(Empresa empresa) {
        validarInicializacao(empresa);

        int proximoIdLocal = 1;
        for (ConfiguracaoTipoTransacao config : CONFIGURACOES_INICIAIS) {
            criarESalvarTipoTransacao(empresa, config, proximoIdLocal++);
        }
    }

    private void validarInicializacao(Empresa empresa) {
        Integer maxIdAtual = tipoTransacaoRepository.findMaxIdLocalByEmpresaId(empresa.getId());
        if (maxIdAtual != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                String.format("A empresa com ID %d já possui tipos de transação inicializados.", empresa.getId()));
        }
    }

    private void criarESalvarTipoTransacao(Empresa empresa, ConfiguracaoTipoTransacao config, int idLocal) {
        TipoTransacao tipo = new TipoTransacao();
        tipo.setEmpresa(empresa);
        tipo.setIdLocalEmpresa(idLocal);
        tipo.setNome(config.nome());
        tipo.setEhRecorrente(config.recorrente());
        tipo.setEhRecebimento(config.recebimento());
        tipo.setPrctJuros(BigDecimal.ZERO);
        
        tipoTransacaoRepository.save(tipo);
    }
}