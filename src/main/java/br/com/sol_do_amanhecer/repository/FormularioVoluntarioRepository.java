package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.FormularioVoluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FormularioVoluntarioRepository extends JpaRepository<FormularioVoluntario, UUID> {
}