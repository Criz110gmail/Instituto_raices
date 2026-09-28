package escuela.asistencia.entity;

public enum EstadoAsistencia {
    PRESENTE("Presente"), AUSENTE("Ausente"), RETARDO("Retardo"), JUSTIFICADA("Justificada");

    private final String etiqueta;
    EstadoAsistencia(String etiqueta) { this.etiqueta = etiqueta; }
    public String getEtiqueta() { return etiqueta; }
}
