package cl.manuel.productosapi.repository;

import cl.manuel.productosapi.categoria.model.Categoria;
import cl.manuel.productosapi.categoria.repository.CategoriaRepository;
import cl.manuel.productosapi.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoRepositoryIntegrationTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void findByActivoTrue_debeRetornarSoloProductosActivos() {
        Categoria categoria = categoriaRepository.save(new Categoria("Periféricos"));

        Producto activo = new Producto("Teclado", categoria, 25000.0, 10);
        Producto inactivo = new Producto("Mouse viejo", categoria, 5000.0, 2);
        inactivo.setActivo(false);
        productoRepository.save(activo);
        productoRepository.save(inactivo);

        List<Producto> resultado = productoRepository.findByActivoTrue();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Teclado");
    }

    @Test
    void findByCategoriaNombreAndActivoTrue_debeFiltrarPorCategoria() {
        Categoria perifericos = categoriaRepository.save(new Categoria("Periféricos"));
        Categoria monitores  = categoriaRepository.save(new Categoria("Monitores"));

        productoRepository.save(new Producto("Teclado", perifericos, 25000.0, 10));
        productoRepository.save(new Producto("Monitor 24", monitores, 150000.0, 5));

        List<Producto> resultado = productoRepository.findByCategoria_NombreAndActivoTrue("Periféricos");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Teclado");
    }

    @Test
    void existsByNombreAndActivoTrue_debeDetectarNombresExistentes() {
        categoriaRepository.save(new Categoria("Periféricos"));

        assertThat(categoriaRepository.existsByNombreAndActivoTrue("Periféricos")).isTrue();
        assertThat(categoriaRepository.existsByNombreAndActivoTrue("NoExiste")).isFalse();
    }
}