# Prueba manual — Rediseño de nómina, entrega 1

Fecha:26/10/2026. Estado: **flujo principal confirmado por el usuario hasta el historial de ambos puestos**. No se dan por confirmadas las pruebas adicionales de seguridad o validaciones que no haya reportado expresamente.

### Avance manual confirmado — 26/10/2026

Cierre27/10/2026: usuario confirmó privacidad aislada con cuenta prueba, módulos
Empleados + Datos laborales, sin Salarios: pestañas laborales visibles pero sin
acción/importe salarial; detalle de puesto y Excel salarial directo denegados.
También aprobados consultas/Excel autorizados y concurrencia de asignación (ver
documento de pendientes). Listo para ensayo entrega2. Sólo lectura por operación
no disponible y pendiente; fechas negativas confirmadas manualmente en el selector,
servidor cubierto por QA sin atribuir envío forzado al usuario.

Actualización27/10/2026: usuario observó bloqueo del selector para fechas anteriores
a hoy (sólo evidencia de interfaz, sin forzar envío). Confirmó dos pestañas asignando
puesto a Elena: primera guardó, segunda exigió recargar; historial con UNA fila
APOYO_TECNICO_48/48h/14000desde01/06/2027,sin fin. Resta bloque manual de permisos;
ver PRUEBAS_PENDIENTES_NOMINA_ENTREGA1.md. No dar por probado manualmente el rechazo
del servidor a fechas inválidas a partir de la protección del calendario.

- Selección desactualizada de puesto antes de asignar: usuario confirmó «El puesto
  cambió. Vuelve a seleccionarlo y revisa su sueldo antes de confirmar» y el cierre
  de la prueba sin otra asignación. Historial de Diana conservado.
- Por autorización del usuario se continúa entrega2 sin dar por aceptada toda
  entrega1. Para27/10/2026 quedan tres bloques: fechas anteriores al ingreso/hoy,
  concurrencia de dos asignaciones y permisos salariales/URL/Excel. Ver también
  PRUEBA_REDISENO_NOMINA_ENTREGA2.md. No repetir pruebas aprobadas del catálogo.

- Edición de catálogo en dos pestañas: usuario confirmó rechazo de la segunda con
  «El puesto cambió desde que lo abriste. Recarga antes de guardar». Protección de
  versión del catálogo aprobada. No equivale al ensayo de dos asignaciones ni al
  de puesto modificado después de seleccionarlo. Restauración de descripción no
  confirmada expresamente.

- Horas/sueldo: usuario confirmó rechazo de campos vacíos, horas0/169 y sueldo0;
  recorrido terminado sin guardar VALIDACION-PUESTO-TEST. Validaciones aprobadas.

- Código duplicado APOYO_TECNICO_40: rechazado; el usuario confirmó que no se
  permite registrar un código existente. Caso de duplicidad del apartado7 aprobado.

El usuario confirmó el último recorrido solicitado:

- Resumen al15/06/2027:40horas y$10,000; al16/06/2027:48horas y$14,000.
- Excel del historial con las dos asignaciones, sus horas, sueldos y vigencias.
- Intento de intercalar asignación desde10/06/2027 rechazado por existir la
  programación del16/06/2027; historial conservado sin asignaciones adicionales.
- Puesto APOYO_TECNICO_40 desactivado: no se ofreció para nuevas asignaciones;
  historial de Diana conservó40horas/$10,000. Usuario confirmó el recorrido completo,
  incluida la reactivación final del catálogo.

Pendientes de confirmación manual: restantes validaciones del apartado7 (excepto
asignación intermedia y puesto inactivo ya confirmados),
concurrencia entre pestañas y permisos del apartado8. No generar todavía nómina
proporcional ni marcar toda la entrega como aceptada.

Implementación desplegada: V135, aplicación UP en18080;1011Java/128JS y ensayo
PostgreSQL17 aislado correctos. Respaldo previo validado y huellas de7tablas idénticas;
46movimientos operativos conservados. No se creó el empleado del ejemplo por herramientas.
Esta entrega implementa puestos con horas/sueldo mensual y asignaciones históricas
desde el empleado. No implementa aún el nuevo cálculo semanal/quincenal/proporcional,
pagos multicuenta, chequeras ni reestructuración de préstamos del plan26/10.

## Objetivo y precauciones

Probar dos variantes de puesto con igual nombre y distinto código, asignar sin
recapturar sueldo/horas, programar un cambio y conservar la fotografía original
aunque se edite el catálogo. Todas estas operaciones son configuración: **no generan
nóminas ni afectan cuentas financieras**.

Usar una cuenta administrativa persistida con permisos de Empleados y Datos laborales
y Consultar/Administrar condiciones salariales. No usar recuperación/bootstrap ni
portales. Los códigos de permisos existentes se conservan; no se concedieron a roles
por desplegar. Sin permiso salarial no debe verse sueldo ni acceder a sus endpoints.

