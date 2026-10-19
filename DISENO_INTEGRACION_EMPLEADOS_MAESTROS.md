# Diseño de integración de empleados y maestros

Revisión del código: 19/10/2026, commit de referencia `c391be1`.
Estado actualizado: PRIMERA ENTREGA IMPLEMENTADA EN V111 (19/10/2026).
Complementa `PLAN_PERSONAL_Y_NOMINA.md`; no sustituye sus acuerdos.
El inventario inicial fue estático. La implementación, verificaciones y publicación
se registran en AGENT.md/CONTEXTO_PROYECTO.md; las secciones siguientes conservan
la propuesta como referencia, no sustituyen el estado de entrega.

Alcance construido: empleado canónico, perfil docente preservado, laboral privada,
historial inmutable, multiplantel y principal/vigencias, filtros paginados/Excel,
foto optimizada y formularios con pestañas/modales compartidos. Cuatro permisos
propios, sin asignación automática. Cuenta mixta y toda nómina quedan pendientes.
Aceptación humana requerida antes de avanzar a condiciones salariales.

## 1. Objetivo y límites

Un expediente por empleado, con perfil docente opcional y varios planteles de la
misma institución. La primera entrega no calcula sueldos ni genera préstamos,
nóminas, pagos, movimientos financieros o recibos. Datos laborales desconocidos
quedan pendientes, nunca se rellenan por deducción.

Maestro no se renombra ni se elimina: conserva su identidad académica. Empleado no
se convierte en Usuario; tener expediente no da acceso al sistema.

## 2. Inventario del código actual

| Dependencia | Estado encontrado | Tratamiento previsto |
| --- | --- | --- |
| `docente/entity/Maestro.java`, V43 y V44 | Institución, número de empleado, nombres, apellidos, email, teléfono, foto, activo y usuario opcional; no contiene plantel laboral | Conservar ID, institución, código docente, actividad y cuenta. Unificar identidad personal en Empleado |
| `AsignacionMaestro` | FK a maestro, grupo y materia, con fechas y estado | No modificar IDs ni vigencias al crear Empleado |
| `PlaneacionSemanal` y V43 | FK a maestro; exclusión de semanas superpuestas por maestro/grupo | Conservar FK y restricciones; no recrear planeaciones |
| `HorarioClase` y V47 | FK a maestro y asignación; exclusiones de cruces | Conservar relaciones, no convertir planteles laborales en horarios |
| `MaestroService` | Alta/edición copian datos personales en Maestro; desactivar también desactiva cuenta y todas las asignaciones; copiar vuelve a poner activo=true | Sustituir escritura personal por servicio de Empleado y separar acciones; una edición no debe reactivar silenciosamente un perfil |
| `MaestroRepository` | Identificación por usuario, bloqueo y búsqueda JPQL por campos personales | Conservar búsqueda por usuario/ID; adaptar JPQL y orden a la identidad canónica |
| `CatalogoConsultaService`, `BusquedaAutocompletadoService` | Criteria busca nombres/código/email directamente en Maestro | Adaptar consultas, filtros, conteos, orden y Excel; no basta cambiar getters Java |
| `HorarioClaseService` | Busca asignaciones por número/nombre/apellido del maestro | Cambiar rutas de Criteria para identidad canónica sin ampliar elegibilidad |
| `FotografiaMaestroService` | Archivo privado asociado a Maestro; reemplazar retira el archivo anterior | Reutilizar mismo Archivo y clave física; centralizar reemplazo/retiro para no dejar referencias a archivos retirados |
| `AccesoPortalMaestroService` | Crea Usuario de tipo PORTAL_MAESTRO e invitación; cuenta opcional y única por maestro | Conservar usuario, contraseña, estado, tokens y flujo de invitación; no crear otra cuenta al migrar |
| `PlaneacionService`, `PortalAlumnoMaestroService`, `HorarioClaseService` | Identifican al maestro por usuario y restringen sus registros académicos | Conservar identificación y reglas; no filtrar silenciosamente por datos laborales aún incompletos |
| `SoportePortalMaestroService` | Sólo maestro/cuenta activos de la institución, permiso y administración institucional | Mantener soporte académico, sin acceso a datos laborales ni agenda privada |
| `AlcanceDatosService`, `SeguridadConfig`, `ModuloCatalogo` | Mantenimiento de Maestros requiere administración institucional; asignar plantel no concede roles | No ampliar alcance ni heredar permisos de nómina desde Maestros |
| `PdfPlaneacionService`, `ExcelPlaneacionService`, `ExcelHorarioClaseService`, catálogos | Consumidores de identidad o DTO académicos | Revisar nombres, filtros, contenido y paginación; preservar versiones publicadas y archivos históricos |
| Calificaciones y asistencia | No se encontró FK directa a Maestro en sus entidades; conservan inscripción y auditoría de usuarios | No inventar una migración maestro_id para estos registros; mantener sus referencias actuales |
| `pendiente/service/PendienteService` y V110 | Propiedad por Usuario; valida tipo/estado/institución, no por Empleado | No reasignar agendas ni permitir consultas por empleado; soporte continúa sin acceso |

