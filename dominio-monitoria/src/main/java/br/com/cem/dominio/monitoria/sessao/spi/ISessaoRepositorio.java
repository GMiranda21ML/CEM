package br.com.cem.dominio.monitoria.sessao.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.time.LocalDateTime;

public interface ISessaoRepositorio extends IAgregadoRepositorio<SessaoIdentificador, Sessao> {
	boolean existePorMonitor(UsuarioIdentificador monitor);

	boolean conflita(
			UsuarioIdentificador monitor,
			UsuarioIdentificador aluno,
			LocalDateTime inicio,
			LocalDateTime fim);

	void bloquearAgenda(UsuarioIdentificador monitor);
}
