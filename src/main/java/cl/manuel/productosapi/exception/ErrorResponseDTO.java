package cl.manuel.productosapi.exception;

import java.util.Map;

/**
 * Estructura uniforme de error que la API devuelve al cliente: código, mensaje y detalle por campo cuando aplica.
 */
public class ErrorResponseDTO {

    private int status;
    private String error;
    private Map<String, String> campos;

    public ErrorResponseDTO(int status, String error, Map<String, String> campos) {
        this.status = status;
        this.error = error;
        this.campos = campos;
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public Map<String, String> getCampos() { return campos; }
}