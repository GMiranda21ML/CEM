package br.com.cem.infraestrutura.acesso.disciplina;

import java.util.Objects;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.cem.aplicacao.acesso.disciplina.DisciplinaConsulta;
import br.com.cem.aplicacao.acesso.disciplina.DisciplinaOrdenacao;
import br.com.cem.aplicacao.acesso.disciplina.DisciplinaProjecao;
import br.com.cem.aplicacao.acesso.disciplina.ProfessorProjecao;
import br.com.cem.aplicacao.acesso.disciplina.spi.IDisciplinaAplicacaoRepositorio;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class DisciplinaAplicacaoRepositorio extends AgregadoAplicacaoRepositorioAbstrato<DisciplinaProjecao, DisciplinaOrdenacao, DisciplinaConsulta>
		implements IDisciplinaAplicacaoRepositorio {
	private final IDisciplinaRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IInstituicaoUsuarioServico instituicao;

	public DisciplinaAplicacaoRepositorio(IDisciplinaRepositorioJpa repositorioJpa, ISegurancaServico seguranca,
			IInstituicaoUsuarioServico instituicao) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.instituicao = Objects.requireNonNull(instituicao);
	}

	@Override protected Page<DisciplinaProjecao> pesquisar(DisciplinaConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta); Objects.requireNonNull(pageable);
		var cursoId = consulta.getCurso().map(id -> {
			try { return Long.valueOf(id.toString()); }
			catch (NumberFormatException excecao) { return -1L; }
		}).orElse(null);
		return repositorioJpa.pesquisar(instituicao.buscarPorUsuario(seguranca.getUsuario()).toString(),
				paraLike(consulta.getTexto()), cursoId, pageable);
	}

	@Override protected String paraPropriedadeOrdenacao(DisciplinaOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);
		return switch (ordenacao) { case NOME -> "nome"; case CODIGO -> "codigo"; case AREA -> "area"; };
	}

	@Override public List<ProfessorProjecao> buscarProfessores(CursoIdentificador curso) {
		Objects.requireNonNull(curso);
		Long id;
		try { id = Long.valueOf(curso.toString()); }
		catch (NumberFormatException excecao) { return List.of(); }
		return repositorioJpa.buscarProfessores(id, instituicao.buscarPorUsuario(seguranca.getUsuario()).toString());
	}
}
