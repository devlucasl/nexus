package br.nexus.controller.web;

import br.nexus.dto.request.AlterarPerfilUsuarioRequest;
import br.nexus.model.PerfilUsuario;
import br.nexus.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioWebController {

    private final UsuarioService usuarioService;

    public UsuarioWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("perfis", PerfilUsuario.values());
        return "pages/usuarios/index";
    }

    @PostMapping("/usuarios/{id}/perfil")
    public String alterarPerfil(
            @PathVariable Long id,
            @RequestParam PerfilUsuario perfil,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.alterarPerfil(
                    id,
                    new AlterarPerfilUsuarioRequest(perfil)
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Perfil do usuário atualizado com sucesso."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/usuarios";
    }
}