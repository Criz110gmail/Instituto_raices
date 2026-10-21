# Prueba de jornadas, esquemas y asignaciones — V119

Implementación nueva; aceptación humana pendiente. La prueba salarial V116 ya está
cerrada y no se repite. Ninguna acción de este ejemplo genera pagos o nóminas.
Fecha base del ejemplo21/10/2026; si las fechas de inicio ya pasaron, usa fechas
futuras y adapta todo el ejemplo. No utilizar personal real sin autorización.

## 1. Revisar acceso y condición previa

Con administrador institucional identificado y módulos Empleados, Datos laborales
de empleados y Puestos y condiciones salariales, entra a Administración → Personal
y nómina. Debes ver Empleados, Puestos laborales, Jornadas laborales, Esquemas de
remuneración y Asignaciones laborales. No se otorgaron nuevos permisos automáticamente.

En Asignaciones laborales busca Bruce Wayne o su código. Debes ver las condiciones
individuales anteriores:12000 desde01/11/2026 sin fin y13000 cancelada desde01/01/2027.
Jornada aparece Sin jornada registrada; es correcto, no se infiere de las clases.
No canceles12000 ni repitas la prueba anterior. Ver abre resumen/historial/gestión
con pestañas; Volver conserva filtros. Empleados → Ver expediente → Condiciones
salariales ahora resume lo vigente y ofrece Ver asignaciones del empleado.

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

Administración → Personal y nómina → Esquemas de remuneración → Nuevo esquema.
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
Selecciona Bruce desde ayuda de Empleado y ESQ-DOC-MAT-TEST desde Esquema.
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
a Lunes a viernes de08:00 a13:00; confirmar guardado. Después Esquemas de remuneración
→ ESQ-DOC-MAT-TEST → Editar: importe13000; confirmar. No cambia asignaciones existentes.

Vuelve a Asignaciones → Bruce → Ver la nueva de enero: debe seguir mostrando30horas
y12000. Es el snapshot autorizado, no los valores actuales del catálogo. No cambia
retroactivamente la condición individual previa tampoco.

## 6. Programar otro cambio

Asignaciones → Ver la de enero → Gestión → Programar cambio (o Nueva asignación
desde listado). Selecciona mismo empleado/esquema: ahora resumen25horas y13000.
Inicio01/02/2027, fin vacío, motivo Cambio de esquema de prueba desde febrero.
Revisa modal y confirma. Enero12000/30horas queda hasta31/01/2027; febrero13000/25horas
queda sin fin. La ficha del empleado resume sólo lo aplicable hoy, no confundir con
programaciones futuras; Ver asignaciones muestra todo.

Filtra por Bruce, Próximas, rango01/02/2027 a28/02/2027. Sólo debe coincidir13000
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

El propietario debe confirmar resultados antes de publicar este recorrido como
CONFIRMADA en Guía de procesos. La guía salarial V118 corresponde al ensayo V116
ya confirmado, no a estos nuevos módulos.
