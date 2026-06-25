package br.nexus.controller.web;

import br.nexus.dto.request.AdicionarItemPedidoRequest;
import br.nexus.dto.request.AjusteEstoqueRequest;
import br.nexus.dto.request.AlterarDisponibilidadeRequest;
import br.nexus.dto.request.CategoriaRequest;
import br.nexus.dto.request.FornecedorRequest;
import br.nexus.dto.request.MovimentacaoEstoqueRequest;
import br.nexus.dto.request.ProdutoRequest;
import br.nexus.dto.request.PromocaoRequest;
import br.nexus.model.TipoDesconto;
import br.nexus.service.CategoriaService;
import br.nexus.service.EstoqueService;
import br.nexus.service.FornecedorService;
import br.nexus.service.ProdutoService;
import br.nexus.service.PromocaoService;
import br.nexus.service.RelatorioService;
import br.nexus.service.VendaService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;
    private final FornecedorService fornecedorService;
    private final EstoqueService estoqueService;
    private final VendaService vendaService;
    private final PromocaoService promocaoService;
    private final RelatorioService relatorioService;

    public WebController(
            ProdutoService produtoService,
            CategoriaService categoriaService,
            FornecedorService fornecedorService,
            EstoqueService estoqueService,
            VendaService vendaService,
            PromocaoService promocaoService,
            RelatorioService relatorioService
    ) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        this.fornecedorService = fornecedorService;
        this.estoqueService = estoqueService;
        this.vendaService = vendaService;
        this.promocaoService = promocaoService;
        this.relatorioService = relatorioService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(required = false) String nome) {
        model.addAttribute("produtos", produtoService.consultar(nome));
        model.addAttribute("nomeBusca", nome == null ? "" : nome);
        return "pages/dashboard/index";
    }

    @GetMapping("/produtos")
    public String produtos(Model model, @RequestParam(required = false) String nome) {
        carregarDadosProdutos(model, nome);
        return "pages/produtos/index";
    }

    @PostMapping("/produtos/criar")
    public String criarProduto(
            @RequestParam String codigo,
            @RequestParam String descricao,
            @RequestParam BigDecimal precoVenda,
            @RequestParam Integer quantidadeAtual,
            @RequestParam Integer estoqueMinimo,
            @RequestParam Long categoriaId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(defaultValue = "true") Boolean disponivel,
            RedirectAttributes redirectAttributes
    ) {
        try {
            produtoService.criar(
                    new ProdutoRequest(
                            codigo,
                            descricao,
                            precoVenda,
                            quantidadeAtual,
                            estoqueMinimo,
                            categoriaId,
                            fornecedorId,
                            disponivel
                    )
            );
            sucesso(redirectAttributes, "Produto cadastrado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/produtos#lista-produtos";
    }

    @PostMapping("/produtos/{id}/editar")
    public String editarProduto(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestParam String descricao,
            @RequestParam BigDecimal precoVenda,
            @RequestParam Integer quantidadeAtual,
            @RequestParam Integer estoqueMinimo,
            @RequestParam Long categoriaId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(defaultValue = "true") Boolean disponivel,
            RedirectAttributes redirectAttributes
    ) {
        try {
            produtoService.editar(
                    id,
                    new ProdutoRequest(
                            codigo,
                            descricao,
                            precoVenda,
                            quantidadeAtual,
                            estoqueMinimo,
                            categoriaId,
                            fornecedorId,
                            disponivel
                    )
            );
            sucesso(redirectAttributes, "Produto atualizado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/produtos#lista-produtos";
    }

    @PostMapping("/produtos/{id}/excluir")
    public String excluirProduto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            produtoService.excluir(id);
            sucesso(redirectAttributes, "Produto excluído ou inativado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/produtos#lista-produtos";
    }

    @PostMapping("/produtos/{id}/disponibilidade")
    public String alterarDisponibilidade(
            @PathVariable Long id,
            @RequestParam Boolean disponivel,
            RedirectAttributes redirectAttributes
    ) {
        try {
            produtoService.alterarDisponibilidade(
                    id,
                    new AlterarDisponibilidadeRequest(disponivel)
            );
            sucesso(redirectAttributes, "Disponibilidade atualizada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/produtos#lista-produtos";
    }

    @GetMapping("/estoque")
    public String estoque(
            Model model,
            @RequestParam(required = false) Long produtoId
    ) {
        model.addAttribute("produtos", produtoService.consultar(null));
        model.addAttribute("produtoSelecionadoId", produtoId);

        if (produtoId != null) {
            model.addAttribute("movimentacoes", estoqueService.listarMovimentacoes(produtoId));
            model.addAttribute("historicos", estoqueService.listarHistoricoAjustes(produtoId));
        }

        return "pages/estoque/index";
    }

    @PostMapping("/estoque/entrada")
    public String registrarEntrada(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam(required = false) String observacao,
            RedirectAttributes redirectAttributes
    ) {
        try {
            estoqueService.registrarEntrada(
                    new MovimentacaoEstoqueRequest(produtoId, quantidade, observacao)
            );
            sucesso(redirectAttributes, "Entrada de estoque registrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/estoque?produtoId=" + produtoId;
    }

    @PostMapping("/estoque/saida")
    public String registrarSaida(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam(required = false) String observacao,
            RedirectAttributes redirectAttributes
    ) {
        try {
            estoqueService.registrarSaida(
                    new MovimentacaoEstoqueRequest(produtoId, quantidade, observacao)
            );
            sucesso(redirectAttributes, "Saída de estoque registrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/estoque?produtoId=" + produtoId;
    }

    @PostMapping("/estoque/ajuste")
    public String ajustarEstoque(
            @RequestParam Long produtoId,
            @RequestParam Integer novaQuantidade,
            @RequestParam String justificativa,
            RedirectAttributes redirectAttributes
    ) {
        try {
            estoqueService.ajustarQuantidade(
                    new AjusteEstoqueRequest(produtoId, novaQuantidade, justificativa)
            );
            sucesso(redirectAttributes, "Ajuste manual de estoque registrado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/estoque?produtoId=" + produtoId;
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivas());
        return "pages/categorias/index";
    }

    @PostMapping("/categorias/criar")
    public String criarCategoria(
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            RedirectAttributes redirectAttributes
    ) {
        try {
            categoriaService.criar(new CategoriaRequest(nome, descricao));
            sucesso(redirectAttributes, "Categoria cadastrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/categorias#lista-categorias";
    }

    @GetMapping("/fornecedores")
    public String fornecedores(Model model) {
        model.addAttribute("fornecedores", fornecedorService.listarAtivos());
        return "pages/fornecedores/index";
    }

    @PostMapping("/fornecedores/criar")
    public String criarFornecedor(
            @RequestParam String nome,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            fornecedorService.criar(new FornecedorRequest(nome, telefone, email));
            sucesso(redirectAttributes, "Fornecedor cadastrado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/fornecedores#lista-fornecedores";
    }

    @PostMapping("/fornecedores/{id}/editar")
    public String editarFornecedor(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            fornecedorService.editar(id, new FornecedorRequest(nome, telefone, email));
            sucesso(redirectAttributes, "Fornecedor atualizado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/fornecedores#lista-fornecedores";
    }

    @GetMapping("/vendas")
    public String vendas(Model model) {
        model.addAttribute("pedidos", vendaService.listarPedidos());
        model.addAttribute("produtos", produtoService.consultar(null));
        return "pages/vendas/index";
    }

    @PostMapping("/vendas/criar")
    public String criarPedido(RedirectAttributes redirectAttributes) {
        try {
            vendaService.criarPedido();
            sucesso(redirectAttributes, "Pedido de venda criado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/vendas";
    }

    @PostMapping("/vendas/{id}/itens")
    public String adicionarItemPedido(
            @PathVariable Long id,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            RedirectAttributes redirectAttributes
    ) {
        try {
            vendaService.adicionarItem(
                    id,
                    new AdicionarItemPedidoRequest(produtoId, quantidade)
            );
            sucesso(redirectAttributes, "Item adicionado ao pedido.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/vendas#pedido-" + id;
    }

    @PostMapping("/vendas/{id}/finalizar")
    public String finalizarVenda(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            vendaService.finalizarVenda(id);
            sucesso(redirectAttributes, "Venda finalizada com baixa automática de estoque.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/vendas#pedido-" + id;
    }

    @PostMapping("/vendas/{id}/cancelar")
    public String cancelarVenda(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            vendaService.cancelarVenda(id);
            sucesso(redirectAttributes, "Venda cancelada e estoque estornado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/vendas#pedido-" + id;
    }

    @GetMapping("/vendas/{id}/comprovante")
    public String comprovante(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("pedido", vendaService.buscar(id));
        model.addAttribute("comprovanteTexto", vendaService.gerarComprovante(id));
        return "pages/vendas/comprovante";
    }

    @GetMapping("/promocoes")
    public String promocoes(Model model) {
        model.addAttribute("promocoes", promocaoService.listar());
        model.addAttribute("produtos", produtoService.consultar(null));
        model.addAttribute("tiposDesconto", TipoDesconto.values());
        model.addAttribute("dataHoje", LocalDate.now());
        return "pages/promocoes/index";
    }

    @PostMapping("/promocoes/criar")
    public String criarPromocao(
            @RequestParam Long produtoId,
            @RequestParam TipoDesconto tipoDesconto,
            @RequestParam BigDecimal valorDesconto,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(defaultValue = "true") Boolean ativa,
            RedirectAttributes redirectAttributes
    ) {
        try {
            promocaoService.criar(
                    new PromocaoRequest(
                            produtoId,
                            tipoDesconto,
                            valorDesconto,
                            dataInicio,
                            dataFim,
                            ativa
                    )
            );
            sucesso(redirectAttributes, "Promoção cadastrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }

        return "redirect:/promocoes#lista-promocoes";
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model) {
        model.addAttribute("itens", relatorioService.gerarRelatorioEstoque());
        return "pages/relatorios/index";
    }

    @GetMapping("/relatorios/estoque.csv")
    public ResponseEntity<byte[]> exportarRelatorioEstoque() {
        byte[] conteudo = relatorioService
                .gerarCsvEstoque()
                .getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename("relatorio-estoque-nexus.csv")
                                .build()
                                .toString()
                )
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(conteudo);
    }

    private void carregarDadosProdutos(Model model, String nome) {
        model.addAttribute("produtos", produtoService.consultar(nome));
        model.addAttribute("categorias", categoriaService.listarAtivas());
        model.addAttribute("fornecedores", fornecedorService.listarAtivos());
        model.addAttribute("nomeBusca", nome == null ? "" : nome);
    }

    private void sucesso(RedirectAttributes redirectAttributes, String mensagem) {
        redirectAttributes.addFlashAttribute("sucesso", mensagem);
    }

    private void erro(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("erro", e.getMessage());
    }
}