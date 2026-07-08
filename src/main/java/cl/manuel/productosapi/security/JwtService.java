package cl.manuel.productosapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Servicio de tokens JWT: genera el token al autenticar y lo valida en cada
 * request. Firma con clave HMAC derivada del secreto configurado.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT firmado para el email dado.
     * @return el token compacto listo para enviar al cliente.
     */
    public String generarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    /**
     * Extrae el email (subject) contenido en el token.
     * @return el email del usuario dueño del token.
     */
    public String extraerEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Valida que el token corresponda al email dado y que no esté expirado.
     * @return true si el token es válido para ese usuario.
     */
    public boolean validarToken(String token, String email) {
        String emailToken = extraerEmail(token);
        return emailToken.equals(email) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}