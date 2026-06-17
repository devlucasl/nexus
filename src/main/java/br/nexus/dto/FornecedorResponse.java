package br.nexus.dto;

import br.nexus.model.Fornecedor;

public record FornecedorResponse(Long id, String nome, String telefone, String email, boolean ativo) {
    public static FornecedorResponse from(Fornecedor fornecedor) {
        return new FornecedorResponse(
            fornecedor.getId(),
            fornecedor.getNome(),
            fornecedor.getTelefone(),
            fornecedor.getEmail(),
            fornecedor.isAtivo()
        );
    }
}
