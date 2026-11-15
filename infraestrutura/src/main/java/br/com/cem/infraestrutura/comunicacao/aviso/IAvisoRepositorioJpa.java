package br.com.cem.infraestrutura.comunicacao.aviso;

import br.com.cem.aplicacao.comunicacao.aviso.AvisoProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface IAvisoRepositorioJpa extends JpaRepository<AvisoJpa, Long> {
	@Query(
"""
select new br.com.cem.aplicacao.comunicacao.aviso.AvisoProjecao(a.identificador, a.titulo, a.texto, a.validade, a.ativo, a.publicadoEm) from AvisoJpa a where (:coordenador = true or (a.ativo = true and a.validade >= :hoje)) and (:texto is null or lower(a.titulo) like :texto escape '\\') order by a.publicadoEm desc, a.identificador desc
""")
	Page<AvisoProjecao> pesquisar(
			boolean coordenador, LocalDate hoje, @Nullable String texto, Pageable pageable);
}
