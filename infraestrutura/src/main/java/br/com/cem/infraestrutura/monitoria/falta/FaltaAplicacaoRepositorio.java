package br.com.cem.infraestrutura.monitoria.falta;

import br.com.cem.aplicacao.monitoria.falta.FaltaConsulta;
import br.com.cem.aplicacao.monitoria.falta.FaltaOrdenacao;
import br.com.cem.aplicacao.monitoria.falta.FaltaProjecao;
import br.com.cem.aplicacao.monitoria.falta.spi.IFaltaAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class FaltaAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<FaltaProjecao, FaltaOrdenacao, FaltaConsulta>
		implements IFaltaAplicacaoRepositorio {
	private final IFaltaRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public FaltaAplicacaoRepositorio(
			IFaltaRepositorioJpa repositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<FaltaProjecao> pesquisar(FaltaConsulta consulta, Pageable pageable) {
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
	protected String paraPropriedadeOrdenacao(FaltaOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case TEXTO -> "observacao";
		};
	}
}
