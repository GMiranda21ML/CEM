package br.com.cem.infraestrutura.comunicacao.faq;

import br.com.cem.aplicacao.comunicacao.faq.FaqProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IFaqRepositorioJpa extends JpaRepository<FaqJpa, Long> {
	@Query(
"""
select new br.com.cem.aplicacao.comunicacao.faq.FaqProjecao(a.identificador, a.pergunta, a.resposta, a.ativo) from FaqJpa a where (:coordenador = true or a.ativo = true) and (:texto is null or lower(a.pergunta) like :texto escape '\\' or lower(a.resposta) like :texto escape '\\')
""")
	Page<FaqProjecao> pesquisar(boolean coordenador, @Nullable String texto, Pageable pageable);

	@Query(
			"select count(a)>0 from FaqJpa a where a.chave = :chave and (:ignorado is null or"
					+ " a.identificador <> :ignorado)")
	boolean existePergunta(String chave, @Nullable Long ignorado);
}
