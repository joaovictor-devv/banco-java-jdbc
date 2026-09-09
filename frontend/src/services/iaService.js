import api from "./api";

export function perguntarIA(pergunta) {
  return api.post("/ia/perguntar", { pergunta });
}

export function analisarIA() {
  return api.get("/ia/analisar");
}

export async function buscarSaudeFinanceira() {
  const response = await api.get("/analise/situacao");
  const dados = response.data;

  return {
    data: {
      status: dados.classificacao,
      mensagem: `Margem livre: ${formatarMoeda(dados.margemLivre)}. Saldo atual: ${formatarMoeda(dados.saldoAtual)}.`,
      dados,
    },
  };
}

export async function buscarSugestoes() {
  const response = await api.get("/analise/sugestoes");
  const itens = response.data || [];

  return {
    data: {
      recurso: "Sugestões Financeiras",
      mensagem: itens.join(" ") || "Nenhuma sugestão automática no momento.",
      itens,
    },
  };
}

export async function buscarResumoInteligente() {
  const response = await api.get("/analise/situacao");
  const dados = response.data;

  return {
    data: {
      recurso: "Resumo Financeiro",
      mensagem: `Situação ${dados.classificacao}. Renda total de ${formatarMoeda(dados.rendaTotal)}, despesas planejadas de ${formatarMoeda(dados.despesasPlanejadas)} e margem livre de ${formatarMoeda(dados.margemLivre)}.`,
      dados,
    },
  };
}

export async function buscarAnaliseGastos() {
  const response = await api.get("/analise/resumo");
  const dados = response.data;

  return {
    data: {
      recurso: "Resumo de Gastos",
      mensagem: `No histórico do mês foram registradas entradas de ${formatarMoeda(dados.totalEntradas)} e saídas de ${formatarMoeda(dados.totalSaidas)}.`,
      dados,
    },
  };
}

export async function buscarPrevisaoMetas() {
  const response = await api.get("/metas");
  const metas = response.data || [];

  return {
    data: {
      recurso: "Viabilidade de Metas",
      mensagem: metas.length
        ? `${metas.length} meta(s) cadastrada(s). O FinIA considera o comprometimento conjunto delas nas análises.`
        : "Nenhuma meta cadastrada no momento.",
      metas,
    },
  };
}

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}
