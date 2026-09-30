package escuela.calendario.entity;

public enum TipoFechaCalendario {
    DIA_INHABIL("Día inhábil"), VACACIONES("Vacaciones"),
    EVENTO_ACADEMICO("Evento académico"), VARIACION_HORARIO("Variación de horario");
    private final String etiqueta;TipoFechaCalendario(String e){etiqueta=e;}public String getEtiqueta(){return etiqueta;}
}
