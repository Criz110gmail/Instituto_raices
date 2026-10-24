# Simplificación de Personal y nómina — acuerdo 24/10/2026

Actualización etapa4: préstamos sin intereses capturables, versión/historial y entrega
técnicamente comprobada en QA. Entregas operativas BLOQUEADAS hasta conectar fase5
(app.nomina.prestamos.entrega-habilitada=false). No hay abonos/liquidación operativos:
no declarar ciclo de préstamo completo. Siguiente: pagos individuales/abonos/reversas,
compensaciones y transición de aplicaciones históricas antes de habilitar entregas.
Ver PRUEBA_SIMPLIFICACION_NOMINA_ETAPA4.md. No crear séptima entrada principal.

Estado24/10: etapas1–3 implementadas técnicamente. Etapa3 conecta calendario actual
y vigencias al generador e incorpora ocasionales por periodo (V132), sin backfill.
Borradores anteriores requieren revisión explícita; aplicadas conservan su fotografía.
Prueba humana integrada pendiente. Siguen4préstamos y5pagos/reversa individual.
Ver PRUEBA_SIMPLIFICACION_NOMINA_ETAPA3.md; entradas históricas posteriores que
describan calendario sólo organizativo quedan superadas por esta etapa.

Sustituye la propuesta de once entradas independientes, no los historiales existentes.
Autorizado implementar POR ETAPAS. No publicar todavía guía de usuario confirmada:
primero simplificar la operación. Diseño compartido, filtros alineados, listado separado
del formulario, retorno con filtros/página, ayuda10/3 y modales con Cancelar.

## Destino acordado: seis entradas principales

Empleados; Puestos laborales; Conceptos de nómina; Periodos de nómina;
Configuración de nómina; Nóminas.
Puesto puede ofrecer jornada de referencia opcional con variante individual: no imponer
una sola jornada rígida a todas las personas del mismo puesto. No exigir otra plantilla
de puesto/sueldo para capturar el salario particular. Sueldo se presenta como percepción
en configuración y desglose, pero conserva UNA fuente canónica, sin duplicar importes.
Calendarios y periodos comparten navegación; retirar menús no elimina datos/tablas.

## Etapas de entrega

1. Organización de navegación y consulta: seis entradas principales, accesos internos
   de transición para herramientas existentes, periodos/calendarios juntos visualmente;
   filtros coherentes, estado/acciones claros en Nóminas, consulta exacta del egreso.
   No afirmar aún que una pestaña unifica la captura o cambia cálculos.
2. Configuración individual unificada: empleado, puesto, vigencia, frecuencia/grupo,
   sueldo/percepciones/deducciones/historial en una captura organizada. Reutilizar fuentes
   vigentes y permisos, compatibilidad explícita con condiciones/asignaciones actuales.
   Reducir pasos de jornadas/esquemas/conceptos por empleado; no segunda fuente salarial.
3. Generación por periodo desde configuración vigente: configuración anual o abierta
   reutilizada mes a mes, altas/cambios efectivos sólo en periodos que correspondan,
   conceptos habituales y ocasionales. Conectar calendario/grupo realmente con generador
   (V131 era sólo organizativo), definir backfill y compatibilidad de borradores previos.
   Semanal/quincenal/proporcionales requieren reglas explícitas antes de automatizar.
4. Préstamos: entrega/importe/saldo, parcialidad, inicio del descuento y liquidación;
   interfaz desde Deducciones con vínculo al préstamo, no descuento fijo sin control.
   Ejemplo Carlos anual, préstamo3000 en junio,500/mes junio–noviembre si no cambia nada;
   limitar última cuota al saldo. Preparar no abona; pago individual confirmado sí.
5. Pagos por empleado y reversas: separar nómina, detalle calculado y pago/intentos por
   empleado; dejar otros empleados pagados cuando falle uno. Revertir pago NO borra su
   derecho a cobrar; vuelve a pendiente. Diferenciar rechazo sin salida y devolución
   efectiva, compensaciones exactas y evidencia/motivo/fecha. Restaurar abono de préstamo
   cuando corresponda. Impedir doble pago/abono/reversa y saldo negativo.

