package escuela.docente.dto;

import java.time.LocalDateTime;

/** Indicación administrativa vigente para la captura propia; no expone el historial completo. */
public record ObservacionRevisionPlaneacion(String titulo, String motivo, LocalDateTime solicitadaEn) { }
