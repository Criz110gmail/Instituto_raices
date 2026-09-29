package escuela.docente.entity;

public enum EstadoPlaneacion {
    BORRADOR("Borrador"), ENVIADA("Enviada"), EN_REVISION("En revisión"),
    REQUIERE_AJUSTES("Requiere ajustes"), PUBLICADA("Publicada"),
    REABIERTA("Reabierta"), DESCARTADA("Descartada");

    private final String etiqueta;
    EstadoPlaneacion(String etiqueta) { this.etiqueta = etiqueta; }
    public String getEtiqueta() { return etiqueta; }
}
