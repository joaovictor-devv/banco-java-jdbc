import { useCallback, useEffect, useState } from "react";
import {
  analisarViabilidadeMeta,
  cadastrarMeta,
  excluirMeta,
  listarMetas,
  simularMeta,
} from "../services/metaService";

function Metas() {
  const [metas, setMetas] = useState([]);
  const [analises, setAnalises] = useState({});
  const [simulacao, setSimulacao] = useState(null);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [simulando, setSimulando] = useState(false);

  const [novaMeta, setNovaMeta] = useState({
    nome: "",
    valorAlvo: "",
    prazoMeses: "",
    valorInicial: "",
    prioridade: "media",
    descricao: "",
  });

  const carregarMetas = useCallback(async () => {
    try {
      const response = await listarMetas();
      const lista = response.data || [];

      const pares = await Promise.all(
        lista.map(async (meta) => {
          try {
            const analise = await analisarViabilidadeMeta(meta.id);
            return [meta.id, analise.data];
          } catch {
            return [meta.id, null];
          }
        })
      );

      setMetas(lista);
      setAnalises(Object.fromEntries(pares));
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Erro ao carregar metas.");
    }
  }, []);

  useEffect(() => {
    let ativo = true;

    listarMetas()
      .then(async (response) => {
        const lista = response.data || [];
        const pares = await Promise.all(
          lista.map(async (meta) => {
            try {
              const analise = await analisarViabilidadeMeta(meta.id);
              return [meta.id, analise.data];
            } catch {
              return [meta.id, null];
            }
          })
        );

        return { lista, pares };
      })
      .then(({ lista, pares }) => {
        if (ativo) {
          setMetas(lista);
          setAnalises(Object.fromEntries(pares));
        }
      })
      .catch((error) => {
        if (ativo) {
          setErro(error.response?.data?.mensagem || "Erro ao carregar metas.");
        }
      });

    return () => {
      ativo = false;
    };
  }, []);

  function atualizarCampo(campo, valor) {
    setNovaMeta((atual) => ({ ...atual, [campo]: valor }));
    setSimulacao(null);
  }

  function dadosDaMeta() {
    return {
      nome: novaMeta.nome.trim(),
      valorAlvo: Number(novaMeta.valorAlvo),
      prazoMeses: Number(novaMeta.prazoMeses),
      valorInicial: Number(novaMeta.valorInicial || 0),
      prioridade: novaMeta.prioridade,
      descricao: novaMeta.descricao.trim(),
    };
  }

  async function executarSimulacao() {
    setMensagem("");
    setErro("");
    setSimulacao(null);

    if (!novaMeta.valorAlvo || Number(novaMeta.valorAlvo) <= 0) {
      setErro("Informe um valor alvo maior que zero para simular a meta.");
      return;
    }

    if (!novaMeta.prazoMeses || Number(novaMeta.prazoMeses) <= 0) {
      setErro("Informe um prazo maior que zero para simular a meta.");
      return;
    }

    setSimulando(true);

    try {
      const response = await simularMeta(dadosDaMeta());
      setSimulacao(response.data);
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Erro ao simular meta.");
    } finally {
      setSimulando(false);
    }
  }

  async function salvarMeta(event) {
    event.preventDefault();
    setMensagem("");
    setErro("");
    setSalvando(true);

    try {
      const response = await cadastrarMeta(dadosDaMeta());

      setMensagem(response.data?.mensagem || "Meta cadastrada com sucesso.");
      setSimulacao(null);
      setNovaMeta({
        nome: "",
        valorAlvo: "",
        prazoMeses: "",
        valorInicial: "",
        prioridade: "media",
        descricao: "",
      });
      await carregarMetas();
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Erro ao cadastrar meta.");
    } finally {
      setSalvando(false);
    }
  }

  async function removerMeta(id) {
    setMensagem("");
    setErro("");

    try {
      const response = await excluirMeta(id);
      setMensagem(response.data?.mensagem || "Meta excluída com sucesso.");
      await carregarMetas();
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Erro ao excluir meta.");
    }
  }

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-8 md:px-12">
      <section className="mb-10">
        <h1 className="text-3xl font-bold text-slate-900">Metas Financeiras</h1>
        <p className="mt-2 text-lg text-slate-500">
          O FinIA verifica cada objetivo considerando também o valor mensal exigido pelas outras metas.
        </p>
      </section>

      {mensagem && (
        <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-700">
          {mensagem}
        </div>
      )}

      {erro && (
        <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          {erro}
        </div>
      )}

      <section className="grid grid-cols-1 gap-8 xl:grid-cols-[380px_1fr]">
        <form
          onSubmit={salvarMeta}
          className="h-fit rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
        >
          <h2 className="text-xl font-semibold text-slate-900">Nova meta</h2>
          <p className="mt-1 text-sm text-slate-500">
            Você pode simular a viabilidade antes de gravar a meta.
          </p>

          <div className="mt-6 space-y-4">
            <Campo
              label="Nome da meta"
              value={novaMeta.nome}
              onChange={(valor) => atualizarCampo("nome", valor)}
              placeholder="Ex: Notebook"
            />

            <Campo
              label="Valor alvo"
              type="number"
              min="0.01"
              step="0.01"
              value={novaMeta.valorAlvo}
              onChange={(valor) => atualizarCampo("valorAlvo", valor)}
              placeholder="Ex: 5000"
            />

            <Campo
              label="Valor já guardado"
              type="number"
              min="0"
              step="0.01"
              required={false}
              value={novaMeta.valorInicial}
              onChange={(valor) => atualizarCampo("valorInicial", valor)}
              placeholder="Ex: 500"
            />

            <Campo
              label="Prazo em meses"
              type="number"
              min="1"
              step="1"
              value={novaMeta.prazoMeses}
              onChange={(valor) => atualizarCampo("prazoMeses", valor)}
              placeholder="Ex: 10"
            />

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-600">
                Prioridade
              </label>
              <select
                value={novaMeta.prioridade}
                onChange={(event) => atualizarCampo("prioridade", event.target.value)}
                className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-blue-600"
              >
                <option value="baixa">Baixa</option>
                <option value="media">Média</option>
                <option value="alta">Alta</option>
              </select>
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-600">
                Descrição
              </label>
              <textarea
                rows="3"
                maxLength="255"
                value={novaMeta.descricao}
                onChange={(event) => atualizarCampo("descricao", event.target.value)}
                className="w-full resize-none rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-blue-600"
                placeholder="Opcional"
              />
            </div>
          </div>

          <button
            type="button"
            onClick={executarSimulacao}
            disabled={simulando || salvando}
            className="mt-6 w-full rounded-xl border border-blue-600 px-5 py-3 font-semibold text-blue-600 hover:bg-blue-50 disabled:opacity-50"
          >
            {simulando ? "Simulando..." : "Simular antes de criar"}
          </button>

          {simulacao && <ResultadoSimulacao analise={simulacao} />}

          <button
            type="submit"
            disabled={salvando || simulando}
            className="mt-3 w-full rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
          >
            {salvando ? "Criando..." : "Criar meta"}
          </button>
        </form>

        <section>
          <div className="mb-5 flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-slate-900">Metas cadastradas</h2>
              <p className="mt-1 text-sm text-slate-500">
                {metas.length} meta(s) no planejamento atual.
              </p>
            </div>
          </div>

          {metas.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center text-slate-500">
              Nenhuma meta cadastrada.
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
              {metas.map((meta) => (
                <MetaCard
                  key={meta.id}
                  meta={meta}
                  analise={analises[meta.id]}
                  onExcluir={() => removerMeta(meta.id)}
                />
              ))}
            </div>
          )}
        </section>
      </section>
    </main>
  );
}

