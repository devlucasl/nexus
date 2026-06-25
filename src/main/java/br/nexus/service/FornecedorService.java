package br.nexus.service;

import br.nexus.dto.request.FornecedorRequest;
import br.nexus.dto.response.FornecedorResponse;
import br.nexus.exception.BusinessException;
import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.Fornecedor;
import br.nexus.repository.FornecedorRepository;
import br.nexus.validation.FornecedorValidator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorValidator fornecedorValidator;

    public FornecedorService(FornecedorRepository fornecedorRepository, FornecedorValidator fornecedorValidator) {
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorValidator = fornecedorValidator;
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
        fornecedorValidator.validarContatoObrigatorio(request);
        Fornecedor fornecedor = fornecedorRepository.save(new Fornecedor(request.nome().trim(), request.telefone(), request.email()));
        return FornecedorResponse.from(fornecedor);
    }

    @Transactional
    public FornecedorResponse editar(Long id, FornecedorRequest request) {
        fornecedorValidator.validarContatoObrigatorio(request);
        Fornecedor fornecedor = buscarEntidade(id);
        fornecedor.setNome(request.nome().trim());
        fornecedor.setTelefone(request.telefone());
        fornecedor.setEmail(request.email());
        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listarAtivos() {
        return fornecedorRepository.findByAtivoTrueOrderByNomeAsc()
            .stream()
            .map(FornecedorResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public Fornecedor buscarEntidade(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado."));

        if (!fornecedor.isAtivo()) {
            throw new BusinessException("Fornecedor está inativo.");
        }

        return fornecedor;
    }
}
