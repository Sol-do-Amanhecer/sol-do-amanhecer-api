package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {

    @Query("SELECT SUM(d.valor) FROM Doacao d WHERE d.dataDoacao BETWEEN :inicio AND :fim")
    Double findTotalByPeriodo(LocalDate inicio, LocalDate fim);
}