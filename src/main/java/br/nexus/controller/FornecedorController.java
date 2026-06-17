package br.nexus.controller;

import br.nexus.dto.FornecedorRequest;
import br.nexus.dto.FornecedorResponse;
import br.nexus.service.FornecedorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorResponse criar(@RequestBody @Valid FornecedorRequest request) {
        return fornecedorService.criar(request);
    }

    @PutMapping("/{id}")
    public FornecedorResponse editar(@PathVariable Long id, @RequestBody @Valid FornecedorRequest request) {
        return fornecedorService.editar(id, request);
    }

    @GetMapping
    public List<FornecedorResponse> listarAtivos() {
        return fornecedorService.listarAtivos();
    }
}
