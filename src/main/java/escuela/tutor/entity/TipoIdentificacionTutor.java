package escuela.tutor.entity;

public enum TipoIdentificacionTutor {
    INE("INE / credencial para votar"),
    LICENCIA_CONDUCIR("Licencia de conducir"),
    PASAPORTE("Pasaporte"),
    OTRA_IDENTIFICACION_OFICIAL("Otra identificación oficial");

    private final String etiqueta;

    TipoIdentificacionTutor(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }
}
