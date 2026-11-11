package br.com.cem.ui.backend.monitoria.avaliacao;

import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoAplicacaoServico;
import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoConsulta;
import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoProjecao;
import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoSessaoResultado;
import br.com.cem.aplicacao.monitoria.avaliacao.SessaoPendenteProjecao;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoAutorizacoes;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoServico;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "monitoria/avaliacao")
public class AvaliacaoControlador extends
		AgregadoControladorAbstrato<AvaliacaoIdentificador, Avaliacao, AvaliacaoAutorizacoes, AvaliacaoProjecao, AvaliacaoConsulta> {
	private final AvaliacaoServico servico;
	private final AvaliacaoAplicacaoServico servicoAplicacao;

	public AvaliacaoControlador(AvaliacaoServico servico, AvaliacaoAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.servicoAplicacao = Objects.requireNonNull(servicoAplicacao);
	}

	@PostMapping("registrar")
	public Avaliacao registrar(@RequestBody RegistrarAvaliacaoRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.registrar(new SessaoIdentificador(requisicao.sessao()), requisicao.nota(),
				requisicao.comentario());
	}

	@PostMapping("{identificador}/editar")
	public void editar(@PathVariable AvaliacaoIdentificador identificador,
			@RequestBody EditarAvaliacaoRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		servico.editar(Objects.requireNonNull(identificador), requisicao.nota(), requisicao.comentario());
	}

	@GetMapping("sessao/{sessao}")
	public AvaliacaoSessaoResultado pesquisarDaSessao(@PathVariable SessaoIdentificador sessao,
			@ModelAttribute AvaliacaoConsulta consulta) {
		return servicoAplicacao.pesquisarDaSessao(Objects.requireNonNull(sessao), Objects.requireNonNull(consulta));
	}

	@GetMapping("pendentes")
	public List<SessaoPendenteProjecao> pesquisarPendentes() {
		return servicoAplicacao.pesquisarPendentes();
	}
}
