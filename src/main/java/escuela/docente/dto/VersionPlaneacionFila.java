package escuela.docente.dto;

import java.time.LocalDateTime;

/** Datos de presentación; el instante original de publicación permanece inalterado. */
public record VersionPlaneacionFila(int numeroRevision, LocalDateTime publicadaEn) { }
