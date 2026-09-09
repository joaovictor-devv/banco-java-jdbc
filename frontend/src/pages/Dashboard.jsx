import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";

function Dashboard() {
  const [situacao, setSituacao] = useState(null);
  const [capacidade, setCapacidade] = useState(null);
  const [sugestoes, setSugestoes] = useState([]);
  const [resumo, setResumo] = useState(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [valorSimulacao, setValorSimulacao] = useState("");
  const [resultadoSimulacao, setResultadoSimulacao] = useState(null);
  const [simulando, setSimulando] = useState(false);

  useEffect(() => {
    async function carregar() {
      setCarregando(true);
      setErro("");

      try {
        const [situacaoResponse, capacidadeResponse, sugestoesResponse, resumoResponse] =
          await Promise.all([
            api.get("/analise/situacao"),
            api.get("/analise/capacidade-gastos"),
            api.get("/analise/sugestoes"),
            api.get("/analise/resumo"),
          ]);

        setSituacao(situacaoResponse.data);
        setCapacidade(capacidadeResponse.data);
        setSugestoes(sugestoesResponse.data || []);
        setResumo(resumoResponse.data);
      } catch (error) {
        setErro(
          error.response?.data?.mensagem ||
            "Não foi possível carregar o resumo financeiro. Cadastre seu planejamento e confira o backend."
        );
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, []);

  async function simularGasto(event) {
    event.preventDefault();
    setErro("");
    setResultadoSimulacao(null);

    const valor = Number(valorSimulacao);
    if (!valor || valor <= 0) {
      setErro("Informe um valor maior que zero para simular o gasto.");
      return;
    }

    setSimulando(true);

    try {
      const response = await api.post("/analise/simular-gasto", { valor });
      setResultadoSimulacao(response.data);
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Não foi possível simular o gasto.");
    } finally {
      setSimulando(false);
    }
  }

  if (carregando) {
    return (
      <main className="min-h-screen bg-slate-50 px-6 py-8 md:px-12">
        <p className="text-slate-500">Carregando situação financeira...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-8 md:px-12">
      <section className="mb-10 flex flex-col gap-5 md:flex-row md:items-start md:justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Visão Geral</h1>
          <p className="mt-2 text-lg text-slate-500">
            Resumo calculado pelo motor financeiro do FinIA.
          </p>
        </div>
        <Link
          to="/insights"
          className="inline-flex items-center justify-center gap-2 rounded-xl bg-slate-900 px-5 py-3 font-semibold text-white hover:bg-slate-800"
        >
          <span className="material-symbols-outlined">psychology</span>
          Conversar com a FinIA
        </Link>
      </section>

      {erro && (
        <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          {erro}
        </div>
      )}

      {!situacao || !capacidade ? (
        <section className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
          <h2 className="text-xl font-semibold text-slate-900">
            Planejamento necessário
          </h2>
          <p className="mt-2 text-slate-500">
            Cadastre suas informações financeiras para liberar as análises.
          </p>
          <Link
            to="/planejamento"
            className="mt-5 inline-flex rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white"
          >
            Cadastrar planejamento
          </Link>
        </section>
      ) : (
        <>
          <section className="mb-8 grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-4">
            <Card
              titulo="Saldo atual"
              valor={formatarMoeda(capacidade.saldoAtual)}
              descricao="Dinheiro disponível informado no perfil."
              destaque
            />
            <Card
              titulo="Renda total"
              valor={formatarMoeda(capacidade.rendaTotal)}
              descricao="Renda mensal somada com a renda extra."
            />
            <Card
              titulo="Pode gastar no mês"
              valor={formatarMoeda(capacidade.capacidadeGastoMensal)}
              descricao="O que sobra depois de despesas, reserva e metas."
              negativo={Number(capacidade.margemAposMetas) < 0}
            />
            <Card
              titulo="Pode gastar agora"
              valor={formatarMoeda(capacidade.capacidadeGastoImediato)}
              descricao="Limite considerando a margem mensal e o saldo disponível."
              destaque
            />
          </section>

          <section className="mb-8 grid grid-cols-1 gap-6 lg:grid-cols-3">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm lg:col-span-2">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm text-slate-500">Classificação financeira</p>
                  <h2 className="mt-1 text-2xl font-bold text-slate-900">
                    {formatarClassificacao(capacidade.classificacao)}
                  </h2>
                </div>
                <span className={badgeClass(capacidade.classificacao)}>
                  {capacidade.classificacao}
                </span>
              </div>

              <p className="mt-4 text-sm leading-relaxed text-slate-600">
                {capacidade.mensagem}
              </p>

              <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
                <Info
                  label="Despesas planejadas"
                  value={formatarMoeda(capacidade.despesasPlanejadas)}
                />
                <Info
                  label="Reserva planejada"
                  value={formatarMoeda(capacidade.reservaPlanejada)}
                />
                <Info
                  label="Metas por mês"
                  value={formatarMoeda(capacidade.comprometimentoMensalMetas)}
                />
                <Info
                  label="Renda comprometida"
                  value={`${Number(capacidade.percentualRendaComprometida || 0).toFixed(2)}%`}
                />
              </div>
            </div>

            <div className="rounded-2xl border border-blue-100 bg-blue-50 p-6">
              <h2 className="text-lg font-semibold text-slate-900">
                Histórico opcional do mês
              </h2>
              <p className="mt-2 text-sm leading-relaxed text-slate-600">
                As transações servem como histórico, mas não são obrigatórias para informar seu saldo atual.
              </p>
              <div className="mt-5 space-y-3 text-sm">
                <Linha label="Entradas" value={formatarMoeda(resumo?.totalEntradas)} />
                <Linha label="Saídas" value={formatarMoeda(resumo?.totalSaidas)} />
                <Linha label="Categoria principal" value={resumo?.categoriaMaiorGasto || "Sem dados"} />
              </div>
            </div>
          </section>

          <section className="mb-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h2 className="text-xl font-semibold text-slate-900">Simular um gasto</h2>
              <p className="mt-2 text-sm text-slate-500">
                Teste uma compra antes de realizá-la. A simulação não altera seu saldo e não salva transações.
              </p>

              <form onSubmit={simularGasto} className="mt-5 flex flex-col gap-3 sm:flex-row">
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={valorSimulacao}
                  onChange={(event) => setValorSimulacao(event.target.value)}
                  placeholder="Ex: 500"
                  className="flex-1 rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-blue-500"
                />
                <button
                  type="submit"
                  disabled={simulando}
                  className="rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                >
                  {simulando ? "Simulando..." : "Simular"}
                </button>
              </form>

              {resultadoSimulacao && (
                <div className="mt-5 rounded-xl bg-slate-50 p-5">
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <span className={badgeAnaliseGasto(resultadoSimulacao.classificacao)}>
                      {formatarClassificacao(resultadoSimulacao.classificacao)}
                    </span>
                    <strong className="text-slate-900">
                      {resultadoSimulacao.recomendado ? "Recomendado" : "Não recomendado"}
                    </strong>
                  </div>
                  <p className="mt-4 text-sm leading-relaxed text-slate-600">
                    {resultadoSimulacao.mensagem}
                  </p>
                  <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
                    <Info
                      label="Valor simulado"
                      value={formatarMoeda(resultadoSimulacao.valorGasto)}
                    />
                    <Info
                      label="Margem após o gasto"
                      value={formatarMoeda(resultadoSimulacao.margemLivreAposGasto)}
                    />
                  </div>
                </div>
              )}
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h2 className="text-xl font-semibold text-slate-900">
                Recomendações do motor financeiro
              </h2>
              <div className="mt-5 space-y-3">
                {sugestoes.length ? (
                  sugestoes.map((sugestao, index) => (
                    <div
                      key={`${index}-${sugestao}`}
                      className="rounded-xl bg-slate-50 p-4 text-sm leading-relaxed text-slate-600"
                    >
                      {sugestao}
                    </div>
                  ))
                ) : (
                  <p className="text-sm text-slate-500">Nenhuma recomendação no momento.</p>
                )}
              </div>
            </div>
          </section>

          <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900">Próximos passos</h2>
            <div className="mt-5 grid gap-3 md:grid-cols-2 xl:grid-cols-4">
              <Atalho to="/perfil" titulo="Atualizar saldo" descricao="Informe quanto você possui agora." />
              <Atalho to="/metas" titulo="Revisar metas" descricao="Veja a viabilidade conjunta dos objetivos." />
              <Atalho to="/revisao-mensal" titulo="Revisão mensal" descricao="Adicione contexto sobre acontecimentos do mês." />
              <Atalho to="/insights" titulo="Perguntar à FinIA" descricao="Receba uma interpretação em linguagem natural." />
            </div>
          </section>
        </>
      )}
    </main>
  );
}

function Card({ titulo, valor, descricao, destaque = false, negativo = false }) {
  const cor = negativo ? "text-red-600" : destaque ? "text-blue-600" : "text-slate-900";

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <p className="text-sm font-medium text-slate-500">{titulo}</p>
      <p className={`mt-3 text-2xl font-bold ${cor}`}>{valor}</p>
      <p className="mt-2 text-sm leading-relaxed text-slate-500">{descricao}</p>
    </div>
  );
}

function Info({ label, value }) {
  return (
    <div className="rounded-xl bg-slate-50 p-4">
      <p className="text-xs text-slate-400">{label}</p>
      <p className="mt-1 font-semibold text-slate-800">{value}</p>
    </div>
  );
}

function Linha({ label, value }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <span className="text-slate-500">{label}</span>
      <span className="text-right font-semibold text-slate-800">{value}</span>
    </div>
  );
}

function Atalho({ to, titulo, descricao }) {
  return (
    <Link to={to} className="rounded-xl border border-slate-200 p-4 transition hover:border-blue-300 hover:bg-blue-50">
      <p className="font-semibold text-slate-900">{titulo}</p>
      <p className="mt-1 text-sm text-slate-500">{descricao}</p>
    </Link>
  );
}

function badgeClass(classificacao) {
  const ruim = ["SEM_RENDA", "DEFICIT", "DESPESAS_ACIMA_DA_RENDA", "RESERVA_INVIAVEL", "METAS_ACIMA_DA_CAPACIDADE"];
  const atencao = ["EQUILIBRADA", "APERTADA", "APERTADA_POR_METAS"];

  if (ruim.includes(classificacao)) {
    return "rounded-full bg-red-50 px-3 py-1 text-xs font-semibold text-red-700";
  }
  if (atencao.includes(classificacao)) {
    return "rounded-full bg-amber-50 px-3 py-1 text-xs font-semibold text-amber-700";
  }
  return "rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700";
}

function badgeAnaliseGasto(classificacao) {
  if (["SALDO_INSUFICIENTE", "NAO_RECOMENDADO"].includes(classificacao)) {
    return "rounded-full bg-red-50 px-3 py-1 text-xs font-semibold text-red-700";
  }
  if (classificacao === "ATENCAO") {
    return "rounded-full bg-amber-50 px-3 py-1 text-xs font-semibold text-amber-700";
  }
  return "rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700";
}

function formatarClassificacao(valor) {
  return String(valor || "Sem classificação")
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/^./, (letra) => letra.toUpperCase());
}

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

export default Dashboard;
