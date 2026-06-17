package br.nexus.validation;

import br.nexus.dto.FornecedorRequest;
import br.nexus.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FornecedorValidator {

    public void validarContatoObrigatorio(FornecedorRequest request) {
        boolean possuiTelefone = StringUtils.hasText(request.telefone());
        boolean possuiEmail = StringUtils.hasText(request.email());

        if (!possuiTelefone && !possuiEmail) {
            throw new BusinessException("Fornecedor deve possuir telefone ou e-mail.");
        }
    }
}
