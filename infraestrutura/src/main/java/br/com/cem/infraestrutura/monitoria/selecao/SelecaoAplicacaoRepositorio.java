package br.com.cem.infraestrutura.monitoria.selecao;

import br.com.cem.aplicacao.monitoria.selecao.DisciplinaProfessorProjecao;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoConsulta;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoOrdenacao;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;
import br.com.cem.aplicacao.monitoria.selecao.spi.ISelecaoAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

public class SelecaoAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<
				SelecaoProjecao, SelecaoOrdenacao, SelecaoConsulta>
		implements ISelecaoAplicacaoRepositorio {
	private final ISelecaoRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public SelecaoAplicacaoRepositorio(
			ISelecaoRepositorioJpa repositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<SelecaoProjecao> pesquisar(SelecaoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		Usuario usuario = usuarioLogado();
		Long usuarioId = Long.valueOf(usuario.getIdentificador().orElseThrow().toString());
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		boolean professor = seguranca.possuiAutorizacao(Autorizacao.PROFESSOR.name());
		boolean aluno = seguranca.possuiAutorizacao(Autorizacao.ALUNO.name());
		return repositorioJpa
				.pesquisar(
						coordenador,
						professor,
						aluno,
						usuarioId,
						usuario.getCurso(),
						relogio.agora().toLocalDate(),
						paraLike(consulta.getTexto()),
						pageable)
				.map(
						p ->
								p.comMonitores(
										repositorioJpa.listarNomesMonitoresAprovados(
												Long.valueOf(p.identificador().toString()))));
	}

	@Override
	public List<DisciplinaProfessorProjecao> listarDisciplinasDoProfessor() {
		Long usuarioId = Long.valueOf(usuarioLogado().getIdentificador().orElseThrow().toString());
		return repositorioJpa.listarDisciplinasDoProfessor(usuarioId);
	}

	@Override
	protected String paraPropriedadeOrdenacao(SelecaoOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case PRAZO -> "prazoInscricao";
		};
	}

	private Usuario usuarioLogado() {
		return usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()));
	}
}
