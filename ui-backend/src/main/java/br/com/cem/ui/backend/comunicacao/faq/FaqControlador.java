package br.com.cem.ui.backend.comunicacao.faq;

import br.com.cem.aplicacao.comunicacao.faq.FaqAplicacaoServico;
import br.com.cem.aplicacao.comunicacao.faq.FaqConsulta;
import br.com.cem.aplicacao.comunicacao.faq.FaqProjecao;
import br.com.cem.dominio.comunicacao.faq.Faq;
import br.com.cem.dominio.comunicacao.faq.FaqIdentificador;
import br.com.cem.dominio.comunicacao.faq.FaqServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "comunicacao/faq")
public class FaqControlador
		extends AgregadoControladorAbstrato<
				FaqIdentificador, Faq, AgregadoAutorizacoes<Faq>, FaqProjecao, FaqConsulta> {
	private final FaqServico servico;

	public FaqControlador(FaqServico servico, FaqAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}
}
