package br.com.cem.infraestrutura.acesso.dominioemail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailProjecao;

public interface IDominioEmailRepositorioJpa extends JpaRepository<DominioEmailJpa, String> {
	@Query("""
			select new br.com.cem.aplicacao.acesso.dominioemail.DominioEmailProjecao(d.dominio)
			  from DominioEmailJpa d
			""")
	Page<DominioEmailProjecao> pesquisar(Pageable pageable);
}
