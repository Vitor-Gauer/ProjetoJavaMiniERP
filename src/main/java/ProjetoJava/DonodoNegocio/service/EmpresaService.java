package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.TipoTransacao;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.TipoTransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final TipoTransacaoRepository tipoTransacaoRepository;

    private record TipoTransacaoConfig(String nome, boolean recorrente, boolean recebimento) {}

    @Transactional
    public void inicializarTiposTransacao(Empresa empresa) {
        Integer currentMaxId = tipoTransacaoRepository.findMaxIdLocalByEmpresaId(empresa.getId());
        if (currentMaxId != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A empresa com ID " + empresa.getId() + " já possui tipos de transação inicializados.");
        }

        List<TipoTransacaoConfig> configs = List.of(
                // Financeiro
                new TipoTransacaoConfig("Despesa", true, false),
                new TipoTransacaoConfig("Receita", true, true),
                new TipoTransacaoConfig("Devendo", false, false),
                new TipoTransacaoConfig("Quitado", false, true),
                new TipoTransacaoConfig("Compra", false, false),
                new TipoTransacaoConfig("Venda", false, true),
                // Estoque
                new TipoTransacaoConfig("Entrada Estoque", true, true),
                new TipoTransacaoConfig("Saida Estoque", true, false),
                new TipoTransacaoConfig("Compra Estoque", false, true),
                new TipoTransacaoConfig("Venda Estoque", false, false)
        );

        int nextId = 1;
        for (TipoTransacaoConfig config : configs) {
            criarTipo(empresa, config, nextId++);
        }
    }

    private void criarTipo(Empresa empresa, TipoTransacaoConfig config, int idLocal) {
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