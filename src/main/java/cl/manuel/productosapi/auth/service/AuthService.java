package cl.manuel.productosapi.auth.service;

import cl.manuel.productosapi.auth.dto.AuthResponseDTO;
import cl.manuel.productosapi.auth.dto.LoginRequestDTO;
import cl.manuel.productosapi.auth.dto.RegisterRequestDTO;
import cl.manuel.productosapi.auth.model.Usuario;
import cl.manuel.productosapi.auth.repository.UsuarioRepository;
import cl.manuel.productosapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthResponseDTO registrar(RegisterRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
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

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponseDTO(token);
    }
}