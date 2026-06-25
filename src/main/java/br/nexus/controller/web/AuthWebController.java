package br.nexus.controller.web;

import br.nexus.dto.request.CadastroUsuarioRequest;
import br.nexus.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    private final UsuarioService usuarioService;

    public AuthWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarUsuario(
            @RequestParam String nome,
            @RequestParam String login,
            @RequestParam String senha,
            @RequestParam String confirmacaoSenha,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.cadastrar(
                    new CadastroUsuarioRequest(
                            nome,
                            login,
                            senha,
                            confirmacaoSenha
                    )
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Cadastro realizado com sucesso. Faça login para acessar o sistema."
            );

            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("loginCadastro", login);

            return "redirect:/cadastro";
        }
    }
}