package cl.manuel.productosapi.dto;

public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private Long categoriaId;
    private String categoriaNombre;
    private Double precio;
    private Integer stock;

    public ProductoResponseDTO(Long id, String nombre, Long categoriaId, String categoriaNombre, Double precio, Integer stock) {
        this.id = id;
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.precio = precio;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Long getCategoriaId() { return categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public Double getPrecio() { return precio; }
    public Integer getStock() { return stock; }
}
