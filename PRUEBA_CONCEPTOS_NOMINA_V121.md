# Prueba del catálogo de Conceptos de nómina — V121

Recorrido confirmado por el propietario21/10/2026. Sólo configura un catálogo: no asigna
conceptos ni importes a empleados, no calcula nómina o impuestos y no mueve dinero.
No repetir asignaciones salariales de Bruce ni modificar catálogos de jornadas.

## Resultado confirmado

El propietario realizó el recorrido completo y confirmó altas, edición, modales,
filtros, Excel, validaciones y protección de cambios entre dos pestañas. Probó
activar y desactivar DESC-TEST para comprobar filtros y después lo dejó Activo.
Resultado final: BON-PUNT-TEST, DESC-TEST y APORT-TEST Activos. No queda una baja
pendiente ni hay que repetir operaciones para documentarlas. No se declara una
auditoría exhaustiva de accesos no reportada. V122 incorpora este caso a Guía de
procesos → Personal → Conceptos de nómina: crear, filtrar y proteger cambios.

## 1. Acceso y estructura

Con administrador institucional identificado, entra a **Administración → Personal y
nómina → Conceptos de nómina**. Requiere el mismo paquete privado de Puestos y
condiciones salariales y lectura de Empleados/Datos laborales. No se concedieron
permisos automáticamente. Si cambiaron permisos, renueva la sesión.

El menú abre sólo listado, filtros, paginación y Excel. **Nuevo concepto** y **Editar**
abren un formulario separado. Los botones Cancelar y Volver conservan los filtros
y página; el formulario no incluye importes ni selección de empleado.

## 2. Crear una percepción y probar Cancelar

En Conceptos de nómina pulsa **Nuevo concepto** y captura:

| Campo | Valor |
|---|---|
| Código | BON-PUNT-TEST |
| Nombre | Bono de puntualidad de prueba |
| Descripción | Bono de prueba para comprobar el catálogo, sin asignarlo a empleados. |
| Tipo de concepto | Percepción |
| Forma de aplicación | Habitual |
| Concepto activo | Marcado |

Pulsa **Crear concepto**. Debe abrir modal con código, nombre, tipo, aplicación,
efecto de referencia, descripción y estado; no debe guardar todavía. Pulsa **Cancelar**:
cierra el modal, conserva captura y no registra nada. Repite y pulsa **Confirmar y
guardar**. Vuelve al listado con aviso de guardado y estado **Activo** verde; efecto
**Suma al empleado** sólo describe el tipo, no es un pago realizado.

Si el código ya existe, reutiliza el registro para consulta o usa otro código de
ensayo autorizado. No crearlo dos veces. El código se normaliza a mayúsculas y no
admite espacios; no se puede repetir dentro de una institución.

## 3. Deducción ocasional

Desde el mismo listado pulsa Nuevo concepto:

| Campo | Valor |
|---|---|
| Código | DESC-TEST |
| Nombre | Descuento autorizado de prueba |
| Descripción | Clasificación de prueba; no descuenta dinero ni recupera préstamos. |
| Tipo | Deducción |
| Aplicación | Ocasional |
| Activo | Marcado |

Revisa modal y confirma. La tabla debe indicar **Resta al empleado** y **Ocasional**.
No se captura porcentaje, saldo de préstamo ni monto legal de retención.

## 4. Aportación patronal

Desde el listado pulsa Nuevo concepto:

| Campo | Valor |
|---|---|
| Código | APORT-TEST |
| Nombre | Aportación de la escuela de prueba |
| Descripción | Costo de referencia de la escuela; no disminuye el pago del empleado. |
| Tipo | Aportación patronal |
| Aplicación | Habitual |
| Activo | Marcado |

Revisa y confirma. La tabla y el modal deben explicar **Costo de la escuela; no reduce
el pago del empleado**. No equivale a configurar una obligación o cálculo fiscal.

## 5. Ayudas, filtros y Excel

1. En listado enfoca **Concepto de nómina**: hasta diez opciones sin escribir.
2. Escribe `BON` y selecciona BON-PUNT-TEST; busca desde tres caracteres. No copies
   la etiqueta a Texto adicional: selecciona la sugerencia. × limpia sólo el concepto.
3. Combina **Estado Activos**, **Tipo Percepción** y **Aplicación Habitual**, y pulsa
   **Aplicar filtros**. Con el concepto seleccionado sólo debe quedar BON-PUNT-TEST.
4. Pulsa **Exportar Excel**: mismas filas y criterios; columnas código, nombre,
   tipo, efecto, aplicación, descripción y estado. No contiene importes.
5. Pulsa Editar del bono y después Volver al listado: conserva filtros/página.
6. **Limpiar** restablece todo. Filtra Deducción/Ocasional para localizar DESC-TEST.

Texto adicional es opcional para código, nombre o descripción. Las ayudas incluyen
inactivos identificados para consultar el historial. Escribir sin seleccionar una
sugerencia muestra validación en vez de consultar todos inadvertidamente.

## 6. Editar y desactivar sin borrar

En el listado selecciona BON-PUNT-TEST, aplica filtros y pulsa **Editar**. Cambia
Descripción a `Descripción actualizada del bono de prueba, sin pagos ni importes.`
Pulsa Guardar cambios; revisa modal y prueba Cancelar primero, luego confirma.

Después edita DESC-TEST y desmarca **Concepto activo**; confirma. Pulsa Limpiar y
filtra Estado **Inactivos**, Tipo **Deducción**, Aplicación **Ocasional**: debe
aparecer DESC-TEST como Inactivo. Excel debe coincidir. Se conserva el registro
y auditoría; no hay botón de borrado. Si se reactiva, vuelve a aparecer como Activo.

## 7. Validaciones y protección de cambios

- Intentar guardar sin código, nombre, tipo o aplicación: mensajes de campo, sin
  guardar ni perder datos. Las ayudas informativas no aparecen como errores.
- Intentar otro registro con código BON-PUNT-TEST (también en minúsculas): rechaza
  duplicado y conserva captura; Cancelar del formulario vuelve al listado sin alta.
- Abrir Editar del mismo concepto en dos pestañas: guardar un cambio en la primera;
  intentar confirmar otro desde la segunda debe pedir recargar, sin sobrescribir.
- Cuenta con lectura salarial y de Empleados/Laboral: consulta/Excel sí, Nuevo y
  Editar no. Sin autorización salarial no accede al menú ni a rutas/ayudas/Excel.
  No retirar derechos al administrador principal para probarlo.

## 8. Cierre

Sólo deben existir los conceptos del ensayo y sus cambios. No debe haber cambios
de sueldo, conceptos asignados a empleados, nóminas, descuentos aplicados, préstamos
ni movimientos financieros por estas capturas. La auditoría privada guarda antes,
después, actor, instante y operación; los registros no se eliminan físicamente.

El propietario confirmó este caso; incorporación editorial mediante V122 a Guía
de procesos. Siguiente entrega propuesta: analizar Conceptos por empleado, con
importes y vigencias, sin duplicar el sueldo base existente ni generar pagos todavía.
