package cl.manuel.productosapi.service;

import cl.manuel.productosapi.categoria.model.Categoria;
import cl.manuel.productosapi.categoria.repository.CategoriaRepository;
import cl.manuel.productosapi.dto.ProductoRequestDTO;
import cl.manuel.productosapi.dto.ProductoResponseDTO;
import cl.manuel.productosapi.model.Producto;
import cl.manuel.productosapi.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void obtenerTodos_debeRetornarListaDeProductos() {

        // ─── Arrange (Preparar) ───
        Categoria categoria = new Categoria("Periféricos");
        Producto teclado = new Producto("Teclado", categoria, 25000.0, 10);
        Producto mouse   = new Producto("Mouse",   categoria, 12000.0, 8);

        // Le decimos al mock qué debe devolver cuando el service lo llame:
        when(productoRepository.findByActivoTrue())
                .thenReturn(List.of(teclado, mouse));

        // ─── Act (Actuar) ───
        List<ProductoResponseDTO> resultado = productoService.obtenerTodos();

        // ─── Assert (Afirmar) ───
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Teclado");
        assertThat(resultado.get(0).getCategoriaNombre()).isEqualTo("Periféricos");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Mouse");

    }

    @Test
    void crear_debeGuardarYRetornarProducto() {

        // ─── Arrange (Preparar) ───
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Monitor");
        dto.setCategoriaId(1L);
        dto.setPrecio(150000.0);
        dto.setStock(5);

        Categoria categoria = new Categoria("Monitores");

        // El service primero busca la categoría por id → devolvemos un Optional con ella
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // El service luego guarda el producto → devolvemos el mismo producto que reciba
        Producto productoGuardado = new Producto("Monitor", categoria, 150000.0, 5);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // ─── Act (Actuar) ───
        ProductoResponseDTO resultado = productoService.crear(dto);

        // ─── Assert (Afirmar) ───
        assertThat(resultado.getNombre()).isEqualTo("Monitor");
        assertThat(resultado.getCategoriaNombre()).isEqualTo("Monitores");
        assertThat(resultado.getPrecio()).isEqualTo(150000.0);
        assertThat(resultado.getStock()).isEqualTo(5);
    }

    @Test
    void crear_conCategoriaInexistente_debeLanzarExcepcion() {

        // ─── Arrange ───
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Monitor");
        dto.setCategoriaId(99L);   // id que NO existe
        dto.setPrecio(150000.0);
        dto.setStock(5);

        // Mockeamos findById para que NO encuentre nada → Optional vacío
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // ─── Act + Assert ───
        assertThatThrownBy(() -> productoService.crear(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoría no encontrada");

        // Verificamos que, al fallar antes, NUNCA se intentó guardar
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarProducto() {
        // Arrange
        Categoria categoria = new Categoria("Periféricos");
        Producto teclado = new Producto("Teclado", categoria, 25000.0, 10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(teclado));

        // Act
        Optional<ProductoResponseDTO> resultado = productoService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Teclado");
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarVacio() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<ProductoResponseDTO> resultado = productoService.obtenerPorId(99L);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void actualizar_cuandoExiste_debeModificarYRetornar() {
        // Arrange
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Teclado Mecánico");
        dto.setCategoriaId(1L);
        dto.setPrecio(45000.0);
        dto.setStock(7);

        Categoria categoria = new Categoria("Periféricos");
        Producto existente = new Producto("Teclado", categoria, 25000.0, 10);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenReturn(existente);

        // Act
        Optional<ProductoResponseDTO> resultado = productoService.actualizar(10L, dto);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Teclado Mecánico");
        assertThat(resultado.get().getPrecio()).isEqualTo(45000.0);
    }

    @Test
    void eliminar_cuandoExiste_debeMarcarInactivoYRetornarTrue() {
        // Arrange
        Categoria categoria = new Categoria("Periféricos");
        Producto producto = new Producto("Teclado", categoria, 25000.0, 10);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        // Act
        boolean resultado = productoService.eliminar(10L);

        // Assert
        assertThat(resultado).isTrue();
        assertThat(producto.getActivo()).isFalse();      // el borrado lógico lo marcó inactivo
        verify(productoRepository).save(producto);        // y lo guardó
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = productoService.eliminar(99L);

        // Assert
        assertThat(resultado).isFalse();
        verify(productoRepository, never()).save(any(Producto.class));  // nunca guardó
    }



}
