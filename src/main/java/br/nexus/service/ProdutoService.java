package br.nexus.service;

import br.nexus.dto.AlterarDisponibilidadeRequest;
import br.nexus.dto.DisponibilidadeResponse;
import br.nexus.dto.ProdutoRequest;
import br.nexus.dto.ProdutoResponse;
import br.nexus.exception.BusinessException;
import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.Categoria;
import br.nexus.model.Fornecedor;
import br.nexus.model.Produto;
import br.nexus.repository.MovimentacaoEstoqueRepository;
import br.nexus.repository.ProdutoRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final CategoriaService categoriaService;
    private final FornecedorService fornecedorService;

    public ProdutoService(
        ProdutoRepository produtoRepository,
        MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
        CategoriaService categoriaService,
        FornecedorService fornecedorService
    ) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.categoriaService = categoriaService;
        this.fornecedorService = fornecedorService;
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        String codigo = normalizarCodigo(request.codigo());
        if (produtoRepository.existsByCodigo(codigo)) {
            throw new BusinessException("Já existe produto cadastrado com o código informado.");
        }

        Produto produto = new Produto();
        aplicarDados(produto, request, codigo);
        produto.setAtivo(true);
        sincronizarDisponibilidade(produto, request.disponivel());

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse editar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        String codigo = normalizarCodigo(request.codigo());

        if (!Objects.equals(produto.getCodigo(), codigo) && produtoRepository.existsByCodigo(codigo)) {
            throw new BusinessException("Já existe produto cadastrado com o código informado.");
        }

        aplicarDados(produto, request, codigo);
        sincronizarDisponibilidade(produto, request.disponivel());

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);

        if (movimentacaoEstoqueRepository.existsByProdutoId(id)) {
            produto.setAtivo(false);
            produto.setDisponivel(false);
            produtoRepository.save(produto);
            return;
        }

        produtoRepository.delete(produto);
    }

    @Transactional
    public ProdutoResponse alterarDisponibilidade(Long id, AlterarDisponibilidadeRequest request) {
        Produto produto = buscarEntidade(id);

        if (Boolean.TRUE.equals(request.disponivel()) && produto.getQuantidadeAtual() <= 0) {
            throw new BusinessException("Produto sem estoque não pode ser marcado como disponível.");
        }

        produto.setAtivo(Boolean.TRUE.equals(request.disponivel()));
        produto.setDisponivel(Boolean.TRUE.equals(request.disponivel()));
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> consultar(String nome) {
        List<Produto> produtos = StringUtils.hasText(nome)
            ? produtoRepository.findByDescricaoContainingIgnoreCaseOrderByDescricaoAsc(nome.trim())
            : produtoRepository.findAllByOrderByDescricaoAsc();

        return produtos.stream()
            .map(ProdutoResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorCodigo(String codigo) {
        return ProdutoResponse.from(buscarEntidadePorCodigo(codigo));
    }

    @Transactional(readOnly = true)
    public DisponibilidadeResponse consultarDisponibilidadePorCodigo(String codigo) {
        return DisponibilidadeResponse.from(buscarEntidadePorCodigo(codigo));
    }

    @Transactional(readOnly = true)
    public DisponibilidadeResponse consultarDisponibilidadePorId(Long id) {
        return DisponibilidadeResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
    }

    @Transactional(readOnly = true)
    public Produto buscarEntidadePorCodigo(String codigo) {
        return produtoRepository.findByCodigo(normalizarCodigo(codigo))
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado para o código informado."));
    }

    private void aplicarDados(Produto produto, ProdutoRequest request, String codigo) {
        Categoria categoria = categoriaService.buscarEntidade(request.categoriaId());
        Fornecedor fornecedor = request.fornecedorId() == null ? null : fornecedorService.buscarEntidade(request.fornecedorId());

        produto.setCodigo(codigo);
        produto.setDescricao(request.descricao().trim());
        produto.setPrecoVenda(request.precoVenda());
        produto.setQuantidadeAtual(request.quantidadeAtual());
        produto.setEstoqueMinimo(request.estoqueMinimo());
        produto.setCategoria(categoria);
        produto.setFornecedor(fornecedor);
    }

    private void sincronizarDisponibilidade(Produto produto, Boolean disponibilidadeSolicitada) {
        boolean desejaDisponivel = disponibilidadeSolicitada == null || disponibilidadeSolicitada;
        boolean possuiEstoque = produto.getQuantidadeAtual() != null && produto.getQuantidadeAtual() > 0;
        produto.setDisponivel(produto.isAtivo() && desejaDisponivel && possuiEstoque);
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }
}
