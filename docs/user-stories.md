# Documentação das User Stories - Projeto Nexus

Este documento detalha as User Stories relacionadas aos módulos de Estoque e Vendas do Projeto Nexus, com base na análise do modelo de dados.

---

## Perfis de Usuário

| Perfil | Descrição |
| :--- | :--- |
| **Gerente de Produtos** | Responsável pelo cadastro, edição, exclusão e gestão da disponibilidade dos produtos. |
| **Operador de Almoxarifado** | Responsável pelo registro de entrada e saída de produtos, e ajustes de estoque. |
| **Vendedor** | Responsável pela criação de pedidos de venda, adição de itens e finalização de vendas. Também consulta produtos e sua disponibilidade. |
| **Auditor** | Responsável por verificar e ajustar quantidades de estoque, com justificativa. |
| **Sistema** | Atua em processos automatizados, como a atualização automática da disponibilidade de produtos. |

---

## Módulo de Gestão de Produtos

### US02 - Cadastrar Produto

**Como:** Um usuário do sistema (ex: gerente de produtos, operador de cadastro)
**Eu quero:** Cadastrar um novo produto no sistema
**Para que:** O produto possa ser gerenciado, vendido e ter seu estoque controlado.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`, `Categoria`, `Fornecedor`.
*   **Fluxo Esperado:**
    1.  O usuário acessa a tela de cadastro de produtos.
    2.  Preenche os campos obrigatórios: `codigo`, `descricao`, `precoVenda`, `estoqueMinimo`.
    3.  Seleciona uma `categoria` existente e, opcionalmente, um `fornecedor`.
    4.  O sistema cria um novo registro na tabela `produto`.
    5.  `quantidadeAtual` é inicializada como `0`.
    6.  `disponibilidade` e `ativo` são inicializados como `true`.
*   **Regras de Negócio:**
    *   `codigo` deve ser único e obrigatório.
    *   `descricao` e `precoVenda` são obrigatórios.
    *   `precoVenda` deve ser maior que zero.
    *   `estoqueMinimo` deve ser maior ou igual a zero.
    *   `id_categoria` é obrigatório.

---

### US03 - Editar Produto

**Como:** Um usuário do sistema (ex: gerente de produtos, operador de cadastro)
**Eu quero:** Editar as informações de um produto existente
**Para que:** Manter os dados do produto atualizados no sistema.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`, `Categoria`, `Fornecedor`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `Produto` existente para edição.
    2.  Os campos do formulário são pré-preenchidos com os dados atuais do produto.
    3.  O usuário altera as informações desejadas (ex: `descricao`, `precoVenda`, `categoria`, `fornecedor`).
    4.  O sistema atualiza o registro correspondente na tabela `produto`.
*   **Regras de Negócio:**
    *   O `id` do produto é usado para identificar o registro a ser atualizado.
    *   As mesmas regras de validação de `US02 - Cadastrar Produto` se aplicam aos campos editados.
    *   O `codigo` do produto geralmente não é editável após o cadastro inicial.

---

### US04 - Excluir Produto

**Como:** Um usuário do sistema (ex: gerente de produtos)
**Eu quero:** Excluir um produto do sistema
**Para que:** Remover produtos obsoletos ou incorretos do catálogo.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `Produto` para exclusão.
    2.  O sistema solicita uma confirmação da exclusão.
    3.  O campo `ativo` do `Produto` é alterado para `false` (exclusão lógica).
    4.  Alternativamente, o registro pode ser removido fisicamente da tabela `produto` (exclusão física), mas a exclusão lógica é preferível para manter histórico e integridade referencial.
*   **Regras de Negócio:**
    *   Um produto não pode ser excluído fisicamente se houver referências a ele em outras tabelas (ex: `MovimentacaoEstoque`, `ItemPedido`). A exclusão lógica (`ativo = false`) é a abordagem mais segura.
    *   O usuário deve ter permissão para realizar a exclusão.

---

### US05 - Alterar Disponibilidade

