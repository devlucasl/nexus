package br.nexus.controller.web;

import br.nexus.dto.AjusteEstoqueRequest;
import br.nexus.dto.AlterarDisponibilidadeRequest;
import br.nexus.dto.CategoriaRequest;
import br.nexus.dto.FornecedorRequest;
import br.nexus.dto.MovimentacaoEstoqueRequest;
import br.nexus.dto.ProdutoRequest;
import br.nexus.service.CategoriaService;
import br.nexus.service.EstoqueService;
import br.nexus.service.FornecedorService;
import br.nexus.service.ProdutoService;
import java.math.BigDecimal;
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

    public WebController(
        ProdutoService produtoService,
        CategoriaService categoriaService,
        FornecedorService fornecedorService,
        EstoqueService estoqueService
    ) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        this.fornecedorService = fornecedorService;
        this.estoqueService = estoqueService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(required = false) String nome) {
        model.addAttribute("produtos", produtoService.consultar(nome));
        model.addAttribute("nomeBusca", nome == null ? "" : nome);
        return "dashboard";
    }

    @GetMapping("/produtos")
    public String produtos(Model model, @RequestParam(required = false) String nome) {
        carregarDadosProdutos(model, nome);
        return "produtos";
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
            produtoService.criar(new ProdutoRequest(codigo, descricao, precoVenda, quantidadeAtual, estoqueMinimo, categoriaId, fornecedorId, disponivel));
            sucesso(redirectAttributes, "Produto cadastrado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/produtos";
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
            produtoService.editar(id, new ProdutoRequest(codigo, descricao, precoVenda, quantidadeAtual, estoqueMinimo, categoriaId, fornecedorId, disponivel));
            sucesso(redirectAttributes, "Produto atualizado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/produtos";
    }

    @PostMapping("/produtos/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.excluir(id);
            sucesso(redirectAttributes, "Produto excluído ou inativado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/produtos";
    }

    @PostMapping("/produtos/{id}/disponibilidade")
    public String alterarDisponibilidade(@PathVariable Long id, @RequestParam Boolean disponivel, RedirectAttributes redirectAttributes) {
        try {
            produtoService.alterarDisponibilidade(id, new AlterarDisponibilidadeRequest(disponivel));
            sucesso(redirectAttributes, "Disponibilidade atualizada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/produtos";
    }

    @GetMapping("/estoque")
    public String estoque(Model model, @RequestParam(required = false) Long produtoId) {
        model.addAttribute("produtos", produtoService.consultar(null));
        model.addAttribute("produtoSelecionadoId", produtoId);
        if (produtoId != null) {
            model.addAttribute("movimentacoes", estoqueService.listarMovimentacoes(produtoId));
            model.addAttribute("historicos", estoqueService.listarHistoricoAjustes(produtoId));
        }
        return "estoque";
    }

    @PostMapping("/estoque/entrada")
    public String registrarEntrada(@RequestParam Long produtoId, @RequestParam Integer quantidade, @RequestParam(required = false) String observacao, RedirectAttributes redirectAttributes) {
        try {
            estoqueService.registrarEntrada(new MovimentacaoEstoqueRequest(produtoId, quantidade, observacao));
            sucesso(redirectAttributes, "Entrada de estoque registrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/estoque?produtoId=" + produtoId;
    }

    @PostMapping("/estoque/saida")
    public String registrarSaida(@RequestParam Long produtoId, @RequestParam Integer quantidade, @RequestParam(required = false) String observacao, RedirectAttributes redirectAttributes) {
        try {
            estoqueService.registrarSaida(new MovimentacaoEstoqueRequest(produtoId, quantidade, observacao));
            sucesso(redirectAttributes, "Saída de estoque registrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/estoque?produtoId=" + produtoId;
    }

    @PostMapping("/estoque/ajuste")
    public String ajustarEstoque(@RequestParam Long produtoId, @RequestParam Integer novaQuantidade, @RequestParam String justificativa, RedirectAttributes redirectAttributes) {
        try {
            estoqueService.ajustarQuantidade(new AjusteEstoqueRequest(produtoId, novaQuantidade, justificativa));
            sucesso(redirectAttributes, "Ajuste manual de estoque registrado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/estoque?produtoId=" + produtoId;
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivas());
        return "categorias";
    }

    @PostMapping("/categorias/criar")
    public String criarCategoria(@RequestParam String nome, @RequestParam(required = false) String descricao, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.criar(new CategoriaRequest(nome, descricao));
            sucesso(redirectAttributes, "Categoria cadastrada com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/categorias";
    }

    @GetMapping("/fornecedores")
    public String fornecedores(Model model) {
        model.addAttribute("fornecedores", fornecedorService.listarAtivos());
        return "fornecedores";
    }

    @PostMapping("/fornecedores/criar")
    public String criarFornecedor(@RequestParam String nome, @RequestParam(required = false) String telefone, @RequestParam(required = false) String email, RedirectAttributes redirectAttributes) {
        try {
            fornecedorService.criar(new FornecedorRequest(nome, telefone, email));
            sucesso(redirectAttributes, "Fornecedor cadastrado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/fornecedores";
    }

    @PostMapping("/fornecedores/{id}/editar")
    public String editarFornecedor(@PathVariable Long id, @RequestParam String nome, @RequestParam(required = false) String telefone, @RequestParam(required = false) String email, RedirectAttributes redirectAttributes) {
        try {
            fornecedorService.editar(id, new FornecedorRequest(nome, telefone, email));
            sucesso(redirectAttributes, "Fornecedor atualizado com sucesso.");
        } catch (Exception e) {
            erro(redirectAttributes, e);
        }
        return "redirect:/fornecedores";
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
