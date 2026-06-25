package br.nexus.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProblemResponse(
    LocalDateTime timestamp,
    int status,
    String erro,
    List<String> mensagens
) {
    public static ProblemResponse of(int status, String erro, List<String> mensagens) {
        return new ProblemResponse(LocalDateTime.now(), status, erro, mensagens);
    }
}
