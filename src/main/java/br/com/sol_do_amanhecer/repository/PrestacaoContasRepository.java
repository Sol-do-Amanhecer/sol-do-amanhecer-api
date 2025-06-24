package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface PrestacaoContasRepository extends JpaRepository<PrestacaoContas, UUID> {

    @Query("SELECT SUM(p.valorPago) FROM PrestacaoContas p WHERE p.dataTransacao BETWEEN :inicio AND :fim")
    Double findTotalByPeriodo(LocalDate inicio, LocalDate fim);

    @Query("""
        SELECT p FROM PrestacaoContas p
        WHERE MONTH(p.dataTransacao) = :mes AND YEAR(p.dataTransacao) = :ano
    """)
    Page<PrestacaoContas> findByMesEAno(@Param("mes") Integer mes, @Param("ano") Integer ano, Pageable pageable);

    @Query("""
        SELECT p FROM PrestacaoContas p
        WHERE MONTH(p.dataTransacao) = :mes
    """)
    Page<PrestacaoContas> findByMes(@Param("mes") Integer mes, Pageable pageable);

    @Query("""
        SELECT p FROM PrestacaoContas p
        WHERE YEAR(p.dataTransacao) = :ano
    """)
    Page<PrestacaoContas> findByAno(@Param("ano") Integer ano, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Doacao d WHERE d.dataDoacao BETWEEN :inicio AND :fim")
    Long findCountByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}