package cl.manuel.productosapi.exception;

/** Se lanza cuando un recurso solicitado no existe. El handler la traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}