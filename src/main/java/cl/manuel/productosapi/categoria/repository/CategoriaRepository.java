package cl.manuel.productosapi.categoria.repository;

import cl.manuel.productosapi.categoria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByActivoTrue();
    Optional<Categoria> findByNombreAndActivoTrue(String nombre);
    boolean existsByNombreAndActivoTrue(String nombre);
}
