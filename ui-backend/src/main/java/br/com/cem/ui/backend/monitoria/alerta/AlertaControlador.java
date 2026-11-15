package br.com.cem.ui.backend.monitoria.alerta;

import br.com.cem.aplicacao.monitoria.alerta.AlertaAplicacaoServico;
import br.com.cem.aplicacao.monitoria.alerta.AlertaConsulta;
import br.com.cem.aplicacao.monitoria.alerta.AlertaProjecao;
import br.com.cem.dominio.monitoria.alerta.Alerta;
import br.com.cem.dominio.monitoria.alerta.AlertaAutorizacoes;
import br.com.cem.dominio.monitoria.alerta.AlertaIdentificador;
import br.com.cem.dominio.monitoria.alerta.AlertaServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/alerta")
public class AlertaControlador
		extends AgregadoControladorAbstrato<
				AlertaIdentificador, Alerta, AlertaAutorizacoes, AlertaProjecao, AlertaConsulta> {
	private final AlertaServico servico;

	public AlertaControlador(AlertaServico servico, AlertaAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("{identificador}/acompanhar")
	public void acompanhar(@PathVariable AlertaIdentificador identificador) {
		servico.acompanhar(identificador);
	}

	@GetMapping("{identificador}/contagem")
	public long contagem(@PathVariable AlertaIdentificador identificador) {
		return servico.contar(identificador);
	}
}
