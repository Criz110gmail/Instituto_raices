# Preparación de nómina mensual — V128

Estado: implementación para probar. **La prueba del propietario aún no está confirmada.**
Esta etapa guarda borradores revisables, no autoriza ni paga nómina. No calcula
impuestos, faltas, horas extra, préstamos, porcentajes ni prorrateos. El importe
propuesto no debe interpretarse como neto fiscal o como pago realizado.

## 1. Revisar los datos que ya probaste, sin volver a registrarlos

Entra al administrador con cuenta institucional y permisos de Empleados, Datos
laborales y administración de configuración salarial. Un usuario de recuperación
no puede administrar este módulo; un maestro tampoco, aunque se le asignen permisos.

Ve a **Administración → Personal y nómina → Periodos de nómina**. Busca y abre
**ENE-2027-TEST · Enero de 2027 — revisión A**. Debe continuar Programado, del
01/01/2027 al 31/01/2027, pago previsto 31/01/2027 y fecha opcional para recibir
incidencias 28/01/2027. Su calendario NOM-MENSUAL-TEST debe estar Activo y Mensual.
No cambies las fechas ni crees otro enero.

Ve a **Asignaciones laborales**, selecciona a **Bruce Wayne · DOC-PRUEBA-02** en
la ayuda Empleado y aplica filtros. Comprueba la asignación de $12,000 MXN, base
Mensual/pago Mensual, inicio 01/01/2027 sin fin. Puede decir Próxima si aún no llega
enero; la preparación usa las fechas del periodo, no solamente la fecha de hoy.
La programación de $13,000 de febrero cancelada no debe participar.

Ve a **Conceptos por empleado**, filtra Bruce y comprueba BON-PUNT-TEST de $500 MXN
Habitual desde 01/01/2027 sin fin. La programación de $600 cancelada no participa.
Ve al expediente del empleado, Datos laborales: ingreso debe cubrir todo enero,
sin baja dentro de ese mes. No alteres estos registros ya aceptados para forzar casos.

**Resultado de referencia, si éstos son sus únicos conceptos activos en enero:**
sueldo $12,000 + bono $500 − deducciones $0 = **propuesto $12,500 MXN**.
Aportaciones de escuela $0. Si hay otros conceptos habituales vigentes, revisa el
desglose: el resultado cambia legítimamente; no los borres para hacer coincidir cifras.

## 2. Consultar empleados, todavía sin guardar

Ve a **Administración → Personal y nómina → Preparación de nómina**. Pulsa
**Nuevo borrador**. En Periodo programado da clic dentro del input: aparecen los
primeros diez. Escribe `ENE-2027-TEST` y selecciona la opción de la ayuda.
Escribir un texto sin seleccionar no identifica el periodo.

En Empleado selecciona Bruce. Pulsa **Consultar empleados**. Debe aparecer en
**Listos para preparar**, con los importes del punto 1. Verifica que no haya importes
editables; se consultan las configuraciones originales.

Los otros dos grupos explican casos distintos:

- **Requieren revisión:** ingreso/baja dentro del periodo, periodo parcial, cambio
  de sueldo o concepto a mitad del mes, moneda distinta o deducciones excesivas.
  No se asigna cero ni se calcula un prorrateo; no se pueden seleccionar.
- **No incluidos:** expediente inactivo, sin sueldo coincidente, frecuencia distinta,
  fuera de fechas laborales o ya incluido en otro borrador vigente coincidente.
  El motivo aparece por empleado.

Los conteos corresponden a los filtros actuales. No deben interpretarse como el
total institucional cuando buscaste sólo a Bruce. Exportar esta consulta a Excel
debe respetar Empleado, búsqueda y clasificación.

## 3. Selección y revisión previa sin cambios reales

En Listos marca solamente a Bruce. Nombre: `Preparación enero 2027 — prueba`.
Motivo: `Revisar sueldo mensual y bono habitual sin autorizar pagos.`
Pulsa **Revisar borrador**. Debes ver una pantalla previa con empleado, sueldo,
percepciones, deducciones, aportaciones patronales y propuesto.
Si sólo está la configuración del punto 1, la propuesta debe ser $12,500 MXN.

Pulsa **Volver a selección sin guardar**: nombre, motivo y selección deben conservarse.
No debe haberse creado un borrador. Vuelve a revisar. Pulsa **Guardar borrador**:
se abre un modal con periodo, empleados, importe y motivo. Pulsa **Cancelar**.
Debe cerrarse el modal sin registrar nada. Vuelve a pulsar y **Confirmar y guardar**.

