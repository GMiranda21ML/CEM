package br.com.cem.infraestrutura.comunicacao.aviso;

import br.com.cem.aplicacao.comunicacao.aviso.AvisoConsulta;
import br.com.cem.aplicacao.comunicacao.aviso.AvisoOrdenacao;
import br.com.cem.aplicacao.comunicacao.aviso.AvisoProjecao;
import br.com.cem.aplicacao.comunicacao.aviso.spi.IAvisoAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class AvisoAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<AvisoProjecao, AvisoOrdenacao, AvisoConsulta>
		implements IAvisoAplicacaoRepositorio {
	private final IAvisoRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public AvisoAplicacaoRepositorio(
			IAvisoRepositorioJpa repositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<AvisoProjecao> pesquisar(AvisoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		return repositorioJpa.pesquisar(
				coordenador,
				relogio.agora().toLocalDate(),
				paraLike(consulta.getTexto()),
				pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(AvisoOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case TEXTO -> "titulo";
		};
	}
}
