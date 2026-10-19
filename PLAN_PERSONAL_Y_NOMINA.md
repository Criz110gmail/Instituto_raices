# Integración de empleados, maestros y nómina

Acuerdo: 18/10/2026. Actualización: 19/10/2026.
Estado: PRIMERA ENTREGA EMPLEADOS/PERFIL DOCENTE IMPLEMENTADA EN V111; NÓMINA PENDIENTE.

V111 materializa identidad canónica, conversión uno a uno de maestros sin cambiar IDs,
expediente personal, fotografía, perfil docente opcional, laboral privada con historial,
planteles/vigencias/principal, permisos propios y listado/Excel paginados. No crea
cuentas ni roles automáticamente ni deduce datos laborales. La gestión académica y
de cuentas permanece en Maestros. Las cuentas mixtas siguen fuera de esta etapa.
Pruebas técnicas y publicación local documentadas en CONTEXTO_PROYECTO.md; falta
aceptación humana de esta entrega. Sueldos/condiciones/puestos normalizados, nómina,
préstamos/pagos/recibos corresponden a etapas posteriores. El resto es diseño futuro,
no debe interpretarse como funcionalidades de nómina ya disponibles.

## Avance del 19/10/2026: inventario y propuesta técnica

Se completó la revisión estática de dependencias y se documentó la propuesta en
`DISENO_INTEGRACION_EMPLEADOS_MAESTROS.md`: relaciones, fuente única, migración
aditiva, compatibilidad, seguridad y matriz de verificaciones. No se creó código,
tabla ni migración; V111 sigue disponible en el repositorio revisado.

Siguiente: revisar propuesta e implementar la primera entrega cuando se autorice.
El acceso de empleados con funciones administrativas y docentes requiere definir
si conservará cuentas separadas o una futura cuenta mixta; la estructura actual
no permite anunciar esa combinación sin cambios de seguridad. Se mantienen intactos
los usuarios y accesos actuales. Esta precisión no revoca el alcance aprobado.

## Alcance aprobado

Nómina se incorpora al alcance futuro. Empezar integrando empleados y maestros, no
programando de golpe todo el cálculo. No incluye inventarios, contabilidad de partida
doble, cálculo legal automático ni emisión/timbrado de CFDI. Los recibos iniciales serán
internos, claramente NO fiscales. No confundir un diseño aprobado con módulo disponible.

## Un solo expediente, funciones distintas

- Empleados será el punto general de captura, sin repetir datos personales en Maestros.
- Un empleado puede tener perfil docente y trabajar en varios planteles de la misma
  institución. Conservar plantel principal, asignaciones y vigencias.
- Activar «También realiza funciones de maestro» habilita la información docente.
  «Habilitar acceso al portal de maestros» es independiente: sugerir usuario editable y
  generar invitación para contraseña, reutilizando los mecanismos existentes.
- Empleado, maestro y usuario son conceptos distintos. No todo empleado necesita cuenta.
  Código laboral, identificador docente y nombre de acceso no son intercambiables.
- No duplicar expedientes ni sueldos por plantel. Asignar un plantel laboral NO concede
  permisos administrativos ni acceso automático a todos sus datos.
- Conservar maestros actuales, IDs, usuarios, materias, grupos, horarios, planeaciones,
  calificaciones y demás historial. No borrar/recrear registros ni fusionar por nombre.
- Pantallas académicas siguen diciendo «Maestro» y seleccionan perfiles docentes
  elegibles; Empleados concentra el mantenimiento general de datos compartidos.
- Baja laboral, fin de asignación docente y desactivar acceso son acciones diferentes:
  mostrar sus efectos y nunca ejecutar una silenciosamente por modificar otra.
- Revisar empleados con funciones administrativas y docentes antes de resolver sus
  accesos; no cambiar automáticamente tipos de usuario existentes.

## Menú y organización de captura

Personal y nómina: Empleados, Conceptos, Calendarios de pago, Préstamos, Nóminas y Reportes.
Publicar entradas conforme estén implementadas, conservando compatibilidad de rutas.

