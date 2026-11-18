package br.com.cem.infraestrutura.monitoria.selecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ISelecaoRepositorioJpa extends JpaRepository<SelecaoJpa, Long> {
	@Query(
			"select count(s) > 0 from SelecaoJpa s where s.disciplina.identificador = :disciplina"
					+ " and s.situacao = br.com.cem.dominio.monitoria.selecao.SelecaoSituacao.ABERTA"
					+ " and (:ignorado is null or s.identificador <> :ignorado)")
	boolean existeAbertaPorDisciplina(Long disciplina, @Nullable Long ignorado);
}
