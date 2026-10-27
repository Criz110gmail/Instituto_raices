# Cierre manual pendiente — Puestos y sueldos (entrega 1)

Preparado27/10/2026. Tres bloques pendientes por acuerdo del usuario. La entrega2
está desplegada, pero se probará después. No crear nóminas ni mover dinero aquí.
No marcar ninguno aprobado hasta recibir confirmación humana.

## Avance confirmado por el usuario — 27/10/2026

Confirmación final del caso aislado: con **prueba** y sólo Empleados + Datos
laborales se ven pestañas01/02/03/04/05/06, no Ver puesto e historial, y se deniegan
GET /admin/empleados/6/puesto y GET /admin/asignaciones/excel?empleadoId=6&estado=TODAS.
Privacidad salarial con Laboral permitido y Salarios omitido, incluida exportación
directa, **aprobada por el usuario**. No fue necesario conceder otro permiso.

Se puede iniciar ensayo entrega2. No confundir este cierre de las pruebas manuales
viables con implementación de sólo lectura: Consultar/Administrar siguen agrupados
por módulo y su separación queda como pendiente de diseño/desarrollo autorizado.
Fechas inválidas: confirmación humana de control de interfaz, no POST forzado.

Con la cuenta correcta **pruebas**, usuario confirmó menú Personal y nómina y
pestañas laborales visibles; resumen de Diana15/06/2027 con40h/$10,000 y16/06/2027
con48h/$14,000, más Excel con ambas asignaciones. Acceso salarial autorizado y
exportación aprobados. No equivale a sólo consulta: el rol concede paquetes completos.
La cuenta **prueba** sin Laboral/Salarios ya mostró pestañas ocultas y denegación de
la ruta salarial. Sigue pendiente aislar privacidad salarial con Laboral permitido
y Salarios omitido, incluido Excel directo; no afirmar ese caso específico aprobado.
Separación Consultar/Administrar no implementada, permanece pendiente explícita.

Diagnóstico posterior: /admin/perfil/resumen mostró **prueba**, asignado a
PRUEBA_PERSONAL_BASE, sólo Empleados activo. El rol con los tres paquetes está en
**pruebas** (plural), QA_SALARIOS_CONSULTA. Las pestañas ocultas/denegación son
correctas para la cuenta singular; las verificaciones iniciales consultaron otra
cuenta. Continuar acceso laboral/salarial con la plural y confirmar antes de aprobar.
No ampliar permisos de prueba para ocultar la diferencia. La limitación de sólo
lectura por paquetes descrita abajo sigue vigente.

### Corrección del bloque3 tras inspección — 27/10/2026

El rol QA_SALARIOS_CONSULTA local ya tiene activos los seis permisos de Empleados,
Laboral y Condiciones salariales (LEER y ADMINISTRAR), asignado activamente al
usuario de ensayo pruebas con alcance institucional. Usuario reportó ver sólo
Empleados y pestañas personales/docentes: no corresponde a las autoridades actuales
persistidas; revisar renovación de sesión/cuenta antes de modificar permisos.

La instrucción inferior de seleccionar sólo Consultar NO es realizable hoy desde
Roles: RolServiceImpl agrupa cada selección por ModuloPermiso y asigna todos sus
permisos; al entrar, ModuloPermiso.expandir vuelve a expandir el paquete. El nombre
del rol no lo limita a lectura. No aprobar la prueba de sólo consulta ni presentar
ese rol como seguro para usuarios de operación. Separar lectura/administración
requiere cambio explícitamente autorizado y pruebas de compatibilidad; pendiente.
La prueba sin acceso al módulo salarial sí puede hacerse omitiendo ese paquete.

- Bloque1: selector impide elegir fechas anteriores a hoy. Protección de interfaz
  observada; no equivale a envío manual de fecha inválida ni a confirmación humana
  de la validación del servidor (cubierta por QA automatizada). No forzar el control.
- Bloque2: confirmado. La segunda pestaña solicitó recargar y no guardó; historial
  de Elena con una sola asignación APOYO_TECNICO_48,48horas,14000mensuales,
  desde01/06/2027,sin fin,Próxima y motivo de prueba concurrente.
- Siguiente pendiente humano: bloque3, permisos de consulta y privacidad salarial.
  No crear otra asignación ni cancelar la fila exitosa para continuar.

## Preparación: empleado auxiliar, sin cambiar a Diana

