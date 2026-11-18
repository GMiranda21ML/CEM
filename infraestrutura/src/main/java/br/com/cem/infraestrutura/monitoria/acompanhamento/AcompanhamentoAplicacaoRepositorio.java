package br.com.cem.infraestrutura.monitoria.acompanhamento;

import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoConsulta;
import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoOrdenacao;
import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoProjecao;
import br.com.cem.aplicacao.monitoria.acompanhamento.AlunoDisciplinaOpcaoProjecao;
import br.com.cem.aplicacao.monitoria.acompanhamento.spi.IAcompanhamentoAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.RelatorioDesempenho;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.spi.IFeedbackRepositorio;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AcompanhamentoAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<
				AcompanhamentoProjecao, AcompanhamentoOrdenacao, AcompanhamentoConsulta>
		implements IAcompanhamentoAplicacaoRepositorio {
	private final IAcompanhamentoRepositorioJpa repositorioJpa;
	private final IAcompanhamentoRepositorio acompanhamentoRepositorio;
	private final IDisciplinaRepositorio disciplinaRepositorio;
	private final ISessaoRepositorio sessaoRepositorio;
	private final IFaltaRepositorio faltaRepositorio;
	private final IFeedbackRepositorio feedbackRepositorio;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public AcompanhamentoAplicacaoRepositorio(
			IAcompanhamentoRepositorioJpa repositorioJpa,
			IAcompanhamentoRepositorio acompanhamentoRepositorio,
			IDisciplinaRepositorio disciplinaRepositorio,
			ISessaoRepositorio sessaoRepositorio,
			IFaltaRepositorio faltaRepositorio,
			IFeedbackRepositorio feedbackRepositorio,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.acompanhamentoRepositorio = Objects.requireNonNull(acompanhamentoRepositorio);
		this.disciplinaRepositorio = Objects.requireNonNull(disciplinaRepositorio);
		this.sessaoRepositorio = Objects.requireNonNull(sessaoRepositorio);
		this.faltaRepositorio = Objects.requireNonNull(faltaRepositorio);
		this.feedbackRepositorio = Objects.requireNonNull(feedbackRepositorio);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<AcompanhamentoProjecao> pesquisar(
			AcompanhamentoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		Long usuarioId = usuarioAtualId();
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		boolean professor = seguranca.possuiAutorizacao(Autorizacao.PROFESSOR.name());
		boolean aluno = seguranca.possuiAutorizacao(Autorizacao.ALUNO.name());
		return repositorioJpa
				.pesquisar(
						coordenador,
						professor,
						aluno,
						usuarioId,
						paraLike(consulta.getTexto()),
						pageable)
				.map(this::enriquecer);
	}

	@Override
	public List<AlunoDisciplinaOpcaoProjecao> listarOpcoesAbertura() {
		return repositorioJpa.listarOpcoesAbertura(usuarioAtualId());
	}

	@Override
	protected String paraPropriedadeOrdenacao(AcompanhamentoOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case ALUNO -> "aluno.nome";
		};
	}

	private AcompanhamentoProjecao enriquecer(AcompanhamentoProjecao base) {
		Acompanhamento acompanhamento =
				acompanhamentoRepositorio.buscarPorIdentificador(base.identificador());
		Disciplina disciplina =
				disciplinaRepositorio.buscarPorIdentificador(base.disciplina());
		Collection<Sessao> sessoes = listarSessoesDisciplina(disciplina);
		Collection<Falta> faltas = listarFaltasDisciplina(disciplina);
		Collection<Feedback> feedbacks =
				feedbackRepositorio.listarPorAlunoEDisciplina(base.aluno(), base.disciplina());
		RelatorioDesempenho relatorio =
				RelatorioDesempenho.calcular(
						acompanhamento,
						sessoes,
						faltas,
						feedbacks,
						Optional.empty(),
						Optional.empty(),
						relogio.agora());
		boolean possuiFeedback =
				feedbackRepositorio.existePorAlunoEDisciplina(base.aluno(), base.disciplina());
		return base.comResumo(
				possuiFeedback,
				relatorio.frequenciaPercentual(),
				relatorio.notaInicial(),
				relatorio.notaAtual(),
				relatorio.tendencia(),
				relatorio.situacao());
	}

	private Collection<Sessao> listarSessoesDisciplina(Disciplina disciplina) {
		Map<SessaoIdentificador, Sessao> mapa = new LinkedHashMap<>();
		for (Sessao s : sessaoRepositorio.listarPorDisciplina(disciplina.getNome())) {
			s.getIdentificador().ifPresent(id -> mapa.put(id, s));
		}
		for (Sessao s :
				sessaoRepositorio.listarPorDisciplina(disciplina.getCodigo().toString())) {
			s.getIdentificador().ifPresent(id -> mapa.put(id, s));
		}
		return mapa.values();
	}

	private Collection<Falta> listarFaltasDisciplina(Disciplina disciplina) {
		Map<FaltaIdentificador, Falta> mapa = new LinkedHashMap<>();
		for (Falta f : faltaRepositorio.listarPorDisciplina(disciplina.getNome())) {
			f.getIdentificador().ifPresent(id -> mapa.put(id, f));
		}
		for (Falta f :
				faltaRepositorio.listarPorDisciplina(disciplina.getCodigo().toString())) {
			f.getIdentificador().ifPresent(id -> mapa.put(id, f));
		}
		return mapa.values();
	}

	private Long usuarioAtualId() {
		return Long.valueOf(
				usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()))
						.getIdentificador()
						.orElseThrow()
						.toString());
	}
}
