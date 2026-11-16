package br.com.cem.ui.backend.monitoria.sessao;

import br.com.cem.aplicacao.monitoria.sessao.ContextoAgenda;
import br.com.cem.aplicacao.monitoria.sessao.SessaoAplicacaoServico;
import br.com.cem.aplicacao.monitoria.sessao.SessaoConsulta;
import br.com.cem.aplicacao.monitoria.sessao.SessaoProjecao;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.RespostaSessao;
import br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoServico;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

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

	@PostMapping("registrar")
	public Sessao registrar(@RequestBody Sessao sessao) {
		return servico.registrar(sessao);
	}

	@PostMapping("{identificador}/adicionar-aluno")
	public Sessao adicionarAluno(
			@PathVariable SessaoIdentificador identificador, @RequestBody AlunoSessaoRequisicao requisicao) {
		return servico.adicionarAluno(identificador, new UsuarioIdentificador(requisicao.aluno()));
	}

	@PostMapping("{identificador}/responder")
	public Sessao responder(
			@PathVariable SessaoIdentificador identificador,
			@RequestBody RespostaSessaoRequisicao requisicao) {
		return servico.responder(
				identificador,
				new RespostaSessao(
						new UsuarioIdentificador(requisicao.aluno()),
						requisicao.situacao(),
						Optional.ofNullable(requisicao.motivo()),
						Optional.ofNullable(requisicao.inicioSugerido())));
	}

	@PostMapping("{identificador}/remarcar-sugestao")
	public Sessao remarcarSugestao(
			@PathVariable SessaoIdentificador identificador,
			@RequestBody AlunoSessaoRequisicao requisicao) {
		return servico.remarcarParaSugestao(
				identificador, new UsuarioIdentificador(requisicao.aluno()));
	}

	@PostMapping("{identificador}/cancelar")
	public void cancelar(@PathVariable SessaoIdentificador identificador) {
		servico.cancelar(identificador);
	}
}
