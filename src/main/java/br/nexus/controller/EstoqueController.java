package br.nexus.controller;

import br.nexus.dto.AjusteEstoqueRequest;
import br.nexus.dto.HistoricoAjusteResponse;
import br.nexus.dto.MovimentacaoEstoqueRequest;
import br.nexus.dto.MovimentacaoEstoqueResponse;
import br.nexus.dto.ProdutoResponse;
import br.nexus.service.EstoqueService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/entrada")
    public ProdutoResponse registrarEntrada(@RequestBody @Valid MovimentacaoEstoqueRequest request) {
        return estoqueService.registrarEntrada(request);
    }

    @PostMapping("/saida")
    public ProdutoResponse registrarSaida(@RequestBody @Valid MovimentacaoEstoqueRequest request) {
        return estoqueService.registrarSaida(request);
    }

    @PostMapping("/ajuste")
    public ProdutoResponse ajustarQuantidade(@RequestBody @Valid AjusteEstoqueRequest request) {
        return estoqueService.ajustarQuantidade(request);
    }

    @GetMapping("/produtos/{produtoId}/historico-ajustes")
    public List<HistoricoAjusteResponse> listarHistoricoAjustes(@PathVariable Long produtoId) {
        return estoqueService.listarHistoricoAjustes(produtoId);
    }

    @GetMapping("/produtos/{produtoId}/movimentacoes")
    public List<MovimentacaoEstoqueResponse> listarMovimentacoes(@PathVariable Long produtoId) {
        return estoqueService.listarMovimentacoes(produtoId);
    }
}
