package escuela.seguridad.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Paquetes funcionales visibles al administrar un rol. Los permisos técnicos se
 * conservan para proteger cada endpoint, pero nunca se conceden de forma aislada.
 */
public enum ModuloPermiso {
    INSTITUCIONES("Instituciones", "Acceso completo a instituciones",
            "INSTITUCION_LEER", "INSTITUCION_ADMINISTRAR"),
    PLANTELES("Planteles", "Acceso completo a planteles",
            "PLANTEL_LEER", "PLANTEL_ADMINISTRAR"),
    NIVELES("Niveles educativos", "Acceso completo a niveles educativos",
            "NIVEL_LEER", "NIVEL_ADMINISTRAR"),
    OFERTA("Niveles por plantel", "Acceso completo a los niveles educativos disponibles en cada plantel",
            "OFERTA_LEER", "OFERTA_ADMINISTRAR"),
    GRADOS("Grados", "Acceso completo a grados",
            "GRADO_LEER", "GRADO_ADMINISTRAR"),
    CICLOS("Ciclos escolares", "Acceso completo a ciclos escolares",
            "CICLO_LEER", "CICLO_ADMINISTRAR"),
    PERIODOS("Periodos académicos", "Acceso completo a periodos académicos",
            "PERIODO_LEER", "PERIODO_ADMINISTRAR"),
    CALENDARIO_ESCOLAR("Calendario escolar", "Consulta, exportación y administración del calendario académico detallado",
            "CALENDARIO_ESCOLAR_LEER", "CALENDARIO_ESCOLAR_ADMINISTRAR"),
    GRUPOS("Grupos", "Acceso completo a grupos",
            "GRUPO_LEER", "GRUPO_ADMINISTRAR"),
    MATERIAS("Materias", "Acceso completo a materias y planes de evaluación por grado",
            "MATERIA_LEER", "MATERIA_ADMINISTRAR"),
    CALIFICACIONES("Calificaciones", "Acceso completo para capturar, publicar y consultar calificaciones",
            "CALIFICACION_LEER", "CALIFICACION_ADMINISTRAR"),
    ASISTENCIA("Asistencia", "Acceso completo para abrir listas y registrar asistencia diaria",
            "ASISTENCIA_LEER", "ASISTENCIA_ADMINISTRAR"),
    BOLETAS("Boletas", "Consulta y exportación de boletas con calificaciones publicadas",
            "BOLETA_CONSULTAR"),
    MAESTROS("Maestros", "Acceso completo a expedientes, cuentas y asignaciones docentes",
            "MAESTRO_LEER", "MAESTRO_ADMINISTRAR"),
    PLANEACIONES("Planeaciones", "Consulta, revisión, publicación y reapertura de planeaciones",
            "PLANEACION_LEER", "PLANEACION_ADMINISTRAR"),
    HORARIOS_CLASE("Horarios y clases", "Consulta, exportación y administración de bloques de clase",
            "HORARIO_CLASE_LEER", "HORARIO_CLASE_ADMINISTRAR"),
    ALUMNOS("Alumnos", "Acceso completo al expediente de alumnos",
            "ALUMNO_LEER", "ALUMNO_ADMINISTRAR"),
    ACTUALIZACIONES_EXPEDIENTE("Actualizaciones familiares", "Consulta y revisión de documentos e información médica enviados por tutores",
            "ACTUALIZACION_EXPEDIENTE_LEER", "ACTUALIZACION_EXPEDIENTE_REVISAR"),
    TUTORES("Tutores", "Acceso completo al expediente de tutores",
            "TUTOR_LEER", "TUTOR_ADMINISTRAR"),
    VINCULOS_TUTOR("Vínculos alumno–tutor", "Acceso completo a vínculos entre alumnos y tutores",
            "VINCULO_TUTOR_LEER", "VINCULO_TUTOR_ADMINISTRAR"),
    INSCRIPCIONES("Inscripciones", "Acceso completo a inscripciones y asignaciones de grupo",
            "INSCRIPCION_LEER", "INSCRIPCION_ADMINISTRAR"),
    CONCEPTOS_COBRO("Conceptos de cobro", "Acceso completo a conceptos de cobro",
            "CONCEPTO_COBRO_LEER", "CONCEPTO_COBRO_ADMINISTRAR"),
    CUOTAS_ALUMNO("Cuotas por alumno", "Acceso completo a cuotas individuales",
            "CUOTA_ALUMNO_LEER", "CUOTA_ALUMNO_ADMINISTRAR"),
    CARGOS("Pagos de alumnos", "Acceso completo a los importes por cobrar de cada alumno",
            "CARGO_LEER", "CARGO_ADMINISTRAR"),
    TIPOS_BECA("Tipos de beca", "Acceso completo al catálogo de becas",
            "TIPO_BECA_LEER", "TIPO_BECA_ADMINISTRAR"),
    BECAS_ALUMNO("Becas por alumno", "Acceso completo a becas individuales",
            "BECA_ALUMNO_LEER", "BECA_ALUMNO_ADMINISTRAR"),
    AJUSTES_CARGO("Historial de ajustes", "Acceso completo a ajustes de cargos",
            "AJUSTE_CARGO_LEER", "AJUSTE_CARGO_ADMINISTRAR"),
    POLITICAS_RECARGO("Políticas de recargo", "Acceso completo a políticas y generación de recargos",
            "POLITICA_RECARGO_LEER", "POLITICA_RECARGO_ADMINISTRAR"),
    MOTIVOS_FINANCIEROS("Motivos financieros", "Acceso completo a motivos financieros",
            "MOTIVO_FINANCIERO_LEER", "MOTIVO_FINANCIERO_ADMINISTRAR"),
    CUENTAS_FINANCIERAS("Cuentas financieras", "Acceso completo a cuentas financieras",
            "CUENTA_FINANCIERA_LEER", "CUENTA_FINANCIERA_ADMINISTRAR"),
    PROVEEDORES("Proveedores", "Consulta, alta, edición y desactivación de proveedores",
            "PROVEEDOR_LEER", "PROVEEDOR_ADMINISTRAR"),
    COMPRAS("Compras", "Captura, consulta, confirmación financiera y cancelación controlada de compras",
            "COMPRA_LEER", "COMPRA_ADMINISTRAR", "COMPRA_CONFIRMAR", "COMPRA_CANCELAR"),
    PAGOS("Pagos", "Acceso completo para registrar, validar, devolver y cancelar pagos",
            "PAGO_LEER", "PAGO_REGISTRAR", "PAGO_VALIDAR", "PAGO_DEVOLVER", "PAGO_CANCELAR"),
    MOVIMIENTOS_FINANCIEROS("Movimientos financieros", "Acceso completo al libro, transferencias, reversas y cortes de caja",
            "MOVIMIENTO_FINANCIERO_LEER", "MOVIMIENTO_FINANCIERO_REGISTRAR",
            "TRANSFERENCIA_CUENTA_REGISTRAR", "MOVIMIENTO_FINANCIERO_REVERTIR",
            "CORTE_CAJA_LEER", "CORTE_CAJA_ADMINISTRAR"),
    RETIROS_FONDO("Retiros de fondos", "Acceso completo a retiros externos de fondos",
            "RETIRO_FONDO_LEER", "RETIRO_FONDO_REGISTRAR"),
    REPORTES_FINANCIEROS("Reportes financieros", "Acceso completo a reportes financieros",
            "REPORTE_FINANCIERO_CONSULTAR"),
    EVENTOS_ESCOLARES("Eventos escolares", "Acceso completo a eventos escolares",
            "EVENTO_ESCOLAR_LEER", "EVENTO_ESCOLAR_ADMINISTRAR"),
    AVISOS("Avisos escolares", "Acceso completo a avisos escolares",
            "AVISO_LEER", "AVISO_ADMINISTRAR"),
    ROLES("Roles y permisos", "Acceso completo a roles y sus módulos",
            "ROL_ADMINISTRAR"),
    USUARIOS("Usuarios", "Acceso completo a usuarios, roles y alcances",
            "USUARIO_ADMINISTRAR"),
    AUDITORIA("Auditoría", "Acceso completo de consulta y exportación de auditoría",
            "AUDITORIA_CONSULTAR"),
    PORTAL_FAMILIAR("Portal familiar", "Acceso técnico derivado de la cuenta del tutor",
            "PORTAL_TUTOR_ACCEDER"),
    PORTAL_MAESTROS("Portal de maestros", "Acceso técnico derivado de la cuenta del maestro",
            "PORTAL_MAESTRO_ACCEDER"),
    SOPORTE_PORTAL("Soporte del portal familiar", "Permite consultar el portal de un tutor en modo de sólo lectura",
            "PORTAL_TUTOR_SOPORTE");

