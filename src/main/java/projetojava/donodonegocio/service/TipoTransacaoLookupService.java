package projetojava.donodonegocio.service;

import projetojava.donodonegocio.model.TipoTransacao;
import projetojava.donodonegocio.repository.TipoTransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoTransacaoLookupService {

    private final TipoTransacaoRepository tipoTransacaoRepository;

    public Optional<TipoTransacao> findByNome(Long empresaId, String nome) {
        if (empresaId == null || nome == null || nome.isBlank()) {
            return Optional.empty();
        }
        return tipoTransacaoRepository.findByEmpresaIdAndNomeIgnoreCase(empresaId, nome.trim());
    }

    public List<TipoTransacao> listAll(Long empresaId) {
        return tipoTransacaoRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(empresaId);
    }

    public List<String> listNomesParaTela(String tipoTela) {
        List<String> nomes = new ArrayList<>();
        if ("venda".equalsIgnoreCase(tipoTela)) {
            nomes.add("Venda");
        } else if ("compra".equalsIgnoreCase(tipoTela)) {
            nomes.add("Compra");
        }
        return nomes;
    }
}
