package br.com.cem.infraestrutura.comunicacao.certificado;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;

public interface ICertificadoRepositorioJpa extends JpaRepository<CertificadoJpa, Long> {
	boolean existsByCodigo(String codigo);

	Optional<CertificadoJpa> findByCodigo(String codigo);

	@Query("""
			select count(c) > 0
			  from CertificadoJpa c
			 where c.beneficiario.identificador = :beneficiario
			   and c.tipo = :tipo
			   and c.disciplina = :disciplina
			   and c.periodoInicio = :periodoInicio
			   and c.periodoFim = :periodoFim
			""")
	boolean existePorIdentidade(Long beneficiario, CertificadoTipo tipo, String disciplina, LocalDate periodoInicio,
			LocalDate periodoFim);
}