function ResultadoSimulacao({ analise }) {
  const classe = analise.classificacao || "ANALISANDO";
  const estilo = classe.includes("INVIAVEL")
    ? "border-red-200 bg-red-50 text-red-700"
    : classe.includes("ATENCAO")
    ? "border-amber-200 bg-amber-50 text-amber-700"
    : "border-emerald-200 bg-emerald-50 text-emerald-700";

  return (
    <div className={`mt-4 rounded-xl border p-4 ${estilo}`}>
      <p className="text-xs font-bold uppercase tracking-wide">
        {classe.replaceAll("_", " ")}
      </p>
      <p className="mt-2 text-sm leading-relaxed">{analise.mensagem}</p>
      <div className="mt-3 grid grid-cols-2 gap-2 text-xs">
        <div>
          <span className="opacity-70">Necessário/mês</span>
          <strong className="mt-1 block">{formatarMoeda(analise.valorMensalNecessario)}</strong>
        </div>
        <div>
          <span className="opacity-70">Margem disponível</span>
          <strong className="mt-1 block">{formatarMoeda(analise.margemDisponivelParaMeta)}</strong>
        </div>
      </div>
    </div>
  );
}

function Campo({
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  min,
  step,
  required = true,
}) {
  return (
    <div>
      <label className="mb-2 block text-sm font-medium text-slate-600">{label}</label>
      <input
        type={type}
        value={value}
        min={min}
        step={step}
        required={required}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-blue-600"
        placeholder={placeholder}
      />
    </div>
  );
}

