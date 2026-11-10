package br.com.cem.infraestrutura.acesso.convite;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.cem.aplicacao.acesso.convite.ConviteConsulta;
import br.com.cem.aplicacao.acesso.convite.ConviteOrdenacao;
import br.com.cem.aplicacao.acesso.convite.ConviteProjecao;
import br.com.cem.aplicacao.acesso.convite.spi.IConviteAplicacaoRepositorio;

import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class ConviteAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<ConviteProjecao, ConviteOrdenacao, ConviteConsulta>
		implements IConviteAplicacaoRepositorio {
	private final IConviteRepositorioJpa repositorioJpa;

	public ConviteAplicacaoRepositorio(IConviteRepositorioJpa repositorioJpa) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected Page<ConviteProjecao> pesquisar(ConviteConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);

		var situacao = consulta.getSituacao().orElse(null);
		return repositorioJpa.pesquisar(situacao, pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(ConviteOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);

		return switch (ordenacao) {
		case EMAIL -> "email";
		case ENVIADO_EM -> "enviadoEm";
		};
	}
}
