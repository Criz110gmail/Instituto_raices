package escuela.common.exception;

public class ConflictoVersionException extends ReglaNegocioException {

    public ConflictoVersionException(String recurso, Long id) {
        super("%s con id %d fue modificado por otro proceso; recarga la información".formatted(recurso, id));
    }
}
