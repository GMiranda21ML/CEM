package br.com.cem.infraestrutura.monitoria.alerta;

import br.com.cem.aplicacao.monitoria.alerta.AlertaProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IAlertaRepositorioJpa extends JpaRepository<AlertaJpa, Long> {
	@Query(
"""
select new br.com.cem.aplicacao.monitoria.alerta.AlertaProjecao(a.identificador, a.pessoa.identificador, a.papel, a.disciplina, a.acompanhado, a.criadoEm) from AlertaJpa a where :coordenador = true and lower(trim(a.pessoa.curso)) = :curso and (lower(a.pessoa.email) like :emailInstitucional escape '\\' or lower(a.pessoa.email) like :subdominioInstitucional escape '\\') and (:texto is null or lower(a.disciplina) like :texto escape '\\')
""")
	Page<AlertaProjecao> pesquisar(boolean coordenador, String curso,
			String emailInstitucional, String subdominioInstitucional,
			@Nullable String texto, Pageable pageable);

	@Query(
			"select count(a)>0 from AlertaJpa a where a.pessoa.identificador=:pessoa and"
					+ " a.papel=:papel and a.disciplina=:disciplina and a.acompanhado=false")
	boolean existeNovo(Long pessoa, String papel, String disciplina);
}
