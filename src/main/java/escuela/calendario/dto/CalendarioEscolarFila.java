package escuela.calendario.dto;

import escuela.calendario.entity.TipoFechaCalendario;
import java.time.*;

public record CalendarioEscolarFila(Long id,String institucion,String ciclo,String alcance,TipoFechaCalendario tipo,
    String titulo,LocalDate fechaInicio,LocalDate fechaFin,LocalTime horaInicio,LocalTime horaFin,
    boolean suspendeClases,String descripcion,boolean activo,Long version) {}