No cambiar el sueldo de Bruce o Ana ni utilizar el borrador de enero para este ensayo.
Usar el empleado nuevo indicado abajo. Si ya existe un código de esta prueba, consultar
qué se guardó antes de crear o confirmar nuevamente. No borrar registros para repetir.
Las fechas propuestas son futuras respecto del26/10/2026; si se ejecuta después de
junio2027, acordar otro mes futuro para todas las fechas antes de empezar.

## 1. Crear el primer puesto

Entrar en **Administración → Personal y nómina → Puestos laborales**.
La entrada debe mostrar listado/filtros/Excel, no un formulario incrustado.
Pulsar **Nuevo puesto** y capturar:

| Campo | Valor |
|---|---|
| Código | APOYO_TECNICO_40 |
| Nombre | Apoyo técnico |
| Horas semanales | 40 |
| Sueldo base mensual | 10000 |
| Descripción | Variante de prueba de 40 horas semanales. |
| Puesto activo | Activado |

Salir del importe y comprobar formato moneda. No debe haber spinner que cambie dinero
al hacer scroll. Pulsar Crear puesto: modal con código/nombre/horas/sueldo/estado.
Probar Cancelar: cierra sin insertar. Volver a Crear puesto y confirmar una sola vez.
Esperado: una fila,40horas,$10,000mensuales,Activo y configuración Completo.

## 2. Crear la segunda variante

Desde el listado pulsar Nuevo puesto y capturar:

| Campo | Valor |
|---|---|
| Código | APOYO_TECNICO_48 |
| Nombre | Apoyo técnico |
| Horas semanales | 48 |
| Sueldo base mensual | 14000 |
| Descripción | Variante de prueba de 48 horas semanales. |
| Puesto activo | Activado |

Confirmar. Se admite el mismo nombre; los códigos son distintos. Buscar APOYO_TECNICO
en Texto adicional y aplicar: deben aparecer únicamente las dos variantes de este
ensayo. Exportar Excel: mismos registros, horas40/48 e importes10000/14000 numéricos.
Entrar a Editar y volver: conservar búsqueda/estado/página/tamaño.

Los puestos anteriores aV135, como DOC-PRI-TEST, pueden mostrar Pendiente de completar.
No se les inventaron horas/sueldo ni se modificaron sus asignaciones existentes.
No es necesario completarlos ni editarlos para este ejemplo.

## 3. Registrar un empleado exclusivo del ensayo

Entrar en **Administración → Personal y nómina → Empleados → Nuevo empleado**.

| Campo | Valor |
|---|---|
| Institución | Instituto Raices, en esta base; seleccionar la institución del ensayo |
| Código laboral | EMP-PUESTOS-TEST-01 |
| Nombre(s) | Diana |
| Primer apellido | Prueba |
| Segundo apellido | Puestos |
| Correo | Vacío; es opcional para esta prueba |
| Teléfono | Vacío; es opcional para esta prueba |

Registrar empleado y confirmar. En el expediente abrir **Datos laborales**:

- Situación: Actividad laboral confirmada.
- Fecha de ingreso laboral:26/10/2026.
- Fecha de baja y motivo de baja: vacíos.
- Guardar información laboral, revisar y confirmar.

En este formulario ya no se selecciona un puesto textual para después repetirlo en
salarios. Actividad laboral puede confirmarse sin sueldo base. La asignación se hace
en el paso siguiente, desde esta misma sección del empleado.
No habilitar perfil docente, grupos ni usuario de acceso para este ensayo.

## 4. Asignar puesto sin recapturar su sueldo

En **Empleados → Diana → Datos laborales**, abrir
**Ver puesto e historial →**.
En el detalle pulsar **Asignar o cambiar puesto**.

1. En Puesto laboral, enfocar: deben aparecer hasta diez sugerencias.
2. Buscar APOYO_TECNICO_40 y seleccionar la sugerencia exacta.
3. Deben completarse40horas y$10,000mensuales, ambos **sólo lectura**.
4. Aplicar desde:01/06/2027.
5. Motivo: `Asignación inicial de prueba: apoyo técnico de 40 horas desde junio de 2027.`
6. Revisar asignación de puesto: comprobar empleado, puesto, horas, sueldo, fecha y motivo.
7. Probar Cancelar: no registra asignación ni pierde captura.
8. Revisar nuevamente y Confirmar y asignar.

Esperado: vuelve al resumen consultando01/06/2027, puesto40/$10,000/sin fin.
Abrir Historial de puestos: una asignación. Puesto/sueldo provienen de una sola fuente,
no hay un sueldo adicional capturado en otra pantalla.