1. Entrar a **Administración → Personal y nómina → Empleados**.
2. Buscar primero código **EMP-PUESTOS-SEG-TEST**. Si ya existe, comprobar su
   ingreso e historial antes de continuar; no duplicar ni borrar para repetir.
3. Si no existe, **Nuevo empleado**: institución del ensayo (Instituto Raices en
   esta base), código EMP-PUESTOS-SEG-TEST, nombre Elena, primer apellido Prueba,
   segundo apellido Seguridad; correo/teléfono opcionales vacíos.
4. Registrar, revisar modal y confirmar.
5. En su expediente abrir **Datos laborales**: Actividad laboral confirmada,
   ingreso **01/10/2026**, baja y motivo vacíos. Guardar, revisar y confirmar.
   El ingreso laboral no admite fechas futuras; la asignación salarial sí.
6. No habilitar usuario docente, no asignar salario ni conceptos aún.
7. Anotar su ID desde la URL del expediente. No confundirlo con Diana (ID6 en
   esta computadora). Si otra base tiene otros IDs, usar los que muestra esa base.

## Bloque 1 — Fechas inválidas, dos intentos sin guardar

1. En **Empleados → Elena → Ver expediente → Datos laborales**, pulsar
   **Ver puesto e historial → Asignar o cambiar puesto**.
2. Seleccionar **APOYO_TECNICO_48 · Apoyo técnico**. Debe cargar48horas y14000MXN
   mensuales desde el catálogo. No modificar el catálogo ni capturar el importe.
3. Anotar primero la fecha que el formulario propone en Aplicar desde: es el hoy
   institucional usado por el servidor. Después cambiar a **30/09/2026**, motivo
   `Prueba de fecha anterior al ingreso.`
4. Revisar asignación y confirmar si abre el modal.
5. Debe rechazar: anterior al ingreso de Elena01/10/2026 y a hoy. No insertar ni
   generar historial salarial exitoso. Ambos límites actúan sin reescribir el pasado.
6. En el mismo formulario intentar **el día anterior a la fecha sugerida al abrir**,
   motivo `Prueba de fecha anterior a hoy.` Si proponía27/10/2026 usar26/10/2026;
   si proponía26/10/2026 usar25/10/2026. Esta fecha sí es posterior al ingreso,
   pero no cumple la regla de hoy/futuro. No asumir que el reloj del navegador
   y la fecha institucional coinciden.
7. Debe bloquear por validación del control o del servidor; nunca guardar.
   No quitar restricciones del HTML ni usar herramientas para forzar el envío.
8. Pulsar **Cancelar**, regresar a Historial: Elena sigue sin asignaciones.

Si se ejecuta después, usar el día anterior a la fecha institucional para el
segundo intento y verificar que siga siendo posterior al ingreso. No cambiar
relojes, ingreso ni fechas históricas para forzarlo.

## Bloque 2 — Dos pestañas asignando puesto

Elena debe seguir sin asignaciones. Si ya tiene una, no repetir este bloque sin
adaptar la prueba: las dos pestañas deben partir de la misma revisión limpia.

1. En Elena, abrir **Datos laborales → Ver puesto e historial**.
2. Abrir **Asignar o cambiar puesto** en dos pestañas del mismo usuario, A y B,
   **antes de guardar en cualquiera**. Duplicar la pestaña de formulario también
   sirve si se abrió antes del guardado.
3. En ambas seleccionar APOYO_TECNICO_48; revisar48horas/14000mensuales.
4. Aplicar desde **01/06/2027**, motivo
   `Prueba de asignación concurrente del mismo empleado.`
5. En A, Revisar asignación de puesto y confirmar. Debe guardar una asignación.
6. En B, sin recargar ni volver a seleccionar el puesto, revisar y confirmar.
7. Debe rechazar por revisión/datos laborales o salariales cambiados; puede indicar
   `Los datos laborales o las condiciones cambiaron. Recarga y revisa antes de confirmar.`
8. Cerrar B sin reintentar. Desde A volver a Historial: **exactamente una** fila,
   APOYO_TECNICO_48,48horas,14000MXNmensuales,desde01/06/2027,sin fin.
9. Resumen por fecha01/06/2027: mismo resultado. No cancelar esa fila como parte
   del ensayo; queda como resultado conocido del empleado auxiliar.