Ficha con navegación por secciones:
1. Ficha del empleado: foto, código y datos personales.
2. Datos laborales: puesto, ingreso, estado, sueldo y frecuencia.
3. Planteles: principal, asignaciones y vigencias.
4. Información docente: sólo cuando tiene ese perfil.
5. Acceso al sistema: vincular/crear cuenta con permisos propios.
6. Nómina y préstamos: configuración e historial protegidos por autorización laboral.

Un permiso académico no permite ver sueldos, préstamos ni documentos laborales.
Conservar portal docente, soporte y privacidad de Mis pendientes.

## Sueldo, conceptos y retenciones

Sueldo BRUTO con base mensual, semanal, diaria o por hora; frecuencia independiente e
historial de vigencia. Mensual sugerido para sueldo fijo, no obligatorio para todos.
No convertir mensual a semanal dividiendo entre cuatro. Definir días/horas, proporcionales
y redondeos con responsable contable antes de calcularlos.

Catálogo institucional: código, nombre, naturaleza y comportamiento. Montos individuales
en la configuración del empleado, no en una única tarifa global. Comportamientos:
- Habitual: sueldo y bono fijo, propuestos en cada periodo vigente.
- Ocasional: ajuste/bono que sólo afecta esa nómina.
- Con saldo: préstamo interno y recuperación en parcialidades.
- Retención: IMSS, Infonavit, ISR y otras, con soporte y vigencia específicos.

Separar aportaciones patronales de deducciones al trabajador; las primeras no reducen
su neto. Primera versión: importes legales determinados por contabilidad, capturados
como MANUALES, con origen, periodo, soporte y revisión. No inventar tasas ni anunciar
cálculo legal automático por sumar/restar conceptos. Automatización legal requiere etapa
propia y validación vigente. Infonavit no es préstamo interno: prever crédito, modalidad,
valor del aviso, vigencias y avisos de retención/modificación/suspensión; no suponer saldo.

Una novedad del borrador no cambia silenciosamente la configuración habitual. Congelar
fotografía de conceptos, bases, montos y distribución al autorizar para preservar historia.

## Calendarios, nóminas y pagos

Semanal configurable, quincenal (1–15 y16–último día) o mensual. Quincenal NO equivale
a cada14 días. Separar inicio, fin, fecha prevista y fecha real de pago.

Flujo: elegir grupo/periodo → generar borrador → revisar novedades → autorizar → pagar
por empleado → recibos e historial. Estados propuestos: BORRADOR, AUTORIZADA,
PARCIALMENTE_PAGADA, PAGADA y CANCELADA. Autorizar congela importes, NO mueve dinero.
Registrar pago identifica cuenta, fecha, importe y referencia y genera el egreso una vez.
Parcialmente pagada significa inicialmente que faltan empleados por pagar; no ofrecer
parcialidades del neto individual sin definir antes su tratamiento.

Evitar duplicidad de empleado/relación laboral en el mismo periodo ordinario y validar
superposiciones; definir excepciones de extraordinarias, ajustes, reingresos y cambios
de calendario antes de imponer claves demasiado restrictivas. Bloquear autorización
con neto negativo hasta resolverlo. Pagos publicados no se editan/eliminan: correcciones
mediante reversas con motivo, historial y compensaciones financieras atómicas.

## Préstamos desde el inicio

Propuesta inicial sin intereses, a confirmar al implementar. Importe autorizado, entrega,
cuenta de origen, primera fecha de descuento, parcialidad, moneda, saldo e historial.
Estados propuestos: BORRADOR, ACTIVO, LIQUIDADO y CANCELADO. Autorización y entrega son
distintas. Abonos extraordinarios y suspensión justificada con trazabilidad.
Ejemplo: $3,000.00, seis descuentos quincenales de $500.00. La última cuota no supera
el saldo. Borrador NO reduce deuda; abono al confirmar pago correspondiente, atómico e
idempotente. Reversa restaura saldo mediante compensación. No cancelar una entrega real
borrándola. Entrega genera egreso una vez; descuento recupera préstamo por compensación
contra lo adeudado al empleado, no inventa otro ingreso bancario en efectivo. Abono
externo sí refleja ingreso real cuando corresponda.

## Varios planteles y costos

