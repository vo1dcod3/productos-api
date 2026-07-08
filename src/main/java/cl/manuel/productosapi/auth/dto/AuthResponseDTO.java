package cl.manuel.productosapi.auth.dto;

/**
 * DTO de salida que devuelve el token JWT tras autenticarse o registrarse.
 */
public class AuthResponseDTO {
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}