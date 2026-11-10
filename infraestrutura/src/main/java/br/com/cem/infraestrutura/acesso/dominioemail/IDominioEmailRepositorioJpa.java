package br.com.cem.infraestrutura.acesso.dominioemail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IDominioEmailRepositorioJpa extends JpaRepository<DominioEmailJpa, String> {
}
