package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
    Usuario findByUsuario(@Param("usuario") String usuario);
}
