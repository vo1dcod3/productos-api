package cl.manuel.productosapi.categoria.controller;

import cl.manuel.productosapi.categoria.dto.CategoriaRequestDTO;
import cl.manuel.productosapi.categoria.dto.CategoriaResponseDTO;
import cl.manuel.productosapi.categoria.service.CategoriaService;
import cl.manuel.productosapi.exception.GlobalExceptionHandler;
import cl.manuel.productosapi.exception.RecursoDuplicadoException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void obtenerTodas_debeRetornar200ConLista() throws Exception {
        when(categoriaService.obtenerTodas()).thenReturn(List.of(
                new CategoriaResponseDTO(1L, "Periféricos"),
                new CategoriaResponseDTO(2L, "Monitores")
        ));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Periféricos"));
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Teclados");
        when(categoriaService.crear(any(CategoriaRequestDTO.class)))
                .thenReturn(new CategoriaResponseDTO(1L, "Teclados"));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Teclados"));
    }

    @Test
    void crear_conNombreVacio_debeRetornar400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();   // nombre null → viola @NotBlank

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_conNombreDuplicado_debeRetornar409() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Periféricos");
        when(categoriaService.crear(any(CategoriaRequestDTO.class)))
                .thenThrow(new RecursoDuplicadoException("Ya existe una categoría con ese nombre"));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Periféricos Gamer");
        when(categoriaService.actualizar(eq(1L), any(CategoriaRequestDTO.class)))
                .thenReturn(Optional.of(new CategoriaResponseDTO(1L, "Periféricos Gamer")));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Periféricos Gamer"));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Cualquiera");
        when(categoriaService.actualizar(eq(99L), any(CategoriaRequestDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        when(categoriaService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(categoriaService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }
}