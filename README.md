# FinIA

Sistema de gestão financeira desenvolvido com Java e Spring Boot, focado em planejamento, capacidade de gastos, metas e análises inteligentes.

## Sobre o projeto

O FinIA ajuda o usuário a entender quanto da renda já está comprometido e quanto ainda pode gastar sem ignorar despesas planejadas, reserva financeira e metas.

O backend é responsável pelos cálculos determinísticos. A IA recebe esses resultados como contexto e os explica em linguagem natural, sem substituir o motor financeiro.

## Funcionalidades atuais

- Perfil financeiro com saldo atual informado pelo usuário
- Planejamento de renda e despesas
- Valor planejado para guardar
- Criação e gerenciamento de metas
- Cálculo do valor mensal necessário para cada meta
- Análise conjunta do comprometimento de todas as metas
- Capacidade de gasto mensal
- Limite de gasto imediato considerando saldo e planejamento
- Simulação de gastos sem alterar o banco
- Simulação de metas sem cadastrá-las
- Histórico opcional de transações
- Revisão mensal
- Sugestões financeiras determinísticas
- Contexto financeiro consolidado para IA
- Perguntas e análise geral usando OpenAI
- Integração com MySQL

## Regra principal do FinIA

Toda decisão financeira deve considerar a situação financeira atual do usuário.

O motor considera:

```text
Renda total
- despesas planejadas
- valor planejado para guardar
- comprometimento mensal das metas
= margem disponível para novos gastos
```

A capacidade de gasto imediato também é limitada pelo saldo atual disponível.

Por isso, possuir dinheiro no saldo não significa automaticamente que todo esse valor seja recomendado para gasto: o FinIA preserva os compromissos mensais e as metas antes de recomendar uma compra.

## Motor financeiro

O `MotorFinanceiroService` centraliza os principais cálculos do sistema e produz uma `CapacidadeFinanceira` com:

- renda total;
- despesas planejadas;
- reserva planejada;
- comprometimento mensal das metas;
- total de compromissos mensais;
- margem antes e depois das metas;
- capacidade de gasto mensal;
- capacidade de gasto imediato;
- percentual da renda comprometida;
- classificação e explicação da situação.

Classificações possíveis incluem situações como `SAUDAVEL`, `APERTADA`, `EQUILIBRADA`, `METAS_ACIMA_DA_CAPACIDADE`, `RESERVA_INVIAVEL` e `DESPESAS_ACIMA_DA_RENDA`.

## Endpoints do motor financeiro

### Situação financeira

`GET /analise/situacao`

### Capacidade de gastos

`GET /analise/capacidade-gastos`

### Simular gasto

`POST /analise/simular-gasto`

```json
{
  "valor": 500
}
```

A simulação não altera saldo e não cria transação.

### Simular meta

`POST /analise/simular-meta`

```json
{
  "nome": "Notebook",
  "valorAlvo": 5000,
  "valorInicial": 500,
  "prazoMeses": 10,
  "prioridade": "media",
  "descricao": "Notebook para estudos"
}
```

A simulação verifica a viabilidade usando as metas já cadastradas, mas não grava a nova meta.

## Integração com OpenAI

O backend utiliza o SDK oficial `openai-java` e a Responses API. A chave não fica no código nem no GitHub: ela deve ser fornecida através da variável de ambiente `OPENAI_API_KEY`.

Também é possível definir `OPENAI_MODEL`. O padrão atual do projeto é `gpt-5.6-luna`.

### Windows PowerShell

```powershell
$env:OPENAI_API_KEY="sua_chave_aqui"
$env:OPENAI_MODEL="gpt-5.6-luna"
```

### Windows CMD

```cmd
set OPENAI_API_KEY=sua_chave_aqui
set OPENAI_MODEL=gpt-5.6-luna
```

A variável deve existir no mesmo ambiente em que a aplicação Spring Boot for iniciada.

## Endpoints da IA

### Perguntar algo à IA

`POST /ia/perguntar`

```json
{
  "pergunta": "Posso comprar um celular de R$ 2.000 considerando minha situação atual?"
}
```

### Analisar uma situação específica

`POST /ia/analisar`

### Gerar análise automática

`GET /ia/analisar`

## Arquitetura

```text
Banco de dados
     ↓
MotorFinanceiroService
     ↓
Situação + capacidade de gasto + metas + histórico
     ↓
Regras determinísticas e simulações
     ↓
ContextoFinanceiroIAService
     ↓
OpenAIService
     ↓
Resposta em linguagem natural
```

O backend oficial do projeto é o `pom.xml` e a pasta `src/` da raiz. A pasta `backend/` permanece apenas como código legado e não deve ser usada para executar a versão atual.

## Tecnologias

- Java 21
- Spring Boot 3
- Maven
- MySQL
- JDBC
- BigDecimal
- React
- Vite
- OpenAI Java SDK
- OpenAI Responses API
- GitHub Actions

## Validação automática

O workflow `.github/workflows/ci.yml` verifica automaticamente a branch de desenvolvimento:

- compilação Maven do backend;
- lint do frontend;
- build do React/Vite.

## Segurança

Nunca faça commit de uma chave real da OpenAI.

O arquivo `.env.example` contém apenas valores de exemplo. Arquivos `.env` reais ficam ignorados pelo Git.
