package br.nexus.service;

import br.nexus.dto.response.RelatorioEstoqueResponse;
import br.nexus.model.Produto;
import br.nexus.repository.ProdutoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RelatorioService {

    private final ProdutoRepository produtoRepository;

    public RelatorioService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<RelatorioEstoqueResponse> gerarRelatorioEstoque() {
        return produtoRepository.findAllByOrderByDescricaoAsc()
                .stream()
                .map(RelatorioEstoqueResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String gerarCsvEstoque() {
        StringBuilder csv = new StringBuilder();

        csv.append("codigo;descricao;categoria;fornecedor;preco_venda;quantidade_atual;estoque_minimo;disponibilidade;estoque_baixo\n");

        for (Produto produto : produtoRepository.findAllByOrderByDescricaoAsc()) {
            csv.append(valor(produto.getCodigo())).append(';')
                    .append(valor(produto.getDescricao())).append(';')
                    .append(valor(produto.getCategoria().getNome())).append(';')
                    .append(valor(produto.getFornecedor() == null ? "" : produto.getFornecedor().getNome())).append(';')
                    .append(produto.getPrecoVenda()).append(';')
                    .append(produto.getQuantidadeAtual()).append(';')
                    .append(produto.getEstoqueMinimo()).append(';')
                    .append(produto.estaDisponivelParaConsulta() ? "DISPONIVEL" : "INDISPONIVEL").append(';')
                    .append(produto.estaComEstoqueBaixo() ? "SIM" : "NAO")
                    .append('\n');
        }

        return csv.toString();
    }

    private String valor(String value) {
        if (value == null) {
            return "";
        }

        return '"' + value.replace("\"", "\"\"") + '"';
    }
}