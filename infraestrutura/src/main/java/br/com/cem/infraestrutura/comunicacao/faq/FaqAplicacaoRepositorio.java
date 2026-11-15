package br.com.cem.infraestrutura.comunicacao.faq;

import br.com.cem.aplicacao.comunicacao.faq.FaqConsulta;
import br.com.cem.aplicacao.comunicacao.faq.FaqOrdenacao;
import br.com.cem.aplicacao.comunicacao.faq.FaqProjecao;
import br.com.cem.aplicacao.comunicacao.faq.spi.IFaqAplicacaoRepositorio;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class FaqAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<FaqProjecao, FaqOrdenacao, FaqConsulta>
		implements IFaqAplicacaoRepositorio {
	private final IFaqRepositorioJpa repositorioJpa;
	private final ISegurancaServico seguranca;
	private final IUsuarioRepositorio usuarios;
	private final IRelogioServico relogio;

	public FaqAplicacaoRepositorio(
			IFaqRepositorioJpa repositorioJpa,
			ISegurancaServico seguranca,
			IUsuarioRepositorio usuarios,
			IRelogioServico relogio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.seguranca = Objects.requireNonNull(seguranca);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	protected Page<FaqProjecao> pesquisar(FaqConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		boolean coordenador = seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name());
		return repositorioJpa.pesquisar(coordenador, paraLike(consulta.getTexto()), pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(FaqOrdenacao ordenacao) {
		return switch (Objects.requireNonNull(ordenacao)) {
			case IDENTIFICADOR -> "identificador";
			case TEXTO -> "pergunta";
		};
	}
}
