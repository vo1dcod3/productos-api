package cl.manuel.productosapi.categoria.repository;

import cl.manuel.productosapi.categoria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos de la entidad Categoria.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    /** Consulta todas las categorías activas. */
    List<Categoria> findByActivoTrue();
    /** Consulta la categoría activa con el nombre dado. */
    Optional<Categoria> findByNombreAndActivoTrue(String nombre);
    /** Indica si existe una categoría activa con el nombre dado. */
    boolean existsByNombreAndActivoTrue(String nombre);
}
