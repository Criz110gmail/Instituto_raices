package escuela.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    ModelAndView noEncontrado(RecursoNoEncontradoException excepcion) {
        return vista(HttpStatus.NOT_FOUND, excepcion.getMessage());
    }

    @ExceptionHandler({ReglaNegocioException.class, ObjectOptimisticLockingFailureException.class})
    ModelAndView conflicto(Exception excepcion) {
        String mensaje = excepcion instanceof ObjectOptimisticLockingFailureException
                ? "La información fue modificada por otro proceso; recarga e intenta nuevamente"
                : excepcion.getMessage();
        return vista(HttpStatus.CONFLICT, mensaje);
    }

    private ModelAndView vista(HttpStatus estado, String mensaje) {
        ModelAndView resultado = new ModelAndView("error/negocio");
        resultado.setStatus(estado);
        resultado.addObject("codigo", estado.value());
        resultado.addObject("titulo", estado.getReasonPhrase());
        resultado.addObject("mensaje", mensaje);
        return resultado;
    }
}
