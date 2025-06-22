package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Acao;
import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AcaoRepository extends JpaRepository<Acao, UUID> {

    Page<Acao> findAll(Pageable pageable);

    Page<Acao> findByTipo(ETipoAcao tipo, Pageable pageable);

    @Query("SELECT a FROM Acao a WHERE YEAR(a.dataAcao) = :ano")
    Page<Acao> findByAno(@Param("ano") Integer ano, Pageable pageable);

    @Query("SELECT a FROM Acao a WHERE MONTH(a.dataAcao) = :mes")
    Page<Acao> findByMes(@Param("mes") Integer mes, Pageable pageable);

    @Query("SELECT a FROM Acao a WHERE a.tipo = :tipo AND YEAR(a.dataAcao) = :ano")
    Page<Acao> findByTipoAndAno(@Param("tipo") ETipoAcao tipo, @Param("ano") Integer ano, Pageable pageable);

    @Query("SELECT a FROM Acao a WHERE YEAR(a.dataAcao) = :ano AND MONTH(a.dataAcao) = :mes")
    Page<Acao> findByAnoAndMes(@Param("ano") Integer ano, @Param("mes") Integer mes, Pageable pageable);

    @Query("SELECT a FROM Acao a WHERE a.tipo = :tipo AND YEAR(a.dataAcao) = :ano AND MONTH(a.dataAcao) = :mes")
    Page<Acao> findByTipoAndAnoAndMes(@Param("tipo") ETipoAcao tipo, @Param("ano") Integer ano, @Param("mes") Integer mes, Pageable pageable);
}