La selección es de la página actual, máximo 100 empleados por borrador. Cambiar la
consulta o página la reinicia; el formulario lo indica. Para más empleados se pueden
crear otros borradores del mismo periodo con personas diferentes, sin duplicaciones.

## 4. Revisar la copia guardada

Después de confirmar se abre el detalle con folio interno `BOR-…`, estado Borrador
y revisión 1. El folio exacto depende de la secuencia; no se captura.

En **Resumen** revisa cobertura e importes. En **Empleados** pulsa **Ver desglose**:
debe mostrar sueldo base de $12,000 y bono de $500, sus fechas originales e importe
propuesto de $12,500. Regresa con **Volver al borrador**.
En **Empleados → Exportar esta revisión a Excel** comprueba hojas Empleados y Desglose.
La exportación usa esa revisión, aunque después cambien los catálogos.

En **Historial** debe aparecer registro con usuario, fecha/hora, motivo y revisión 1.
No debe haber pagos recibidos, egresos, autorizaciones fiscales ni movimientos
financieros por guardar este borrador. Las cuentas conservan exactamente su saldo.

## 5. Probar filtros, retorno y duplicado

Vuelve al listado. Selecciona calendario, periodo y empleado, estado Borrador,
fechas 01/01/2027–31/01/2027. Aplica filtros. Entra a Ver y vuelve: deben mantenerse.
Exportar Excel desde el listado debe usar esos criterios. Los rangos buscan cobertura
coincidente del periodo, no fecha de captura del borrador.

Abre Nuevo borrador, elige el mismo periodo y Bruce, consulta. Ya no debe ser Listo:
en **No incluidos** debe explicar que está incluido en un borrador vigente.
No prepares otro calendario con las mismas fechas para intentar duplicarlo:
la protección también impide duplicar al empleado entre calendarios coincidentes.

## 6. Actualización explícita y versiones

Ve a **Preparación de nómina → Ver tu borrador → Gestión → Revisar actualización**.
No basta con abrir esta vista para cambiar el borrador. Compara totales guardados
con propuesta actual. Si no cambió ninguna configuración, deben ser iguales.

Motivo: `Verificar nueva revisión conservando la copia anterior.` Pulsa
**Confirmar nueva revisión** y en el modal primero Cancelar: debe seguir revisión 1.
Repite y confirma. Debe quedar revisión 2; revisión 1 sigue accesible en Historial.
Ambas conservan sus propios empleados, importes, calendario, periodo y fechas.
Excel de revisión 1 debe seguir siendo igual al original.

La actualización conserva la selección original de empleados. Si alguno dejó de
ser elegible o requiere revisión, se bloquea toda la actualización; no se excluye
silenciosamente ni se cambia su importe a cero. Para otra selección se cancela el
borrador y se prepara uno nuevo. Cambiar configuraciones fuera del módulo nunca
modifica automáticamente una revisión guardada.

## 7. Dos pestañas y cancelación del borrador de prueba

Abre el detalle en dos pestañas **antes** de cancelar. En la primera ve a Gestión,
motivo `Cancelar solamente la preparación de prueba; conservar historial.` Pulsa
**Revisar cancelación**, luego Cancelar: el estado debe continuar Borrador.
Repite y confirma: debe quedar Cancelado, revisiones 1 y 2 conservadas.
En la segunda pestaña intenta la cancelación con su formulario anterior: debe avisar
que cambió en otra pestaña, sin otra gestión ni movimientos financieros.

Vuelve a Nuevo borrador y consulta enero/Bruce: vuelve a ser elegible porque la
cancelación liberó su inclusión, pero **no guardes otro** salvo que se indique.
El periodo enero sigue Programado; sueldo y bono siguen intactos. Febrero sigue
Cancelado. Las cuentas y los movimientos financieros no cambian en toda esta prueba.

## Cierre y pasos posteriores

Comunica qué puntos coincidieron y cualquier diferencia antes de incorporar este
recorrido como confirmado a la Guía de procesos. No se debe publicar una guía con
pruebas humanas todavía pendientes. La siguiente etapa se acordará después de esta
aceptación: incidencias, revisión/autorización, cálculos fiscales y pagos tienen
reglas propias; esta entrega no los implementa ni supone autorizados.
