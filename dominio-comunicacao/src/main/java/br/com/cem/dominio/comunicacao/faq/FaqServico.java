package br.com.cem.dominio.comunicacao.faq;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.comunicacao.faq.spi.IFaqRepositorio;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Optional;

public final class FaqServico
		extends AgregadoServicoAbstrato<FaqIdentificador, Faq, AgregadoAutorizacoes<Faq>> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe faq com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe faq com o identificador %s";
	public static final String PERGUNTA_EXISTENTE_MODELO = "Já existe a pergunta %s no FAQ";
	private final IFaqRepositorio repositorio;

	public FaqServico(ISegurancaServico seguranca, IFaqRepositorio repositorio) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
	}

	@Override
	public AgregadoAutorizacoes<Faq> criarAutorizacoes(Optional<FaqIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coord = papel(Autorizacao.COORDENADOR);
		boolean visivel =
				identificador.isEmpty()
						|| !repositorio.existePorIdentificador(identificador.get())
						|| repositorio.buscarPorIdentificador(identificador.get()).getAtivo();
		return new AgregadoAutorizacoes<>(coord, coord || visivel, coord, coord);
	}

	private boolean papel(Autorizacao papel) {
		return getSegurancaServico().possuiAutorizacao(papel.name());
	}

	@Override
	protected void validarInclusao(Faq a) {
		Objects.requireNonNull(a);
		Validate.isTrue(a.getIdentificador().isEmpty(), "Identificador gerado automaticamente");
	}

	@Override
	protected void validarInclusaoAlteracao(Faq a) {
		Objects.requireNonNull(a);
		validarInexistencia(
				p -> repositorio.existePorPergunta(p, a.getIdentificador()),
				a.getPergunta(),
				PERGUNTA_EXISTENTE_MODELO);
	}
}
