package br.com.cem.ui.backend.monitoria.acompanhamento;

import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoAplicacaoServico;
import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoConsulta;
import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoProjecao;
import br.com.cem.aplicacao.monitoria.acompanhamento.AlunoDisciplinaOpcaoProjecao;
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoAutorizacoes;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoServico;
import br.com.cem.dominio.monitoria.acompanhamento.RelatorioDesempenho;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/acompanhamento")
public class AcompanhamentoControlador
		extends AgregadoControladorAbstrato<
				AcompanhamentoIdentificador,
				Acompanhamento,
				AcompanhamentoAutorizacoes,
				AcompanhamentoProjecao,
				AcompanhamentoConsulta> {
	private final AcompanhamentoServico servico;
	private final AcompanhamentoAplicacaoServico aplicacao;

	public AcompanhamentoControlador(
			AcompanhamentoServico servico, AcompanhamentoAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.aplicacao = Objects.requireNonNull(aplicacao);
	}

	@GetMapping("opcoes")
	public List<AlunoDisciplinaOpcaoProjecao> opcoes() {
		return aplicacao.listarOpcoesAbertura();
	}

	@GetMapping("{identificador}/relatorio")
	public RelatorioDesempenho relatorio(
			@PathVariable AcompanhamentoIdentificador identificador,
			@RequestParam Optional<LocalDate> de,
			@RequestParam Optional<LocalDate> ate) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(de);
		Objects.requireNonNull(ate);
		return servico.emitirRelatorio(identificador, de, ate);
	}

	@PostMapping("abrir")
	public Acompanhamento abrir(@RequestBody AbrirAcompanhamentoRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.abrir(
				requisicao.aluno(),
				requisicao.disciplina(),
				requisicao.metaNota(),
				requisicao.metaFrequencia());
	}

	@PostMapping("{identificador}/metas")
	public Acompanhamento editarMetas(
			@PathVariable AcompanhamentoIdentificador identificador,
			@RequestBody EditarMetasRequisicao requisicao) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(requisicao);
		return servico.editarMetas(
				identificador, requisicao.metaNota(), requisicao.metaFrequencia());
	}

	@PostMapping("{identificador}/encerrar")
	public Acompanhamento encerrar(
			@PathVariable AcompanhamentoIdentificador identificador,
			@RequestBody EncerrarAcompanhamentoRequisicao requisicao) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(requisicao);
		return servico.encerrar(identificador, requisicao.parecerFinal());
	}
}
