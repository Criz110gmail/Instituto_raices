package escuela.admin.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map;

public enum ModuloCatalogo {
    ALUMNOS("alumnos", "Alumnos", List.of("Matrícula", "Alumno", "CURP", "Nacimiento", "Ingreso")),
    TUTORES("tutores", "Tutores", List.of("Tutor", "Teléfono", "Correo", "Cuenta de acceso", "Institución")),
    VINCULOS_TUTOR("vinculos-tutor", "Familiares del alumno", List.of("Alumno", "Tutor", "Parentesco", "Permisos", "Vigencia")),
    INSCRIPCIONES("inscripciones", "Inscripciones", List.of("Inscripción", "Alumno", "Plantel", "Ciclo", "Grado", "Vigencia")),
    GRUPOS("grupos", "Grupos", List.of("Grupo", "Código", "Plantel", "Grado", "Turno", "Capacidad")),
    ACTUALIZACIONES_EXPEDIENTE("actualizaciones-expediente", "Actualizaciones familiares", List.of()),
    MATERIAS("materias", "Materias", List.of("Código", "Materia", "Institución", "Descripción")),
    MAESTROS("maestros", "Maestros", List.of("Número", "Maestro", "Correo", "Cuenta de acceso", "Institución")),
    HORARIOS_CLASE("horarios-clases", "Horarios y clases", List.of()),
    CALENDARIO_ESCOLAR("calendario-escolar", "Calendario escolar", List.of()),
    PLANEACIONES("planeaciones", "Planeaciones", List.of()),
    ASISTENCIA("asistencia", "Asistencia", List.of("Fecha", "Alumno", "Matrícula", "Grupo", "Plantel", "Observaciones")),
    CALIFICACIONES("calificaciones", "Calificaciones", List.of("Alumno", "Matrícula", "Materia", "Periodo", "Grado", "Resultado")),
    BOLETAS("boletas", "Boletas", List.of()),
    CONCEPTOS_COBRO("conceptos-cobro", "Conceptos de cobro", List.of("Código", "Concepto", "Institución", "Categoría", "Reglas")),
    CUOTAS_ALUMNO("cuotas-alumno", "Cuotas por alumno", List.of("Alumno", "Concepto", "Importe", "Frecuencia", "Vencimiento", "Generación")),
    TIPOS_BECA("tipos-beca", "Tipos de beca", List.of("Código", "Tipo de beca", "Institución", "Descripción")),
    BECAS_ALUMNO("becas-alumno", "Becas por alumno", List.of("Alumno", "Tipo", "Concepto", "Beneficio", "Vigencia")),
    CARGOS("cargos", "Adeudos de alumnos", List.of("Alumno", "Concepto", "Descripción", "Periodo", "Vencimiento", "Importe", "Saldo")),
    CONVENIOS_PAGO("convenios-pago", "Convenios de pago", List.of()),
    AJUSTES_CARGO("ajustes-cargo", "Historial de ajustes", List.of("Alumno", "Concepto", "Tipo", "Efecto", "Monto", "Fecha", "Motivo")),
    POLITICAS_RECARGO("politicas-recargo", "Políticas de recargo", List.of("Concepto", "Institución", "Recargo", "Gracia", "Periodicidad", "Límite", "Generación")),
    AVISOS("avisos", "Avisos escolares", List.of()),
    EVENTOS_ESCOLARES("eventos-escolares", "Eventos escolares", List.of()),
    PAGOS("pagos", "Pagos recibidos", List.of("Folio", "Tutor", "Plantel de registro", "Fecha", "Método", "Origen", "Total recibido", "Distribución", "Total de cargos vinculados", "Saldo actual por pagar", "Comprobantes")),
    MOTIVOS_FINANCIEROS("motivos-financieros", "Motivos financieros", List.of("Código", "Motivo", "Institución", "Naturaleza", "Categoría")),
    CUENTAS_FINANCIERAS("cuentas-financieras", "Cuentas financieras", List.of("Código", "Cuenta", "Alcance", "Tipo", "Institución financiera", "Identificador", "Saldo inicial", "Saldo actual", "Fecha inicial")),
    MOVIMIENTOS_FINANCIEROS("movimientos-financieros", "Movimientos financieros", List.of()),
    RETIROS_FONDO("retiros-fondo", "Retiros de fondos", List.of()),
    REPORTES_FINANCIEROS("reportes-financieros", "Reportes financieros", List.of()),
    PROVEEDORES("proveedores", "Proveedores", List.of()),
    COMPRAS("compras", "Compras", List.of()),
    INSTITUCIONES("instituciones", "Instituciones", List.of("Código", "Nombre", "Zona horaria", "Moneda")),
    PLANTELES("planteles", "Planteles", List.of("Código", "Plantel", "Institución", "Ciudad")),
    NIVELES("niveles", "Niveles educativos", List.of("Código", "Nivel", "Institución")),
    OFERTA("oferta", "Niveles por plantel", List.of("Plantel", "Nivel", "Clave CCT")),
    GRADOS("grados", "Grados", List.of("Código", "Grado", "Nivel")),
    CICLOS("ciclos", "Ciclos escolares", List.of("Código", "Ciclo", "Inicio", "Fin", "Predeterminado")),
    PERIODOS("periodos", "Periodos de evaluación", List.of("Código", "Periodo", "Nivel", "Tipo", "Fechas")),
    ROLES("roles", "Roles y permisos", List.of("Código", "Rol", "Institución", "Descripción")),
    USUARIOS("usuarios", "Usuarios", List.of("Usuario", "Correo", "Institución", "Credencial")),
    AUDITORIA("auditoria", "Auditoría", List.of()),
    PORTAL_TUTOR("portal-tutor", "Soporte del portal familiar", List.of()),
    PORTAL_MAESTRO("portal-maestro", "Soporte del portal de maestros", List.of()),
    GUIAS("guias", "Guía de procesos", List.of());

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

