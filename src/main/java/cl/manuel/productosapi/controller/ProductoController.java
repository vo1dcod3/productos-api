package cl.manuel.productosapi.controller;

import cl.manuel.productosapi.dto.ProductoRequestDTO;
import cl.manuel.productosapi.dto.ProductoResponseDTO;
import cl.manuel.productosapi.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST de productos bajo la ruta base {@code /api/productos}.
 * Expone el CRUD delegando la lógica en {@link ProductoService} y traduciendo los resultados a códigos HTTP.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** GET /api/productos: devuelve 200 con la lista de productos activos. */
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    /** GET /api/productos/{id}: devuelve 200 con el producto, o 404 si no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/productos/buscar: devuelve 200 con los productos de la categoría indicada. */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    /** POST /api/productos: crea un producto y devuelve 201 con el producto creado. */
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
    }

    /** PUT /api/productos/{id}: devuelve 200 con el producto actualizado, o 404 si no existe. */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        return productoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/productos/{id}: devuelve 204 si se eliminó, o 404 si no existe. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
