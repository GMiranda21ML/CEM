package br.com.cem.ui.backend.monitoria.selecao;

import br.com.cem.aplicacao.monitoria.selecao.DisciplinaProfessorProjecao;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoAplicacaoServico;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoConsulta;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoAutorizacoes;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoServico;

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
@RequestMapping(Backend.PREFIXO + "monitoria/selecao")
public class SelecaoControlador
		extends AgregadoControladorAbstrato<
				SelecaoIdentificador,
				Selecao,
				SelecaoAutorizacoes,
				SelecaoProjecao,
				SelecaoConsulta> {
	private final SelecaoServico servico;
	private final SelecaoAplicacaoServico aplicacao;

	public SelecaoControlador(SelecaoServico servico, SelecaoAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.aplicacao = Objects.requireNonNull(aplicacao);
	}

	@GetMapping("disciplinas")
	public List<DisciplinaProfessorProjecao> disciplinas() {
		return aplicacao.listarDisciplinasDoProfessor();
	}

	@PostMapping("abrir")
	public Selecao abrir(@RequestBody AbrirSelecaoRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.abrir(
				requisicao.disciplina(),
				requisicao.prazoInscricao(),
				requisicao.vagas(),
				requisicao.crMinimo(),
				requisicao.pesoCr(),
				requisicao.pesoEntrevista(),
				requisicao.patamarMinimo());
	}

	@PostMapping("{identificador}/editar")
	public Selecao editar(
			@PathVariable SelecaoIdentificador identificador,
			@RequestBody EditarSelecaoRequisicao requisicao) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(requisicao);
		return servico.editar(
				identificador,
				requisicao.prazoInscricao(),
				requisicao.vagas(),
				requisicao.crMinimo(),
				requisicao.pesoCr(),
				requisicao.pesoEntrevista(),
				requisicao.patamarMinimo());
	}

	@PostMapping("{identificador}/encerrar")
	public Selecao encerrar(@PathVariable SelecaoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		return servico.encerrar(identificador);
	}
}
