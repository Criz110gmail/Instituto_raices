package escuela.common.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super("No se encontró %s con id %d".formatted(recurso, id));
    }
}
