package br.com.cem.infraestrutura.comunicacao.faq;

import br.com.cem.dominio.comunicacao.faq.Faq;
import br.com.cem.dominio.comunicacao.faq.FaqIdentificador;
import br.com.cem.dominio.comunicacao.faq.FaqServico;
import br.com.cem.dominio.comunicacao.faq.spi.IFaqRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class FaqRepositorio extends AgregadoRepositorioAbstrato<FaqIdentificador, Faq, Long, FaqJpa>
		implements IFaqRepositorio {
	private final IFaqRepositorioJpa repositorioJpa;

	public FaqRepositorio(IFaqRepositorioJpa repositorioJpa) {
		super(repositorioJpa, FaqServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected FaqJpa paraJpa(Faq a) {
		Objects.requireNonNull(a);
		return new FaqJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				a.getPergunta(),
				a.getResposta(),
				a.getAtivo());
	}

	@Override
	protected Faq paraAgregado(FaqJpa a) {
		Objects.requireNonNull(a);
		return new Faq(
				Optional.of(
						new FaqIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				a.getPergunta(),
				a.getResposta(),
				a.getAtivo());
	}

	@Override
	protected Long paraIdentificadorJpa(FaqIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					FaqServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(FaqIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}

	@Override
	public boolean existePorPergunta(String pergunta, Optional<FaqIdentificador> ignorado) {
		return repositorioJpa.existePergunta(
				Objects.requireNonNull(pergunta).strip().toLowerCase(Locale.ROOT),
				Objects.requireNonNull(ignorado).map(this::paraIdentificadorJpa).orElse(null));
	}
}
