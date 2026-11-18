package br.com.cem.ui.backend.monitoria.candidatura;

import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaAplicacaoServico;
import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaConsulta;
import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaProjecao;
import br.com.cem.aplicacao.monitoria.candidatura.SelecaoAbertaProjecao;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaAutorizacoes;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/candidatura")
public class CandidaturaControlador
		extends AgregadoControladorAbstrato<
				CandidaturaIdentificador,
				Candidatura,
				CandidaturaAutorizacoes,
				CandidaturaProjecao,
				CandidaturaConsulta> {
	private final CandidaturaServico servico;
	private final CandidaturaAplicacaoServico aplicacao;

	public CandidaturaControlador(
			CandidaturaServico servico, CandidaturaAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.aplicacao = Objects.requireNonNull(aplicacao);
	}

	@GetMapping("abertas")
	public List<SelecaoAbertaProjecao> abertas() {
		return aplicacao.listarSelecoesAbertasAluno();
	}

	@PostMapping("candidatar")
	public Candidatura candidatar(@RequestBody CandidatarRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.candidatar(requisicao.selecao(), requisicao.cr());
	}

	@PostMapping("{identificador}/cancelar")
	public Candidatura cancelar(@PathVariable CandidaturaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return servico.cancelar(identificador);
	}

	@PostMapping("{identificador}/entrevista")
	public Candidatura registrarEntrevista(
			@PathVariable CandidaturaIdentificador identificador,
			@RequestBody RegistrarEntrevistaRequisicao requisicao) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(requisicao);
		return servico.registrarEntrevista(
				identificador, requisicao.nota(), requisicao.comentario());
	}

	@PostMapping("{identificador}/aprovar")
	public Candidatura aprovar(@PathVariable CandidaturaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return servico.aprovar(identificador);
	}

	@PostMapping("{identificador}/reprovar")
	public Candidatura reprovar(@PathVariable CandidaturaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return servico.reprovar(identificador);
	}
}
