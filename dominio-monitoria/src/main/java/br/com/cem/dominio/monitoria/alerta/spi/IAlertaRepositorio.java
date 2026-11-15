package br.com.cem.dominio.monitoria.alerta.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.alerta.Alerta;
import br.com.cem.dominio.monitoria.alerta.AlertaIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IAlertaRepositorio extends IAgregadoRepositorio<AlertaIdentificador, Alerta> {
	boolean existeNovo(UsuarioIdentificador pessoa, String papel, String disciplina);
}
