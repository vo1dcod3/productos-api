package cl.manuel.productosapi.categoria.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para crear o actualizar una categoría.
 */
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    public CategoriaRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
