package br.com.cem.dominio.acesso.dominioemail.spi;

import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IDominioEmailRepositorio extends IAgregadoRepositorio<DominioEmailDominio, DominioEmail> {
}
