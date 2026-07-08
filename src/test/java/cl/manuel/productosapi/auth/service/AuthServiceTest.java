package cl.manuel.productosapi.auth.service;

import cl.manuel.productosapi.auth.dto.AuthResponseDTO;
import cl.manuel.productosapi.auth.dto.LoginRequestDTO;
import cl.manuel.productosapi.auth.dto.RegisterRequestDTO;
import cl.manuel.productosapi.auth.model.Usuario;
import cl.manuel.productosapi.auth.repository.UsuarioRepository;
import cl.manuel.productosapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_conEmailNuevo_debeGuardarYRetornarToken() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("nuevo@test.cl");
        dto.setPassword("123456");

        when(usuarioRepository.findByEmail("nuevo@test.cl")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashFalso");
        when(jwtService.generarToken("nuevo@test.cl")).thenReturn("token-jwt-falso");

        // Act
        AuthResponseDTO resultado = authService.registrar(dto);

        // Assert
        assertThat(resultado.getToken()).isEqualTo("token-jwt-falso");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_conEmailExistente_debeLanzarExcepcion() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("existe@test.cl");
        dto.setPassword("123456");

        Usuario usuarioExistente = new Usuario("existe@test.cl", "hash", "USER");
        when(usuarioRepository.findByEmail("existe@test.cl")).thenReturn(Optional.of(usuarioExistente));

        // Act + Assert
        assertThatThrownBy(() -> authService.registrar(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El email ya está registrado");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_conCredencialesValidas_debeRetornarToken() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@test.cl");
        dto.setPassword("123456");

        Usuario usuario = new Usuario("user@test.cl", "hashGuardado", "USER");
        when(usuarioRepository.findByEmail("user@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hashGuardado")).thenReturn(true);
        when(jwtService.generarToken("user@test.cl")).thenReturn("token-valido");

        // Act
        AuthResponseDTO resultado = authService.login(dto);

        // Assert
        assertThat(resultado.getToken()).isEqualTo("token-valido");
    }

    @Test
    void login_conEmailInexistente_debeLanzarExcepcion() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("noexiste@test.cl");
        dto.setPassword("123456");

        when(usuarioRepository.findByEmail("noexiste@test.cl")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Credenciales inválidas");
    }

    @Test
    void login_conPasswordIncorrecta_debeLanzarExcepcion() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@test.cl");
        dto.setPassword("claveIncorrecta");

        Usuario usuario = new Usuario("user@test.cl", "hashGuardado", "USER");
        when(usuarioRepository.findByEmail("user@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("claveIncorrecta", "hashGuardado")).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Credenciales inválidas");

        verify(jwtService, never()).generarToken(anyString());   // nunca generó token
    }
}