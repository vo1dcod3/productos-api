package cl.manuel.productosapi.exception;

/** Se lanza cuando un recurso choca con otro que ya existe. El handler la traduce a HTTP 409. */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