Una sola nómina por relación laboral aunque trabaje en varios planteles. Distribución
propuesta por porcentaje con vigencia y copia por nómina: sueldo $12,000.00, A60%
($7,200.00), B40% ($4,800.00). No son dos sueldos ni transferencias bancarias.
Cerrar antes de implementar: suma100%, redondeo de centavos, alcance de aprobación,
costos institucionales y si distribuye sólo sueldo o también bonos/aportaciones; cambios
durante el periodo y configuración de porcentajes. No asumir reglas aún no acordadas.

## Modelo conceptual, todavía sin tablas nuevas

Contrastar nombres y cardinalidades con el código antes del diseño definitivo:
Empleado; vínculo al Maestro existente; EmpleadoPlantel con principal/vigencia;
CondicionLaboral/SueldoVigente; DistribucionCosto; ConceptoNomina/EmpleadoConcepto;
CalendarioPago/PeriodoNomina; Nomina/NominaEmpleado/NominaDetalle;
PrestamoEmpleado/MovimientoPrestamo; PagoNomina vinculado a movimiento/reversa.
Reutilizar archivos privados y auditoría adecuada. No crear tabla universal de personas
ni fusionar alumnos/tutores en esta etapa. Definir fuente única de datos personales.

## Primera etapa al retomar

1. Leer AGENT.md, CONTEXTO_PROYECTO.md, modelo de entidades y este documento íntegros.
   Revisar cambios locales y migraciones; nunca leer el contenido real de .env.
2. Inventariar Maestro, FK, usuarios, documentos, permisos, consultas, rutas, portales,
   soporte, PDF/Excel y agenda que dependan de su identidad y alcance.
3. Presentar antes de ejecutar estrategia concreta aditiva Empleado/perfil docente,
   compatibilidad y fuente única de datos; conservar identificadores históricos.
4. Definir correspondencias de maestros actuales sin fusionar por nombre/correo/foto.
   Detectar ambigüedades y revisarlas, no asignar cuentas equivocadas automáticamente.
5. No inventar sueldo, fecha laboral, RFC/NSS ni porcentajes históricos. Admitir datos
   laborales pendientes y excluir del cálculo hasta completarlos. Conservar planteles
   docentes existentes verificados, sin inventar asignaciones.
6. Implementar Empleados + perfil docente + multi-plantel y regresiones; NO generar
   nóminas todavía. Después: condiciones/conceptos, calendarios, préstamos, preparación/
   autorización, pagos/reversas y recibos/reportes; entregas verificables separadas.

V110 es el último estado documentado; V111 estaba libre al redactar. Revalidar antes
de numerar; nunca modificar migraciones aplicadas. Integración aún NO iniciada.

## Decisiones pendientes y calidad

- Fórmulas de conversión, proporcionales, días/horas pagados y redondeos, con contabilidad.
- Novedades manuales inicialmente: faltas, permisos y horas extra, pendientes de concretar.
  No tomar asistencia escolar como laboral ni generar descuentos por ella automáticamente.
- Reglas/prioridad/límites aplicables de descuentos y préstamos, sin intereses a confirmar.
- Distribución de costos, pagos parciales individuales y reversa de nómina ya pagada.
- Permisos para preparar, autorizar y pagar; protección de datos/documentos laborales.

Mantener formularios alineados con navegación, móvil/claro/oscuro, moneda $1,000.00 y
fechas dd/MM/yyyy. Confirmaciones detalladas y Cancelar sólo cierra; errores locales,
CSRF, versiones, idempotencia, auditoría sin credenciales, paginación BD y Excel por
bloques con mismos filtros. Relaciones voluminosas: primeros diez al enfocar, búsqueda
desde tres caracteres. Pruebas técnicas no equivalen a aceptación humana.

Hoy NO código, SQL, migración, despliegue, reinicio, datos reales ni commit. Siguen
pendientes aceptación de agendaV110, ocho escenarios familiares, sesiones5m temporal/
restaurar30m (aplazado; sigue30m), Compras documental, guías de portales y respaldos.
El ejemplo financiero de traspaso/reversa ya está cerrado, no repetir operaciones.