    /** Estados válidos para el filtro de este listado; evita mostrar opciones que el módulo no entiende. */
    public List<Map.Entry<String, String>> estadosFiltro() {
        var todos = Map.entry("TODOS", "Todos");
        return switch (this) {
            case CICLOS, PERIODOS -> List.of(todos, Map.entry("PLANIFICADO", "Planificado"),
                    Map.entry("ABIERTO", "Abierto"), Map.entry("CERRADO", "Cerrado"));
            case CALIFICACIONES -> List.of(todos, Map.entry("BORRADOR", "Borrador"),
                    Map.entry("PUBLICADA", "Publicada"));
            case ASISTENCIA -> List.of(todos, Map.entry("PRESENTE", "Presente"),
                    Map.entry("AUSENTE", "Ausente"), Map.entry("RETARDO", "Retardo"),
                    Map.entry("JUSTIFICADA", "Justificada"));
            case ACTUALIZACIONES_EXPEDIENTE -> List.of(todos, Map.entry("ENVIADA", "Pendiente de revisión"),
                    Map.entry("APROBADA", "Aprobada"), Map.entry("RECHAZADA", "Rechazada"));
            case TUTORES, MAESTROS -> List.of(todos, Map.entry("ACTIVO", "Activo"),
                    Map.entry("INACTIVO", "Inactivo"), Map.entry("SIN_CUENTA", "Sin cuenta de portal"),
                    Map.entry("CUENTA_ACTIVA", "Cuenta activa"),
                    Map.entry("CUENTA_PENDIENTE", "Pendiente de activación"),
                    Map.entry("CUENTA_INACTIVA", "Cuenta desactivada o bloqueada"));
            case INSCRIPCIONES -> List.of(todos, Map.entry("PREINSCRITA", "Preinscrita"),
                    Map.entry("ACTIVA", "Activa"), Map.entry("BAJA", "Baja"),
                    Map.entry("FINALIZADA", "Finalizada"), Map.entry("CANCELADA", "Cancelada"));
            case CUOTAS_ALUMNO -> List.of(todos, Map.entry("ACTIVA", "Activa"),
                    Map.entry("SUSPENDIDA", "Suspendida"), Map.entry("FINALIZADA", "Finalizada"));
            case BECAS_ALUMNO -> List.of(todos, Map.entry("ACTIVA", "Activa"),
                    Map.entry("SUSPENDIDA", "Suspendida"), Map.entry("FINALIZADA", "Finalizada"),
                    Map.entry("CANCELADA", "Cancelada"));
            case CARGOS -> List.of(todos, Map.entry("CON_SALDO", "Con saldo pendiente"),
                    Map.entry("PENDIENTE", "Pendiente sin abonos"), Map.entry("PARCIAL", "Pago parcial"),
                    Map.entry("VENCIDO", "Vencido"), Map.entry("LIQUIDADO", "Liquidado — saldo $0"),
                    Map.entry("CONVENIDO", "Incluido en convenio"), Map.entry("CANCELADO", "Cancelado"));
            case CONVENIOS_PAGO -> List.of(todos, Map.entry("VIGENTE", "Vigente"),
                    Map.entry("CANCELADO", "Cancelado"));
            case PAGOS -> List.of(todos, Map.entry("PENDIENTE_VALIDACION", "Pendiente de validación"),
                    Map.entry("VALIDADO", "Validado"), Map.entry("RECHAZADO", "Rechazado"),
                    Map.entry("CANCELADO", "Cancelado"));
            case COMPRAS -> List.of(todos, Map.entry("BORRADOR", "Borrador"),
                    Map.entry("CONFIRMADA", "Confirmada"), Map.entry("CANCELADA", "Cancelada"));
            case AJUSTES_CARGO -> List.of(todos, Map.entry("BECA", "Beca"),
                    Map.entry("DESCUENTO", "Descuento"), Map.entry("RECARGO", "Recargo"),
                    Map.entry("CORRECCION", "Corrección"));
            case USUARIOS -> List.of(todos, Map.entry("ACTIVO", "Activo"),
                    Map.entry("INACTIVO", "Inactivo"), Map.entry("INVITADO", "Invitado"),
                    Map.entry("BLOQUEADO", "Bloqueado"));
            default -> List.of(todos, Map.entry("ACTIVO", "Activo"), Map.entry("INACTIVO", "Inactivo"));
        };
    }

