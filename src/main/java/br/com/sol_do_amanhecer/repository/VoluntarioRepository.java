package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Voluntario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoluntarioRepository extends JpaRepository<Voluntario, UUID> {

    Page<Voluntario> findByAtivoAndAprovadoIsNotNull(Boolean ativo, Pageable pageable);

    Page<Voluntario> findByAprovadoIsNull(Pageable pageable);

    Page<Voluntario> findAllByAprovadoIsNotNull(Pageable pageable);
}