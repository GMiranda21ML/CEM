package br.com.cem.infraestrutura.monitoria.alerta;

import br.com.cem.aplicacao.monitoria.alerta.AlertaConsulta;
import br.com.cem.aplicacao.monitoria.alerta.AlertaOrdenacao;
import br.com.cem.aplicacao.monitoria.alerta.AlertaProjecao;
import br.com.cem.aplicacao.monitoria.alerta.spi.IAlertaAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.Locale;

public class AlertaAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<
				AlertaProjecao, AlertaOrdenacao, AlertaConsulta>
		implements IAlertaAplicacaoRepositorio {
	private final IAlertaRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public AlertaAplicacaoRepositorio(
			IAlertaRepositorioJpa repositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<AlertaProjecao> pesquisar(AlertaConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		if (!coordenador) return Page.empty(pageable);
		var usuario = usuarios.buscarPorEmail(new UsuarioEmail(seguranca.getUsuario()));
		var curso = usuario.getCurso();
		if (!usuario.estaAtivo() || usuario.getPapel() != Autorizacao.COORDENADOR
				|| curso == null || curso.isBlank()) return Page.empty(pageable);
		var dominio = usuario.getEmailInstitucional().getDominio();
		return repositorioJpa.pesquisar(true, curso.strip().toLowerCase(Locale.ROOT),
				"%@" + dominio, "%." + dominio, paraLike(consulta.getTexto()), pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(AlertaOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case TEXTO -> "disciplina";
		};
	}
}
