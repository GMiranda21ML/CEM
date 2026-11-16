package br.com.cem.infraestrutura.monitoria.sessao;

import br.com.cem.aplicacao.monitoria.sessao.ContextoAgenda;
import br.com.cem.aplicacao.monitoria.sessao.SessaoConsulta;
import br.com.cem.aplicacao.monitoria.sessao.SessaoOrdenacao;
import br.com.cem.aplicacao.monitoria.sessao.SessaoProjecao;
import br.com.cem.aplicacao.monitoria.sessao.spi.ISessaoAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class SessaoAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<
				SessaoProjecao, SessaoOrdenacao, SessaoConsulta>
		implements ISessaoAplicacaoRepositorio {
	@Override
	public ContextoAgenda contexto() {
		var usuario =
				usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()))
						;
		var identificador = usuario.getIdentificador().orElseThrow();
		return new ContextoAgenda(
				identificador.toString(),
				repositorioJpa.pessoas(
						seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name()),
						Long.valueOf(identificador.toString()),
						usuario.getCurso()),
				repositorioJpa.campusAtivos(),
				usuario.getCurso());
	}

	private final ISessaoRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public SessaoAplicacaoRepositorio(
			ISessaoRepositorioJpa repositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<SessaoProjecao> pesquisar(SessaoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		return repositorioJpa.pesquisar(
				coordenador,
				Long.valueOf(
						usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()))
								.getIdentificador()
								.orElseThrow()
								.toString()),
				paraLike(consulta.getTexto()),
				pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(SessaoOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case TEXTO -> "disciplina";
		};
	}
}
