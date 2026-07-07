package cl.manuel.productosapi.categoria.service;

import cl.manuel.productosapi.categoria.dto.CategoriaRequestDTO;
import cl.manuel.productosapi.categoria.dto.CategoriaResponseDTO;
import cl.manuel.productosapi.categoria.model.Categoria;
import cl.manuel.productosapi.categoria.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(c -> new CategoriaResponseDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList());
    }

    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        if (categoriaRepository.existsByNombreAndActivoTrue(dto.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = new Categoria(dto.getNombre());
        categoriaRepository.save(categoria);
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNombre());
    }

    public Optional<CategoriaResponseDTO> actualizar(Long id, CategoriaRequestDTO dto) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNombre(dto.getNombre());
                    categoriaRepository.save(categoria);
                    return new CategoriaResponseDTO(categoria.getId(), categoria.getNombre());
                });
    }

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
