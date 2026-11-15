package br.com.cem.dominio.comunicacao.pergunta.spi;

import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IPerguntaRepositorio
		extends IAgregadoRepositorio<PerguntaIdentificador, Pergunta> {
	void bloquear(PerguntaIdentificador identificador);
}
