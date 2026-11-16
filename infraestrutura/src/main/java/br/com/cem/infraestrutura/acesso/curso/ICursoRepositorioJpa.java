package br.com.cem.infraestrutura.acesso.curso;

import br.com.cem.aplicacao.acesso.curso.CursoProjecao;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ICursoRepositorioJpa extends JpaRepository<CursoJpa, Long> {
	@Query("select count(c) > 0 from CursoJpa c where lower(c.nome) = lower(:nome) and c.instituicao = :instituicao and (:ignorado is null or c.identificador <> :ignorado)")
	boolean existePorNome(String nome, String instituicao, @Nullable Long ignorado);

	@Query("select count(c) > 0 from CursoJpa c where lower(c.sigla) = lower(:sigla) and c.instituicao = :instituicao and (:ignorado is null or c.identificador <> :ignorado)")
	boolean existePorSigla(String sigla, String instituicao, @Nullable Long ignorado);

	@Query("select count(d) > 0 from DisciplinaJpa d where d.curso.identificador = :curso")
	boolean possuiDisciplinas(Long curso);

	@Query("select count(u) > 0 from UsuarioJpa u join CursoJpa c on lower(trim(u.curso)) = lower(c.nome) where c.identificador = :curso and lower(substring(u.email, locate('@', u.email) + 1)) = lower(c.instituicao)")
	boolean possuiUsuarios(Long curso);

	@Query("""
		select new br.com.cem.aplicacao.acesso.curso.CursoProjecao(
		 c.identificador, c.instituicao, c.nome, c.sigla,
		 (select count(d) from DisciplinaJpa d where d.curso.identificador = c.identificador),
		 (select count(u) from UsuarioJpa u where lower(trim(u.curso)) = lower(c.nome)
		   and lower(substring(u.email, locate('@', u.email) + 1)) = lower(c.instituicao)))
		from CursoJpa c
		where c.instituicao = :instituicao
		  and (:texto is null or lower(c.nome) like :texto escape '\\' or lower(c.sigla) like :texto escape '\\')
		""")
	Page<CursoProjecao> pesquisar(String instituicao, @Nullable String texto, Pageable pageable);
}