    public String seccion() {
        return switch (this) {
            case ALUMNOS, TUTORES, VINCULOS_TUTOR, INSCRIPCIONES, GRUPOS, ACTUALIZACIONES_EXPEDIENTE -> "Operación escolar · Control escolar";
            case MATERIAS, MAESTROS, HORARIOS_CLASE, CALENDARIO_ESCOLAR, PLANEACIONES, ASISTENCIA, CALIFICACIONES, BOLETAS -> "Operación escolar · Gestión académica";
            case CONCEPTOS_COBRO, CUOTAS_ALUMNO, TIPOS_BECA, BECAS_ALUMNO, CARGOS, CONVENIOS_PAGO, AJUSTES_CARGO, POLITICAS_RECARGO -> "Operación escolar · Cobranza escolar";
            case EVENTOS_ESCOLARES, AVISOS -> "Operación escolar · Comunicación";
            case PAGOS, MOTIVOS_FINANCIEROS, CUENTAS_FINANCIERAS, MOVIMIENTOS_FINANCIEROS, RETIROS_FONDO, REPORTES_FINANCIEROS -> "Administración · Finanzas";
            case PROVEEDORES, COMPRAS -> "Administración · Compras y proveedores";
            case INSTITUCIONES, PLANTELES, NIVELES, OFERTA, GRADOS, CICLOS, PERIODOS -> "Administración · Configuración escolar";
            case ROLES, USUARIOS, AUDITORIA, PORTAL_TUTOR, PORTAL_MAESTRO, GUIAS -> "Administración · Seguridad y soporte";
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
            case CALENDARIO_ESCOLAR -> "CALENDARIO_ESCOLAR";
            case GRUPOS -> "GRUPO";
            case MATERIAS -> "MATERIA";
            case CALIFICACIONES -> "CALIFICACION";
            case ASISTENCIA -> "ASISTENCIA";
            case BOLETAS -> "BOLETA";
            case MAESTROS -> "MAESTRO";
            case PLANEACIONES -> "PLANEACION";
            case HORARIOS_CLASE -> "HORARIO_CLASE";
            case ALUMNOS -> "ALUMNO";
            case ACTUALIZACIONES_EXPEDIENTE -> "ACTUALIZACION_EXPEDIENTE";
            case TUTORES -> "TUTOR";
            case VINCULOS_TUTOR -> "VINCULO_TUTOR";
            case INSCRIPCIONES -> "INSCRIPCION";
            case CONCEPTOS_COBRO -> "CONCEPTO_COBRO";
            case CUOTAS_ALUMNO -> "CUOTA_ALUMNO";
            case CARGOS -> "CARGO";
            case CONVENIOS_PAGO -> "CONVENIO_PAGO";
            case TIPOS_BECA -> "TIPO_BECA";
            case BECAS_ALUMNO -> "BECA_ALUMNO";
            case AJUSTES_CARGO -> "AJUSTE_CARGO";
            case POLITICAS_RECARGO -> "POLITICA_RECARGO";
            case MOTIVOS_FINANCIEROS -> "MOTIVO_FINANCIERO";
            case CUENTAS_FINANCIERAS -> "CUENTA_FINANCIERA";
            case PROVEEDORES -> "PROVEEDOR";
            case COMPRAS -> "COMPRA";
            case PAGOS -> "PAGO";
            case MOVIMIENTOS_FINANCIEROS -> "MOVIMIENTO_FINANCIERO";
            case RETIROS_FONDO -> "RETIRO_FONDO";
            case REPORTES_FINANCIEROS -> "REPORTE_FINANCIERO";
            case EVENTOS_ESCOLARES -> "EVENTO_ESCOLAR";
            case AVISOS -> "AVISO";
            case ROLES -> "ROL";
            case USUARIOS -> "USUARIO";
            case AUDITORIA -> "AUDITORIA";
            case PORTAL_TUTOR -> "PORTAL_TUTOR_SOPORTE";
            case PORTAL_MAESTRO -> "PORTAL_MAESTRO_SOPORTE";
            case GUIAS -> "GUIA_PROCESOS_CONSULTAR";
        };
        if (this == PORTAL_TUTOR) return permisos.contains("PORTAL_TUTOR_SOPORTE");
        if (this == PORTAL_MAESTRO) return permisos.contains("PORTAL_MAESTRO_SOPORTE");
        if (this == GUIAS) return permisos.contains("GUIA_PROCESOS_CONSULTAR");
        if (this == PAGOS) return permisos.contains("PAGO_LEER") || permisos.contains("PAGO_REGISTRAR")
                || permisos.contains("PAGO_VALIDAR") || permisos.contains("PAGO_DEVOLVER")
                || permisos.contains("PAGO_CANCELAR");
        if (this == COMPRAS) return permisos.contains("COMPRA_LEER") || permisos.contains("COMPRA_ADMINISTRAR")
                || permisos.contains("COMPRA_CONFIRMAR") || permisos.contains("COMPRA_CANCELAR");
        if (this == MOVIMIENTOS_FINANCIEROS) return permisos.contains("MOVIMIENTO_FINANCIERO_LEER")
                || permisos.contains("MOVIMIENTO_FINANCIERO_REGISTRAR")
                || permisos.contains("TRANSFERENCIA_CUENTA_REGISTRAR")
                || permisos.contains("MOVIMIENTO_FINANCIERO_REVERTIR")
                || permisos.contains("CORTE_CAJA_LEER") || permisos.contains("CORTE_CAJA_ADMINISTRAR");
        if (this == RETIROS_FONDO) return permisos.contains("RETIRO_FONDO_LEER")
                || permisos.contains("RETIRO_FONDO_REGISTRAR");
        if (this == REPORTES_FINANCIEROS) return permisos.contains("REPORTE_FINANCIERO_CONSULTAR");
        if (this == BOLETAS) return permisos.contains("BOLETA_CONSULTAR");
        if (this == PLANEACIONES) return permisos.contains("PLANEACION_LEER") || permisos.contains("PLANEACION_ADMINISTRAR");
        if (this == CALENDARIO_ESCOLAR) return permisos.contains("CALENDARIO_ESCOLAR_LEER") || permisos.contains("CALENDARIO_ESCOLAR_ADMINISTRAR");
        if (this == ACTUALIZACIONES_EXPEDIENTE) return permisos.contains("ACTUALIZACION_EXPEDIENTE_LEER") || permisos.contains("ACTUALIZACION_EXPEDIENTE_REVISAR");
        if (this == HORARIOS_CLASE) return permisos.contains("HORARIO_CLASE_LEER") || permisos.contains("HORARIO_CLASE_ADMINISTRAR");
        if (this == EVENTOS_ESCOLARES) return permisos.contains("EVENTO_ESCOLAR_LEER")
                || permisos.contains("EVENTO_ESCOLAR_ADMINISTRAR");
        if (this == AVISOS) return permisos.contains("AVISO_LEER") || permisos.contains("AVISO_ADMINISTRAR");
        if (this == AUDITORIA) return permisos.contains("AUDITORIA_CONSULTAR");
        boolean administra = permisos.contains(base + "_ADMINISTRAR");
        if (this == ROLES || this == USUARIOS) return administra;
        return administra || permisos.contains(base + "_LEER");
    }

