function PageHeader({ pergunta, titulo, descricao, acao }) {
  return (
    <header className="mb-8 flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
      <div className="max-w-3xl">
        {pergunta && (
          <p className="mb-2 text-sm font-bold uppercase tracking-[0.14em] text-cyan-700">
            {pergunta}
          </p>
        )}
        <h1 className="text-3xl font-extrabold tracking-tight text-[#0A192F] sm:text-4xl">
          {titulo}
        </h1>
        {descricao && (
          <p className="mt-3 max-w-2xl text-base leading-7 text-slate-600 sm:text-lg">
            {descricao}
          </p>
        )}
      </div>
      {acao && <div className="shrink-0">{acao}</div>}
    </header>
  );
}

export default PageHeader;
