package cl.manuel.productosapi.repository;

import cl.manuel.productosapi.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos de la entidad Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Consulta todos los productos activos. */
    List<Producto> findByActivoTrue();

    /** Consulta los productos activos que pertenecen a la categoría con el nombre dado. */
    List<Producto> findByCategoria_NombreAndActivoTrue(String nombre);
}
