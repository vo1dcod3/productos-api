package cl.manuel.productosapi.controller;

import cl.manuel.productosapi.dto.ProductoRequestDTO;
import cl.manuel.productosapi.dto.ProductoResponseDTO;
import cl.manuel.productosapi.exception.GlobalExceptionHandler;
import cl.manuel.productosapi.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Monta MockMvc con solo este controller + el manejador global de errores
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void obtenerTodos_debeRetornar200ConLista() throws Exception {
        ProductoResponseDTO p = new ProductoResponseDTO(1L, "Teclado", 1L, "Periféricos", 25000.0, 10);
        when(productoService.obtenerTodos()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"))
                .andExpect(jsonPath("$[0].categoriaNombre").value("Periféricos"));
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() throws Exception {
        ProductoResponseDTO p = new ProductoResponseDTO(1L, "Teclado", 1L, "Periféricos", 25000.0, 10);
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(productoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Monitor");
        dto.setCategoriaId(1L);
        dto.setPrecio(150000.0);
        dto.setStock(5);

        ProductoResponseDTO creado = new ProductoResponseDTO(1L, "Monitor", 1L, "Monitores", 150000.0, 5);
        when(productoService.crear(any(ProductoRequestDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Monitor"));
    }

    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {
        ProductoRequestDTO dto = new ProductoRequestDTO();   // todo vacío → viola las validaciones

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        when(productoService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(productoService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound());
    }
}