package br.com.cem.ui.backend.monitoria.sessao;

import br.com.cem.aplicacao.monitoria.sessao.ContextoAgenda;
import br.com.cem.aplicacao.monitoria.sessao.SessaoAplicacaoServico;
import br.com.cem.aplicacao.monitoria.sessao.SessaoConsulta;
import br.com.cem.aplicacao.monitoria.sessao.SessaoProjecao;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/sessao")
public class SessaoControlador
		extends AgregadoControladorAbstrato<
				SessaoIdentificador, Sessao, SessaoAutorizacoes, SessaoProjecao, SessaoConsulta> {
	private final SessaoServico servico;
	private final SessaoAplicacaoServico aplicacao;

	public SessaoControlador(SessaoServico servico, SessaoAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.aplicacao = Objects.requireNonNull(aplicacao);
	}

	@GetMapping("contexto")
	public ContextoAgenda contexto() {
		return aplicacao.contexto();
	}

	@PostMapping("{identificador}/cancelar")
	public void cancelar(@PathVariable SessaoIdentificador identificador) {
		servico.cancelar(identificador);
	}
}
