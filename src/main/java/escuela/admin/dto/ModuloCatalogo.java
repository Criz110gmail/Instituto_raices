package escuela.admin.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum ModuloCatalogo {
    INSTITUCIONES("instituciones", "Instituciones", List.of("Código", "Nombre", "Zona horaria", "Moneda")),
    PLANTELES("planteles", "Planteles", List.of("Código", "Plantel", "Institución", "Ciudad")),
    NIVELES("niveles", "Niveles educativos", List.of("Código", "Nivel", "Institución", "Orden")),
    OFERTA("oferta", "Oferta educativa", List.of("Plantel", "Nivel", "Clave CCT")),
    GRADOS("grados", "Grados", List.of("Código", "Grado", "Nivel", "Orden")),
    CICLOS("ciclos", "Ciclos escolares", List.of("Código", "Ciclo", "Inicio", "Fin", "Predeterminado")),
    PERIODOS("periodos", "Periodos académicos", List.of("Código", "Periodo", "Nivel", "Tipo", "Fechas")),
    GRUPOS("grupos", "Grupos", List.of("Grupo", "Código", "Plantel", "Grado", "Turno", "Capacidad")),
    ALUMNOS("alumnos", "Alumnos", List.of("Matrícula", "Alumno", "CURP", "Nacimiento", "Ingreso")),
    TUTORES("tutores", "Tutores", List.of("Tutor", "Teléfono", "Correo", "Cuenta de acceso", "Institución")),
    VINCULOS_TUTOR("vinculos-tutor", "Vínculos alumno–tutor", List.of("Alumno", "Tutor", "Parentesco", "Permisos", "Vigencia")),
    INSCRIPCIONES("inscripciones", "Inscripciones", List.of("Inscripción", "Alumno", "Plantel", "Ciclo", "Grado", "Vigencia")),
    CONCEPTOS_COBRO("conceptos-cobro", "Conceptos de cobro", List.of("Código", "Concepto", "Institución", "Categoría", "Reglas")),
    CUOTAS_ALUMNO("cuotas-alumno", "Cuotas por alumno", List.of("Alumno", "Concepto", "Importe", "Frecuencia", "Vencimiento", "Generación")),
    CARGOS("cargos", "Cargos", List.of("Alumno", "Concepto", "Descripción", "Periodo", "Vencimiento", "Importe", "Saldo")),
    TIPOS_BECA("tipos-beca", "Tipos de beca", List.of("Código", "Tipo de beca", "Institución", "Descripción")),
    BECAS_ALUMNO("becas-alumno", "Becas por alumno", List.of("Alumno", "Tipo", "Concepto", "Beneficio", "Vigencia")),
    AJUSTES_CARGO("ajustes-cargo", "Ajustes de cargos", List.of("Alumno", "Concepto", "Tipo", "Efecto", "Monto", "Fecha", "Motivo")),
    POLITICAS_RECARGO("politicas-recargo", "Políticas de recargo", List.of("Concepto", "Institución", "Recargo", "Gracia", "Periodicidad", "Límite", "Generación")),
    MOTIVOS_FINANCIEROS("motivos-financieros", "Motivos financieros", List.of("Código", "Motivo", "Institución", "Naturaleza", "Categoría")),
    CUENTAS_FINANCIERAS("cuentas-financieras", "Cuentas financieras", List.of("Código", "Cuenta", "Alcance", "Tipo", "Institución financiera", "Identificador", "Saldo inicial", "Fecha inicial")),
    PAGOS("pagos", "Pagos", List.of("Folio", "Tutor", "Plantel de registro", "Fecha", "Método", "Monto", "Distribución", "Comprobantes")),
    MOVIMIENTOS_FINANCIEROS("movimientos-financieros", "Movimientos financieros", List.of()),
    ROLES("roles", "Roles y permisos", List.of("Código", "Rol", "Institución", "Descripción")),
    USUARIOS("usuarios", "Usuarios", List.of("Usuario", "Correo", "Institución", "Credencial"));

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

    public String seccion() {
        return switch (this) {
            case INSTITUCIONES, PLANTELES, NIVELES, OFERTA, GRADOS, CICLOS, PERIODOS, GRUPOS -> "Estructura";
            case ALUMNOS, TUTORES, VINCULOS_TUTOR -> "Personas";
            case INSCRIPCIONES -> "Trayectoria";
            case CONCEPTOS_COBRO, CUOTAS_ALUMNO, CARGOS, TIPOS_BECA, BECAS_ALUMNO, AJUSTES_CARGO, POLITICAS_RECARGO -> "Cobranza";
            case MOTIVOS_FINANCIEROS, CUENTAS_FINANCIERAS, PAGOS, MOVIMIENTOS_FINANCIEROS -> "Finanzas";
            case ROLES, USUARIOS -> "Seguridad";
        };
    }

    public boolean visibleCon(Set<String> permisos) {
        String base = switch (this) {
            case INSTITUCIONES -> "INSTITUCION";
            case PLANTELES -> "PLANTEL";
            case NIVELES -> "NIVEL";
            case OFERTA -> "OFERTA";
            case GRADOS -> "GRADO";
            case CICLOS -> "CICLO";
            case PERIODOS -> "PERIODO";
            case GRUPOS -> "GRUPO";
            case ALUMNOS -> "ALUMNO";
            case TUTORES -> "TUTOR";
            case VINCULOS_TUTOR -> "VINCULO_TUTOR";
            case INSCRIPCIONES -> "INSCRIPCION";
            case CONCEPTOS_COBRO -> "CONCEPTO_COBRO";
            case CUOTAS_ALUMNO -> "CUOTA_ALUMNO";
            case CARGOS -> "CARGO";
            case TIPOS_BECA -> "TIPO_BECA";
            case BECAS_ALUMNO -> "BECA_ALUMNO";
            case AJUSTES_CARGO -> "AJUSTE_CARGO";
            case POLITICAS_RECARGO -> "POLITICA_RECARGO";
            case MOTIVOS_FINANCIEROS -> "MOTIVO_FINANCIERO";
            case CUENTAS_FINANCIERAS -> "CUENTA_FINANCIERA";
            case PAGOS -> "PAGO";
            case MOVIMIENTOS_FINANCIEROS -> "MOVIMIENTO_FINANCIERO";
            case ROLES -> "ROL";
            case USUARIOS -> "USUARIO";
        };
        if (this == PAGOS) return permisos.contains("PAGO_LEER") || permisos.contains("PAGO_REGISTRAR")
                || permisos.contains("PAGO_VALIDAR") || permisos.contains("PAGO_DEVOLVER");
        if (this == MOVIMIENTOS_FINANCIEROS) return permisos.contains("MOVIMIENTO_FINANCIERO_LEER")
                || permisos.contains("MOVIMIENTO_FINANCIERO_REGISTRAR")
                || permisos.contains("TRANSFERENCIA_CUENTA_REGISTRAR");
        boolean administra = permisos.contains(base + "_ADMINISTRAR");
        if (this == ROLES || this == USUARIOS) return administra;
        return administra || permisos.contains(base + "_LEER");
    }

    public boolean mantenimientoDisponible() {
        return this == INSTITUCIONES || this == PLANTELES || this == NIVELES
                || this == OFERTA || this == GRADOS || this == CICLOS || this == PERIODOS
                || this == GRUPOS || this == ALUMNOS || this == TUTORES
                || this == VINCULOS_TUTOR || this == INSCRIPCIONES
                || this == CONCEPTOS_COBRO || this == CUOTAS_ALUMNO
                || this == CARGOS
                || this == TIPOS_BECA || this == BECAS_ALUMNO || this == AJUSTES_CARGO || this == POLITICAS_RECARGO
                || this == MOTIVOS_FINANCIEROS
                || this == CUENTAS_FINANCIERAS
                || this == PAGOS
                || this == ROLES || this == USUARIOS;
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
            case ALUMNOS -> "/admin/alumnos";
            case TUTORES -> "/admin/tutores";
            case VINCULOS_TUTOR -> "/admin/vinculos-tutor";
            case INSCRIPCIONES -> "/admin/inscripciones";
            case CONCEPTOS_COBRO -> "/admin/conceptos-cobro";
            case CUOTAS_ALUMNO -> "/admin/cuotas-alumno";
            case CARGOS -> "/admin/cargos";
            case TIPOS_BECA -> "/admin/tipos-beca";
            case BECAS_ALUMNO -> "/admin/becas-alumno";
            case AJUSTES_CARGO -> "/admin/ajustes-cargo";
            case POLITICAS_RECARGO -> "/admin/politicas-recargo";
            case MOTIVOS_FINANCIEROS -> "/admin/motivos-financieros";
            case CUENTAS_FINANCIERAS -> "/admin/cuentas-financieras";
            case PAGOS -> "/admin/pagos";
            case MOVIMIENTOS_FINANCIEROS -> "/admin/movimientos-financieros";
            case ROLES -> "/admin/roles";
            case USUARIOS -> "/admin/usuarios";
            default -> "";
        };
    }

    public String rutaListado() {
        return this == MOVIMIENTOS_FINANCIEROS
                ? "/admin/movimientos-financieros"
                : "/admin/catalogos/" + slug;
    }

    public String segmentoNuevo() {
        return this == PLANTELES || this == NIVELES || this == GRADOS || this == CICLOS
                || this == PERIODOS || this == GRUPOS || this == ALUMNOS || this == TUTORES
                || this == VINCULOS_TUTOR || this == INSCRIPCIONES
                || this == CONCEPTOS_COBRO || this == CUOTAS_ALUMNO
                || this == CARGOS
                || this == TIPOS_BECA || this == BECAS_ALUMNO || this == AJUSTES_CARGO || this == POLITICAS_RECARGO
                || this == MOTIVOS_FINANCIEROS
                || this == CUENTAS_FINANCIERAS
                || this == PAGOS
                || this == ROLES || this == USUARIOS
                ? "/nuevo" : "/nueva";
    }

    public static ModuloCatalogo desde(String slug) {
        return Arrays.stream(values()).filter(m -> m.slug.equalsIgnoreCase(slug)).findFirst()
                .orElse(INSTITUCIONES);
    }
}
