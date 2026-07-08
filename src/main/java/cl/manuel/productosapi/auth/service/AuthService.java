package cl.manuel.productosapi.auth.service;

import cl.manuel.productosapi.auth.dto.AuthResponseDTO;
import cl.manuel.productosapi.auth.dto.LoginRequestDTO;
import cl.manuel.productosapi.auth.dto.RegisterRequestDTO;
import cl.manuel.productosapi.auth.model.Usuario;
import cl.manuel.productosapi.auth.repository.UsuarioRepository;
import cl.manuel.productosapi.exception.CredencialesInvalidasException;
import cl.manuel.productosapi.exception.RecursoDuplicadoException;
import cl.manuel.productosapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Lógica de autenticación: registro de usuarios y login, emitiendo un token JWT.
 * Encapsula el cifrado de contraseñas y la generación de tokens para mantener el resto de la API ajeno a esos detalles.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un usuario nuevo y devuelve un token JWT ya autenticado.
     * @throws RuntimeException si el email ya está registrado.
     */
    public AuthResponseDTO registrar(RegisterRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RecursoDuplicadoException("El email ya está registrado");
        }

        Usuario usuario = new Usuario(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                "USER"
        );

        usuarioRepository.save(usuario);
        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponseDTO(token);
    }

    /**
     * Valida las credenciales y devuelve un token JWT si son correctas.
     * @throws RuntimeException si el email no existe o la contraseña no coincide.
     */
    public AuthResponseDTO login(LoginRequestDTO dto) {
        // Mensaje genérico a propósito: no revelar si el email existe (seguridad)
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            // Mismo mensaje que el email inexistente: evita distinguir usuario válido de contraseña errónea
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponseDTO(token);
    }
}