10. Diana conserva sus dos filas40/10000 y48/14000; ninguna cuenta se altera.

Esto no es la prueba ya aprobada de dos pestañas editando el catálogo: aquí se
verifica la asignación única al empleado.

## Bloque 3 — Permisos de consulta y privacidad salarial

Usar cuentas/roles **exclusivos de prueba**, administrativos y de la misma
institución, con alcance institucional. No modificar el rol del administrador real
ni quitarle acceso. Usar una ventana privada/perfil separado para el usuario de
prueba: todas las pestañas de un mismo perfil comparten sesión.

### 3A. Preparar un rol sólo de consulta salarial

1. Como administrador, **Administración → Seguridad y soporte → Roles y permisos**.
2. Crear o revisar rol **QA_SALARIOS_CONSULTA**, nombre `Prueba de consulta salarial`.
3. Habilitar sólo Consultar en **Empleados**, **Datos laborales de empleados** y
   **Puestos y condiciones salariales**. No Administrar, finanzas ni seguridad.
4. No otorgar aún Configuración de nómina; corresponde al ensayo nuevo posterior.
5. Guardar con confirmación. En **Seguridad y soporte → Usuarios**, crear cuenta
   administrativa de prueba `qa.salarios.consulta`, misma institución, activa,
   con ese rol y alcance institucional. Usar correo/datos de prueba que controle
   el administrador y el flujo de activación/contraseña del sistema. No enviar
   contraseñas al chat ni reutilizar las de operación.
6. Si ya existe una cuenta de ensayo equivalente, verificar sus roles completos:
   no debe conservar otro rol amplio que invalide la prueba.
7. Entrar con esa cuenta en ventana privada. Tras cambiar roles, cerrar sesión y
   volver a entrar para renovar permisos.

### 3B. Probar lectura permitida, escritura prohibida

1. **Personal y nómina → Empleados → Diana → Ver expediente → Datos laborales**.
2. Abrir **Ver puesto e historial**. Consultar15/06/2027:10000/40horas;
   consultar16/06/2027:14000/48horas.
3. **Historial de puestos → Exportar historial a Excel**: debe exportar ambas
   asignaciones con esos importes y vigencias.
4. No debe aparecer **Asignar o cambiar puesto** ni permitir editar el catálogo.
5. Pegar directamente `http://localhost:18080/admin/empleados/6/puesto/nuevo`:
   acceso denegado o redirección de seguridad, nunca formulario editable ni500.
   En otra computadora sustituir6 por el ID real de Diana.
6. No intentar pagos ni enviar solicitudes manuales para evadir la interfaz.

### 3C. Probar empleado sin permiso salarial

1. Como administrador real (ventana normal), crear rol **QA_EMPLEADOS_SIN_SUELDO**
   con sólo Consultar **Empleados** y **Datos laborales de empleados**.
2. Crear otra cuenta administrativa de ensayo `qa.empleados.sin.sueldo`, misma
   institución y alcance institucional, exclusivamente con ese rol.
3. Cerrar la sesión privada anterior y entrar con esta segunda cuenta.
4. Abrir **Empleados → Diana → Ver expediente → Datos laborales**.
5. No debe verse tarjeta salarial, importe ni enlace Ver puesto e historial.
   Tampoco Configuración de nómina: no tiene el permiso nuevo.
6. Pegar las siguientes URL de esta base:
   - `http://localhost:18080/admin/empleados/6/puesto?fecha=2027-06-16`
   - `http://localhost:18080/admin/empleados/6/puesto/nuevo`
   - `http://localhost:18080/admin/asignaciones/excel?empleadoId=6&estado=TODAS`
7. Las tres deben denegar/redirigir de forma segura; no descargar Excel salarial,
   mostrar importes, generar registros ni presentar Whitelabel/500.
8. Volver al listado de **Empleados**, filtrar Diana y Exportar Excel: este Excel
   general puede mostrar identidad y datos laborales autorizados, **no sueldos**.

Al terminar, cerrar la sesión de ensayo. Mantener el administrador original intacto.
Reportar resultados por bloque y cualquier mensaje inesperado. Con estos tres
bloques confirmados se cierra el ensayo anterior y se inicia
**PRUEBA_REDISENO_NOMINA_ENTREGA2.md**, habilitando sus permisos nuevos sólo al rol
administrativo autorizado; no concederlos a las cuentas negativas de este ensayo.
