package escuela.admin.dto;

public record EventoEscolarFila(Long id, String titulo, String institucion, String plantel,
                                String ciclo, String inicio, String fin, String tipo,
                                String estado, String alcance, long destinatarios) { }
