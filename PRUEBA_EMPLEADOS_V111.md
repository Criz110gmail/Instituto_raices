# Aceptación de Empleados y Maestros — V111

Esta entrega no calcula nóminas ni afecta cuentas financieras. Pruebas técnicas
aprobadas no sustituyen estas comprobaciones con el propietario. Registrar aquí
el resultado real antes de trasladarlo a la guía de procesos como caso confirmado.

## 1. Autorizar el módulo

1. Entra con tu cuenta administrativa identificada, no el acceso de recuperación.
2. Abre **Administración → Seguridad y soporte → Roles y permisos** y edita el rol
   apropiado. Habilita **Empleados**. Sólo para quien deba consultar/mantener la
   información laboral habilita también **Datos laborales de empleados**.
3. Guarda y vuelve a iniciar sesión para actualizar las autoridades de tu cuenta.
   El sistema no concedió estos permisos automáticamente a ningún rol.
4. Abre **Administración → Personal y nómina → Empleados**.

## 2. Comprobar maestros existentes sin recrearlos

1. Busca el código/nombre de un maestro que ya existía y abre **Ver expediente**.
2. En **Ficha del empleado** comprueba nombre y fotografía; en **Información docente**
   comprueba su código y estado. En **Acceso** debe aparecer la misma cuenta de antes.
3. En **Datos laborales** inicialmente debe decir **Por confirmar**: no se tomaron las
   fechas técnicas ni las del ciclo como contratación. Planteles queda sin confirmar.
4. Desde **Información docente → Ver grupos y materias**, revisa que las asignaciones
   previas sigan iguales. El portal y soporte docente deben seguir encontrando al
   mismo maestro y sus planeaciones. No uses datos reales de otro usuario para acceder.
5. Regresa a Empleados → expediente → **Datos personales**, realiza una corrección
   legítima de contacto y pulsa **Guardar datos personales**. Revisa el modal; primero
   pulsa **Cancelar** y comprueba que no se guardó. Confirma sólo si la corrección es
   real y autorizada. Debe reflejarse también en la ficha académica, sin reactivar una
   cuenta o perfil deshabilitado. Correo de contacto no cambia el correo de login.

## 3. Alta y perfil opcional

Si decides registrar un empleado de prueba, quedará como expediente en esta base:
no existe borrado de prueba. Prefiere un ambiente de capacitación o una alta real
autorizada. Ejemplo ficticio para ese ambiente:

1. Empleados → **Nuevo empleado**: institución actual, código `EMP-TEST-V111`, nombres
   `Personal`, primer apellido `Prueba`, correo `personal@example.invalid` opcional.
2. Pulsa **Registrar empleado**; cancela el primer modal para comprobar que no crea
   nada. Después revisa y confirma. Se crea sin usuario y sin perfil docente.
3. En el expediente → **Información docente**, revisa el código docente sugerido y
   pulsa **Habilitar perfil docente**. Cancela para probar; vuelve a abrir y confirma.
4. Debe existir un solo perfil en Maestros. **Acceso** continúa sin cuenta hasta que
   una persona autorizada gestione la invitación docente en el flujo existente.
5. No crea grupos, permisos, sueldo, contrato ni un segundo empleado al habilitarlo.

## 4. Laboral y varios planteles

Usa fechas confirmadas; para la fixture anterior en ambiente de pruebas:

1. Expediente → **Datos laborales**: situación **Actividad laboral confirmada**, puesto
   `Docente de primaria`, fecha ingreso `01/10/2026`. Guarda y confirma.
2. El historial privado debe incluir fecha, identificador de quien guardó, puesto,
   situación y fecha de ingreso. Sin permisos laborales, ni pestañas ni campos deben
   aparecer; tampoco debe poder consultarse/guardarse por URL.
3. **Planteles** → enfoca Plantel: aparecen hasta diez opciones de la institución;
   desde tres caracteres filtra. Selecciona el primero, inicio `01/10/2026`, sin fin,
   **Principal** marcado. Agrega y confirma.
4. Si hay otro plantel activo de la misma institución, agrégalo desde `01/10/2026`
   sin marcar Principal: se permite trabajar en ambos.
5. Intenta otra vigencia superpuesta del mismo plantel o un segundo principal durante
   las mismas fechas: debe mostrarse validación y conservar los registros anteriores.
6. Para cambiar principal, finaliza la vigencia anterior en una fecha acordada y
   registra otra desde el día siguiente. Las fechas finales son inclusivas. Conserva
   historial; no cambia los grupos ni concede permisos por trabajar en ese plantel.
7. **Baja laboral** requiere fecha/motivo y no desactiva cuentas ni grupos. **Deshabilitar
   perfil docente** es otra acción y debe bloquearse si existen asignaciones/horarios
   vigentes o planeaciones pendientes. La disponibilidad del acceso es una tercera
   acción independiente de Maestros → Portal de maestros.

## 5. Consulta y exportación

1. En el listado filtra por código o nombre y por **Con perfil docente**.
2. Verifica paginación y que **Exportar Excel** incluya exactamente el mismo universo
   filtrado, no todos los empleados. Exporta identidad/perfil, no información laboral
   privada con un permiso básico.
3. Revisa pestañas, ayudas, botones y modal Cancelar en móvil y con ambos temas.

Resultado humano: **PENDIENTE**. Después de confirmarlo, documentar el caso validado
en la guía interactiva; no marcar como confirmado por la sola ejecución de tests.
