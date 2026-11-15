package br.com.cem.ui.backend.comunicacao.pergunta;

import br.com.cem.aplicacao.comunicacao.pergunta.PerguntaAplicacaoServico;
import br.com.cem.aplicacao.comunicacao.pergunta.PerguntaConsulta;
import br.com.cem.aplicacao.comunicacao.pergunta.PerguntaProjecao;
import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaAutorizacoes;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaIdentificador;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "comunicacao/pergunta")
public class PerguntaControlador
		extends AgregadoControladorAbstrato<
				PerguntaIdentificador,
				Pergunta,
				PerguntaAutorizacoes,
				PerguntaProjecao,
				PerguntaConsulta> {
	private final PerguntaServico servico;

	public PerguntaControlador(PerguntaServico servico, PerguntaAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("enviar")
	public Pergunta enviar(@RequestBody EnviarPerguntaRequisicao dados) {
		Objects.requireNonNull(dados);
		return servico.enviar(dados.texto());
	}

	@PostMapping("{identificador}/editar")
	public void editar(
			@PathVariable PerguntaIdentificador identificador,
			@RequestBody EditarPerguntaRequisicao dados) {
		Objects.requireNonNull(dados);
		servico.editar(identificador, dados.texto());
	}

	@PostMapping("{identificador}/responder")
	public void responder(
			@PathVariable PerguntaIdentificador identificador,
			@RequestBody ResponderPerguntaRequisicao dados) {
		Objects.requireNonNull(dados);
		servico.responder(identificador, dados.pergunta(), dados.resposta(), dados.ativo());
	}

	@PostMapping("{identificador}/descartar")
	public void descartar(@PathVariable PerguntaIdentificador identificador) {
		servico.descartar(identificador);
	}
}
