package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, UUID> {
}
