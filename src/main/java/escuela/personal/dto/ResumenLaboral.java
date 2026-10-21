package escuela.personal.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Datos guardados, no la captura pendiente del formulario. */
public record ResumenLaboral(String situacion, LocalDate ingreso, LocalDate baja) {
 public String etiqueta(){return "ACTIVO".equals(situacion)?"Activo":("BAJA".equals(situacion)?"Baja laboral":"Por confirmar");}
 public String clase(){return "ACTIVO".equals(situacion)?"positivo":"neutro";}
 public String ingresoTexto(){return fecha(ingreso);}
 public String bajaTexto(){return fecha(baja);}
 private static String fecha(LocalDate fecha){return fecha==null?"Sin registrar":fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
}
