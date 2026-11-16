import { CartaoSessao } from './CartaoSessao';
import { useState } from 'react';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { clienteConsultas, useAutenticacao } from '../../../d5';
import { useAcao } from '../../comunicacao/api';
import {
  consultaContexto,
  consultaDisponibilidades,
  consultaFaltas,
  consultaSessoes,
  type Disponibilidade,
} from '../api';

export async function loader() {
  await Promise.all([
    clienteConsultas.ensureQueryData(consultaContexto),
    clienteConsultas.ensureQueryData(consultaSessoes()),
    clienteConsultas.ensureQueryData(consultaDisponibilidades()),
    clienteConsultas.ensureQueryData(consultaFaltas()),
  ]);
  return null;
}
export function AgendaDeReposicao() {
  const { autorizacoesDe, autorizacoes } = useAutenticacao();
  const coordenador = autorizacoes.includes('COORDENADOR');
  const { data: contexto } = useSuspenseQuery(consultaContexto);
  const { data: sessoes } = useSuspenseQuery(consultaSessoes());
  const { data: disponibilidades } = useSuspenseQuery(consultaDisponibilidades());
  const { data: faltas } = useSuspenseQuery(consultaFaltas());
  const [edicao, definirEdicao] = useState<Disponibilidade | null>(null);
  const [filtro, definirFiltro] = useState({ texto: '', de: '', ate: '', situacao: 'TODAS' });
  const formSessao = useForm<{
    disciplina: string;
    professor: string;
    monitor: string;
    campus: string;
    sala: string;
    alunos: string[];
    inicio: string;
    fim: string;
  }>({ defaultValues: { alunos: [], monitor: contexto.usuario } });
  const monitorSelecionado = formSessao.watch('monitor');
  const formDisponibilidade = useForm<{
    disciplina: string;
    monitor: string;
    campus: string;
    diaSemana: string;
    inicio: string;
    fim: string;
  }>({ defaultValues: { monitor: contexto.usuario, diaSemana: '1' } });
  const acao = useAcao(() => {
    definirEdicao(null);
    formSessao.reset({ alunos: [], monitor: contexto.usuario });
    formDisponibilidade.reset({
      monitor: contexto.usuario,
      diaSemana: '1',
      disciplina: '',
      campus: '',
      inicio: '',
      fim: '',
    });
  });
  const nome = (id: string) => contexto.pessoas.find((p) => p.identificador === id)?.nome ?? id;
  const alunos = contexto.pessoas.filter((p) => p.papel === 'ALUNO');
  const cursoMonitor = contexto.pessoas.find((p) => p.identificador === monitorSelecionado)?.curso ?? contexto.cursoUsuario;
  const alunosElegiveis = alunos.filter((p) => !cursoMonitor || p.curso === cursoMonitor);
  const professores = contexto.pessoas.filter((p) => p.papel === 'PROFESSOR');
  const horasSemana = disponibilidades.resultado.projecoes
    .filter((d) => d.monitor === contexto.usuario)
    .reduce((total, d) => {
      const [inicioHora, inicioMinuto] = d.inicio.split(':').map(Number);
      const [fimHora, fimMinuto] = d.fim.split(':').map(Number);
      return total + (fimHora * 60 + fimMinuto - inicioHora * 60 - inicioMinuto) / 60;
    }, 0);
  const sessoesFiltradas = sessoes.resultado.projecoes.filter((sessao) => {
    const inicio = new Date(sessao.inicio);
    const textoOk = !filtro.texto || sessao.disciplina.toLocaleLowerCase().includes(filtro.texto.toLocaleLowerCase());
    const dataOk = (!filtro.de || inicio >= new Date(`${filtro.de}T00:00:00`)) && (!filtro.ate || inicio <= new Date(`${filtro.ate}T23:59:59`));
    const situacaoOk = filtro.situacao === 'TODAS' || (filtro.situacao === 'CANCELADAS' ? sessao.cancelada : filtro.situacao === 'HISTORICO' ? !sessao.cancelada && new Date(sessao.fim) <= new Date() : !sessao.cancelada && new Date(sessao.fim) > new Date());
    return textoOk && dataOk && situacaoOk;
  });
  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">MONITORIA</span>
          <h1>{coordenador ? 'Agenda de reposição' : 'Minhas sessões e disponibilidade'}</h1>
          <p>
            {coordenador
              ? 'Cadastre as sessões que originam as faltas e a disponibilidade semanal dos monitores.'
              : 'Consulte suas sessões, informe a ausência do monitor e mantenha seus horários disponíveis.'}
          </p>
        </div>
      </div>
      <div className="content-grid">
        {(coordenador || autorizacoes.includes('ALUNO')) && (
          <section className="panel">
            <h2>Cadastrar sessão</h2>
            <p className="panel-sub">Registre a sessão e convide alunos do curso do monitor.</p>
            <form
              className="form-grid"
              onSubmit={formSessao.handleSubmit((v) =>
                acao.mutate({
                  caminho: 'monitoria/sessao/registrar',
                  corpo: {
                    ...v,
                    monitor: coordenador ? v.monitor : contexto.usuario,
                    identificador: null,
                    cancelada: false,
                  },
                }),
              )}
            >
              <div className="field full">
                <label htmlFor="sessao-disciplina">Disciplina *</label>
                <input
                  id="sessao-disciplina"
                  required
                  maxLength={150}
                  {...formSessao.register('disciplina', { required: true })}
                />
              </div>
              <div className="field">
                <label htmlFor="sessao-professor">Professor *</label>
                <select
                  id="sessao-professor"
                  required
                  {...formSessao.register('professor', { required: true })}
                >
                  <option value="">Selecione</option>
                  {professores.map((p) => (
                    <option key={p.identificador} value={p.identificador}>
                      {p.nome}
                    </option>
                  ))}
                </select>
              </div>
              {coordenador ? <div className="field">
                <label htmlFor="sessao-monitor">Monitor *</label>
                <select
                  id="sessao-monitor"
                  required
                  {...formSessao.register('monitor', { required: true })}
                >
                  <option value="">Selecione</option>
                  {alunos.map((p) => (
                    <option key={p.identificador} value={p.identificador}>
                      {p.nome}
                    </option>
                  ))}
                </select>
              </div> : <div className="field"><label>Monitor</label><input value={nome(contexto.usuario)} readOnly /></div>}
              <div className="field">
                <label htmlFor="sessao-campus">Campus *</label>
                <select
                  id="sessao-campus"
                  required
                  {...formSessao.register('campus', { required: true })}
                >
                  <option value="">Selecione</option>
                  {contexto.campus.map((c) => (
                    <option key={c.identificador} value={c.identificador}>
                      {c.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="field">
                <label htmlFor="sessao-alunos">Alunos *</label>
                <select
                  id="sessao-alunos"
                  multiple
                  required
                  {...formSessao.register('alunos', { required: true })}
                >
                  {alunosElegiveis.map((p) => (
                    <option key={p.identificador} value={p.identificador}>
                      {p.nome}
                    </option>
                  ))}
                </select>
                <small>Selecione um ou mais alunos.</small>
              </div>
              <div className="field">
                <label htmlFor="sessao-sala">Sala *</label>
                <input id="sessao-sala" required maxLength={100} {...formSessao.register('sala', { required: true })} />
              </div>
              <div className="field">
                <label htmlFor="sessao-inicio">Início *</label>
                <input
                  id="sessao-inicio"
                  type="datetime-local"
                  required
                  {...formSessao.register('inicio', { required: true })}
                />
              </div>
              <div className="field">
                <label htmlFor="sessao-fim">Fim *</label>
                <input
                  id="sessao-fim"
                  type="datetime-local"
                  required
                  {...formSessao.register('fim', { required: true })}
                />
              </div>
              <div className="form-actions full">
                <button className="button button-primary" disabled={acao.isPending}>
                  Cadastrar sessão
                </button>
              </div>
            </form>
          </section>
        )}
        {autorizacoesDe('disponibilidade')?.incluir && (
          <section className="panel" id="disponibilidade">
            <h2>{edicao ? 'Editar disponibilidade' : 'Disponibilidade do monitor'}</h2>
            <p className="panel-sub">
              Minha semana: {horasSemana} h de 20 h usadas. Horários entre 7h e 22h, sem
              sobreposição.
            </p>
            <form
              className="form-grid"
              onSubmit={formDisponibilidade.handleSubmit((v) =>
                acao.mutate({
                  caminho: edicao
                    ? `monitoria/disponibilidade/${edicao.identificador}/alterar`
                    : 'monitoria/disponibilidade/incluir',
                  corpo: {
                    ...v,
                    monitor: coordenador ? v.monitor : contexto.usuario,
                    diaSemana: Number(v.diaSemana),
                    identificador: edicao?.identificador ?? null,
                  },
                }),
              )}
            >
              <div className="field full">
                <label htmlFor="disp-disciplina">Disciplina *</label>
                <input
                  id="disp-disciplina"
                  required
                  maxLength={150}
                  {...formDisponibilidade.register('disciplina', { required: true })}
                />
              </div>
              {coordenador && (
                <div className="field">
                  <label htmlFor="disp-monitor">Monitor *</label>
                  <select
                    id="disp-monitor"
                    required
                    {...formDisponibilidade.register('monitor', { required: true })}
                  >
                    <option value="">Selecione</option>
                    {alunos.map((p) => (
                      <option key={p.identificador} value={p.identificador}>
                        {p.nome}
                      </option>
                    ))}
                  </select>
                </div>
              )}
              <div className="field">
                <label htmlFor="disp-campus">Campus *</label>
                <select
                  id="disp-campus"
                  required
                  {...formDisponibilidade.register('campus', { required: true })}
                >
                  <option value="">Selecione</option>
                  {contexto.campus.map((c) => (
                    <option key={c.identificador} value={c.identificador}>
                      {c.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="field">
                <label htmlFor="disp-dia">Dia da semana *</label>
                <select id="disp-dia" {...formDisponibilidade.register('diaSemana')}>
                  {['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado', 'Domingo'].map(
                    (dia, i) => (
                      <option value={i + 1} key={dia}>
                        {dia}
                      </option>
                    ),
                  )}
                </select>
              </div>
              <div className="field">
                <label htmlFor="disp-inicio">Início *</label>
                <input
                  id="disp-inicio"
                  type="time"
                  min="07:00"
                  max="21:59"
                  required
                  {...formDisponibilidade.register('inicio', {
                    required: true,
                    min: '07:00',
                    max: '21:59',
                  })}
                />
              </div>
              <div className="field">
                <label htmlFor="disp-fim">Fim *</label>
                <input
                  id="disp-fim"
                  type="time"
                  min="07:01"
                  max="22:00"
                  required
                  {...formDisponibilidade.register('fim', {
                    required: true,
                    min: '07:01',
                    max: '22:00',
                    validate: (fim) => !formDisponibilidade.getValues('inicio') || fim > formDisponibilidade.getValues('inicio'),
                  })}
                />
              </div>
              <div className="form-actions full">
                <button className="button button-primary" disabled={acao.isPending}>
                  Salvar disponibilidade
                </button>
                {edicao && (
                  <button
                    className="button"
                    type="button"
                    onClick={() => {
                      definirEdicao(null);
                      formDisponibilidade.reset({ monitor: contexto.usuario, diaSemana: '1' });
                    }}
                  >
                    Cancelar
                  </button>
                )}
              </div>
            </form>
          </section>
        )}
      </div>
      <section className="panel">
        <h2>Horários disponíveis</h2>
        {disponibilidades.resultado.projecoes.map((d) => (
          <article className="item-card" key={d.identificador}>
            <strong>{d.disciplina}</strong>
            <p>
              {nome(d.monitor)} ·{' '}
              {
                ['', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado', 'Domingo'][
                  d.diaSemana
                ]
              }{' '}
              · {d.inicio.slice(0, 5)}–{d.fim.slice(0, 5)}
            </p>
            {(coordenador || d.monitor === contexto.usuario) && (
              <div className="row-actions">
                <button
                  className="button button-small"
                  onClick={() => {
                    definirEdicao(d);
                    formDisponibilidade.reset({ ...d, diaSemana: String(d.diaSemana) });
                  }}
                >
                  Editar
                </button>
                <button
                  className="button button-danger button-small"
                  disabled={acao.isPending}
                  onClick={() =>
                    acao.mutate({ caminho: `monitoria/disponibilidade/${d.identificador}/excluir` })
                  }
                >
                  Excluir
                </button>
              </div>
            )}
          </article>
        ))}
        {!disponibilidades.resultado.total && (
          <p className="empty">Nenhuma disponibilidade cadastrada</p>
        )}
      </section>
      <section className="panel">
        <h2>{coordenador ? 'Sessões' : 'Minhas sessões'}</h2>
        <div className="form-grid">
          <div className="field"><label htmlFor="filtro-sessao-disciplina">Disciplina</label><input id="filtro-sessao-disciplina" value={filtro.texto} onChange={(e) => definirFiltro({ ...filtro, texto: e.target.value })} /></div>
          <div className="field"><label htmlFor="filtro-sessao-de">A partir de</label><input id="filtro-sessao-de" type="date" value={filtro.de} onChange={(e) => definirFiltro({ ...filtro, de: e.target.value })} /></div>
          <div className="field"><label htmlFor="filtro-sessao-ate">Até</label><input id="filtro-sessao-ate" type="date" value={filtro.ate} onChange={(e) => definirFiltro({ ...filtro, ate: e.target.value })} /></div>
          <div className="field"><label htmlFor="filtro-sessao-situacao">Período</label><select id="filtro-sessao-situacao" value={filtro.situacao} onChange={(e) => definirFiltro({ ...filtro, situacao: e.target.value })}><option value="TODAS">Todas</option><option value="FUTURAS">Próximas</option><option value="HISTORICO">Histórico</option><option value="CANCELADAS">Canceladas</option></select></div>
        </div>
        {sessoesFiltradas.map((s) => (
          <CartaoSessao
            key={s.identificador}
            sessao={s}
            contexto={contexto}
            faltas={faltas.resultado.projecoes}
          />
        ))}
        {!sessoesFiltradas.length && <p className="empty">Nenhuma sessão encontrada para os filtros selecionados</p>}
      </section>
    </>
  );
}
