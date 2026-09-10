export function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

export function formatarPercentual(valor) {
  return `${Number(valor || 0).toLocaleString("pt-BR", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 1,
  })}%`;
}

export function formatarMesReferencia(valor) {
  if (!/^\d{4}-\d{2}$/.test(String(valor || ""))) {
    return valor || "";
  }

  const [ano, mes] = String(valor).split("-").map(Number);
  const data = new Date(ano, mes - 1, 1);
  const texto = new Intl.DateTimeFormat("pt-BR", {
    month: "short",
    year: "2-digit",
  }).format(data);

  return texto.replace(" de ", "/").replace(".", "");
}

export function formatarClassificacao(valor) {
  const classificacoes = {
    SAUDAVEL: "Saudável",
    APERTADA: "Atenção",
    EQUILIBRADA: "Sem margem livre",
    SEM_RENDA: "Sem renda cadastrada",
    DEFICIT: "Saldo projetado negativo",
    DEFICIT_MENSAL: "Mês no negativo",
    GASTOS_ACIMA_DA_RENDA: "Gastos acima da renda",
    DESPESAS_ACIMA_DA_RENDA: "Gastos acima da renda",
    RESERVA_INVIAVEL: "Valor para guardar muito alto",
    METAS_ACIMA_DA_CAPACIDADE: "Metas acima da capacidade",
    APERTADA_POR_METAS: "Metas exigem atenção",
    VIAVEL: "Viável",
    VIAVEL_COM_ATENCAO: "Viável com atenção",
    INVIAVEL: "Inviável",
    CONCLUIDA: "Concluída",
    RECOMENDADO: "Cabe no orçamento",
    ATENCAO: "Cabe, mas exige cuidado",
    NAO_RECOMENDADO: "Não recomendado",
    SALDO_INSUFICIENTE: "Saldo insuficiente",
  };

  if (classificacoes[valor]) {
    return classificacoes[valor];
  }

  return String(valor || "Sem classificação")
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/^./, (letra) => letra.toUpperCase());
}

export function classificacaoEhProblema(valor) {
  return [
    "SEM_RENDA",
    "DEFICIT",
    "DEFICIT_MENSAL",
    "GASTOS_ACIMA_DA_RENDA",
    "DESPESAS_ACIMA_DA_RENDA",
    "RESERVA_INVIAVEL",
    "METAS_ACIMA_DA_CAPACIDADE",
    "INVIAVEL",
    "NAO_RECOMENDADO",
    "SALDO_INSUFICIENTE",
  ].includes(valor);
}

export function classificacaoEhAtencao(valor) {
  return [
    "APERTADA",
    "EQUILIBRADA",
    "APERTADA_POR_METAS",
    "VIAVEL_COM_ATENCAO",
    "ATENCAO",
  ].includes(valor);
}
