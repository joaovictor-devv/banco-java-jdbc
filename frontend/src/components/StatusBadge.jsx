import {
  classificacaoEhAtencao,
  classificacaoEhProblema,
  formatarClassificacao,
} from "../utils/finance";

function StatusBadge({ valor }) {
  let classes = "bg-emerald-50 text-emerald-700 border-emerald-200";

  if (classificacaoEhProblema(valor)) {
    classes = "bg-red-50 text-red-700 border-red-200";
  } else if (classificacaoEhAtencao(valor)) {
    classes = "bg-amber-50 text-amber-700 border-amber-200";
  }

  return (
    <span
      className={`inline-flex items-center gap-2 rounded-full border px-3 py-1.5 text-xs font-bold ${classes}`}
    >
      <span className="h-2 w-2 rounded-full bg-current opacity-80" />
      {formatarClassificacao(valor)}
    </span>
  );
}

export default StatusBadge;
