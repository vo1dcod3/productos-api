package cl.manuel.productosapi.dto;

public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private Double precio;
    private Integer stock;

    public ProductoResponseDTO(Long id, String nombre, String categoria, Double precio, Integer stock) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public Double getPrecio() { return precio; }
    public Integer getStock() { return stock; }
}