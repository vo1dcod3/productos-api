package cl.manuel.productosapi.auth.controller;

import cl.manuel.productosapi.auth.dto.AuthResponseDTO;
import cl.manuel.productosapi.auth.dto.LoginRequestDTO;
import cl.manuel.productosapi.auth.dto.RegisterRequestDTO;
import cl.manuel.productosapi.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registro(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}