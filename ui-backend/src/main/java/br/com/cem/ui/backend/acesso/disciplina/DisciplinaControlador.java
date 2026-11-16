package br.com.cem.ui.backend.acesso.disciplina;

import java.util.List;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.com.cem.aplicacao.acesso.disciplina.DisciplinaAplicacaoServico;
import br.com.cem.aplicacao.acesso.disciplina.DisciplinaConsulta;
import br.com.cem.aplicacao.acesso.disciplina.DisciplinaProjecao;
import br.com.cem.aplicacao.acesso.disciplina.ProfessorProjecao;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaAutorizacoes;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.DisciplinaServico;
import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "acesso/disciplina")
public class DisciplinaControlador extends AgregadoControladorAbstrato<DisciplinaIdentificador, Disciplina,
		DisciplinaAutorizacoes, DisciplinaProjecao, DisciplinaConsulta> {
	private final DisciplinaServico servico;
	private final DisciplinaAplicacaoServico servicoAplicacao;
	public DisciplinaControlador(DisciplinaServico servico, DisciplinaAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.servicoAplicacao = Objects.requireNonNull(servicoAplicacao);
	}
	@PostMapping("cadastrar")
	public Disciplina cadastrar(@RequestBody CadastrarDisciplinaRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.cadastrar(requisicao.nome(), new br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo(requisicao.codigo()),
				requisicao.area(), new CursoIdentificador(requisicao.curso()),
				new br.com.cem.dominio.acesso.usuario.UsuarioIdentificador(requisicao.professor()));
	}
	@PostMapping("{identificador}/editar")
	public void editar(@PathVariable DisciplinaIdentificador identificador,
			@RequestBody EditarDisciplinaRequisicao requisicao) {
		Objects.requireNonNull(identificador); Objects.requireNonNull(requisicao);
		var existente = servico.buscar(identificador);
		servico.alterar(existente.alterarDados(requisicao.nome(), requisicao.area(), requisicao.professor()));
	}
	@GetMapping("professores")
	public List<ProfessorProjecao> professores(@RequestParam CursoIdentificador curso) {
		return servicoAplicacao.buscarProfessores(Objects.requireNonNull(curso));
	}
}
