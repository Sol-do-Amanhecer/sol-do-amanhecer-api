package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ObjetivoMensalRepository extends JpaRepository<ObjetivoMensal, UUID> {

    Page<ObjetivoMensal> findByMesAndAno(EMes mes, Integer ano, Pageable pageable);

    Page<ObjetivoMensal> findByMes(EMes mes, Pageable pageable);

    Page<ObjetivoMensal> findByAno(Integer ano, Pageable pageable);

    boolean existsByMesAndAno(EMes mes, Integer ano);
}