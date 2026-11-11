package br.com.cem.infraestrutura.relogio;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.cem.dominio.spi.IRelogioServico;

@Component
public final class RelogioSistemaServico implements IRelogioServico {
	@Override
	public LocalDateTime agora() {
		return LocalDateTime.now();
	}
}
