package br.com.cem.aplicacao.monitoria.avaliacao;

import java.util.List;
import java.util.Objects;

import br.com.cem.aplicacao.monitoria.avaliacao.spi.IAvaliacaoAplicacaoRepositorio;
import br.com.cem.aplicacao.monitoria.avaliacao.spi.ISessaoPendenteAplicacaoRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoAutorizacoes;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoServico;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class AvaliacaoAplicacaoServico extends
		AgregadoAplicacaoServicoAbstrato<AvaliacaoIdentificador, Avaliacao, AvaliacaoAutorizacoes, AvaliacaoProjecao, AvaliacaoConsulta> {
	private final IAvaliacaoAplicacaoRepositorio repositorio;
	private final ISessaoPendenteAplicacaoRepositorio sessaoPendenteRepositorio;
	private final AvaliacaoServico servico;
	private final ISegurancaServico segurancaServico;
	private final IUsuarioRepositorio usuarioRepositorio;

	public AvaliacaoAplicacaoServico(IAvaliacaoAplicacaoRepositorio repositorio,
			ISessaoPendenteAplicacaoRepositorio sessaoPendenteRepositorio, AvaliacaoServico servico,
			ISegurancaServico segurancaServico, IUsuarioRepositorio usuarioRepositorio) {
		super(repositorio, servico);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.sessaoPendenteRepositorio = Objects.requireNonNull(sessaoPendenteRepositorio);
		this.servico = Objects.requireNonNull(servico);
		this.segurancaServico = Objects.requireNonNull(segurancaServico);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
	}

	public AvaliacaoSessaoResultado pesquisarDaSessao(SessaoIdentificador sessao, AvaliacaoConsulta consulta) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(consulta);
		servico.validarConsultaDaSessao(sessao);
		return new AvaliacaoSessaoResultado(repositorio.pesquisarDaSessao(consulta, sessao),
				repositorio.calcularMedia(sessao));
	}

	public List<SessaoPendenteProjecao> pesquisarPendentes() {
		return sessaoPendenteRepositorio.pesquisar(usuarioAtual());
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioRepositorio.buscarPorEmail(new UsuarioEmail(segurancaServico.getUsuario())).getIdentificador()
				.orElseThrow();
	}
}