## Reglas permanentes de historia y permisos

- No cambiar nóminas aplicadas al editar configuración. Borradores sólo se actualizan
  mediante revisión explícita y otra copia histórica. No sobrescribir revisiones.
- Mantener consulta de empleados y cada concepto original en nómina aplicada.
- Calendario/fecha prevista no es orden bancaria; aplicación actual usa fecha actual.
- Registro salarial, de pagos y documentos privados; no exponer sueldos en libro global.
  No otorgar permisos ni convertir cuentas por reorganizar menú.
- Nómina actual sólo calcula mensual completo compatible, sin automatización legal,
  préstamos ni reversa individual. Esas funciones NO se declaran listas por el diseño.
- Periodo anual debe aclararse como ordinario anual o extraordinario antes de admitirlo.
  No inferir que ya existe cálculo anual/quincenal/semanal.
- Flujo futuro contempla preparación/autorización y estado de pago individual/conjunto;
  definir migración de BORRADOR/APLICADA existentes antes de cambiar sus estados.
- Actual aplicación es UN egreso consolidado: no dividirlo retrospectivamente ni inventar
  pagos individuales. Estrategia de transición explícita y conciliada en etapa5.

## Evidencia humana de esta computadora, no repetir

Propietario confirma Carlos Prueba Nómina EMP-NOM-TEST-01, configuración mensual de enero,
borrador BOR-000001/revisión1 y aplicación24/10/2026 a10:54México según tabla aportada.
Egreso NOMINA12500 en BANCO-NOMINA-TEST (cuenta6 según URL), antes20000/después7500.
Confirma estado Aplicada y Gestión no permite aplicar otra vez. No inferir ID del egreso
de la secuencia #1 por cuenta. No afirmar que se probaron todos los controles negativos.
Captura15/10 muestra Sin asignación/percepciones0; enero15/01/2027 muestra sueldo12000 y
bono500 según consulta acotada. No alterar ni repetir estas operaciones para probar UI.
Guía integrada de este caso se pospone expresamente hasta mejorar el flujo.

## Inicio técnico de etapa1

Sólo navegación/presentación/consultas, sin migración ni recálculo. Ocultar entradas
secundarias únicamente del menú, no de autorización; mantener rutas y accesos internos
con permisos existentes. Vista exacta del movimiento filtra por ID y alcance en servidor
y conserva filtro al exportar/paginar. No dar permisos de lectura financiera por salario.
Probar claro/oscuro/escritorio/móvil y estados Borrador/Aplicada/Cancelada, revisiones
históricas, lectura y botones autorizados. Confirmaciones vigentes intactas.
Revisar estado real de Git/migraciones al retomar: V131 última; V132 libre al acuerdo.
No commit/push ni lectura del contenido real de .env; preservar cambios previos de puesto.

### Verificación de la primera entrega

Implementación de etapa1 comprobada el24/10: 991 Java de suite, 3 IT PostgreSQL aislados,
114 JS y package correctos. Render real de estados e historia, permisos, egreso exacto
y Excel. Revisión visual ficticia en escritorio/móvil y temas claro/oscuro; pendiente
aceptación del propietario. No se modificó la aplicación BOR-000001 ni saldos reales.
Continuar etapa2; las etapas2–5 no están implementadas por esta entrega.

## Entrega de etapa2 — 24/10/2026

Captura integrada en Configuración → empleado → Configurar nómina: sueldo directo sin
plantilla obligatoria, calendario actual y hasta20 conceptos habituales con sus vigencias.
Cambios por selección explícita; motivo y modal. Transacción única con versiones, bloqueo,
permisos y servicios existentes. Una sola fuente salarial, sin recálculos ni pagos.
Cambio de concepto existente desde su fila; detalle incluye programación futura/histórica.
No hereda jornada/horas de un esquema previo; éstos conservan sus registros originales.
Calendario sigue organizativo actual. Etapas3–5 no implementadas por esta entrega.
Ver PRUEBA_SIMPLIFICACION_NOMINA_ETAPA2.md; aceptación humana y guía confirmada pendientes.
