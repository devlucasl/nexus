package br.nexus.dto;

import br.nexus.model.Categoria;

public record CategoriaResponse(Long id, String nome, String descricao, boolean ativo) {
    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao(), categoria.isAtivo());
    }
}
