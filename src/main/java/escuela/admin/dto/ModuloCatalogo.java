package escuela.admin.dto;

import java.util.Arrays;
import java.util.List;

public enum ModuloCatalogo {
    INSTITUCIONES("instituciones", "Instituciones", List.of("Código", "Nombre", "Zona horaria", "Moneda")),
    PLANTELES("planteles", "Planteles", List.of("Código", "Plantel", "Institución", "Ciudad")),
    NIVELES("niveles", "Niveles educativos", List.of("Código", "Nivel", "Institución", "Orden")),
    OFERTA("oferta", "Oferta educativa", List.of("Plantel", "Nivel", "Clave CCT")),
    GRADOS("grados", "Grados", List.of("Código", "Grado", "Nivel", "Orden")),
    CICLOS("ciclos", "Ciclos escolares", List.of("Código", "Ciclo", "Inicio", "Fin", "Predeterminado")),
    PERIODOS("periodos", "Periodos académicos", List.of("Código", "Periodo", "Nivel", "Tipo", "Fechas")),
    GRUPOS("grupos", "Grupos", List.of("Grupo", "Código", "Plantel", "Grado", "Turno", "Capacidad")),
    ROLES("roles", "Roles y permisos", List.of("Código", "Rol", "Institución", "Descripción"));

    private final String slug;
    private final String titulo;
    private final List<String> columnas;

    ModuloCatalogo(String slug, String titulo, List<String> columnas) {
        this.slug = slug;
        this.titulo = titulo;
        this.columnas = columnas;
    }

    public String slug() { return slug; }
    public String titulo() { return titulo; }
    public List<String> columnas() { return columnas; }

    public boolean mantenimientoDisponible() {
        return this == INSTITUCIONES || this == PLANTELES || this == NIVELES
                || this == OFERTA || this == GRADOS || this == CICLOS || this == PERIODOS
                || this == GRUPOS || this == ROLES;
    }

    public String rutaMantenimiento() {
        return switch (this) {
            case INSTITUCIONES -> "/admin/instituciones";
            case PLANTELES -> "/admin/planteles";
            case NIVELES -> "/admin/niveles";
            case OFERTA -> "/admin/oferta";
            case GRADOS -> "/admin/grados";
            case CICLOS -> "/admin/ciclos";
            case PERIODOS -> "/admin/periodos";
            case GRUPOS -> "/admin/grupos";
            case ROLES -> "/admin/roles";
            default -> "";
        };
    }

    public String segmentoNuevo() {
        return this == PLANTELES || this == NIVELES || this == GRADOS || this == CICLOS
                || this == PERIODOS || this == GRUPOS || this == ROLES
                ? "/nuevo" : "/nueva";
    }

    public static ModuloCatalogo desde(String slug) {
        return Arrays.stream(values()).filter(m -> m.slug.equalsIgnoreCase(slug)).findFirst()
                .orElse(INSTITUCIONES);
    }
}
