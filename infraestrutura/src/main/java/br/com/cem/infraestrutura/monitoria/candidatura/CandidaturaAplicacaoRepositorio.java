package br.com.cem.infraestrutura.monitoria.candidatura;

import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaConsulta;
import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaOrdenacao;
import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaProjecao;
import br.com.cem.aplicacao.monitoria.candidatura.SelecaoAbertaProjecao;
import br.com.cem.aplicacao.monitoria.candidatura.spi.ICandidaturaAplicacaoRepositorio;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.dominio.spi.IRelogioServico;
import br.com.cem.infraestrutura.monitoria.selecao.ISelecaoRepositorioJpa;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CandidaturaAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<
				CandidaturaProjecao, CandidaturaOrdenacao, CandidaturaConsulta>
		implements ICandidaturaAplicacaoRepositorio {
	private final ICandidaturaRepositorioJpa repositorioJpa;
	private final ISelecaoRepositorioJpa selecaoRepositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public CandidaturaAplicacaoRepositorio(
			ICandidaturaRepositorioJpa repositorioJpa,
			ISelecaoRepositorioJpa selecaoRepositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.selecaoRepositorioJpa = Objects.requireNonNull(selecaoRepositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<CandidaturaProjecao> pesquisar(
			CandidaturaConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		Long usuarioId = Long.valueOf(usuarioLogado().getIdentificador().orElseThrow().toString());
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		boolean professor = seguranca.possuiAutorizacao(Autorizacao.PROFESSOR.name());
		boolean aluno = seguranca.possuiAutorizacao(Autorizacao.ALUNO.name());
		return repositorioJpa.pesquisar(
				coordenador,
				professor,
				aluno,
				usuarioId,
				consulta.getSelecao().map(id -> Long.valueOf(id.toString())).orElse(null),
				paraLike(consulta.getTexto()),
				pageable);
	}

	@Override
	public List<SelecaoAbertaProjecao> listarSelecoesAbertasAluno() {
		Usuario usuario = usuarioLogado();
		Long alunoId = Long.valueOf(usuario.getIdentificador().orElseThrow().toString());
		LocalDate hoje = relogio.agora().toLocalDate();
		List<SelecaoProjecao> selecoes =
				repositorioJpa.listarSelecoesAbertasCurso(usuario.getCurso(), hoje);
		List<CandidaturaProjecao> ativas = repositorioJpa.listarAtivasDoAluno(alunoId);
		return selecoes.stream()
				.map(s -> construirSelecaoAberta(s, ativas, hoje))
				.toList();
	}

	private SelecaoAbertaProjecao construirSelecaoAberta(
			SelecaoProjecao selecao, List<CandidaturaProjecao> ativas, LocalDate hoje) {
		SelecaoProjecao enriquecida =
				selecao.comMonitores(
						selecaoRepositorioJpa.listarNomesMonitoresAprovados(
								Long.valueOf(selecao.identificador().toString())));
		boolean cursoCompativel = true;
		long monitoriasNoSemestre =
				ativas.stream()
						.filter(
								c ->
										c.situacao() == CandidaturaSituacao.APROVADA
												&& c.semestre().equals(enriquecida.semestre()))
						.map(CandidaturaProjecao::disciplina)
						.distinct()
						.count();
		boolean limiteDisponivel = monitoriasNoSemestre < 2;
		Optional<CandidaturaProjecao> minhaCandidatura =
				ativas.stream()
						.filter(c -> c.disciplina().equals(enriquecida.disciplina()))
						.findFirst();
		boolean semCandidaturaAtiva = minhaCandidatura.isEmpty();
		Optional<String> situacaoCandidaturaAtiva =
				minhaCandidatura.map(c -> c.situacao().name());
		boolean dentroDoPrazo =
				enriquecida.situacao() == SelecaoSituacao.ABERTA
						&& !hoje.isAfter(enriquecida.prazoInscricao());
		boolean elegivel =
				cursoCompativel && limiteDisponivel && semCandidaturaAtiva && dentroDoPrazo;
		return new SelecaoAbertaProjecao(
				enriquecida,
				elegivel,
				cursoCompativel,
				limiteDisponivel,
				monitoriasNoSemestre,
				semCandidaturaAtiva,
				situacaoCandidaturaAtiva,
				dentroDoPrazo,
				minhaCandidatura);
	}

	@Override
	protected String paraPropriedadeOrdenacao(CandidaturaOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case CRIACAO -> "criadaEm";
		};
	}

	private Usuario usuarioLogado() {
		return usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()));
	}
}
