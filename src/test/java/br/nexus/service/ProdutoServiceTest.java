package service;

import br.nexus.dto.request.ProdutoRequest;
import br.nexus.exception.BusinessException;
import br.nexus.repository.CategoriaRepository;
import br.nexus.repository.FornecedorRepository;
import br.nexus.repository.MovimentacaoEstoqueRepository;
import br.nexus.repository.ProdutoRepository;
import br.nexus.repository.PromocaoRepository;
import br.nexus.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Mock
    private PromocaoRepository promocaoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveBloquearCadastroQuandoCodigoJaExiste() {
        ProdutoRequest request = new ProdutoRequest(
                "P001",
                "Mouse Gamer",
                new BigDecimal("100.00"),
                10,
                2,
                1L,
                null,
                true
        );

        when(produtoRepository.existsByCodigo("P001")).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> produtoService.criar(request)
        );
    }
}