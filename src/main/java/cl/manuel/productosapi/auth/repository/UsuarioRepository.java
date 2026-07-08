package cl.manuel.productosapi.auth.repository;

import cl.manuel.productosapi.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos de la entidad Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /** Consulta el usuario que tiene el email dado. */
    Optional<Usuario> findByEmail(String email);
}