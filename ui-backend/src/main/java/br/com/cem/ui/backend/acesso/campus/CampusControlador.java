package br.com.cem.ui.backend.acesso.campus;

import java.util.Objects;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.aplicacao.acesso.campus.CampusAplicacaoServico;
import br.com.cem.aplicacao.acesso.campus.CampusConsulta;
import br.com.cem.aplicacao.acesso.campus.CampusProjecao;
import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusAutorizacoes;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.CampusServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "acesso/campus")
public class CampusControlador extends
		AgregadoControladorAbstrato<CampusIdentificador, Campus, CampusAutorizacoes, CampusProjecao, CampusConsulta> {
	private final CampusServico servico;

	public CampusControlador(CampusServico servico, CampusAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);
		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("cadastrar")
	public Campus cadastrar(@RequestBody CadastrarCampusRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.cadastrar(requisicao.nome(), requisicao.endereco());
	}

	@PostMapping("{identificador}/inativar")
	public void inativar(@PathVariable CampusIdentificador identificador) {
		servico.inativar(Objects.requireNonNull(identificador));
	}

	@PostMapping("{identificador}/reativar")
	public void reativar(@PathVariable CampusIdentificador identificador) {
		servico.reativar(Objects.requireNonNull(identificador));
	}
}
