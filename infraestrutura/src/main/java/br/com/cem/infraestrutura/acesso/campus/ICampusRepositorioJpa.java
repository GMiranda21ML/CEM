package br.com.cem.infraestrutura.acesso.campus;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.aplicacao.acesso.campus.CampusProjecao;
import br.com.cem.dominio.acesso.campus.CampusSituacao;

public interface ICampusRepositorioJpa extends JpaRepository<CampusJpa, Long> {
	@Query("""
			select count(c) > 0
			  from CampusJpa c
			 where lower(c.nome) = lower(:nome)
			   and c.instituicao = :instituicao
			   and (:identificadorIgnorado is null or c.identificador <> :identificadorIgnorado)
			""")
	boolean existePorNome(String nome, String instituicao, @Nullable Long identificadorIgnorado);

	@Query("""
			select new br.com.cem.aplicacao.acesso.campus.CampusProjecao(c.identificador, c.instituicao, c.nome, c.endereco, c.situacao)
			  from CampusJpa c
			 where c.instituicao = :instituicao
			   and (:nome is null or lower(c.nome) like :nome escape '\\')
			   and (:situacao is null or c.situacao = :situacao)
			""")
	Page<CampusProjecao> pesquisar(String instituicao, @Nullable String nome, @Nullable CampusSituacao situacao,
			Pageable pageable);
}
