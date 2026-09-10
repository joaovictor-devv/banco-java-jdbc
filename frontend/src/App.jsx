import { BrowserRouter, Route, Routes } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard";
import Insights from "./pages/Insights";
import Metas from "./pages/Metas";
import Perfil from "./pages/Perfil";
import Planejamento from "./pages/Planejamento";
import Simulacoes from "./pages/Simulacoes";

function App() {
  return (
    <BrowserRouter>
      <Sidebar />
      <div className="pb-24 pt-16 md:ml-64 md:pb-0 md:pt-0">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/planejamento" element={<Planejamento />} />
          <Route path="/metas" element={<Metas />} />
          <Route path="/simulacoes" element={<Simulacoes />} />
          <Route path="/insights" element={<Insights />} />
          <Route path="/perfil" element={<Perfil />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
