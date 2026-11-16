package br.com.cem.infraestrutura.monitoria.disponibilidade;

import br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IDisponibilidadeRepositorioJpa extends JpaRepository<DisponibilidadeJpa, Long> {
	@Query(
"""
select new br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeProjecao(a.identificador, a.disciplina, a.monitor.identificador, a.campus.identificador, a.diaSemana, a.inicio, a.fim) from DisponibilidadeJpa a where (:coordenador = true or a.monitor.identificador = :usuario) and (:texto is null or lower(a.disciplina) like :texto escape '\\')
""")
	Page<DisponibilidadeProjecao> pesquisar(
			boolean coordenador, Long usuario, @Nullable String texto, Pageable pageable);

	@Query(
			"select a from DisponibilidadeJpa a where a.monitor.identificador=:monitor and"
					+ " a.disciplina=:disciplina")
	List<DisponibilidadeJpa> buscarDoMonitor(Long monitor, String disciplina);

	@Query("select a from DisponibilidadeJpa a where a.monitor.identificador=:monitor")
	List<DisponibilidadeJpa> listarDoMonitor(Long monitor);
}
