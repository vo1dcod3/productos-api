package cl.manuel.productosapi.categoria.service;

import cl.manuel.productosapi.categoria.dto.CategoriaRequestDTO;
import cl.manuel.productosapi.categoria.dto.CategoriaResponseDTO;
import cl.manuel.productosapi.categoria.model.Categoria;
import cl.manuel.productosapi.categoria.repository.CategoriaRepository;
import cl.manuel.productosapi.exception.RecursoDuplicadoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lógica de negocio de categorías: consultas, creación, actualización y borrado lógico.
 * Traduce entre entidades ({@link Categoria}) y DTOs para no exponer el modelo de datos hacia la API.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /** Devuelve todas las categorías activas (no eliminadas lógicamente). */
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(c -> new CategoriaResponseDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Crea una categoría nueva y devuelve su DTO de respuesta.
     * @throws RuntimeException si ya existe una categoría activa con el mismo nombre.
     */
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        if (categoriaRepository.existsByNombreAndActivoTrue(dto.getNombre())) {
            throw new RecursoDuplicadoException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = new Categoria(dto.getNombre());
        categoriaRepository.save(categoria);
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNombre());
    }

    /** Actualiza el nombre de una categoría; devuelve el DTO actualizado o vacío si el id no existe. */
    public Optional<CategoriaResponseDTO> actualizar(Long id, CategoriaRequestDTO dto) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNombre(dto.getNombre());
                    categoriaRepository.save(categoria);
                    return new CategoriaResponseDTO(categoria.getId(), categoria.getNombre());
                });
    }

    /** Realiza el borrado lógico de una categoría; devuelve true si existía, false en caso contrario. */
    public boolean eliminar(Long id) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setActivo(false);
                    categoriaRepository.save(categoria);
                    return true;
                })
                .orElse(false);
    }
}