La evidencia anterior es inspección de archivos, no una certificación de registros
existentes en PostgreSQL. Antes de migrar hay que verificar las correspondencias en
la instalación destino mediante controles agregados, sin exponer datos personales.

## 3. Relaciones propuestas

```text
Institución
  └─ Empleado
       ├─ 0..1 perfil Maestro existente
       │    ├─ asignaciones grupo/materia
       │    ├─ planeaciones
       │    ├─ horarios
       │    └─ 0..1 Usuario docente existente
       ├─ 0..N asignaciones EmpleadoPlantel, con vigencia
       └─ datos laborales básicos pendientes o confirmados

Usuario ── sus propios pendientes personales (sin cambiar su propietario)
```

### Empleado

- ID nuevo, institución inmutable, código laboral único por institución, nombres,
  apellidos, teléfono/email de contacto y referencia a fotografía privada.
- Puesto y fecha de ingreso laboral sólo cuando estén confirmados. No copiar la
  fecha de alta técnica, inicio del ciclo o primera clase como fecha de contratación.
- Separar expediente operativo, actividad docente y situación laboral. Un maestro
  activo no demuestra un contrato laboral vigente; uno inactivo tampoco demuestra baja.
- Mostrar claramente datos laborales pendientes. Nadie entra a un futuro cálculo de
  nómina hasta validar sus condiciones y relación laboral en esa etapa.
- No capturar sueldo en esta entrega. Sueldo/base/frecuencia/vigencias requieren el
  modelo posterior de condiciones laborales; no poner una cifra global sin historia.

### Maestro como perfil docente

- Incorporar vínculo único `maestro.empleado_id` a Empleado de la misma institución.
  Un empleado tiene como máximo un perfil docente; no duplicarlo por plantel.
- Mantener `maestro.id`, código docente actual `numero_empleado`, usuario y estado
  docente. Código laboral, código docente y username son conceptos independientes.
- Conservar todos los maestros, incluso inactivos y sin cuenta; no migrar sólo activos.
- Deshabilitar funciones docentes conserva el perfil y su historial; volver a
  habilitarlo reutiliza ese mismo ID, no crea otro maestro.

### EmpleadoPlantel

- Empleado, plantel de la misma institución, inicio y fin opcional, indicador principal
  y datos de auditoría/versionado; sin sueldo ni porcentaje de costos en esta relación.
- Intervalos inclusivos, inicio requerido al confirmar la asignación, fin >= inicio.
  Evitar solapamientos para el mismo empleado/plantel y más de un principal simultáneo.
  Permitir varios planteles distintos durante el mismo intervalo.
- No borrar asignaciones pasadas ni sobrescribir fechas para representar otro traslado.
  Una relación nueva debe conservar la historia de la anterior.
- Si no hay información laboral fiable, mostrar «Plantel laboral por confirmar».
  Los planteles de sus grupos aparecen aparte como actividad académica comprobable;
  no crear automáticamente relaciones laborales ni elegir principal por orden de ID.
- El vínculo laboral no concede acceso a alumnos, caja, expedientes o administración.
  Asignación docente y autorización de datos conservan sus propias reglas.

## 4. Fuente única de cada dato

| Dato | Fuente después del cambio | Compatibilidad |
| --- | --- | --- |
| Nombre, apellidos, contacto, fotografía del expediente | Empleado | Maestros y portales leen la misma identidad, sin segunda captura |
| Código laboral | Empleado | En migración inicial puede conservarse el valor conocido del número actual, sin equiparar IDs |
| Código/ID docente y actividad del perfil | Maestro | Los selectores académicos siguen enviando maestro.id |
| Grupos, materias y vigencias docentes | AsignacionMaestro | No inferir relaciones laborales |
| Fechas, estados y contenido académico | Entidades académicas existentes | Conservar historia, publicaciones y restricciones |
| Username, correo de acceso, foto de cuenta, contraseña y estado de cuenta | Usuario | Independientes del contacto/foto del expediente; no sincronizar automáticamente |
| Planteles laborales y principal | EmpleadoPlantel confirmado | No usar UsuarioRol como fuente de adscripción laboral |
| Sueldo y retenciones | Condiciones laborales futuras | No disponibles en esta entrega ni en DTO académicos |
| Agenda privada | Usuario original | Nunca compartirla por ser empleado o entrar desde soporte |

