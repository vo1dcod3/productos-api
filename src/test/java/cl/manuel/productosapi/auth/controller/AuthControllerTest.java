package cl.manuel.productosapi.auth.controller;

import cl.manuel.productosapi.auth.dto.AuthResponseDTO;
import cl.manuel.productosapi.auth.dto.LoginRequestDTO;
import cl.manuel.productosapi.auth.dto.RegisterRequestDTO;
import cl.manuel.productosapi.auth.service.AuthService;
import cl.manuel.productosapi.exception.CredencialesInvalidasException;
import cl.manuel.productosapi.exception.GlobalExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registro_conDatosValidos_debeRetornar201ConToken() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("nuevo@test.cl");
        dto.setPassword("123456");
        when(authService.registrar(any(RegisterRequestDTO.class)))
                .thenReturn(new AuthResponseDTO("token-jwt"));

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    @Test
    void login_conCredencialesValidas_debeRetornar200ConToken() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@test.cl");
        dto.setPassword("123456");
        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn(new AuthResponseDTO("token-valido"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-valido"));
    }

    @Test
    void login_conCredencialesInvalidas_debeRetornar401() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@test.cl");
        dto.setPassword("claveMala");
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new CredencialesInvalidasException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}