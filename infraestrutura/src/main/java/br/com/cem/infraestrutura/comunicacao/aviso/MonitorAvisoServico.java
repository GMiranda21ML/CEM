package br.com.cem.infraestrutura.comunicacao.aviso;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.aviso.spi.IMonitorAvisoServico;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class MonitorAvisoServico implements IMonitorAvisoServico {
	private final ISessaoRepositorio sessoes;

	public MonitorAvisoServico(ISessaoRepositorio sessoes) {
		this.sessoes = Objects.requireNonNull(sessoes);
	}

	@Override
	public boolean eMonitor(UsuarioIdentificador usuario) {
		return sessoes.existePorMonitor(Objects.requireNonNull(usuario));
	}
}
