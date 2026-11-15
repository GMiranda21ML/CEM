package br.com.cem.dominio.comunicacao.aviso.spi;

import br.com.cem.dominio.comunicacao.aviso.Aviso;
import br.com.cem.dominio.comunicacao.aviso.AvisoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IAvisoRepositorio extends IAgregadoRepositorio<AvisoIdentificador, Aviso> {}
