package br.com.cem.ui.backend.monitoria.disponibilidade;

import br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeAplicacaoServico;
import br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeConsulta;
import br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeProjecao;
import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeIdentificador;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/disponibilidade")
public class DisponibilidadeControlador
		extends AgregadoControladorAbstrato<
				DisponibilidadeIdentificador,
				Disponibilidade,
				AgregadoAutorizacoes<Disponibilidade>,
				DisponibilidadeProjecao,
				DisponibilidadeConsulta> {
	private final DisponibilidadeServico servico;

	public DisponibilidadeControlador(
			DisponibilidadeServico servico, DisponibilidadeAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}
}
