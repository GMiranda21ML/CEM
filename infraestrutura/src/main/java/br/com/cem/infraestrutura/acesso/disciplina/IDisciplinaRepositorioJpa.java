package br.com.cem.infraestrutura.acesso.disciplina;

import br.com.cem.aplicacao.acesso.disciplina.DisciplinaProjecao;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.aplicacao.acesso.disciplina.ProfessorProjecao;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface IDisciplinaRepositorioJpa extends JpaRepository<DisciplinaJpa, Long> {
	@Query("select count(d) > 0 from DisciplinaJpa d where lower(d.codigo) = lower(:codigo) and d.instituicao = :instituicao and (:ignorado is null or d.identificador <> :ignorado)")
	boolean existePorCodigo(String codigo, String instituicao, @Nullable Long ignorado);

	@Query("select count(d) > 0 from DisciplinaJpa d where lower(d.nome) = lower(:nome) and d.curso.identificador = :curso and (:ignorado is null or d.identificador <> :ignorado)")
	boolean existePorNomeNoCurso(String nome, Long curso, @Nullable Long ignorado);

	@Query("""
		select count(s) > 0 from SessaoJpa s join s.professor p, DisciplinaJpa d
		where d.identificador = :disciplina
		  and lower(s.disciplina) in (lower(d.nome), lower(d.codigo))
		  and lower(trim(p.curso)) = lower(d.curso.nome)
		  and lower(substring(p.email, locate('@', p.email) + 1)) = lower(d.instituicao)
		""")
	boolean possuiVinculosEmSessoes(Long disciplina);

	@Query("""
		select count(v) > 0 from DisponibilidadeJpa v join v.monitor m, DisciplinaJpa d
		where d.identificador = :disciplina
		  and lower(v.disciplina) in (lower(d.nome), lower(d.codigo))
		  and lower(trim(m.curso)) = lower(d.curso.nome)
		  and lower(substring(m.email, locate('@', m.email) + 1)) = lower(d.instituicao)
		""")
	boolean possuiVinculosEmDisponibilidades(Long disciplina);

	@Query("""
		select new br.com.cem.aplicacao.acesso.disciplina.DisciplinaProjecao(
		 d.identificador, c.identificador, c.nome, u.identificador, u.nome, d.nome, d.codigo, d.area,
		 (select count(s) from SessaoJpa s join s.professor p where lower(s.disciplina) in (lower(d.nome), lower(d.codigo))
		   and lower(trim(p.curso)) = lower(c.nome)
		   and lower(substring(p.email, locate('@', p.email) + 1)) = lower(d.instituicao)) +
		 (select count(v) from DisponibilidadeJpa v join v.monitor m where lower(v.disciplina) in (lower(d.nome), lower(d.codigo))
		   and lower(trim(m.curso)) = lower(c.nome)
		   and lower(substring(m.email, locate('@', m.email) + 1)) = lower(d.instituicao)))
		from DisciplinaJpa d join d.curso c join d.professor u
		where d.instituicao = :instituicao
		  and (:texto is null or lower(d.nome) like :texto escape '\\'
		    or lower(d.codigo) like :texto escape '\\' or lower(d.area) like :texto escape '\\')
		  and (:curso is null or c.identificador = :curso)
		""")
	Page<DisciplinaProjecao> pesquisar(String instituicao, @Nullable String texto, @Nullable Long curso,
			Pageable pageable);

	@Query("select count(d) > 0 from DisciplinaJpa d where d.curso.identificador = :curso")
	boolean existePorCurso(Long curso);

	@Query("""
		select new br.com.cem.aplicacao.acesso.disciplina.ProfessorProjecao(u.identificador, u.nome)
		from UsuarioJpa u join CursoJpa c on lower(trim(u.curso)) = lower(c.nome)
		where c.identificador = :curso and c.instituicao = :instituicao
		  and u.papel = br.com.cem.dominio.Autorizacao.PROFESSOR
		  and u.situacao = br.com.cem.dominio.acesso.usuario.UsuarioSituacao.ATIVO
		  and lower(substring(u.email, locate('@', u.email) + 1)) = lower(c.instituicao)
		order by lower(u.nome)
		""")
	List<ProfessorProjecao> buscarProfessores(Long curso, String instituicao);
}
