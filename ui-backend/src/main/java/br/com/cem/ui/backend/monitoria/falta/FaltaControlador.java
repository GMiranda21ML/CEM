package br.com.cem.ui.backend.monitoria.falta;

import br.com.cem.aplicacao.monitoria.falta.FaltaAplicacaoServico;
import br.com.cem.aplicacao.monitoria.falta.FaltaConsulta;
import br.com.cem.aplicacao.monitoria.falta.FaltaProjecao;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaAutorizacoes;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.falta.FaltaServico;
import br.com.cem.dominio.monitoria.falta.SugestaoReposicao;
import br.com.cem.dominio.monitoria.sessao.Sessao;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/falta")
public class FaltaControlador
		extends AgregadoControladorAbstrato<
				FaltaIdentificador, Falta, FaltaAutorizacoes, FaltaProjecao, FaltaConsulta> {
	private final FaltaServico servico;

	public FaltaControlador(FaltaServico servico, FaltaAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("registrar")
	public Falta registrar(@RequestBody RegistrarFaltaRequisicao dados) {
		Objects.requireNonNull(dados);
		return servico.registrar(dados.sessao(), dados.pessoa(), dados.papel(), dados.observacao());
	}

	@PostMapping("{identificador}/editar")
	public void editar(
			@PathVariable FaltaIdentificador identificador,
			@RequestBody EditarFaltaRequisicao dados) {
		Objects.requireNonNull(dados);
		servico.editar(identificador, dados.observacao());
	}

	@GetMapping("{identificador}/sugestao")
	public List<SugestaoReposicao> sugestao(@PathVariable FaltaIdentificador identificador) {
		return servico.sugerir(identificador);
	}

	@PostMapping("{identificador}/agendar")
	public Sessao agendar(
			@PathVariable FaltaIdentificador identificador, @RequestBody SugestaoReposicao dados) {
		return servico.agendar(identificador, dados);
	}
}