Como la fecha es futura, al volver al expediente con fecha de hoy puede indicar
Sin sueldo aplicable hoy. Eso es correcto: consultar01/06/2027 en el detalle del puesto.
Los estados Próxima/Vigente/Anterior del historial se refieren a la fecha actual del
sistema; la tarjeta del resumen se refiere a la fecha expresamente consultada.

## 5. Comprobar que el catálogo no reescribe la asignación

Entrar en **Puestos laborales**, buscar APOYO_TECNICO_40 y pulsar Editar.
Cambiar sueldo mensual a11000, mantener horas40 y guardar con confirmación.

Volver a **Empleados → Diana → Datos laborales → Ver puesto e historial**.
Consultar01/06/2027: debe continuar en **$10,000 y40horas**.
El catálogo ahora ofrece11000 a nuevas asignaciones; Diana conserva el acuerdo
guardado anteriormente. No se cambia un contrato silenciosamente al editar catálogo.

## 6. Programar cambio de puesto

Desde el detalle de puesto de Diana, pulsar Asignar o cambiar puesto:

| Campo | Valor |
|---|---|
| Puesto | APOYO_TECNICO_48 · Apoyo técnico |
| Horas automáticas | 48 |
| Sueldo automático | $14,000 mensuales |
| Aplicar desde | 16/06/2027 |
| Motivo | Cambio programado de prueba a variante de 48 horas desde el 16 de junio. |

Revisar y confirmar. No capturar una baja laboral ni cerrar manualmente el primer puesto.

En Resumen por fecha comprobar:

| Consulta | Resultado esperado |
|---|---|
| 15/06/2027 | APOYO_TECNICO_40,40horas,$10,000 |
| 16/06/2027 | APOYO_TECNICO_48,48horas,$14,000 |

En Historial:

| Puesto | Inicio | Fin | Sueldo histórico |
|---|---|---|---:|
| APOYO_TECNICO_40 | 01/06/2027 | 15/06/2027 | $10,000 |
| APOYO_TECNICO_48 | 16/06/2027 | Sin fecha de fin | $14,000 |

El catálogo40 conserva11000, pero la primera asignación conserva10000. Exportar
historial a Excel: mismo empleado, ambas asignaciones, horas/sueldos/fechas/motivos.
El historial es paginado y el Excel exporta por bloques, no todo a memoria.

**Todavía no generar nómina para este cambio parcial.** El cálculo proporcional de
junio que daría$12,000 corresponde a la entrega del motor, no está habilitado aquí.

## 7. Validaciones sin registrar datos incorrectos

- Nuevo puesto sin horas o sin sueldo: errores visibles, no guardar.
- Horas0/169 o sueldo0: rechazar; corregir antes de confirmar.
- Repetir código APOYO_TECNICO_40: rechazar sin duplicar. No se prohíbe repetir nombre.
- Intentar fecha de asignación anterior al ingreso o anterior a hoy: rechazar.
- Otra asignación con la misma fecha o una fecha intercalada antes de una programación
  posterior: rechazar; explicar que se debe atender primero la programación existente.
- Cambiar un puesto seleccionado desde otra pestaña antes de confirmar: rechazar versión
  vieja, volver a seleccionarlo y revisar. No tomar sueldo distinto al mostrado a escondidas.
- Dos pestañas de asignación abiertas con la misma revisión: sólo una debe guardar;
  la otra informa que cambió la configuración. No aceptar dos veces el mismo formulario.
- Puesto inactivo o pendiente de completar no debe permitir asignación nueva.

Para verificar desactivación sin afectar la fotografía de Diana: desactivar40 (ya
completo), comprobar que no se ofrece en ayuda de activos y que historial sigue10000;
reactivarlo al terminar si se desea. No cambia cuentas ni revoca puestos ya asignados.

## 8. Permisos, diseño y cierre del ensayo

Con una cuenta de prueba de sólo consulta salarial: puede consultar datos autorizados
y Excel, pero no abrir ni ejecutar Asignar/cambiar. Con acceso a Empleados/Datos
laborales sin permisos salariales: no ver montos ni acceder a detalle/contexto/Excel
salariales pegando una URL. No cambiar permisos de usuarios reales para este ensayo.

Revisar tema claro/oscuro, celular/escritorio, autocompletados, estados y modal.
Cancelar dentro del modal no equivale a cancelar una asignación ya guardada.

Resultado final esperado: Diana activa, dos asignaciones históricas según paso6;
catálogo40=11000, asignación40=10000;48=14000; cuentas y movimientos sin cambios.
Si se hacen ensayos negativos, no deben agregar asignaciones exitosas extra.

Reportar resultados para incorporar el recorrido confirmado a Guía de procesos.
No marcar probado por el usuario sólo porque pasó la prueba automatizada de QA.
