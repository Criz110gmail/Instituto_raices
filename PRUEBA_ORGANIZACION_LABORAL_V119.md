# Prueba de jornadas, esquemas y asignaciones — V119

Recorrido principal confirmado por el propietario21/10/2026. La prueba salarial V116 ya está
cerrada y no se repite. Ninguna acción de este ejemplo genera pagos o nóminas.
Fecha base del ejemplo21/10/2026; si las fechas de inicio ya pasaron, usa fechas
futuras y adapta todo el ejemplo. No utilizar personal real sin autorización.

Avance previo confirmado por el propietario21/10/2026: punto4 realizado. Bruce tiene la
asignación id3 de01/01/2027 sin fin,12000MXN/30horas; condición individual id1
de01/11/2026 a31/12/2026 y condición id2 de13000 cancelada. No repetir punto4;
continuar desde punto5. El módulo antes llamado Esquemas de remuneración ahora se
presenta como Puestos y sueldos configurados. Códigos ESQ, IDs y /admin/esquemas
no cambiaron. Confirmar este punto no cierra toda la prueba V119 ni publica la guía.

## Resultado del recorrido principal — confirmado21/10/2026

Propietario confirmó puntos5–7 y Excel con los filtros de febrero:

- Editar catálogos a25horas/13000 no alteró enero: mantuvo30horas/12000.
- Asignación4 desde01/02/2027 registró25horas/13000, sin fin; enero3 terminó31/01/2027.
- Excel filtrado coincidió con la consulta; no exportó registros fuera del filtro.
- Cancelada la asignación4 de febrero: conserva25horas/13000 y fechas originales.
- Enero3 recuperó fin vacío y mantiene30horas/12000, estado Próxima.
- Noviembre1 quedó observado previamente con fin31/12/2026; fila2 continúa Cancelada.

No repetir operaciones ni volver a cancelar/programar para documentar. Este cierre
corresponde al recorrido principal y Excel confirmado; no declara una auditoría de
permisos ni comprobaciones adicionales de saldos no confirmadas por el propietario.
Incorporación editorial mediante V120: Guía de procesos → Personal → Puesto, jornada
y sueldo: asignar, programar cambios y cancelar el futuro,12 etapas. Resumen mejorado:
sueldo/jornada destacados, puesto/configuración y vigencia separados, motivo y aviso
histórico; Cancelada aclara que no aplica aunque conserve fin vacío. No cambia datos.

## 1. Revisar acceso y condición previa

Con administrador institucional identificado y módulos Empleados, Datos laborales
de empleados y Puestos y condiciones salariales, entra a Administración → Personal
y nómina. Debes ver Empleados, Puestos laborales, Jornadas laborales, Esquemas de
remuneración y Asignaciones laborales. No se otorgaron nuevos permisos automáticamente.

En Asignaciones laborales enfoca el filtro **Empleado**: muestra hasta10 opciones
iniciales de tu institución. Escribe `Bruce` (la búsqueda comienza desde3 caracteres)
y selecciona a Bruce Wayne en la lista. Deja **Puesto**, **Jornada**, **Configuración de puesto y sueldo** y **Texto adicional** vacíos y
pulsa **Aplicar filtros**. La etiqueta incluye código y nombre, pero filtra por ID.
Quitar empleado (×) elimina sólo ese criterio; Limpiar restablece todos los filtros.
La selección se conserva al ver/volver, paginar y exportar Excel. También está el
mismo selector en Empleados, junto a Búsqueda adicional para código/correo/otros datos.
Debes ver las condiciones
individuales anteriores:12000 desde01/11/2026 sin fin y13000 cancelada desde01/01/2027.
Jornada aparece Sin jornada registrada; es correcto, no se infiere de las clases.
No canceles12000 ni repitas la prueba anterior. Ver abre resumen/historial/gestión
con pestañas; Volver conserva filtros. Empleados → Ver expediente → Condiciones
salariales ahora resume lo vigente y ofrece Ver asignaciones del empleado.

