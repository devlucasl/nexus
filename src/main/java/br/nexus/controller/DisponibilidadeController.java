package br.nexus.controller;

import br.nexus.dto.DisponibilidadeResponse;
import br.nexus.service.ProdutoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/disponibilidade")
public class DisponibilidadeController {

    private final ProdutoService produtoService;

    public DisponibilidadeController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/produtos/{id}")
    public DisponibilidadeResponse consultarPorId(@PathVariable Long id) {
        return produtoService.consultarDisponibilidadePorId(id);
    }

    @GetMapping("/produtos/codigo/{codigo}")
    public DisponibilidadeResponse consultarPorCodigo(@PathVariable String codigo) {
        return produtoService.consultarDisponibilidadePorCodigo(codigo);
    }
}
