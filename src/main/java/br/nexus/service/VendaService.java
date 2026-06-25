package br.nexus.service;

import br.nexus.dto.request.AdicionarItemPedidoRequest;
import br.nexus.dto.response.PedidoVendaResponse;
import br.nexus.exception.BusinessException;
import br.nexus.exception.ResourceNotFoundException;
import br.nexus.model.ItemPedido;
import br.nexus.model.MovimentacaoEstoque;
import br.nexus.model.PedidoVenda;
import br.nexus.model.Produto;
import br.nexus.model.StatusPedidoVenda;
import br.nexus.model.TipoMovimentacao;
import br.nexus.model.Usuario;
import br.nexus.repository.MovimentacaoEstoqueRepository;
import br.nexus.repository.PedidoVendaRepository;
import br.nexus.repository.ProdutoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VendaService {

    private final PedidoVendaRepository pedidoVendaRepository;
    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;
    private final PromocaoService promocaoService;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public VendaService(
            PedidoVendaRepository pedidoVendaRepository,
            ProdutoService produtoService,
            ProdutoRepository produtoRepository,
            UsuarioService usuarioService,
            PromocaoService promocaoService,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository
    ) {
        this.pedidoVendaRepository = pedidoVendaRepository;
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
        this.promocaoService = promocaoService;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    @Transactional
    public PedidoVendaResponse criarPedido() {
        PedidoVenda pedido = new PedidoVenda(gerarNumeroPedido(), usuarioAtual());
        return PedidoVendaResponse.from(pedidoVendaRepository.save(pedido));
    }

    @Transactional
    public PedidoVendaResponse adicionarItem(Long pedidoId, AdicionarItemPedidoRequest request) {
        PedidoVenda pedido = buscarEntidade(pedidoId);

        validarPedidoAberto(pedido);

        Produto produto = produtoService.buscarEntidade(request.produtoId());

        validarProdutoParaVenda(produto);
        validarEstoqueDisponivel(produto, request.quantidade(), pedido);

        BigDecimal descontoUnitario = promocaoService.calcularDescontoUnitario(produto);

        Optional<ItemPedido> itemExistente = pedido.getItens()
                .stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemPedido item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + request.quantidade());
            item.setPrecoUnitario(produto.getPrecoVenda());
            item.setDescontoAplicado(descontoUnitario);
            item.recalcularSubtotal();
        } else {
            ItemPedido novoItem = new ItemPedido(
                    produto,
                    request.quantidade(),
                    produto.getPrecoVenda(),
                    descontoUnitario
            );

            pedido.adicionarItem(novoItem);
        }

        recalcularTotalPedido(pedido);

        return PedidoVendaResponse.from(pedidoVendaRepository.save(pedido));
    }

    @Transactional
    public PedidoVendaResponse finalizarVenda(Long pedidoId) {
        PedidoVenda pedido = buscarEntidade(pedidoId);

        validarPedidoAberto(pedido);

        if (pedido.getItens().isEmpty()) {
            throw new BusinessException("Pedido sem itens não pode ser finalizado.");
        }

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();

            validarProdutoParaVenda(produto);

            if (produto.getQuantidadeAtual() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto " + produto.getCodigo() + ".");
            }

            BigDecimal descontoUnitario = promocaoService.calcularDescontoUnitario(produto);

            item.setPrecoUnitario(produto.getPrecoVenda());
            item.setDescontoAplicado(descontoUnitario);
            item.recalcularSubtotal();

            produto.setQuantidadeAtual(produto.getQuantidadeAtual() - item.getQuantidade());

            if (produto.getQuantidadeAtual() == 0) {
                produto.setDisponivel(false);
            }

            produtoRepository.save(produto);

            registrarMovimentacao(
                    produto,
                    TipoMovimentacao.VENDA,
                    item.getQuantidade(),
                    "Baixa automática da venda " + pedido.getNumero()
            );
        }

        recalcularTotalPedido(pedido);

        pedido.setStatus(StatusPedidoVenda.FINALIZADO);
        pedido.setDataFinalizacao(LocalDateTime.now());

        return PedidoVendaResponse.from(pedidoVendaRepository.save(pedido));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PedidoVendaResponse cancelarVenda(Long pedidoId) {
        PedidoVenda pedido = buscarEntidade(pedidoId);

        if (!pedido.estaFinalizado()) {
            throw new BusinessException("Somente vendas finalizadas podem ser canceladas.");
        }

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();

            produto.setQuantidadeAtual(produto.getQuantidadeAtual() + item.getQuantidade());

            if (produto.isAtivo() && produto.getQuantidadeAtual() > 0) {
                produto.setDisponivel(true);
            }

            produtoRepository.save(produto);

            registrarMovimentacao(
                    produto,
                    TipoMovimentacao.ESTORNO,
                    item.getQuantidade(),
                    "Estorno do cancelamento da venda " + pedido.getNumero()
            );
        }

        pedido.setStatus(StatusPedidoVenda.CANCELADO);

        return PedidoVendaResponse.from(pedidoVendaRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoVendaResponse> listarPedidos() {
        return pedidoVendaRepository.findAllByOrderByDataAberturaDesc()
                .stream()
                .map(PedidoVendaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoVendaResponse buscar(Long id) {
        return PedidoVendaResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public PedidoVenda buscarEntidade(Long id) {
        return pedidoVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de venda não encontrado."));
    }

    @Transactional(readOnly = true)
    public String gerarComprovante(Long pedidoId) {
        PedidoVenda pedido = buscarEntidade(pedidoId);

        if (!pedido.estaFinalizado() && !StatusPedidoVenda.CANCELADO.equals(pedido.getStatus())) {
            throw new BusinessException("Comprovante disponível apenas para venda finalizada ou cancelada.");
        }

        StringBuilder comprovante = new StringBuilder();

        comprovante.append("NEXUS - COMPROVANTE DE VENDA\n");
        comprovante.append("Pedido: ").append(pedido.getNumero()).append("\n");
        comprovante.append("Status: ").append(pedido.getStatus().name()).append("\n");
        comprovante.append("Data: ").append(pedido.getDataFinalizacao()).append("\n");
        comprovante.append("Atendente: ").append(pedido.getUsuario().getLogin()).append("\n\n");

        for (ItemPedido item : pedido.getItens()) {
            comprovante.append(item.getProduto().getCodigo())
                    .append(" - ")
                    .append(item.getProduto().getDescricao())
                    .append(" | Qtd: ")
                    .append(item.getQuantidade())
                    .append(" | Unitário: R$ ")
                    .append(item.getPrecoUnitario().subtract(item.getDescontoAplicado()))
                    .append(" | Subtotal: R$ ")
                    .append(item.getSubtotal())
                    .append("\n");
        }

        comprovante.append("\nTOTAL: R$ ").append(pedido.getValorTotal()).append("\n");

        return comprovante.toString();
    }

    private void validarPedidoAberto(PedidoVenda pedido) {
        if (!pedido.estaAberto()) {
            throw new BusinessException("Pedido não está aberto para alteração.");
        }
    }

    private void validarProdutoParaVenda(Produto produto) {
        if (!produto.isAtivo() || !produto.isDisponivel() || produto.getQuantidadeAtual() <= 0) {
            throw new BusinessException("Produto " + produto.getCodigo() + " está indisponível para venda.");
        }
    }

    private void validarEstoqueDisponivel(
            Produto produto,
            Integer quantidadeAdicional,
            PedidoVenda pedido
    ) {
        int quantidadeJaNoPedido = pedido.getItens()
                .stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .mapToInt(ItemPedido::getQuantidade)
                .sum();

        if (produto.getQuantidadeAtual() < quantidadeJaNoPedido + quantidadeAdicional) {
            throw new BusinessException(
                    "Estoque insuficiente para adicionar o produto " + produto.getCodigo() + " ao pedido."
            );
        }
    }

    private void recalcularTotalPedido(PedidoVenda pedido) {
        BigDecimal total = pedido.getItens()
                .stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(total);
    }

    private void registrarMovimentacao(
            Produto produto,
            TipoMovimentacao tipo,
            Integer quantidade,
            String observacao
    ) {
        movimentacaoEstoqueRepository.save(
                new MovimentacaoEstoque(
                        produto,
                        usuarioAtual(),
                        tipo,
                        quantidade,
                        observacao
                )
        );
    }

    private Usuario usuarioAtual() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.buscarPorLogin(login);
    }

    private String gerarNumeroPedido() {
        String numero;

        do {
            numero = "PED-"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (pedidoVendaRepository.existsByNumero(numero));

        return numero;
    }
}