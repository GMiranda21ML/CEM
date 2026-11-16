package br.com.cem.infraestrutura.acesso.disciplina;

import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.spi.IProfessorValidadorServico;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;

@Component
public final class ProfessorValidadorServico implements IProfessorValidadorServico {
	private final IUsuarioRepositorioJpa usuarios;
	private final ICursoRepositorioJpa cursos;

	public ProfessorValidadorServico(IUsuarioRepositorioJpa usuarios, ICursoRepositorioJpa cursos) {
		this.usuarios = Objects.requireNonNull(usuarios);
		this.cursos = Objects.requireNonNull(cursos);
	}

	@Override
	public boolean isProfessorValido(UsuarioIdentificador professor, CursoIdentificador curso,
			InstituicaoIdentificador instituicao) {
		Objects.requireNonNull(professor); Objects.requireNonNull(curso); Objects.requireNonNull(instituicao);
		try {
			var usuario = usuarios.findById(Long.valueOf(professor.toString()));
			var cursoJpa = cursos.findById(Long.valueOf(curso.toString()));
			if (usuario.isEmpty() || cursoJpa.isEmpty()) return false;
			var u = usuario.get();
			var c = cursoJpa.get();
			var email = u.getEmail();
			var separador = email.lastIndexOf('@');
			return separador >= 0 && u.getPapel() == Autorizacao.PROFESSOR
					&& u.getSituacao() == UsuarioSituacao.ATIVO && u.getCurso() != null
					&& u.getCurso().trim().equalsIgnoreCase(c.getNome())
					&& email.substring(separador + 1).equalsIgnoreCase(instituicao.toString())
					&& c.getInstituicao().equalsIgnoreCase(instituicao.toString());
		} catch (NumberFormatException excecao) {
			return false;
		}
	}
}
