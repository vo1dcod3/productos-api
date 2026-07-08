package cl.manuel.productosapi.service;

import cl.manuel.productosapi.categoria.model.Categoria;
import cl.manuel.productosapi.categoria.repository.CategoriaRepository;
import cl.manuel.productosapi.dto.ProductoRequestDTO;
import cl.manuel.productosapi.dto.ProductoResponseDTO;
import cl.manuel.productosapi.model.Producto;
import cl.manuel.productosapi.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lógica de negocio de productos: consultas, creación, actualización y borrado lógico.
 * Traduce entre entidades ({@link Producto}) y DTOs para no exponer el modelo de datos hacia la API.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /** Devuelve todos los productos activos (no eliminados lógicamente). */
    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** Busca un producto por id. Optional vacío si no existe. */
    public Optional<ProductoResponseDTO> obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /** Devuelve los productos activos de una categoría, buscada por su nombre. */
    public List<ProductoResponseDTO> obtenerPorCategoria(String nombre) {
        return productoRepository.findByCategoria_NombreAndActivoTrue(nombre)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea un producto asociándolo a una categoría existente.
     * @throws RuntimeException si la categoría indicada no existe.
     */
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Producto producto = new Producto(dto.getNombre(), categoria, dto.getPrecio(), dto.getStock());
        return toResponseDTO(productoRepository.save(producto));
    }

    /**
     * Actualiza un producto existente. Optional vacío si el producto no existe.
     * @throws RuntimeException si la categoría indicada no existe.
     */
    public Optional<ProductoResponseDTO> actualizar(Long id, ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(dto.getNombre());
                    producto.setCategoria(categoria);
                    producto.setPrecio(dto.getPrecio());
                    producto.setStock(dto.getStock());
                    return toResponseDTO(productoRepository.save(producto));
                });
    }

    /**
     * Borrado lógico: marca el producto como inactivo en vez de eliminarlo de la base.
     * @return true si el producto existía y se desactivó; false si no existía.
     */
    public boolean eliminar(Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setActivo(false);
                    productoRepository.save(producto);
                    return true;
                })
                .orElse(false);
    }

    /** Convierte una entidad Producto en su DTO de respuesta, tolerando categoría nula. */
    private ProductoResponseDTO toResponseDTO(Producto p) {
        // Un producto podría no tener categoría (datos antiguos): se refleja como "Sin categoría".
        Long categoriaId = p.getCategoria() != null ? p.getCategoria().getId() : null;
        String categoriaNombre = p.getCategoria() != null ? p.getCategoria().getNombre() : "Sin categoría";
        return new ProductoResponseDTO(
                p.getId(),
                p.getNombre(),
                categoriaId,
                categoriaNombre,
                p.getPrecio(),
                p.getStock()
        );
    }
}