function MetaCard({ meta, analise, onExcluir }) {
  const progresso = meta.valorAlvo > 0
    ? Math.min((Number(meta.valorInicial || 0) / Number(meta.valorAlvo)) * 100, 100)
    : 0;

  const classe = analise?.classificacao || "ANALISANDO";
  const estilo = classe.includes("INVIAVEL")
    ? "bg-red-50 text-red-700"
    : classe.includes("ATENCAO")
    ? "bg-amber-50 text-amber-700"
    : "bg-emerald-50 text-emerald-700";

  return (
    <article className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h3 className="text-lg font-semibold text-slate-900">{meta.nome}</h3>
          <p className="mt-1 text-sm text-slate-500">
            {formatarMoeda(meta.valorInicial)} de {formatarMoeda(meta.valorAlvo)}
          </p>
        </div>
        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${estilo}`}>
          {classe.replaceAll("_", " ")}
        </span>
      </div>

      <div className="mt-5 h-2 overflow-hidden rounded-full bg-slate-100">
        <div className="h-full rounded-full bg-blue-600" style={{ width: `${progresso}%` }} />
      </div>

      <div className="mt-5 grid grid-cols-2 gap-3 text-sm">
        <Info label="Prazo" value={`${meta.prazoMeses} meses`} />
        <Info label="Prioridade" value={meta.prioridade} />
        <Info
          label="Necessário/mês"
          value={analise ? formatarMoeda(analise.valorMensalNecessario) : "..."}
        />
        <Info
          label="Margem disponível"
          value={analise ? formatarMoeda(analise.margemDisponivelParaMeta) : "..."}
        />
      </div>

      {analise?.mensagem && (
        <p className="mt-5 rounded-xl bg-slate-50 p-4 text-sm leading-relaxed text-slate-600">
          {analise.mensagem}
        </p>
      )}

      <button
        type="button"
        onClick={onExcluir}
        className="mt-5 text-sm font-semibold text-red-600 hover:text-red-700"
      >
        Excluir meta
      </button>
    </article>
  );
}

function Info({ label, value }) {
  return (
    <div className="rounded-xl bg-slate-50 p-3">
      <p className="text-xs text-slate-400">{label}</p>
      <p className="mt-1 font-semibold text-slate-700">{value}</p>
    </div>
  );
}

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

export default Metas;
