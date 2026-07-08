package cl.manuel.productosapi.categoria.controller;

import cl.manuel.productosapi.categoria.dto.CategoriaRequestDTO;
import cl.manuel.productosapi.categoria.dto.CategoriaResponseDTO;
import cl.manuel.productosapi.categoria.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST de categorías bajo la ruta base {@code /api/categorias}.
 * Expone el CRUD delegando la lógica en {@link CategoriaService} y traduciendo los resultados a códigos HTTP.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /** GET /api/categorias: devuelve 200 con la lista de categorías activas. */
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }

    /** POST /api/categorias: crea una categoría y devuelve 201 con la categoría creada. */
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    /** PUT /api/categorias/{id}: devuelve 200 con la categoría actualizada, o 404 si no existe. */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        return categoriaService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/categorias/{id}: devuelve 204 si se eliminó, o 404 si no existe. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (categoriaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
