import { useEffect, useState } from "react";
import CurrencyInput from "../components/CurrencyInput";
import PageHeader from "../components/PageHeader";
import StatusBadge from "../components/StatusBadge";
import api from "../services/api";
import { formatarMoeda, formatarPercentual } from "../utils/finance";

const formularioInicial = {
  nome: "",
  valorAlvo: "",
  valorInicial: "0",
  prazoMeses: "",
  prioridade: "media",
  descricao: "",
};

function Metas() {
  const [metas, setMetas] = useState([]);
  const [form, setForm] = useState(formularioInicial);
  const [analise, setAnalise] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [simulando, setSimulando] = useState(false);
  const [criando, setCriando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [editandoProgresso, setEditandoProgresso] = useState(null);
  const [valorProgresso, setValorProgresso] = useState("");

  async function carregarMetas() {
    try {
      const response = await api.get("/metas/resumo");
      setMetas(response.data || []);
    } catch (error) {
      if (error.response?.status !== 404) {
        setErro(error.response?.data?.mensagem || "Não foi possível carregar suas metas.");
      }
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarMetas();
  }, []);

  function alterar(campo, valor) {
    setForm((atual) => ({ ...atual, [campo]: valor }));
    setAnalise(null);
    setErro("");
    setSucesso("");
  }

  function payload() {
    return {
      nome: form.nome.trim(),
      valorAlvo: numero(form.valorAlvo),
      valorInicial: numero(form.valorInicial),
      prazoMeses: Number(form.prazoMeses),
      prioridade: form.prioridade,
      descricao: form.descricao.trim() || null,
    };
  }

  async function simular(event) {
    event.preventDefault();
    setErro("");
    setSucesso("");
    setSimulando(true);

    try {
      const response = await api.post("/analise/simular-meta", payload());
      setAnalise(response.data);
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Confira os dados da meta e tente novamente.");
    } finally {
      setSimulando(false);
    }
  }

  async function criar() {
    if (!analise?.viavel) return;

    setErro("");
    setSucesso("");
    setCriando(true);

    try {
      await api.post("/metas", payload());
      setForm(formularioInicial);
      setAnalise(null);
      setSucesso("Meta criada. O FinIA já passou a considerar esse valor nos seus cálculos.");
      await carregarMetas();
    } catch (error) {
      if (error.response?.status === 422 && error.response?.data?.analise) {
        setAnalise(error.response.data.analise);
      }
      setErro(error.response?.data?.mensagem || error.response?.data?.message || "Não foi possível criar a meta.");
    } finally {
      setCriando(false);
    }
  }

  async function salvarProgresso(id) {
    setErro("");
    setSucesso("");
    try {
      await api.patch(`/metas/${id}/progresso`, { valorAtual: numero(valorProgresso) });
      setEditandoProgresso(null);
      setValorProgresso("");
      setSucesso("Progresso atualizado.");
      await carregarMetas();
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Não foi possível atualizar o progresso.");
    }
  }

  async function excluir(id) {
    if (!window.confirm("Excluir esta meta?")) return;
    setErro("");
    setSucesso("");
    try {
      await api.delete(`/metas/${id}`);
      setSucesso("Meta excluída.");
      await carregarMetas();
    } catch (error) {
      setErro(error.response?.data?.mensagem || "Não foi possível excluir a meta.");
    }
  }

  return (
    <main className="min-h-screen bg-[#F6FAFE] px-4 py-8 sm:px-6 md:px-10 lg:px-12">
      <PageHeader
        pergunta="Consigo alcançar isso?"
        titulo="Metas"
        descricao="Crie um objetivo e veja antes se ele cabe no seu orçamento. Se o prazo estiver apertado, o FinIA mostra uma opção mais possível."
      />

      {erro && <Feedback tipo="erro">{erro}</Feedback>}
      {sucesso && <Feedback>{sucesso}</Feedback>}

      <section>
        <div className="flex items-end justify-between gap-4">
          <div>
            <h2 className="text-xl font-extrabold text-[#0A192F]">Suas metas</h2>
            <p className="mt-1 text-sm text-slate-500">O valor mensal de todas elas entra automaticamente nos cálculos.</p>
          </div>
          <span className="rounded-full bg-slate-100 px-3 py-1.5 text-xs font-bold text-slate-600">
            {metas.length} {metas.length === 1 ? "meta" : "metas"}
          </span>
        </div>

        {carregando ? (
          <div className="mt-4 finia-card p-6 text-sm font-semibold text-slate-500">Carregando metas...</div>
        ) : metas.length === 0 ? (
          <div className="mt-4 rounded-2xl border border-dashed border-slate-300 bg-white/60 p-7 text-center">
            <span className="material-symbols-outlined !text-[32px] text-cyan-800">flag</span>
            <p className="mt-3 font-extrabold text-[#0A192F]">Você ainda não tem metas</p>
            <p className="mx-auto mt-1 max-w-lg text-sm leading-6 text-slate-500">
              Use o formulário abaixo para testar seu primeiro objetivo. Ele só será salvo se couber no orçamento atual.
            </p>
          </div>
        ) : (
          <div className="mt-4 grid gap-4 xl:grid-cols-2">
            {metas.map((item) => (
              <MetaCard
                key={item.meta.id}
                item={item}
                editando={editandoProgresso === item.meta.id}
                valorProgresso={valorProgresso}
                onAbrirProgresso={() => {
                  setEditandoProgresso(item.meta.id);
                  setValorProgresso(String(item.meta.valorInicial ?? 0));
                }}
                onCancelarProgresso={() => setEditandoProgresso(null)}
                onChangeProgresso={setValorProgresso}
                onSalvarProgresso={() => salvarProgresso(item.meta.id)}
                onExcluir={() => excluir(item.meta.id)}
              />
            ))}
          </div>
        )}
      </section>

      <section className="mt-10 grid gap-6 xl:grid-cols-[1fr_0.9fr]">
        <form onSubmit={simular} className="finia-card p-6 sm:p-8">
          <div className="flex items-start gap-4">
            <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-cyan-50 text-cyan-800">
              <span className="material-symbols-outlined">add_task</span>
            </div>
            <div>
              <h2 className="text-xl font-extrabold text-[#0A192F]">Testar uma nova meta</h2>
              <p className="mt-1 text-sm leading-6 text-slate-500">Primeiro veja se cabe. Depois você decide se quer criar.</p>
            </div>
          </div>

          <div className="mt-7 grid gap-5 sm:grid-cols-2">
            <label className="sm:col-span-2">
              <span className="text-sm font-bold text-[#0A192F]">O que você quer alcançar?</span>
              <input
                value={form.nome}
                onChange={(event) => alterar("nome", event.target.value)}
                placeholder="Ex.: Notebook para estudar"
                className="finia-input mt-2 h-12 px-4"
                required
                maxLength={100}
              />
            </label>

            <CurrencyInput
              label="Quanto custa?"
              value={form.valorAlvo}
              onChange={(event) => alterar("valorAlvo", event.target.value)}
              placeholder="5.000,00"
              required
            />
            <CurrencyInput
              label="Quanto você já guardou?"
              value={form.valorInicial}
              onChange={(event) => alterar("valorInicial", event.target.value)}
              placeholder="1.000,00"
              required
            />

            <label>
              <span className="text-sm font-bold text-[#0A192F]">Em quantos meses?</span>
              <input
                type="number"
                min="1"
                max="600"
                value={form.prazoMeses}
                onChange={(event) => alterar("prazoMeses", event.target.value)}
                placeholder="10"
                className="finia-input mt-2 h-12 px-4"
                required
              />
            </label>

            <label>
              <span className="text-sm font-bold text-[#0A192F]">Qual a importância?</span>
              <select
                value={form.prioridade}
                onChange={(event) => alterar("prioridade", event.target.value)}
                className="finia-input mt-2 h-12 px-4"
              >
                <option value="alta">Alta</option>
                <option value="media">Média</option>
                <option value="baixa">Baixa</option>
              </select>
            </label>

            <label className="sm:col-span-2">
              <span className="text-sm font-bold text-[#0A192F]">Observação <span className="font-medium text-slate-400">(opcional)</span></span>
              <textarea
                value={form.descricao}
                onChange={(event) => alterar("descricao", event.target.value)}
                placeholder="Por que essa meta é importante para você?"
                className="finia-input mt-2 min-h-24 resize-y p-4"
                maxLength={255}
              />
            </label>
          </div>

          <button
            type="submit"
            disabled={simulando}
            className="finia-button-primary mt-7 w-full px-5 py-3.5 sm:w-auto"
          >
            {simulando ? "Calculando..." : "Ver se essa meta cabe"}
          </button>
        </form>

        <section className="finia-card p-6 sm:p-8">
          <h2 className="text-xl font-extrabold text-[#0A192F]">Resultado da meta</h2>
          {!analise ? (
            <div className="mt-6 rounded-2xl bg-slate-50 p-6 text-center">
              <span className="material-symbols-outlined !text-[32px] text-slate-400">calculate</span>
              <p className="mt-3 font-bold text-slate-700">Preencha a meta e clique em “Ver se essa meta cabe”.</p>
              <p className="mt-1 text-sm leading-6 text-slate-500">O cálculo considera seu orçamento e todas as metas já cadastradas.</p>
            </div>
          ) : (
            <ResultadoMeta analise={analise} criando={criando} onCriar={criar} />
          )}
        </section>
      </section>
    </main>
  );
}

function MetaCard({ item, editando, valorProgresso, onAbrirProgresso, onCancelarProgresso, onChangeProgresso, onSalvarProgresso, onExcluir }) {
  const { meta, analise } = item;
  const percentual = Math.min(100, Math.max(0, Number(meta.valorInicial || 0) / Math.max(1, Number(meta.valorAlvo || 1)) * 100));

  return (
    <article className="finia-card p-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="text-lg font-extrabold text-[#0A192F]">{meta.nome}</h3>
            <StatusBadge valor={analise.classificacao} />
          </div>
          <p className="mt-1 text-sm text-slate-500">{meta.prazoMeses} meses · importância {rotuloPrioridade(meta.prioridade)}</p>
        </div>
        <button onClick={onExcluir} className="rounded-lg p-2 text-slate-400 transition hover:bg-red-50 hover:text-red-600" aria-label={`Excluir meta ${meta.nome}`}>
          <span className="material-symbols-outlined">delete</span>
        </button>
      </div>

      <div className="mt-6 flex items-end justify-between gap-4">
        <div>
          <p className="text-xs font-semibold text-slate-500">Você já guardou</p>
          <p className="finia-number mt-1 text-xl font-extrabold text-cyan-800">{formatarMoeda(meta.valorInicial)}</p>
        </div>
        <p className="finia-number text-right text-sm font-bold text-slate-600">de {formatarMoeda(meta.valorAlvo)}</p>
      </div>
      <div className="mt-3 h-2.5 overflow-hidden rounded-full bg-slate-100">
        <div className="h-full rounded-full bg-cyan-600 transition-all" style={{ width: `${percentual}%` }} />
      </div>
      <p className="mt-2 text-xs font-semibold text-slate-500">{formatarPercentual(percentual)} concluído</p>

      <div className="mt-5 grid gap-3 sm:grid-cols-2">
        <MiniInfo label="Falta guardar" valor={formatarMoeda(analise.valorRestante)} />
        <MiniInfo label="Por mês" valor={formatarMoeda(analise.valorMensalNecessario)} />
      </div>

      {editando ? (
        <div className="mt-5 rounded-xl bg-slate-50 p-4">
          <label className="text-sm font-bold text-[#0A192F]">Quanto já está guardado agora?</label>
          <div className="mt-2 flex gap-2">
            <input
              type="number"
              min="0"
              step="0.01"
              value={valorProgresso}
              onChange={(event) => onChangeProgresso(event.target.value)}
              className="finia-input h-11 min-w-0 flex-1 px-3"
            />
            <button type="button" onClick={onSalvarProgresso} className="finia-button-primary px-4">Salvar</button>
          </div>
          <button type="button" onClick={onCancelarProgresso} className="mt-2 text-xs font-bold text-slate-500 hover:text-slate-700">Cancelar</button>
        </div>
      ) : (
        <button type="button" onClick={onAbrirProgresso} className="finia-button-secondary mt-5 px-4 py-2.5 text-sm">
          Atualizar progresso
        </button>
      )}
    </article>
  );
}

function ResultadoMeta({ analise, criando, onCriar }) {
  return (
    <div className="mt-6">
      <div className="flex items-center justify-between gap-3">
        <StatusBadge valor={analise.classificacao} />
        <span className="text-xs font-bold text-slate-500">Calculado com sua situação atual</span>
      </div>

      <div className="mt-5 grid gap-3 sm:grid-cols-2">
        <MiniInfo label="Falta guardar" valor={formatarMoeda(analise.valorRestante)} />
        <MiniInfo label="Precisa guardar por mês" valor={formatarMoeda(analise.valorMensalNecessario)} destaque />
        <MiniInfo label="Hoje você tem disponível" valor={formatarMoeda(analise.margemDisponivelParaMeta)} />
        <MiniInfo label="Outras metas já usam" valor={formatarMoeda(analise.comprometimentoOutrasMetas)} />
      </div>

      {analise.viavel ? (
        <div className={`mt-5 rounded-2xl border p-5 ${analise.classificacao === "VIAVEL_COM_ATENCAO" ? "border-amber-200 bg-amber-50" : "border-emerald-200 bg-emerald-50"}`}>
          <p className="font-extrabold text-[#0A192F]">
            {analise.classificacao === "VIAVEL_COM_ATENCAO" ? "Essa meta é possível, mas vai deixar pouco espaço livre." : "Essa meta cabe no seu orçamento."}
          </p>
          <p className="mt-2 text-sm leading-6 text-slate-600">
            Ela usará {formatarPercentual(analise.percentualMargemComprometida)} do que está disponível para uma nova meta.
          </p>
          <button type="button" onClick={onCriar} disabled={criando} className="finia-button-primary mt-5 w-full px-5 py-3">
            {criando ? "Criando..." : "Criar esta meta"}
          </button>
        </div>
      ) : (
        <div className="mt-5 rounded-2xl border border-red-200 bg-red-50 p-5">
          <p className="font-extrabold text-red-800">Essa meta não cabe nesse prazo.</p>
          <p className="mt-2 text-sm leading-6 text-red-700">
            Você precisaria guardar {formatarMoeda(analise.valorMensalNecessario)} por mês, mas hoje existem {formatarMoeda(analise.margemDisponivelParaMeta)} disponíveis para ela.
          </p>
          {analise.prazoMinimoViavelMeses && (
            <div className="mt-4 grid gap-3 sm:grid-cols-2">
              <MiniInfo label="Prazo mínimo" valor={`${analise.prazoMinimoViavelMeses} meses`} />
              <MiniInfo label="Prazo mais confortável" valor={`${analise.prazoConfortavelMeses} meses`} destaque />
            </div>
          )}
          <p className="mt-4 text-xs font-semibold leading-5 text-red-700">A meta não será salva enquanto estiver inviável.</p>
        </div>
      )}
    </div>
  );
}

function MiniInfo({ label, valor, destaque = false }) {
  return (
    <div className={`rounded-xl p-4 ${destaque ? "bg-cyan-50" : "bg-slate-50"}`}>
      <p className="text-xs font-semibold text-slate-500">{label}</p>
      <p className={`finia-number mt-1 font-extrabold ${destaque ? "text-cyan-800" : "text-[#0A192F]"}`}>{valor}</p>
    </div>
  );
}

function Feedback({ children, tipo }) {
  const classes = tipo === "erro"
    ? "border-red-200 bg-red-50 text-red-700"
    : "border-emerald-200 bg-emerald-50 text-emerald-700";
  return <div className={`mb-6 rounded-xl border px-4 py-3 text-sm font-semibold ${classes}`}>{children}</div>;
}

function rotuloPrioridade(prioridade) {
  if (prioridade === "alta") return "alta";
  if (prioridade === "baixa") return "baixa";
  return "média";
}

function numero(valor) {
  const convertido = Number(valor);
  return Number.isFinite(convertido) ? convertido : 0;
}

export default Metas;
