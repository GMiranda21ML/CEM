package br.com.cem.infraestrutura.monitoria.feedback;

import br.com.cem.aplicacao.monitoria.feedback.FeedbackConsulta;
import br.com.cem.aplicacao.monitoria.feedback.FeedbackOrdenacao;
import br.com.cem.aplicacao.monitoria.feedback.FeedbackProjecao;
import br.com.cem.aplicacao.monitoria.feedback.spi.IFeedbackAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class FeedbackAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<
				FeedbackProjecao, FeedbackOrdenacao, FeedbackConsulta>
		implements IFeedbackAplicacaoRepositorio {
	private final IFeedbackRepositorioJpa repositorioJpa;
	private final ISessaoRepositorio sessaoRepositorio;
	private final IAcompanhamentoRepositorio acompanhamentoRepositorio;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;

	public FeedbackAplicacaoRepositorio(
			IFeedbackRepositorioJpa repositorioJpa,
			ISessaoRepositorio sessaoRepositorio,
			IAcompanhamentoRepositorio acompanhamentoRepositorio,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.sessaoRepositorio = Objects.requireNonNull(sessaoRepositorio);
		this.acompanhamentoRepositorio = Objects.requireNonNull(acompanhamentoRepositorio);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
	}

	@Override
	protected Page<FeedbackProjecao> pesquisar(FeedbackConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		Long usuarioId =
				Long.valueOf(
						usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()))
								.getIdentificador()
								.orElseThrow()
								.toString());
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
				.map(this::preencherBloqueio);
	}

	private FeedbackProjecao preencherBloqueio(FeedbackProjecao p) {
		Sessao sessao = sessaoRepositorio.buscarPorIdentificador(p.sessao());
		boolean bloqueado =
				acompanhamentoRepositorio.existeBloqueioPorEncerramento(
						p.aluno(), sessao.getDisciplina(), sessao.getProfessor());
		return new FeedbackProjecao(
				p.identificador(),
				p.sessao(),
				p.sessaoInicio(),
				p.disciplinaNome(),
				p.aluno(),
				p.alunoNome(),
				p.autor(),
				p.nota(),
				p.comentario(),
				p.criadoEm(),
				p.atualizadoEm(),
				bloqueado);
	}

	@Override
	protected String paraPropriedadeOrdenacao(FeedbackOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case CRIACAO -> "criadoEm";
		};
	}
}
