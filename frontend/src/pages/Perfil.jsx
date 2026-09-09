import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  atualizarPerfilFinanceiro,
  buscarPerfilFinanceiro,
} from "../services/perfilFinanceiroService";

function Perfil() {
  const [perfil, setPerfil] = useState(null);
  const [saldo, setSaldo] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    carregarPerfil();
  }, []);

  async function carregarPerfil() {
    setCarregando(true);
    setErro("");

    try {
      const response = await buscarPerfilFinanceiro();
      setPerfil(response.data);
      setSaldo(String(response.data?.saldoAtual ?? 0));
    } catch (error) {
      setPerfil(null);
      setErro(
        error.response?.data?.mensagem ||
          "Cadastre primeiro seu planejamento financeiro."
      );
    } finally {
      setCarregando(false);
    }
  }

  async function salvarSaldo(event) {
    event.preventDefault();
    setMensagem("");
    setErro("");

    if (!perfil) return;

    const saldoNumerico = Number(saldo);
    if (!Number.isFinite(saldoNumerico) || saldoNumerico < 0) {
      setErro("Informe um saldo válido, maior ou igual a zero.");
      return;
    }

    setSalvando(true);

    try {
      await atualizarPerfilFinanceiro(perfil.id, {
        rendaMensal: perfil.rendaMensal,
        rendaExtra: perfil.rendaExtra,
        gastoMoradia: perfil.gastoMoradia,
        gastoAgua: perfil.gastoAgua,
        gastoEnergia: perfil.gastoEnergia,
        gastoInternet: perfil.gastoInternet,
        gastoTransporte: perfil.gastoTransporte,
        gastoAlimentacao: perfil.gastoAlimentacao,
        outrasDespesas: perfil.outrasDespesas,
        valorPlanejadoGuardar: perfil.valorPlanejadoGuardar,
        objetivoPrincipal: perfil.objetivoPrincipal,
        saldoAtual: saldoNumerico,
      });

      setMensagem("Saldo atual atualizado com sucesso.");
      await carregarPerfil();
    } catch (error) {
      setErro(
        error.response?.data?.mensagem || "Não foi possível atualizar o saldo."
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <main className="min-h-screen bg-slate-50 px-6 py-8 md:px-12">
        <p className="text-slate-500">Carregando perfil financeiro...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-8 md:px-12">
      <section className="mb-10">
        <h1 className="text-3xl font-bold text-slate-900">Perfil Financeiro</h1>
        <p className="mt-2 text-lg text-slate-500">
          Consulte seu planejamento e informe o saldo que você possui agora.
        </p>
      </section>

      {erro && (
        <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          {erro}
        </div>
      )}

      {mensagem && (
        <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-700">
          {mensagem}
        </div>
      )}

      {!perfil ? (
        <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold text-slate-900">
            Nenhum planejamento cadastrado
          </h2>
          <p className="mt-2 text-sm text-slate-500">
            O saldo faz parte do perfil financeiro. Cadastre o planejamento antes de continuar.
          </p>
          <Link
            to="/planejamento"
            className="mt-5 inline-flex rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700"
          >
            Ir para planejamento
          </Link>
        </section>
      ) : (
        <section className="grid grid-cols-1 gap-8 lg:grid-cols-3">
          <div className="space-y-6 lg:col-span-2">
            <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
              <Card titulo="Renda mensal" valor={formatarMoeda(perfil.rendaMensal)} />
              <Card titulo="Renda extra" valor={formatarMoeda(perfil.rendaExtra)} />
              <Card titulo="Saldo atual" valor={formatarMoeda(perfil.saldoAtual)} destaque />
            </div>

            <form
              onSubmit={salvarSaldo}
              className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
            >
              <div className="mb-5 flex items-start gap-4">
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                  <span className="material-symbols-outlined">account_balance_wallet</span>
                </div>
                <div>
                  <h2 className="text-xl font-semibold text-slate-900">
                    Saldo disponível agora
                  </h2>
                  <p className="mt-1 text-sm text-slate-500">
                    Este valor é usado pelo FinIA para saber se uma compra cabe no dinheiro disponível. Não é necessário cadastrar uma transação para informar esse saldo.
                  </p>
                </div>
              </div>

              <label className="mb-2 block text-sm font-medium text-slate-600">
                Saldo atual em reais
              </label>
              <div className="flex flex-col gap-3 sm:flex-row">
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={saldo}
                  onChange={(event) => setSaldo(event.target.value)}
                  className="flex-1 rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-blue-600"
                  placeholder="Ex: 1200"
                />
                <button
                  type="submit"
                  disabled={salvando}
                  className="rounded-xl bg-blue-600 px-6 py-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                >
                  {salvando ? "Salvando..." : "Atualizar saldo"}
                </button>
              </div>
            </form>
          </div>

          <aside className="space-y-6">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h2 className="text-lg font-semibold text-slate-900">
                Objetivo principal
              </h2>
              <p className="mt-3 text-sm leading-relaxed text-slate-600">
                {perfil.objetivoPrincipal}
              </p>
              <Link
                to="/planejamento"
                className="mt-5 inline-flex text-sm font-semibold text-blue-600 hover:text-blue-700"
              >
                Editar planejamento
              </Link>
            </div>

            <div className="rounded-2xl border border-blue-100 bg-blue-50 p-6">
              <h3 className="font-semibold text-slate-900">Como a FinIA usa isso?</h3>
              <p className="mt-2 text-sm leading-relaxed text-slate-600">
                A análise combina saldo atual, renda, despesas, valor planejado para guardar e o comprometimento mensal de todas as metas.
              </p>
              <Link
                to="/insights"
                className="mt-5 inline-flex rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white"
              >
                Abrir FinIA IA
              </Link>
            </div>
          </aside>
        </section>
      )}
    </main>
  );
}

function Card({ titulo, valor, destaque = false }) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <p className="text-sm text-slate-500">{titulo}</p>
      <p className={`mt-2 text-2xl font-bold ${destaque ? "text-blue-600" : "text-slate-900"}`}>
        {valor}
      </p>
    </div>
  );
}

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

export default Perfil;
