package br.com.cem.ui.backend.monitoria.feedback;

import br.com.cem.aplicacao.monitoria.feedback.FeedbackAplicacaoServico;
import br.com.cem.aplicacao.monitoria.feedback.FeedbackConsulta;
import br.com.cem.aplicacao.monitoria.feedback.FeedbackProjecao;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackAutorizacoes;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.feedback.FeedbackServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/feedback")
public class FeedbackControlador
		extends AgregadoControladorAbstrato<
				FeedbackIdentificador,
				Feedback,
				FeedbackAutorizacoes,
				FeedbackProjecao,
				FeedbackConsulta> {
	private final FeedbackServico servico;

	public FeedbackControlador(
			FeedbackServico servico, FeedbackAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("registrar")
	public Feedback registrar(@RequestBody RegistrarFeedbackRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.registrar(
				requisicao.sessao(),
				requisicao.aluno(),
				requisicao.nota(),
				requisicao.comentario());
	}

	@PostMapping("{identificador}/editar")
	public Feedback editar(
			@PathVariable FeedbackIdentificador identificador,
			@RequestBody EditarFeedbackRequisicao requisicao) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(requisicao);
		return servico.editar(identificador, requisicao.nota(), requisicao.comentario());
	}
}
