package br.com.cem.dominio.comunicacao.faq.spi;

import br.com.cem.dominio.comunicacao.faq.Faq;
import br.com.cem.dominio.comunicacao.faq.FaqIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.util.Optional;

public interface IFaqRepositorio extends IAgregadoRepositorio<FaqIdentificador, Faq> {
	boolean existePorPergunta(String pergunta, Optional<FaqIdentificador> ignorado);
}
