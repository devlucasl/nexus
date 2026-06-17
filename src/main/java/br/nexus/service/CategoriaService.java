package br.nexus.service;

import br.nexus.dto.CategoriaRequest;
import br.nexus.dto.CategoriaResponse;
import br.nexus.exception.BusinessException;
import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.Categoria;
import br.nexus.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe uma categoria com esse nome.");
        }

        Categoria categoria = categoriaRepository.save(new Categoria(request.nome().trim(), request.descricao()));
        return CategoriaResponse.from(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarAtivas() {
        return categoriaRepository.findByAtivoTrueOrderByNomeAsc()
            .stream()
            .map(CategoriaResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public Categoria buscarEntidade(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        if (!categoria.isAtivo()) {
            throw new BusinessException("Categoria está inativa.");
        }

        return categoria;
    }
}