## Ayudas independientes para consultar los catálogos

Todos los selectores de catálogo muestran hasta diez registros al enfocar o dar clic,
sin necesidad de escribir. Desde tres caracteres buscan por código o nombre;
selecciona una sugerencia y después pulsa **Aplicar filtros**. Cada selección usa
su ID, no la etiqueta concatenada. Puedes combinar los criterios y limpiarlos con ×.
**Limpiar** restablece la consulta completa. **Texto adicional** es libre y opcional,
no es una ayuda de catálogo ni sustituye la selección.

- Puestos laborales: **Puesto**.
- Jornadas laborales: **Jornada**.
- Puestos y sueldos configurados: **Configuración de puesto y sueldo**, **Puesto** y **Jornada**.
- Asignaciones laborales: **Empleado**, **Puesto**, **Jornada** y **Configuración de puesto y sueldo**.
- Consulta anterior de condiciones salariales del empleado: **Puesto**.
- Empleados conserva la ayuda **Empleado** implementada previamente.

En filtros se pueden localizar catálogos inactivos para revisar el historial; aparecen
identificados. Para crear una asignación siguen ofreciéndose sólo catálogos activos.
Pantalla y Excel aplican las mismas selecciones; listado, detalle y regreso conservan
los filtros de organización laboral. Las búsquedas y tablas están acotadas en servidor.

## 2. Crear jornada reutilizable

Administración → Personal y nómina → Jornadas laborales. Busca JOR-MAT-TEST; si ya
existe con estos valores reutilízala. Si no: Nueva jornada, formulario separado:

| Campo | Valor |
|---|---|
| Código | JOR-MAT-TEST |
| Nombre | Matutina de prueba |
| Horas semanales | 30 |
| Distribución y observaciones | Lunes a viernes de08:00 a14:00; jornada de prueba. |
| Registro activo | Marcado |

Crear registro → comprobar resumen → Cancelar: conserva datos sin guardar. Repite
y confirma: vuelve al listado con filtros. Activo verde; Excel respeta filtros.
Las horas son capacidad semanal; la descripción es referencia, no agenda estructurada
ni control de asistencia, descansos automáticos u horas pagables.

## 3. Crear esquema

Administración → Personal y nómina → Puestos y sueldos configurados → Nuevo esquema.
Reutiliza ESQ-DOC-MAT-TEST si ya existe y coincide, no dupliques.

| Campo | Valor |
|---|---|
| Código | ESQ-DOC-MAT-TEST |
| Nombre | Docente matutino de prueba |
| Puesto | Seleccionar DOC-PRI-TEST · Docente de primaria desde la ayuda |
| Jornada | Seleccionar JOR-MAT-TEST · Matutina de prueba desde la ayuda |
| Sueldo bruto base | 12000.00 |
| Base del sueldo | Mensual |
| Frecuencia de pago | Mensual |
| Registro activo | Marcado |

Ayudas:10 al enfocar, búsqueda desde3 caracteres. El importe es text/moneda, sin
spinner ni cambios por scroll. Moneda institucional MXN, no editable. Crear registro,
revisar modal, probar Cancelar y después confirmar. Tabla muestra puesto/jornada,
30horas y12000. Guardar el esquema todavía no cambia el sueldo de ningún empleado.

## 4. Asignar al empleado sin recapturar sueldo

Administración → Personal y nómina → Asignaciones laborales → Nueva asignación.
Dentro del formulario **Nueva asignación**, enfoca **Empleado** y selecciona Bruce.
Enfoca **Configuración de puesto y sueldo**, escribe `ESQ-DOC-MAT-TEST` y selecciona
la sugerencia **ESQ-DOC-MAT-TEST · Docente matutino de prueba**.
No lo busques únicamente en el listado de Asignaciones antes de asignarlo:
ese listado contiene condiciones/asignaciones del personal, no todos los esquemas
del catálogo. Crear el esquema no crea una asignación automáticamente.
Para comprobar el catálogo, entra a **Puestos y sueldos configurados**, selecciona ese
esquema en su filtro y pulsa **Aplicar filtros**. El esquema existe y estaba activo
en la comprobación de sólo lectura del21/10/2026; no hay que duplicarlo.
En Revisa la configuración deben aparecer puesto, Matutina30horas,12000MXN y
Mensual/Mensual. Si la consulta falla no se puede enviar: vuelve a seleccionar.