    public boolean mantenimientoDisponible() {
        return this == INSTITUCIONES || this == PLANTELES || this == NIVELES
                || this == OFERTA || this == GRADOS || this == CICLOS || this == PERIODOS
                || this == GRUPOS || this == MATERIAS || this == CALIFICACIONES || this == ASISTENCIA || this == ALUMNOS || this == TUTORES || this == MAESTROS
                || this == VINCULOS_TUTOR || this == INSCRIPCIONES
                || this == CONCEPTOS_COBRO || this == CUOTAS_ALUMNO
                || this == CARGOS
                || this == CONVENIOS_PAGO
                || this == TIPOS_BECA || this == BECAS_ALUMNO || this == AJUSTES_CARGO || this == POLITICAS_RECARGO
                || this == MOTIVOS_FINANCIEROS
                || this == CUENTAS_FINANCIERAS
                || this == PROVEEDORES || this == COMPRAS
                || this == PAGOS
                || this == HORARIOS_CLASE
                || this == CALENDARIO_ESCOLAR
                || this == ACTUALIZACIONES_EXPEDIENTE
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
            case CALENDARIO_ESCOLAR -> "/admin/calendario-escolar";
            case GRUPOS -> "/admin/grupos";
            case MATERIAS -> "/admin/materias";
            case CALIFICACIONES -> "/admin/calificaciones";
            case ASISTENCIA -> "/admin/asistencia";
            case BOLETAS -> "/admin/boletas";
            case MAESTROS -> "/admin/maestros";
            case PLANEACIONES -> "/admin/planeaciones";
            case HORARIOS_CLASE -> "/admin/horarios-clases";
            case ALUMNOS -> "/admin/alumnos";
            case ACTUALIZACIONES_EXPEDIENTE -> "/admin/actualizaciones-expediente";
            case TUTORES -> "/admin/tutores";
            case VINCULOS_TUTOR -> "/admin/vinculos-tutor";
            case INSCRIPCIONES -> "/admin/inscripciones";
            case CONCEPTOS_COBRO -> "/admin/conceptos-cobro";
            case CUOTAS_ALUMNO -> "/admin/cuotas-alumno";
            case CARGOS -> "/admin/cargos";
            case CONVENIOS_PAGO -> "/admin/convenios-pago";
            case TIPOS_BECA -> "/admin/tipos-beca";
            case BECAS_ALUMNO -> "/admin/becas-alumno";
            case AJUSTES_CARGO -> "/admin/ajustes-cargo";
            case POLITICAS_RECARGO -> "/admin/politicas-recargo";
            case MOTIVOS_FINANCIEROS -> "/admin/motivos-financieros";
            case CUENTAS_FINANCIERAS -> "/admin/cuentas-financieras";
            case PROVEEDORES -> "/admin/proveedores";
            case COMPRAS -> "/admin/compras";
            case PAGOS -> "/admin/pagos";
            case MOVIMIENTOS_FINANCIEROS -> "/admin/movimientos-financieros";
            case RETIROS_FONDO -> "/admin/retiros-fondo";
            case REPORTES_FINANCIEROS -> "/admin/reportes-financieros/tesoreria";
            case EVENTOS_ESCOLARES -> "/admin/eventos-escolares";
            case AVISOS -> "/admin/avisos";
            case ROLES -> "/admin/roles";
            case USUARIOS -> "/admin/usuarios";
            case AUDITORIA -> "/admin/auditoria";
            case PORTAL_TUTOR -> "/admin/portal-soporte";
            case PORTAL_MAESTRO -> "/admin/portal-maestros-soporte";
            case GUIAS -> "/admin/guias";
            default -> "";
        };
    }

    public String rutaListado() {
        return this == BOLETAS || this == PLANEACIONES || this == HORARIOS_CLASE || this == CALENDARIO_ESCOLAR || this == ACTUALIZACIONES_EXPEDIENTE || this == PROVEEDORES || this == COMPRAS || this == CONVENIOS_PAGO || this == MOVIMIENTOS_FINANCIEROS || this == RETIROS_FONDO || this == REPORTES_FINANCIEROS || this == EVENTOS_ESCOLARES || this == AVISOS || this == AUDITORIA || this == PORTAL_TUTOR
                || this == PORTAL_MAESTRO || this == GUIAS ? rutaMantenimiento()
                : "/admin/catalogos/" + slug;
    }

    public String segmentoNuevo() {
        return this == PLANTELES || this == NIVELES || this == GRADOS || this == CICLOS
                || this == PERIODOS || this == GRUPOS || this == CALIFICACIONES || this == ASISTENCIA || this == ALUMNOS || this == TUTORES || this == MAESTROS
                || this == VINCULOS_TUTOR || this == INSCRIPCIONES
                || this == CONCEPTOS_COBRO || this == CUOTAS_ALUMNO
                || this == CARGOS
                || this == CONVENIOS_PAGO
                || this == TIPOS_BECA || this == BECAS_ALUMNO || this == AJUSTES_CARGO || this == POLITICAS_RECARGO
                || this == MOTIVOS_FINANCIEROS
                || this == CUENTAS_FINANCIERAS
                || this == PROVEEDORES
                || this == PAGOS
                || this == HORARIOS_CLASE
                || this == CALENDARIO_ESCOLAR
                || this == ROLES || this == USUARIOS
                ? (this == CALIFICACIONES || this == ASISTENCIA ? "/captura" : "/nuevo") : "/nueva";
    }

    public static ModuloCatalogo desde(String slug) {
        return Arrays.stream(values()).filter(m -> m.slug.equalsIgnoreCase(slug)).findFirst()
                .orElse(INSTITUCIONES);
    }
}