    private final String nombre;
    private final String descripcion;
    private final Set<String> permisos;
    private final String permisoRepresentante;

    ModuloPermiso(String nombre, String descripcion, String... permisos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = Set.of(permisos);
        this.permisoRepresentante = permisos[0];
    }

    public String nombre() { return nombre; }
    public String descripcion() { return descripcion; }
    public Set<String> permisos() { return permisos; }

    public boolean asignableARol() { return this != PORTAL_FAMILIAR && this != PORTAL_MAESTROS; }

    public boolean contiene(String permiso) {
        return permisos.contains(permiso);
    }

    public boolean disponibleEn(Collection<String> permisosDisponibles) {
        return permisos.stream().anyMatch(permisosDisponibles::contains);
    }

    public Optional<String> permisoRepresentante(Collection<String> permisosDisponibles) {
        if (permisosDisponibles.contains(permisoRepresentante)) {
            return Optional.of(permisoRepresentante);
        }
        return permisos.stream().filter(permisosDisponibles::contains).sorted().findFirst();
    }

    public static Optional<ModuloPermiso> dePermiso(String permiso) {
        return Arrays.stream(values()).filter(modulo -> modulo.contiene(permiso)).findFirst();
    }

    public static Set<String> expandir(Collection<String> permisosOtorgados) {
        Set<String> resultado = new LinkedHashSet<>(permisosOtorgados);
        Arrays.stream(values())
                .filter(modulo -> permisosOtorgados.stream().anyMatch(modulo::contiene))
                .forEach(modulo -> resultado.addAll(modulo.permisos));
        return resultado;
    }
}
