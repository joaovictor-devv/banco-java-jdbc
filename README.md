# FinIA

Sistema de gestão financeira desenvolvido com Java e Spring Boot, focado em controle de gastos, organização financeira, metas e análises inteligentes.

## Sobre o projeto

O FinIA auxilia no gerenciamento financeiro pessoal através de uma API REST organizada em camadas.

O sistema permite registrar transações, acompanhar saldo, criar metas, analisar a viabilidade financeira e utilizar IA para interpretar a situação financeira atual do usuário.

## Funcionalidades

- Cadastro de receitas e despesas
- Controle de saldo financeiro
- Histórico de transações
- Criação e gerenciamento de metas
- Análise de viabilidade das metas
- Análise de gastos antes da realização
- Sugestões financeiras determinísticas
- Contexto financeiro consolidado para IA
- Perguntas financeiras usando OpenAI
- Análise financeira automática usando IA
- Organização por categorias
- Validações de regras financeiras
- Tratamento global de exceções
- Integração com MySQL

## Regra principal do FinIA

Toda decisão financeira deve considerar a situação financeira atual do usuário.

O motor financeiro calcula os valores objetivos, como renda, despesas, saldo, margem livre e valor mensal necessário para uma meta. A IA recebe esses dados e interpreta os resultados em linguagem natural.

A IA não deve inventar valores nem substituir os cálculos do backend.

## Integração com OpenAI

O backend utiliza o SDK oficial `openai-java` e a Responses API. A chave não fica no código nem no GitHub: ela deve ser fornecida através da variável de ambiente `OPENAI_API_KEY`.

Também é possível definir `OPENAI_MODEL`. O padrão configurado no projeto é `gpt-5-mini`.

### Windows PowerShell

```powershell
$env:OPENAI_API_KEY="sua_chave_aqui"
$env:OPENAI_MODEL="gpt-5-mini"
```

Depois execute o backend normalmente pelo IntelliJ ou Maven.

### Windows CMD

```cmd
set OPENAI_API_KEY=sua_chave_aqui
set OPENAI_MODEL=gpt-5-mini
```

A variável deve existir no mesmo ambiente em que a aplicação Spring Boot for iniciada.

## Endpoints da IA

### Perguntar algo à IA

`POST /ia/perguntar`

Exemplo:

```json
{
  "pergunta": "Posso comprar um celular de R$ 2.000 considerando minha situação atual?"
}
```

### Analisar uma situação específica

`POST /ia/analisar`

```json
{
  "pergunta": "Essa meta de R$ 5.000 em 6 meses é realista?"
}
```

### Gerar análise automática

`GET /ia/analisar`

Esse endpoint solicita à IA uma análise geral da situação financeira atual, considerando os dados calculados pelo FinIA.

## Arquitetura

O projeto segue arquitetura em camadas:

- Controllers
- Services
- Repositories
- DTOs
- Models
- Configurações globais

Fluxo da IA:

```txt
Banco de dados
     ↓
Motor financeiro
     ↓
Situação financeira + saldo + despesas + metas + sugestões
     ↓
ContextoFinanceiroIAService
     ↓
OpenAIService
     ↓
OpenAI Responses API
     ↓
Resposta em português
```

## Tecnologias

- Java 21
- Spring Boot 3
- Maven
- MySQL
- JDBC
- BigDecimal
- OpenAI Java SDK
- OpenAI Responses API
- REST API

## Segurança

Nunca faça commit de uma chave real da OpenAI.

O arquivo `.env.example` contém apenas um exemplo. Arquivos `.env` reais estão ignorados pelo Git.
