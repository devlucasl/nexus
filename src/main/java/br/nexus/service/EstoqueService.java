package br.nexus.service;

import br.nexus.dto.request.AjusteEstoqueRequest;
import br.nexus.dto.response.HistoricoAjusteResponse;
import br.nexus.dto.request.MovimentacaoEstoqueRequest;
import br.nexus.dto.response.MovimentacaoEstoqueResponse;
import br.nexus.dto.response.ProdutoResponse;
import br.nexus.exception.BusinessException;
import br.nexus.model.HistoricoAjusteEstoque;
import br.nexus.model.MovimentacaoEstoque;
import br.nexus.model.Produto;
import br.nexus.model.TipoMovimentacao;
import br.nexus.model.Usuario;
import br.nexus.repository.HistoricoAjusteEstoqueRepository;
import br.nexus.repository.MovimentacaoEstoqueRepository;
import br.nexus.repository.ProdutoRepository;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final HistoricoAjusteEstoqueRepository historicoAjusteEstoqueRepository;

    public EstoqueService(
        ProdutoService produtoService,
        ProdutoRepository produtoRepository,
        UsuarioService usuarioService,
        MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
        HistoricoAjusteEstoqueRepository historicoAjusteEstoqueRepository
    ) {
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.historicoAjusteEstoqueRepository = historicoAjusteEstoqueRepository;
    }

    @Transactional
    public ProdutoResponse registrarEntrada(MovimentacaoEstoqueRequest request) {
        Produto produto = produtoService.buscarEntidade(request.produtoId());
        validarProdutoAtivo(produto);

        produto.setQuantidadeAtual(produto.getQuantidadeAtual() + request.quantidade());
        if (produto.getQuantidadeAtual() > 0) {
            produto.setDisponivel(true);
        }

        produtoRepository.save(produto);
        registrarMovimentacao(produto, TipoMovimentacao.ENTRADA, request.quantidade(), request.observacao());

        return ProdutoResponse.from(produto);
    }

    @Transactional
    public ProdutoResponse registrarSaida(MovimentacaoEstoqueRequest request) {
        Produto produto = produtoService.buscarEntidade(request.produtoId());
        validarProdutoAtivo(produto);

        if (produto.getQuantidadeAtual() < request.quantidade()) {
            throw new BusinessException("Estoque insuficiente para registrar saída.");
        }

        produto.setQuantidadeAtual(produto.getQuantidadeAtual() - request.quantidade());
        if (produto.getQuantidadeAtual() == 0) {
            produto.setDisponivel(false);
        }

        produtoRepository.save(produto);
        registrarMovimentacao(produto, TipoMovimentacao.SAIDA, request.quantidade(), request.observacao());

        return ProdutoResponse.from(produto);
    }

    @Transactional
    public ProdutoResponse ajustarQuantidade(AjusteEstoqueRequest request) {
        Produto produto = produtoService.buscarEntidade(request.produtoId());
        validarProdutoAtivo(produto);

        Integer quantidadeAnterior = produto.getQuantidadeAtual();
        Integer quantidadeNova = request.novaQuantidade();

        if (quantidadeAnterior.equals(quantidadeNova)) {
            throw new BusinessException("A nova quantidade deve ser diferente da quantidade atual.");
        }

        produto.setQuantidadeAtual(quantidadeNova);
        produto.setDisponivel(quantidadeNova > 0);
        produtoRepository.save(produto);

        Usuario usuario = usuarioAtual();
        historicoAjusteEstoqueRepository.save(new HistoricoAjusteEstoque(
            produto,
            usuario,
            quantidadeAnterior,
            quantidadeNova,
            request.justificativa()
        ));

        registrarMovimentacao(produto, TipoMovimentacao.AJUSTE, Math.abs(quantidadeNova - quantidadeAnterior), request.justificativa());

        return ProdutoResponse.from(produto);
    }

    @Transactional(readOnly = true)
    public List<HistoricoAjusteResponse> listarHistoricoAjustes(Long produtoId) {
        produtoService.buscarEntidade(produtoId);
        return historicoAjusteEstoqueRepository.findByProdutoIdOrderByDataHoraDesc(produtoId)
            .stream()
            .map(HistoricoAjusteResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponse> listarMovimentacoes(Long produtoId) {
        produtoService.buscarEntidade(produtoId);
        return movimentacaoEstoqueRepository.findByProdutoIdOrderByDataHoraDesc(produtoId)
            .stream()
            .map(MovimentacaoEstoqueResponse::from)
            .toList();
    }

    private void registrarMovimentacao(Produto produto, TipoMovimentacao tipo, Integer quantidade, String observacao) {
        movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
            produto,
            usuarioAtual(),
            tipo,
            quantidade,
            observacao
        ));
    }

    private Usuario usuarioAtual() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.buscarPorLogin(login);
    }

    private void validarProdutoAtivo(Produto produto) {
        if (!produto.isAtivo()) {
            throw new BusinessException("Produto inativo não permite movimentação de estoque.");
        }
    }
}
