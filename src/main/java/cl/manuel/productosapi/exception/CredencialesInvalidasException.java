package cl.manuel.productosapi.exception;
/** Se lanza cuando el login recibe credenciales incorrectas. El handler la traduce a HTTP 401. */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
