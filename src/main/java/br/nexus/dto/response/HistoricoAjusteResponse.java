package br.nexus.dto.response;

import br.nexus.model.HistoricoAjusteEstoque;
import java.time.LocalDateTime;

public record HistoricoAjusteResponse(
    Long id,
    Long produtoId,
    String codigoProduto,
    String usuario,
    Integer quantidadeAnterior,
    Integer quantidadeNova,
    String justificativa,
    LocalDateTime dataHora
) {
    public static HistoricoAjusteResponse from(HistoricoAjusteEstoque historico) {
        return new HistoricoAjusteResponse(
            historico.getId(),
            historico.getProduto().getId(),
            historico.getProduto().getCodigo(),
            historico.getUsuario().getLogin(),
            historico.getQuantidadeAnterior(),
            historico.getQuantidadeNova(),
            historico.getJustificativa(),
            historico.getDataHora()
        );
    }
}