No dejar dos formularios que escriban por separado los nombres o la foto. Las rutas
antiguas serán adaptadores al servicio canónico con sus autorizaciones; no otra fuente.

## 5. Migración compatible, sin fusionar personas

1. **Preparación:** comprobar versión real de esquema, respaldos coordinados de BD y
   archivos y estado del repositorio. V110 es la última migración presente; V111 estaba
   libre al revisar. No modificar migraciones aplicadas ni asumir que Git trae los datos.
2. **Correspondencia determinista:** por cada maestro existente crear un Empleado y
   enlazarlo por su ID docente original, no por nombre, correo o fotografía. Registrar
   trazabilidad de procedencia para comprobar y no repetir la conversión. Empleado.id
   no tiene por qué coincidir con Maestro.id. Conflictos se reportan, no se fusionan.
3. **Copiar lo comprobable:** identidad/contacto y mismo Archivo de fotografía,
   institución y código conocido. No mover ni duplicar bytes, usuarios, contraseñas,
   asignaciones, planeaciones u horarios. No inferir fechas laborales, sueldo, RFC/NSS,
   principal ni porcentajes de costos. Estado laboral queda pendiente.
4. **Extender:** nuevas entidades y vínculo único, coherencia de institución y controles
   de integridad. La FK puede ser transitoriamente nullable durante la conversión;
   no entregar el módulo con maestros sin vincular o fallback silencioso de identidad.
5. **Cambiar consumidores:** lecturas, Criteria/JPQL, DTO, formularios, foto, soporte y
   exportaciones pasan a Empleado para datos personales. Conservar FK académicas e IDs
   en rutas. No modificar únicamente getters: las búsquedas SQL también deben cambiar.
6. **Legado:** las columnas personales anteriores pueden mantenerse temporalmente como
   copia técnica de migración, sin lectura ni escritura funcional independiente.
   Resolver explícitamente sus NOT NULL antes de permitir nuevos perfiles; un docente
   nuevo no debe necesitar capturar nombres dos veces ni fallar al insertar por campos
   obsoletos. No activar edición antes de completar el cambio de fuente. La eliminación
   física del legado sería una entrega posterior, no parte de la migración aditiva.
7. **Comprobación:** contar maestros antes/después y vínculos únicos, verificar que no
   cambien usuarios/archivos/FK/asignaciones, ausencia de cruces de institución y muestras
   ficticias de coincidencia de identidad. No duplicar maestros para corregir conflictos.
8. **Publicación:** probar alta nueva además de datos migrados, permisos, portal y
   exportaciones. Sólo desplegar implementación autorizada y probada, avisando de la
   pérdida de sesiones al recrear app; conservar PostgreSQL y volúmenes. No prometer
   rollback sólo cambiando el JAR: después del cambio de fuente el código anterior
   podría leer columnas obsoletas. Recuperación exige un plan validado de datos y archivos.

En este documento no se reserva ni se crea V111; su número debe revalidarse al implementar.

## 6. Acciones separadas y confirmaciones

| Acción | Efecto | Lo que NO debe hacer silenciosamente |
| --- | --- | --- |
| Editar datos personales | Actualizar Empleado y versión | Reactivar Maestro o Usuario, cambiar cuenta de acceso |
| Registrar baja laboral | Guardar fecha/motivo e historia laboral confirmada | Desactivar cuenta, borrar perfil o finalizar grupos automáticamente |
| Deshabilitar perfil docente | Impedir nuevas operaciones docentes según reglas acordadas | Eliminar historia, cambiar sueldo o desactivar un usuario administrativo |
| Finalizar asignación docente | Finalizar vínculo grupo/materia seleccionado | Finalizar todos los planteles laborales o dar baja al empleado |
| Desactivar acceso | Deshabilitar cuenta seleccionada, con permiso de cuentas | Borrar empleado, grupos, planeaciones o agenda |

Antes de deshabilitar funciones docentes, revisar asignaciones, horarios y borradores
vigentes. Si requieren tratamiento, impedir la acción y explicar qué finalizar o revisar;
no reutilizar `MaestroService.desactivar` como baja laboral masiva. La implementación
debe definir y probar las reglas exactas de vigencia antes de habilitar esas acciones.
Confirmaciones siempre muestran qué cambia, qué permanece y Cancelar sin envío.

## 7. Navegación y autorización de la primera entrega

- Menú Personal y nómina publica sólo Empleados; no mostrar módulos de cálculo vacíos.
- Pestañas: Ficha del empleado, Datos personales, Datos laborales básicos, Planteles,
  Información docente y Acceso. Secciones y acciones sólo con autorización apropiada.
