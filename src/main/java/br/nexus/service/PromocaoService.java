package br.nexus.service;

import br.nexus.dto.request.PromocaoRequest;
import br.nexus.dto.response.PromocaoResponse;
import br.nexus.exception.BusinessException;
import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.Produto;
import br.nexus.model.Promocao;
import br.nexus.model.TipoDesconto;
import br.nexus.repository.PromocaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProdutoService produtoService;

    public PromocaoService(
            PromocaoRepository promocaoRepository,
            ProdutoService produtoService
    ) {
        this.promocaoRepository = promocaoRepository;
        this.produtoService = produtoService;
    }

    @Transactional
    public PromocaoResponse criar(PromocaoRequest request) {
        Produto produto = produtoService.buscarEntidade(request.produtoId());

        validarPromocao(produto, request);

        Promocao promocao = new Promocao(
                produto,
                request.tipoDesconto(),
                request.valorDesconto(),
                request.dataInicio(),
                request.dataFim()
        );

        promocao.setAtiva(request.ativa() == null || request.ativa());

        return PromocaoResponse.from(promocaoRepository.save(promocao));
    }

    @Transactional(readOnly = true)
    public List<PromocaoResponse> listar() {
        return promocaoRepository.findAllByOrderByDataInicioDesc()
                .stream()
                .map(PromocaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Promocao buscarEntidade(Long id) {
        return promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada."));
    }

    @Transactional(readOnly = true)
    public Optional<Promocao> buscarPromocaoAtiva(Produto produto) {
        LocalDate hoje = LocalDate.now();

        return promocaoRepository.findByProdutoIdAndAtivaTrueOrderByDataInicioDesc(produto.getId())
                .stream()
                .filter(promocao -> promocao.estaVigente(hoje))
                .max(Comparator.comparing(
                        promocao -> promocao.calcularDescontoUnitario(produto.getPrecoVenda())
                ));
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularDescontoUnitario(Produto produto) {
        return buscarPromocaoAtiva(produto)
                .map(promocao -> promocao.calcularDescontoUnitario(produto.getPrecoVenda()))
                .orElse(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularPrecoPromocional(Produto produto) {
        BigDecimal desconto = calcularDescontoUnitario(produto);
        return produto.getPrecoVenda().subtract(desconto);
    }

    private void validarPromocao(Produto produto, PromocaoRequest request) {
        if (!produto.isAtivo()) {
            throw new BusinessException("Não é possível criar promoção para produto inativo.");
        }

        if (request.dataInicio().isAfter(request.dataFim())) {
            throw new BusinessException("Data inicial não pode ser maior que a data final da promoção.");
        }

        if (TipoDesconto.PERCENTUAL.equals(request.tipoDesconto())
                && request.valorDesconto().compareTo(new BigDecimal("100.00")) > 0) {
            throw new BusinessException("Desconto percentual não pode ser maior que 100%.");
        }

        if (TipoDesconto.VALOR.equals(request.tipoDesconto())
                && request.valorDesconto().compareTo(produto.getPrecoVenda()) > 0) {
            throw new BusinessException("Desconto em valor não pode ser maior que o preço do produto.");
        }
    }
}