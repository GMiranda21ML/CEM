import { useSuspenseQuery } from '@tanstack/react-query';
import { Link } from 'react-router';
import { clienteConsultas, useAutenticacao } from '../../d5';
import { consultaAvisos, data, hoje } from '../comunicacao/api';
import { PendenciasCoordenacao } from './PendenciasCoordenacao';

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaAvisos());
  return null;
}

export function Inicio() {
  const { usuario, autorizacoesDe } = useAutenticacao();
  const { data: avisos } = useSuspenseQuery(consultaAvisos());
  const lista = avisos.resultado.projecoes.filter((a) => a.ativo && a.validade >= hoje());

  return (
    <div className="container py-4">
      <h1 className="h4">Cem</h1>
      <p className="text-muted">
        Você está autenticado como <strong>{usuario}</strong>.
      </p>
      {autorizacoesDe('alerta')?.buscar && <PendenciasCoordenacao />}
      <section className="panel mural">
        <h2>Mural de avisos</h2>
        {lista.map((a) => (
          <article className="notice-card" key={a.identificador}>
            <h3>{a.titulo}</h3>
            <p className="preservar-texto">{a.texto}</p>
            <small>
              Publicado em {data(a.publicadoEm)} · válido até {data(a.validade)}
            </small>
          </article>
        ))}
        {!lista.length && <p className="muted">Nenhum aviso no momento.</p>}
        <Link to="/comunicacao/aviso/pesquisa" className="text-action">
          Ver mural
        </Link>
      </section>
    </div>
  );
}
