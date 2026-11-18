package br.com.cem.dominio.monitoria.feedback.spi;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.util.List;

public interface IFeedbackRepositorio
		extends IAgregadoRepositorio<FeedbackIdentificador, Feedback> {
	boolean existePorSessaoEAluno(SessaoIdentificador sessao, UsuarioIdentificador aluno);

	boolean existePorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina);

	List<Feedback> listarPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina);
}
