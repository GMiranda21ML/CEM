package br.com.cem.dominio.monitoria.falta.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IFaltaRepositorio extends IAgregadoRepositorio<FaltaIdentificador, Falta> {
	boolean existePorSessaoEPessoa(SessaoIdentificador sessao, UsuarioIdentificador pessoa);

	long contar(UsuarioIdentificador pessoa, String papel, String disciplina);
}
