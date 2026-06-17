package br.nexus.controller;

import br.nexus.dto.AlterarDisponibilidadeRequest;
import br.nexus.dto.MessageResponse;
import br.nexus.dto.ProdutoRequest;
import br.nexus.dto.ProdutoResponse;
import br.nexus.service.ProdutoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@RequestBody @Valid ProdutoRequest request) {
        return produtoService.criar(request);
    }

    @PutMapping("/{id}")
    public ProdutoResponse editar(@PathVariable Long id, @RequestBody @Valid ProdutoRequest request) {
        return produtoService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return new MessageResponse("Produto removido ou inativado com sucesso.");
    }

    @PatchMapping("/{id}/disponibilidade")
    public ProdutoResponse alterarDisponibilidade(@PathVariable Long id, @RequestBody @Valid AlterarDisponibilidadeRequest request) {
        return produtoService.alterarDisponibilidade(id, request);
    }

    @GetMapping
    public List<ProdutoResponse> consultar(@RequestParam(required = false) String nome) {
        return produtoService.consultar(nome);
    }

    @GetMapping("/codigo/{codigo}")
    public ProdutoResponse buscarPorCodigo(@PathVariable String codigo) {
        return produtoService.buscarPorCodigo(codigo);
    }
}
