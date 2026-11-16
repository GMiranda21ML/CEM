package br.com.cem.ui.backend.acesso.curso;

import java.util.Objects;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.cem.aplicacao.acesso.curso.CursoAplicacaoServico;
import br.com.cem.aplicacao.acesso.curso.CursoConsulta;
import br.com.cem.aplicacao.acesso.curso.CursoProjecao;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoAutorizacoes;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoServico;
import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "acesso/curso")
public class CursoControlador extends AgregadoControladorAbstrato<CursoIdentificador, Curso, CursoAutorizacoes,
		CursoProjecao, CursoConsulta> {
	private final CursoServico servico;
	public CursoControlador(CursoServico servico, CursoAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);
		this.servico = Objects.requireNonNull(servico);
	}
	@PostMapping("cadastrar")
	public Curso cadastrar(@RequestBody CadastrarCursoRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.cadastrar(requisicao.nome(), requisicao.sigla());
	}
}
