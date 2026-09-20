package escuela.portal.dto;

public record PortalHijoResumen(
        Long alumnoId, String matricula, String nombre, String parentesco,
        boolean contactoPrincipal, boolean responsableFinanciero, boolean puedeVerFinanzas,
        boolean tieneFotografia, String plantel, String ciclo, String grado, String grupo) {

    public boolean accesoFinanciero() {
        return responsableFinanciero && puedeVerFinanzas;
    }
}
