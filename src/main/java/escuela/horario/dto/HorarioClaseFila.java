package escuela.horario.dto;

import java.time.*;

public record HorarioClaseFila(Long id,String institucion,String plantel,String ciclo,String maestro,
        String numeroEmpleado,String grado,String grupo,String materia,Integer diaSemana,String dia,
        LocalTime horaInicio,LocalTime horaFin,LocalDate fechaInicio,LocalDate fechaFin,String aula,
        String observaciones,boolean activo,Long version) {}
