package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Acao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcaoRepository extends JpaRepository<Acao, UUID> {
}
