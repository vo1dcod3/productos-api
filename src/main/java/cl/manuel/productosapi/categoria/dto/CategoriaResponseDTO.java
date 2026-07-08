package cl.manuel.productosapi.categoria.dto;

/**
 * DTO de salida con los datos de una categoría que se devuelven al cliente.
 */
public class CategoriaResponseDTO {

    private Long id;
    private String nombre;

    public CategoriaResponseDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