**Como:** Um usuário do sistema (ex: gerente de produtos, vendedor)
**Eu quero:** Alterar o status de disponibilidade de um produto
**Para que:** Indicar se um produto está ou não disponível para venda, independentemente do estoque físico.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `Produto`.
    2.  Altera o valor do campo `disponibilidade` (true/false).
    3.  O sistema atualiza o registro do `Produto` na tabela `produto`.
*   **Regras de Negócio:**
    *   A alteração da disponibilidade pode ser manual, permitindo que um produto com estoque seja marcado como indisponível (ex: para manutenção) ou um produto sem estoque seja marcado como disponível (ex: pré-venda).

---

### US06 - Consultar Produto

**Como:** Um usuário do sistema (ex: vendedor, operador de estoque)
**Eu quero:** Consultar os detalhes de um produto específico
**Para que:** Obter informações completas sobre o produto, como descrição, preço, estoque atual e fornecedor.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`, `Categoria`, `Fornecedor`.
*   **Fluxo Esperado:**
    1.  O usuário informa um critério de busca (ex: ID, código, parte da descrição).
    2.  O sistema busca o `Produto` correspondente na tabela `produto`.
    3.  Exibe todos os detalhes do produto, incluindo `codigo`, `descricao`, `precoVenda`, `quantidadeAtual`, `estoqueMinimo`, `disponibilidade`, `ativo`, e informações da `categoria` e `fornecedor` associados.
*   **Regras de Negócio:**
    *   A consulta deve ser eficiente e retornar resultados precisos.
    *   Pode haver diferentes critérios de busca (por ID, por código, por descrição).

---

### US07 - Pesquisar por Código

**Como:** Um usuário do sistema (ex: vendedor, operador de estoque)
**Eu quero:** Pesquisar um produto rapidamente usando seu código único
**Para que:** Agilizar a localização de produtos em operações como venda ou movimentação de estoque.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`.
*   **Fluxo Esperado:**
    1.  O usuário informa o `codigo` do produto.
    2.  O sistema realiza uma busca direta na tabela `produto` utilizando o campo `codigo`.
    3.  Exibe os detalhes do `Produto` encontrado.
*   **Regras de Negócio:**
    *   A busca por `codigo` deve ser exata e rápida, aproveitando o índice `idx_produto_codigo`.
    *   Se nenhum produto for encontrado com o código informado, o sistema deve notificar o usuário.

---

## Módulo de Estoque

### US08 - Registrar Entrada Estoque

**Como:** Um usuário do sistema (ex: Operador de Almoxarifado, gerente de estoque)
**Eu quero:** Registrar a entrada de produtos no estoque
**Para que:** O sistema reflita o aumento da quantidade disponível e mantenha um histórico das movimentações.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`, `MovimentacaoEstoque`, `Usuario`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `Produto` existente.
    2.  Informa a `quantidade` de entrada (deve ser um valor positivo).
    3.  O sistema cria um novo registro na tabela `movimentacao_estoque`.
    4.  O campo `tipo` da `MovimentacaoEstoque` é definido como `ENTRADA` (conforme `TipoMovimentacao.java`).
    5.  A `quantidade_atual` do `Produto` correspondente é **incrementada** pela quantidade informada.
    6.  O `id_usuario` do usuário logado é associado à movimentação.
    7.  A `data_hora` da movimentação é registrada automaticamente.
*   **Regras de Negócio:**
    *   A quantidade de entrada deve ser maior que zero.
    *   Um produto deve existir para que a entrada seja registrada.
    *   O usuário deve ter permissão para realizar a operação.

---

### US09 - Registrar Saída Estoque

**Como:** Um usuário do sistema (ex: vendedor, Operador de Almoxarifado)
**Eu quero:** Registrar a saída de produtos do estoque
**Para que:** O sistema reflita a diminuição da quantidade disponível e mantenha um histórico das movimentações.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`, `MovimentacaoEstoque`, `Usuario`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `Produto` existente.
    2.  Informa a `quantidade` de saída (deve ser um valor positivo).
    3.  O sistema cria um novo registro na tabela `movimentacao_estoque`.
    4.  O campo `tipo` da `MovimentacaoEstoque` é definido como `SAIDA` (conforme `TipoMovimentacao.java`).
    5.  A `quantidade_atual` do `Produto` correspondente é **decrementada** pela quantidade informada.
    6.  O `id_usuario` do usuário logado é associado à movimentação.
    7.  A `data_hora` da movimentação é registrada automaticamente.
