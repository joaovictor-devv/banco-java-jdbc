import api from "./api";

export function perguntarIA(pergunta) {
  return api.post("/ia/perguntar", { pergunta });
}

export function analisarIA() {
  return api.get("/ia/analisar");
}
