package br.com.cem.dominio.monitoria.sessao.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ISessaoRepositorio extends IAgregadoRepositorio<SessaoIdentificador, Sessao> {
	boolean existePorMonitor(UsuarioIdentificador monitor);

	default Collection<Sessao> listarPorDisciplina(String disciplina) {
		Objects.requireNonNull(disciplina);
		return List.of();
	}

	boolean conflita(
			UsuarioIdentificador monitor,
			UsuarioIdentificador aluno,
			LocalDateTime inicio,
			LocalDateTime fim);

	default boolean conflita(
			UsuarioIdentificador monitor,
			UsuarioIdentificador aluno,
			LocalDateTime inicio,
			LocalDateTime fim,
			Optional<SessaoIdentificador> identificadorIgnorado) {
		Objects.requireNonNull(identificadorIgnorado);
		return conflita(monitor, aluno, inicio, fim);
	}

	void bloquearAgenda(UsuarioIdentificador monitor);
}
