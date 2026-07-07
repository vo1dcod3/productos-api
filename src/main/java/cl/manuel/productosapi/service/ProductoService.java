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

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public List<ProductoResponseDTO> obtenerPorCategoria(String nombre) {
        return productoRepository.findByCategoria_NombreAndActivoTrue(nombre)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Producto producto = new Producto(dto.getNombre(), categoria, dto.getPrecio(), dto.getStock());
        return toResponseDTO(productoRepository.save(producto));
    }

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

    public boolean eliminar(Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setActivo(false);
                    productoRepository.save(producto);
                    return true;
                })
                .orElse(false);
    }

    private ProductoResponseDTO toResponseDTO(Producto p) {
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
