import { useSuspenseQuery } from '@tanstack/react-query';
import { Link } from 'react-router';
import { consultaPerguntas } from '../comunicacao/api';
import { consultaAlertas } from '../monitoria/api';

export function PendenciasCoordenacao() {
  const { data: alertas } = useSuspenseQuery(consultaAlertas());
  const { data: perguntas } = useSuspenseQuery(consultaPerguntas());
  return (
    <div className="content-grid pendencias-inicio">
      <Link className="panel home-card" to="/monitoria/alerta/pesquisa">
        <b>{alertas.resultado.projecoes.filter((a) => !a.acompanhado).length}</b>
        <span>alerta(s) de faltas para acompanhar</span>
      </Link>
      <Link className="panel home-card" to="/comunicacao/faq/pesquisa">
        <b>{perguntas.resultado.projecoes.filter((p) => p.situacao === 'PENDENTE').length}</b>
        <span>pergunta(s) aguardando resposta</span>
      </Link>
    </div>
  );
}
