package br.com.cem.infraestrutura.comunicacao.certificado;

import java.time.LocalDate;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.aplicacao.comunicacao.certificado.CertificadoProjecao;
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

	@Query("""
			select new br.com.cem.aplicacao.comunicacao.certificado.CertificadoProjecao(c.identificador, c.codigo, u.identificador, c.beneficiarioNome, c.tipo, c.disciplina, c.disciplinaNome, c.periodoInicio, c.periodoFim, c.quantidadeSessoes, c.cargaHorariaMinutos, c.emitidoEm)
			  from CertificadoJpa c
			  join c.beneficiario u
			 where (:beneficiario is null or u.identificador = :beneficiario)
			   and (:tipo is null or c.tipo = :tipo)
			   and (:disciplina is null or c.disciplina = :disciplina)
			""")
	Page<CertificadoProjecao> pesquisar(@Nullable Long beneficiario, @Nullable CertificadoTipo tipo,
			@Nullable String disciplina, Pageable pageable);
}
