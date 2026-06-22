package cl.manuel.productosapi.service;

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

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(p -> toResponseDTO(p))
                .collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(p -> toResponseDTO(p));
    }

    public List<ProductoResponseDTO> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria)
                .stream()
                .map(p -> toResponseDTO(p))
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Producto producto = new Producto(dto.getNombre(), dto.getCategoria(), dto.getPrecio(), dto.getStock());
        return toResponseDTO(productoRepository.save(producto));
    }

    public Optional<ProductoResponseDTO> actualizar(Long id, ProductoRequestDTO datos) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(datos.getNombre());
                    producto.setCategoria(datos.getCategoria());
                    producto.setPrecio(datos.getPrecio());
                    producto.setStock(datos.getStock());
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
        return new ProductoResponseDTO(p.getId(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock());
    }
}