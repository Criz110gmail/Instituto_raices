package escuela.docente.dto;

import escuela.docente.entity.EstadoPlaneacion;
import java.time.Instant;

public record HistorialPlaneacionFila(EstadoPlaneacion anterior, EstadoPlaneacion nuevo,
                                      String motivo, String actor, Instant ocurridoEn) { }
