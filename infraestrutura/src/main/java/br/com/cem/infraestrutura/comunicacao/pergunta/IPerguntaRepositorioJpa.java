package br.com.cem.infraestrutura.comunicacao.pergunta;

import br.com.cem.aplicacao.comunicacao.pergunta.PerguntaProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPerguntaRepositorioJpa extends JpaRepository<PerguntaJpa, Long> {
	@org.springframework.data.jpa.repository.Lock(
			jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from PerguntaJpa p where p.identificador = :identificador")
	PerguntaJpa bloquear(Long identificador);

	@Query(
"""
select new br.com.cem.aplicacao.comunicacao.pergunta.PerguntaProjecao(a.identificador, a.autor.identificador, a.texto, a.situacao, a.resposta, a.criadaEm) from PerguntaJpa a where (:coordenador = true or a.autor.identificador = :usuario) and (:texto is null or lower(a.texto) like :texto escape '\\')
""")
	Page<PerguntaProjecao> pesquisar(
			boolean coordenador, Long usuario, @Nullable String texto, Pageable pageable);
}
