package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.UUID;

public interface PrestacaoContasRepository extends JpaRepository<PrestacaoContas, UUID> {

    @Query("SELECT SUM(p.valorPago) FROM PrestacaoContas p WHERE p.dataTransacao BETWEEN :inicio AND :fim")
    Double findTotalByPeriodo(LocalDate inicio, LocalDate fim);
}