- Proponer permisos propios de lectura/mantenimiento de Empleados y de datos laborales;
  validar nombres en el catálogo técnico al implementar. MAESTRO_LEER/ADMINISTRAR no
  conceden sueldos, datos laborales protegidos ni agenda. Ningún rol se amplía por migración.
- Primera entrega: administración general de empleados con alcance institucional,
  coherente con el mantenimiento actual de Maestros. Gestión delegada por plantel de
  personas compartidas requiere una política adicional; no habilitarla por inferencia.
- La ficha académica conserva identidad docente visible y asignaciones con sus permisos;
  edición de identidad canónica exige autorización explícita de Empleados. Sin ella,
  mostrar consulta, no un enlace que eluda la comprobación ni un redirect sin permiso.
- Mantener rutas y permisos académicos existentes, foto privada inline, portal y soporte.
  No enviar sueldo o información laboral como campos ocultos, JSON, columnas Excel o
  valores del modelo a quien sólo tiene acceso académico.
- JPA paginado y filtros en BD; Excel POI por bloques con exactamente los mismos
  filtros y datos permitidos. Autocompletado de diez al enfocar y búsqueda desde tres.
- Mismo diseño de formularios/filtros, pestañas, ayudas, moneda, fechas, ambos temas y móvil.

## 8. Cuentas mixtas: decisión a cerrar antes de habilitar ese flujo

`Usuario.tipoCuenta` hoy es uno de ADMINISTRATIVO, PORTAL_TUTOR o PORTAL_MAESTRO;
la agenda privada también comprueba ese tipo. No basta asignar otro rol para convertir
una cuenta administrativa en docente. No modificar tipo, principal ni credenciales
de una cuenta existente para resolver la integración.

La conversión conserva `Maestro.usuario_id` y el tipo de sus cuentas actuales. Un
empleado nuevo queda sin acceso por defecto; el acceso docente opcional reutiliza
la invitación existente con autorización explícita. La posibilidad de una sola cuenta
para administrar y enseñar, o de vincular cuentas separadas a un mismo empleado,
requiere elección y diseño de seguridad propios antes de ofrecerla. No fusionar por
correo ni copiar roles/agenda. Ese punto no impide crear empleados sin acceso o
preservar los maestros actuales, pero sí impide anunciar acceso mixto implementado.

## 9. Verificaciones exigidas antes de entregar código

- Maestro activo/inactivo, con/sin cuenta, con/sin foto: identidad correcta y mismo ID.
- Homónimos y correos coincidentes: dos registros no se fusionan automáticamente.
- Crear empleado no docente, activar perfil una vez, volver a activarlo sin duplicar;
  maestro nuevo no falla por las columnas antiguas ni recibe acceso automáticamente.
- Editar identidad una vez y verla en catálogos, filtros, horarios, planeaciones,
  portal, soporte y exportaciones; no reaparecen datos antiguos por otro endpoint.
- Reemplazar foto sin copiar archivos históricos ni dejar un consumidor usando una
  foto retirada; cuenta conserva su propia fotografía cuando sea distinta.
- Varios planteles, principal por periodo, solapamientos, vigencias e institución;
  adscripción laboral no concede acceso a grupos de otro plantel.
- Versión incorrecta y dos ediciones: error local, no pérdida silenciosa de cambios.
- Acciones separadas: editar no reactiva; baja laboral no ejecuta desactivación antigua;
  cerrar asignación no altera otras; cancelar modal no cambia ningún registro.
- Permisos y rutas directas: sólo académico no obtiene datos laborales por pantalla,
  formulario, JSON, documentos laborales o Excel; otra institución queda excluida.
  La fotografía común autorizada en la ficha docente conserva su acceso privado actual.
- Usuarios, login, soporte y privacidad de Mis pendientes conservados, sin cambios
  de propietarios, tipos, contraseñas, timeout, roles o permisos asignados.
- Excel con más de una página y filtros; sin cargar todo el catálogo; regresión de
  reportes, PDF de planeación, portal familiar y búsquedas relacionadas.
- QA ficticia escritorio/móvil y claro/oscuro. Pruebas técnicas no reemplazan la
  aceptación humana ni autorizan operar datos personales o financieros de instalación.

## 10. Resultado de esta sesión y siguiente paso

Inventario y estrategia documentados; ninguna tabla, código de aplicación o pantalla
ha cambiado. No se consultaron `.env`, registros personales ni contraseñas. No hubo
SQL, compilación, reinicio, despliegue, operaciones financieras ni commit.

Siguiente: revisar esta propuesta con el propietario y resolver el flujo de cuentas
mixtas antes de habilitarlo. Después implementar la primera entrega acotada por etapas
con migración aditiva y regresiones; no comenzar el cálculo de nómina todavía.
