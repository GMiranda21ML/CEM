package br.com.cem.dominio.monitoria.avaliacao.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IAvaliacaoRepositorio extends IAgregadoRepositorio<AvaliacaoIdentificador, Avaliacao> {
	boolean existePorSessaoEAutor(SessaoIdentificador sessao, UsuarioIdentificador autor);
}