*   **Regras de Negócio:**
    *   A quantidade de saída deve ser maior que zero.
    *   Não deve ser permitida a saída de uma quantidade maior do que a `quantidade_atual` disponível em estoque (evitar estoque negativo).
    *   Um produto deve existir para que a saída seja registrada.
    *   O usuário deve ter permissão para realizar a operação.

---

### US10 - Ajustar Quantidade

**Como:** Um usuário do sistema (ex: gerente de estoque, auditor)
**Eu quero:** Ajustar manualmente a quantidade de um produto no estoque, fornecendo uma justificativa
**Para que:** Corrigir discrepâncias de estoque (perdas, ganhos não registrados) e manter um registro auditável das alterações.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`, `HistoricoAjusteEstoque`, `Usuario`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `Produto` existente.
    2.  Informa a `quantidade_nova` desejada para o produto.
    3.  Fornece uma `justificativa` obrigatória para o ajuste.
    4.  O sistema registra a `quantidade_anterior` do produto.
    5.  A `quantidade_atual` do `Produto` é **atualizada** para a `quantidade_nova` informada.
    6.  Um novo registro é criado na tabela `historico_ajuste_estoque`, contendo o `id_produto`, `id_usuario`, `quantidade_anterior`, `quantidade_nova`, `justificativa` e `data_hora` do ajuste.
*   **Regras de Negócio:**
    *   A `justificativa` para o ajuste é obrigatória.
    *   A `quantidade_nova` deve ser um valor não negativo.
    *   Um produto deve existir para que o ajuste seja realizado.
    *   O usuário deve ter permissão específica para realizar ajustes de estoque.
    *   A `MovimentacaoEstoque` também pode registrar um `tipo` de `AJUSTE` para consolidar todas as movimentações em um único histórico, além do `HistoricoAjusteEstoque` para detalhes específicos de auditoria.

---

## Módulo de Vendas

### US11 - Criar Pedido de Venda

**Como:** Um usuário do sistema (ex: vendedor)
**Eu quero:** Criar um novo pedido de venda
**Para que:** Registrar a intenção de compra de um cliente e iniciar o processo de venda.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `PedidoVenda`, `Usuario`.
*   **Fluxo Esperado:**
    1.  O usuário inicia a criação de um novo pedido.
    2.  O sistema gera um `numero` único para o `PedidoVenda`.
    3.  O `id_usuario` do vendedor logado é associado ao pedido.
    4.  O `status` do `PedidoVenda` é definido como `ABERTO` (conforme `StatusPedido.java`).
    5.  A `data_abertura` é registrada automaticamente.
    6.  O `valor_total` é inicializado como `0` (zero) e será atualizado conforme itens são adicionados.
    7.  O sistema cria um novo registro na tabela `pedido_venda`.
*   **Regras de Negócio:**
    *   Um `numero` de pedido deve ser único.
    *   O `status` inicial deve ser `ABERTO`.
    *   O `id_usuario` é obrigatório.

---

### US12 - Adicionar Item ao Pedido

**Como:** Um usuário do sistema (ex: vendedor)
**Eu quero:** Adicionar produtos a um pedido de venda existente
**Para que:** Detalhar os produtos e quantidades que o cliente deseja comprar.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `PedidoVenda`, `ItemPedido`, `Produto`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `PedidoVenda` com `status` `ABERTO`.
    2.  Seleciona um `Produto` para adicionar ao pedido.
    3.  Informa a `quantidade` desejada do produto.
    4.  O sistema verifica a `quantidade_atual` do `Produto` em estoque para garantir disponibilidade.
    5.  Um novo registro é criado na tabela `item_pedido`.
    6.  O `id_pedido` e `id_produto` são associados.
    7.  A `quantidade` do item é registrada.
    8.  O `preco_unitario` do `Produto` no momento da adição é registrado no `ItemPedido`.
    9.  Qualquer `desconto_aplicado` (se houver lógica de promoção) é registrado.
    10. O `subtotal` do item (`quantidade * preco_unitario - desconto_aplicado`) é calculado e registrado.
    11. O `valor_total` do `PedidoVenda` é **atualizado** com o `subtotal` do novo item.
*   **Regras de Negócio:**
    *   O pedido deve estar com `status` `ABERTO` para que itens possam ser adicionados.
    *   A `quantidade` do item deve ser maior que zero.
    *   A `quantidade` solicitada não pode exceder a `quantidade_atual` disponível em estoque.
    *   O `preco_unitario` e `subtotal` são obrigatórios.

---

### US13 - Finalizar Venda

**Como:** Um usuário do sistema (ex: vendedor)
**Eu quero:** Finalizar um pedido de venda
**Para que:** Confirmar a transação, atualizar o estoque e registrar a venda como concluída.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `PedidoVenda`, `ItemPedido`, `Produto`, `MovimentacaoEstoque`, `Usuario`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona um `PedidoVenda` com `status` `ABERTO`.
    2.  Confirma a finalização da venda.
    3.  O `status` do `PedidoVenda` é alterado para `FINALIZADO` (conforme `StatusPedido.java`).
    4.  A `data_finalizacao` é registrada.
    5.  Para cada `ItemPedido` associado:
        *   A `quantidade_atual` do `Produto` correspondente é **decrementada** pela `quantidade` do item.
        *   Um novo registro é criado na tabela `movimentacao_estoque` com `tipo` `SAIDA` e `observacao` referente ao `PedidoVenda`.
    6.  O `valor_total` do `PedidoVenda` é consolidado.
*   **Regras de Negócio:**
    *   O pedido deve estar com `status` `ABERTO` para ser finalizado.
    *   Todos os produtos no pedido devem ter `quantidade_atual` suficiente em estoque no momento da finalização.
    *   Após a finalização, o pedido não pode mais ser alterado (itens adicionados/removidos).
    *   O `id_usuario` é obrigatório para registrar a movimentação de estoque.

---

## Módulo de Disponibilidade

### US20 - Consultar Disponibilidade

**Como:** Um usuário do sistema (ex: vendedor, cliente)
**Eu quero:** Consultar a disponibilidade de um produto
**Para que:** Saber se o produto pode ser vendido ou se está em estoque baixo/indisponível.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`.
*   **Fluxo Esperado:**
    1.  O usuário seleciona ou pesquisa um `Produto`.
    2.  O sistema exibe o status de `disponibilidade` (`true`/`false`) do produto.
    3.  Opcionalmente, pode exibir a `quantidade_atual` e comparar com o `estoque_minimo` para indicar se o estoque está baixo (`possuiEstoqueBaixo()`).