Inicio01/01/2027, fin vacío. Motivo: Asignación del esquema matutino de prueba a
partir de enero de2027. Revisar y asignar → Cancelar primero, después confirmar.
La asignación guarda importe y versiones del catálogo; el usuario no captura salario.

En el listado busca Bruce. La condición individual12000 desde01/11 termina31/12/2026;
la nueva asignación12000 inicia01/01/2027, sin fin, jornada30horas. La cancelada13000
anterior conserva estado/historial. Ver → Resumen permite distinguir cada registro.
Planteles, cuentas y grupos no cambian. Si aparece otra programación activa detén
la prueba y revísala; no intercalar cambios antes de una posterior ya registrada.

## 5. Editar catálogos sin cambiar lo asignado

Jornadas laborales → buscar JOR-MAT-TEST → Editar: cambiar horas a25 y descripción
a Lunes a viernes de08:00 a13:00; confirmar guardado. Después Puestos y sueldos configurados
→ ESQ-DOC-MAT-TEST → Editar: importe13000; confirmar. No cambia asignaciones existentes.

Vuelve a Asignaciones → Bruce → Ver la nueva de enero: debe seguir mostrando30horas
y12000. Es el snapshot autorizado, no los valores actuales del catálogo. No cambia
retroactivamente la condición individual previa tampoco.

## 6. Programar otro cambio

Asignaciones → Ver la de enero → Gestión → Programar cambio (o Nueva asignación
desde listado). Selecciona mismo empleado y Configuración de puesto y sueldo
ESQ-DOC-MAT-TEST: ahora resumen25horas y13000.
Inicio01/02/2027, fin vacío, motivo Cambio de configuración de prueba desde febrero.
Revisa modal y confirma. Enero12000/30horas queda hasta31/01/2027; febrero13000/25horas
queda sin fin. La ficha del empleado resume sólo lo aplicable hoy, no confundir con
programaciones futuras; Ver asignaciones muestra todo.

En el filtro Empleado selecciona Bruce desde la ayuda; deja Puesto, Jornada,
Configuración de puesto y sueldo y Texto adicional vacíos, elige Próximas y rango
01/02/2027 a28/02/2027 y aplica filtros. Sólo debe coincidir13000
si no hay otras filas del ensayo. Excel respeta exactamente filtros y muestra importes
numéricos, horas, snapshots, fechas y motivos. Quita filtros para continuar.

## 7. Cancelar sólo la última futura

Asignaciones → Ver la de febrero13000 → Gestión → Cancelar programación futura.
Motivo Cancelar cambio de febrero de prueba; conservar esquema anterior. Revisar
cancelación → Cancelar primero, luego repetir y confirmar.13000 febrero queda Cancelada;
12000 enero recupera fin vacío y mantiene30horas, aunque el catálogo diga25/13000.
Historial conserva asignaciones, cierres y cancelación. No permite borrar vigentes.

## 8. Cierre

Cuentas financieras sin movimientos/saldos modificados. Empleado, identidad, acceso,
planteles y clases intactos. Con cuenta sin autorización salarial no se ven menús ni
resúmenes salariales y rutas/Excel/ayudas se deniegan. No retirar permisos del admin
principal ni modificar roles operativos para hacer pruebas.

El recorrido principal y Excel ya fueron confirmados por el propietario; V120 los
incorpora a Guía de procesos, sin repetir operaciones. No marcar como
realizadas las comprobaciones independientes que no fueron confirmadas. La guía
salarial V118 corresponde al ensayo V116, no a estos nuevos módulos.
