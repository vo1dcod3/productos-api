package cl.manuel.productosapi.categoria.service;

import cl.manuel.productosapi.categoria.dto.CategoriaRequestDTO;
import cl.manuel.productosapi.categoria.dto.CategoriaResponseDTO;
import cl.manuel.productosapi.categoria.model.Categoria;
import cl.manuel.productosapi.categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void obtenerTodas_debeRetornarListaDeCategorias() {
        // Arrange
        Categoria c1 = new Categoria("Periféricos");
        Categoria c2 = new Categoria("Monitores");
        when(categoriaRepository.findByActivoTrue()).thenReturn(List.of(c1, c2));

        // Act
        List<CategoriaResponseDTO> resultado = categoriaService.obtenerTodas();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Periféricos");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Monitores");
    }

    @Test
    void crear_conNombreNuevo_debeGuardarYRetornar() {
        // Arrange
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Teclados");
        when(categoriaRepository.existsByNombreAndActivoTrue("Teclados")).thenReturn(false);

        // Act
        CategoriaResponseDTO resultado = categoriaService.crear(dto);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("Teclados");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void crear_conNombreExistente_debeLanzarExcepcion() {
        // Arrange
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Periféricos");
        when(categoriaRepository.existsByNombreAndActivoTrue("Periféricos")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> categoriaService.crear(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una categoría con ese nombre");

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void actualizar_cuandoExiste_debeModificarYRetornar() {
        // Arrange
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Periféricos Gamer");
        Categoria existente = new Categoria("Periféricos");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));

        // Act
        Optional<CategoriaResponseDTO> resultado = categoriaService.actualizar(1L, dto);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Periféricos Gamer");
        verify(categoriaRepository).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Arrange
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Cualquiera");
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<CategoriaResponseDTO> resultado = categoriaService.actualizar(99L, dto);

        // Assert
        assertThat(resultado).isEmpty();
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void eliminar_cuandoExiste_debeMarcarInactivoYRetornarTrue() {
        // Arrange
        Categoria categoria = new Categoria("Periféricos");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Act
        boolean resultado = categoriaService.eliminar(1L);

        // Assert
        assertThat(resultado).isTrue();
        assertThat(categoria.getActivo()).isFalse();
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = categoriaService.eliminar(99L);

        // Assert
        assertThat(resultado).isFalse();
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
}