*   **Regras de Negócio:**
    *   A `disponibilidade` é um indicador primário para venda.
    *   Produtos com `disponibilidade = false` não devem ser oferecidos para venda, mesmo que tenham `quantidade_atual > 0`.

---

### US22 - Disponibilidade Automática

**Como:** O sistema
**Eu quero:** Que a disponibilidade de um produto seja atualizada automaticamente
**Para que:** Refletir o status real do estoque sem intervenção manual constante.

**Detalhes Técnicos (Baseado no Modelo de Dados):**

*   **Entidades Envolvidas:** `Produto`.
*   **Fluxo Esperado:**
    1.  Quando a `quantidade_atual` de um `Produto` atinge `0` (zero) ou um valor pré-definido (ex: `estoque_minimo`), o sistema automaticamente define `disponibilidade` como `false`.
    2.  Quando a `quantidade_atual` de um `Produto` volta a ser maior que `0` (zero) ou um limite de reabastecimento, o sistema automaticamente define `disponibilidade` como `true`.
*   **Regras de Negócio:**
    *   A lógica de `possuiEstoqueBaixo()` na entidade `Produto` pode ser usada como gatilho para alertas ou para influenciar a `disponibilidade`.
    *   A `disponibilidade` automática deve ser configurável (ex: pode ser desativada para produtos específicos).
    *   Esta funcionalidade geralmente é implementada em uma camada de serviço ou por *triggers* no banco de dados, ou em um processo em *background* que monitora o estoque.
