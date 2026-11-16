package br.com.cem.dominio.monitoria.disponibilidade.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.util.Collection;

public interface IDisponibilidadeRepositorio
		extends IAgregadoRepositorio<DisponibilidadeIdentificador, Disponibilidade> {
	Collection<Disponibilidade> buscarDoMonitor(UsuarioIdentificador monitor, String disciplina);

	Collection<Disponibilidade> listarDoMonitor(UsuarioIdentificador monitor);
}
