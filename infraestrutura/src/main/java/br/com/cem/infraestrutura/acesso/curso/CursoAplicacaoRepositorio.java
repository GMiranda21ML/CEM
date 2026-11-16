package br.com.cem.infraestrutura.acesso.curso;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.cem.aplicacao.acesso.curso.CursoConsulta;
import br.com.cem.aplicacao.acesso.curso.CursoOrdenacao;
import br.com.cem.aplicacao.acesso.curso.CursoProjecao;
import br.com.cem.aplicacao.acesso.curso.spi.ICursoAplicacaoRepositorio;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class CursoAplicacaoRepositorio extends AgregadoAplicacaoRepositorioAbstrato<CursoProjecao, CursoOrdenacao, CursoConsulta>
		implements ICursoAplicacaoRepositorio {
	private final ICursoRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IInstituicaoUsuarioServico instituicao;

	public CursoAplicacaoRepositorio(ICursoRepositorioJpa repositorioJpa, ISegurancaServico seguranca,
			IInstituicaoUsuarioServico instituicao) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.instituicao = Objects.requireNonNull(instituicao);
	}

	@Override protected Page<CursoProjecao> pesquisar(CursoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta); Objects.requireNonNull(pageable);
		var dominio = instituicao.buscarPorUsuario(seguranca.getUsuario()).toString();
		return repositorioJpa.pesquisar(dominio, paraLike(consulta.getTexto()), pageable);
	}

	@Override protected String paraPropriedadeOrdenacao(CursoOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);
		return switch (ordenacao) { case NOME -> "nome"; case SIGLA -> "sigla"; };
	}
}
