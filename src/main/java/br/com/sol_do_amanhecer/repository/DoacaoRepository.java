package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Doacao;
import br.com.sol_do_amanhecer.shared.enums.EMeioDoacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {

    @Query("SELECT SUM(d.valor) FROM Doacao d WHERE d.dataDoacao BETWEEN :inicio AND :fim")
    Double findTotalByPeriodo(LocalDate inicio, LocalDate fim);

    Page<Doacao> findAll(Pageable pageable);

    Page<Doacao> findByMeioDoacaoAndDataDoacaoBetween(EMeioDoacao meioDoacao, LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    Page<Doacao> findByMeioDoacao(EMeioDoacao meioDoacao, Pageable pageable);

    Page<Doacao> findByDataDoacaoBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    @Query("SELECT COUNT(p) FROM PrestacaoContas p WHERE p.dataTransacao BETWEEN :inicio AND :fim")
    Long findCountByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}