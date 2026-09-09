# FinIA

Ferramenta de gestão financeira pessoal desenvolvida com Java e Spring Boot. O foco do FinIA é ajudar o usuário a entender sua situação atual, avaliar metas, testar decisões antes de tomá-las e receber explicações em linguagem natural.

## Ideia central

O FinIA separa duas responsabilidades:

```text
Motor financeiro -> calcula
FinIA / IA       -> explica
```

A IA não decide os valores financeiros e não substitui as regras do backend. Renda, gastos, reserva, metas, capacidade de gasto e projeções são calculados de forma determinística.

## Fluxo atual do produto

```text
Saldo atual
   +
Orçamento mensal
   +
Metas
   ↓
Motor financeiro
   ↓
Capacidade de gasto e viabilidade
   ↓
Simulações futuras
   ↓
FinIA explica os resultados
```

## Funcionalidades principais

- saldo atual informado manualmente;
- orçamento rápido;
- renda mensal e renda extra;
- gastos mensais estimados;
- valor planejado para guardar;
- capacidade de gasto mensal;
- limite de gasto imediato;
- classificação da situação financeira;
- criação, edição e acompanhamento de metas;
- análise conjunta de todas as metas;
- prazo mínimo viável e prazo confortável sugerido para metas;
- simulação de gastos sem alterar o banco;
- simulação de metas antes do cadastro;
- projeção financeira de 1 a 60 meses;
- cenários com mudança de renda, gastos e despesas extraordinárias;
- projeção da evolução das metas;
- aportes extras simulados em metas;
- histórico de transações opcional;
- explicações usando OpenAI;
- explicação de uma simulação pela IA apenas quando solicitada.

A antiga funcionalidade de revisão mensal permanece apenas como código legado de compatibilidade. O fluxo principal do projeto passa a usar `Simulações`.

## Motor financeiro

A regra principal é:

```text
Renda total
- gastos mensais
- valor planejado para guardar
- comprometimento mensal das metas
= margem disponível
```

A capacidade de gasto imediato ainda considera o saldo atual:

```text
pode gastar agora = menor valor entre saldo atual e capacidade mensal
```

Ter dinheiro em conta não significa automaticamente que todo esse valor esteja livre para gastar.

## Orçamento rápido

O frontend novo não precisa enviar um formulário grande de categorias.

### Ler orçamento

`GET /orcamento`

### Salvar ou substituir orçamento rápido

`PUT /orcamento`

```json
{
  "rendaMensal": 2500,
  "rendaExtra": 300,
  "gastosMensais": 1400,
  "valorPlanejadoGuardar": 400
}
```

O backend mantém compatibilidade com o perfil financeiro antigo, mas concentra o total dos gastos no orçamento rápido.

## Saldo atual

### Atualizar apenas o saldo

`PUT /perfil-financeiro/saldo`

```json
{
  "saldoAtual": 1200
}
```

Não é necessário reenviar todo o perfil para alterar o saldo.

## Capacidade de gastos

### Situação financeira

`GET /analise/situacao`

### Capacidade detalhada

`GET /analise/capacidade-gastos`

### Simular uma compra

`POST /analise/simular-gasto`

```json
{
  "valor": 1500
}
```

A chamada não altera saldo e não cria transação.

## Metas

### Criar

`POST /metas`

### Listar

`GET /metas`

### Buscar

`GET /metas/{id}`

### Analisar viabilidade

`GET /metas/{id}/viabilidade`

Além da classificação, a análise pode retornar:

- valor necessário por mês;
- margem disponível;
- percentual da margem exigido;
- prazo mínimo viável;
- prazo confortável sugerido.

### Simular uma meta antes de cadastrar

`POST /analise/simular-meta`

```json
{
  "nome": "Notebook",
  "valorAlvo": 5000,
  "valorInicial": 500,
  "prazoMeses": 10,
  "prioridade": "alta",
  "descricao": "Notebook para estudos"
}
```

### Editar meta

`PUT /metas/{id}`

### Atualizar somente o progresso

`PATCH /metas/{id}/progresso`

```json
{
  "valorAtual": 1800
}
```

### Excluir

`DELETE /metas/{id}`

## Simulações futuras

A simulação é totalmente isolada. Ela nunca altera saldo, orçamento ou metas reais.

### Projetar cenário

`POST /simulacoes`

Exemplo:

```json
{
  "nomeCenario": "Compra de celular e projeção de 6 meses",
  "meses": 6,
  "rendaMensal": 2500,
  "rendaExtraMensal": 300,
  "gastosMensais": 1400,
  "gastoExtraordinario": 1500,
  "mesGastoExtraordinario": 1,
  "aporteExtraMetasMensal": 100,
  "metaPrioritariaId": 1
}
```

Todos os campos financeiros do cenário são opcionais. Quando um valor não é enviado, o motor usa a situação financeira atual como ponto de partida.

A resposta contém:

- saldo inicial;
- saldo final projetado;
- variação do saldo;
- classificação final;
- evolução mês a mês;
- margem mensal de cada período;
- despesas extraordinárias;
- total reservado;
- total destinado às metas;
- progresso atual e projetado de cada meta;
- mês de conclusão quando uma meta for concluída dentro da simulação.

O período aceito é de 1 a 60 meses.

## IA

A integração com OpenAI é chamada apenas quando necessária. Dashboard, metas, capacidade de gasto e simulações funcionam sem gastar créditos de IA.

### Perguntar

`POST /ia/perguntar`

```json
{
  "pergunta": "Posso comprar um celular de R$ 1.500?"
}
```

### Análise geral

`GET /ia/analisar`

### Explicar uma simulação

`POST /ia/explicar-simulacao`

O corpo é o mesmo de `POST /simulacoes`. Primeiro o motor calcula todo o cenário; somente depois a IA recebe os resultados e os explica.

## OpenAI

A chave nunca deve ser salva no código.

Variáveis:

```text
OPENAI_API_KEY
OPENAI_MODEL
```

O modelo pode ser trocado por variável de ambiente sem alteração no código.

## Banco de dados

O projeto usa MySQL. As simulações futuras não precisam de novas tabelas porque são temporárias e não são persistidas.

O saldo atual já usa a coluna `saldo_atual` em `perfil_financeiro`.

## Backend oficial

Use o projeto da raiz:

```text
pom.xml
src/main/java/...
```

A pasta `backend/` é legado e não deve ser executada.

## Executar

```bash
mvn spring-boot:run
```

Servidor padrão:

```text
http://localhost:8080
```

## Tecnologias

- Java 21
- Spring Boot 3.2.5
- Maven
- MySQL
- JDBC
- BigDecimal
- OpenAI Java SDK
- OpenAI Responses API
- GitHub Actions

## Validação automática

A branch `dev/finia-desenvolvimento` possui CI automático para compilar o backend e validar o frontend existente no repositório.

## Regra de segurança do projeto

Nenhuma chave real da OpenAI deve ser commitada. Use variáveis de ambiente.
