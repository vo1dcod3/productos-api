package cl.manuel.productosapi.repository;

import cl.manuel.productosapi.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaAndActivoTrue(String categoria);

    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.activo = true")
    List<String> findCategoriasActivas();
}