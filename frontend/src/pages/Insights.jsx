import { useEffect, useState } from "react";
import { analisarIA, perguntarIA } from "../services/iaService";

function Insights() {
  const [pergunta, setPergunta] = useState("");
  const [resposta, setResposta] = useState("");
  const [analise, setAnalise] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [carregandoAnalise, setCarregandoAnalise] = useState(true);
  const [erro, setErro] = useState("");

  async function carregarAnalise() {
    setCarregandoAnalise(true);
    setErro("");

    try {
      const response = await analisarIA();
      setAnalise(response.data.resposta || "A IA não retornou uma análise.");
    } catch (error) {
      setErro(
        error.response?.data?.mensagem ||
          "Não foi possível carregar a análise da IA. Verifique se o backend está ligado e se a OPENAI_API_KEY foi configurada."
      );
    } finally {
      setCarregandoAnalise(false);
    }
  }

  useEffect(() => {
    carregarAnalise();
  }, []);

  async function enviarPergunta(event) {
    event.preventDefault();

    if (!pergunta.trim()) return;

    setCarregando(true);
    setErro("");
    setResposta("");

    try {
      const response = await perguntarIA(pergunta.trim());
      setResposta(response.data.resposta || "A IA não retornou uma resposta.");
      setPergunta("");
    } catch (error) {
      setErro(
        error.response?.data?.mensagem ||
          "Não foi possível consultar a IA. Verifique o backend e a chave da OpenAI."
      );
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-8 md:px-12">
      <section className="mb-8">
        <h1 className="text-3xl font-bold text-slate-900">FinIA IA</h1>
        <p className="mt-2 text-lg text-slate-500">
          Converse com a IA usando sua situação financeira atual.
        </p>
      </section>

      {erro && (
        <div className="mb-6 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          {erro}
        </div>
      )}

      <section className="mb-8 rounded-2xl border border-blue-100 bg-blue-50 p-6">
        <div className="mb-4 flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-600 text-white">
            <span className="material-symbols-outlined">auto_awesome</span>
          </div>
          <div>
            <h2 className="text-xl font-semibold text-slate-900">
              Análise financeira atual
            </h2>
            <p className="text-sm text-slate-500">
              Gerada considerando os cálculos e dados do FinIA.
            </p>
          </div>
        </div>

        <div className="rounded-xl bg-white p-5 text-sm leading-7 text-slate-700 whitespace-pre-line">
          {carregandoAnalise
            ? "Analisando sua situação financeira..."
            : analise || "Nenhuma análise disponível."}
        </div>

        <button
          type="button"
          onClick={carregarAnalise}
          disabled={carregandoAnalise}
          className="mt-4 rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:opacity-50"
        >
          Atualizar análise
        </button>
      </section>

      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-5">
          <h2 className="text-xl font-semibold text-slate-900">
            Pergunte ao FinIA
          </h2>
          <p className="mt-1 text-sm text-slate-500">
            Exemplos: “Posso comprar um celular de R$ 2.000?” ou “Minha meta de R$ 5.000 em 6 meses é viável?”
          </p>
        </div>

        <form onSubmit={enviarPergunta} className="flex flex-col gap-3 md:flex-row">
          <input
            value={pergunta}
            onChange={(event) => setPergunta(event.target.value)}
            placeholder="Digite sua pergunta financeira..."
            className="flex-1 rounded-xl border border-slate-300 px-4 py-3 text-sm outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />

          <button
            type="submit"
            disabled={carregando || !pergunta.trim()}
            className="rounded-xl bg-slate-900 px-6 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:opacity-50"
          >
            {carregando ? "Analisando..." : "Perguntar"}
          </button>
        </form>

        {resposta && (
          <div className="mt-6 rounded-xl border border-slate-200 bg-slate-50 p-5 text-sm leading-7 text-slate-700 whitespace-pre-line">
            {resposta}
          </div>
        )}
      </section>
    </main>
  );
}

export default Insights;
