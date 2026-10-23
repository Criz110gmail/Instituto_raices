# Configuración de nómina por empleado — V131

Prueba humana pendiente. Esta entrega organiza las fuentes existentes y registra
el calendario actual del empleado. No calcula, aplica ni paga nómina.

## Preparación: conservar lo que ya probaste

Utiliza **Bruce Wayne · DOC-PRUEBA-02**, sin volver a crearlo ni modificar su sueldo,
beca, bono, asignaciones o borrador anterior. En el caso aceptado tiene sueldo mensual
$12,000 y bono BON-PUNT-TEST $500 desde enero de 2027. La asignación histórica de
enero conserva 30 horas semanales aunque después se haya editado el catálogo.

Necesitas una cuenta administrativa institucional con lectura de Empleados, Datos
laborales y Condiciones salariales. Para asignar calendario necesita además administrar
Condiciones salariales. No se otorgan permisos nuevos automáticamente. No requiere
permiso de movimientos financieros y no admite cuenta de recuperación o portal.

## 1. Abrir la configuración

1. Entra en **Administración → Personal y nómina → Configuración de nómina**.
2. En **Empleado**, haz clic dentro del campo: deben aparecer hasta diez sugerencias.
   Escribe `Bruce` y selecciona **DOC-PRUEBA-02 · Bruce Wayne**.
3. En **Consultar sueldo y conceptos al**, captura **15/01/2027**. Es importante:
   la fecha de hoy es anterior a las configuraciones de enero, por eso consultar hoy
   podría mostrar Sin sueldo y ningún bono, sin que se hayan perdido registros.
4. Deja **Configuración → Todos** y pulsa **Aplicar filtros**.
5. Debe aparecer Bruce con sueldo $12,000 y percepción fija $500, si no se agregaron
   otros conceptos al caso. El calendario inicialmente dirá **Por asignar**.
   Las deducciones reflejan los conceptos reales; no asumir cero si hay otros ensayos.
6. Pulsa **Ver configuración**. La pantalla tiene **Resumen**, **Conceptos recurrentes**
   e **Historial**. Volver al listado debe conservar empleado, fecha y demás filtros.

También puedes entrar desde **Empleados → Ver expediente → Condiciones salariales → Abrir
configuración de nómina**; este acceso sólo aparece a quien puede consultar salarios.

## 2. Revisar sueldo, jornada y cambios sin duplicarlos

1. En **Resumen → Puesto y sueldo asignados**, verifica el puesto histórico,
   jornada, 30 horas semanales, $12,000 MXN y base/frecuencia Mensual de enero.
2. Pulsa **Ver asignaciones y cambios programados**: abre Asignaciones laborales
   filtrado por Bruce, donde se conservan las programaciones y cancelaciones previas.
3. No registres otra asignación para esta prueba. Regresa a Configuración de nómina.
4. La consulta no debe haber creado sueldos, borradores ni movimientos financieros.

## 3. Asignar el calendario con confirmación

1. En el **Resumen** de Bruce, pulsa **Asignar o cambiar calendario**.
2. En **Calendario activo**, enfoca el campo: muestra diez sugerencias; escribe
   `NOM-MENSUAL-TEST` y selecciona **Nómina mensual de prueba**, el calendario activo
   del ejemplo anterior. No selecciones un periodo como ENE-2027-TEST: aquí se asigna
   el calendario, no una nómina particular.
3. En **Motivo obligatorio**, captura:
   `Asignación del calendario mensual de Bruce para organizar su configuración de nómina.`
4. Pulsa **Revisar y guardar calendario**. El modal debe mostrar empleado, calendario
   anterior, nuevo calendario y motivo, además de **Cancelar** y **Confirmar y guardar
   calendario**.
5. Primero pulsa **Cancelar**. Debe cerrar el modal y conservar la captura, sin guardar.
6. Abre de nuevo el modal, revisa y confirma. Debe regresar a la configuración con
   mensaje de guardado y mostrar el calendario mensual asignado.
7. En **Historial**, comprueba fecha/hora `dd/MM/yyyy HH:mm`, usuario, anterior
   **Sin calendario**, nuevo **NOM-MENSUAL-TEST · Nómina mensual de prueba** y motivo.
8. El sueldo sigue $12,000, el bono $500 y el borrador anterior conserva su revisión.
   Ninguna cuenta financiera ni movimiento cambia.

El calendario es una **referencia actual**, no una programación futura por fechas.
Cambiar la fecha de consulta sólo cambia sueldo/conceptos mostrados; no cambia el
calendario asignado. En esta entrega el generador mensual sigue sus reglas anteriores:
la nueva referencia no excluye empleados de borradores ni convierte sueldos.

## 4. Revisar conceptos y vigencias

1. Abre **Conceptos recurrentes**. Debe aparecer BON-PUNT-TEST con su importe $500,
   tipo Percepción, inicio 01/01/2027 y el fin real configurado, posiblemente Sin fecha
   de fin. No se ha creado una copia del bono.
2. **Ver concepto** abre su detalle original. **Gestionar conceptos e historial**
   abre Conceptos por empleado filtrado por Bruce y por la fecha consultada.
3. **Asignar concepto recurrente** abre el formulario existente con Bruce ya
   seleccionado. Para este ensayo no lo guardes: no repitas BON-PUNT-TEST.
4. Las modificaciones de importe, término o cancelación se siguen haciendo desde
   Conceptos por empleado, con sus confirmaciones y vigencias existentes.
5. Regresa al listado Configuración de nómina y consulta **01/12/2026**. El bono que
   empieza en enero no debe aparecer. El sueldo mostrado será el vigente en diciembre;
   no exijas que sea cero porque Bruce ya tenía una condición anterior.
6. Vuelve a **15/01/2027**: reaparece el bono de $500. No se modificó ninguna fecha.

Los conceptos se consultan por el día elegido, no se calcula un total del periodo.
Un concepto que sólo cubre parte de un mes todavía requiere revisión al preparar
nómina. No convierte automáticamente un importe mensual a quincenal o semanal.

## 5. Filtros y Excel

1. En el listado, selecciona Bruce, fecha **15/01/2027** y calendario
   **NOM-MENSUAL-TEST**; pulsa **Aplicar filtros**.
2. Pulsa **Exportar Excel**. Debe contener sólo los empleados que coinciden con esos
   filtros, no todo el personal. Bruce debe conservar sueldo $12,000 y percepción $500
   del caso, con columnas de fecha consultada, moneda, base y frecuencia.
3. Cambia **Configuración → Sin calendario**: Bruce no debe aparecer después de la
   asignación. **Con calendario** lo incluye. **Sin sueldo en la fecha consultada**
   se refiere a la fecha elegida, no a que todo su historial esté vacío.
4. Pulsa **Limpiar**: restablece fecha de hoy y todos los empleados autorizados.
5. Revisa **Ver → Volver al listado** con tus filtros. La tabla muestra como máximo
   25 empleados por página y Excel recorre todos los resultados filtrados por bloques.

## 6. Cambio de calendario y protección de dos pestañas

Sólo si deseas probar un cambio, crea un calendario adicional de capacitación:
**Personal y nómina → Calendarios de nómina → Nuevo calendario**, código
`NOM-CONFIG-TEST`, nombre `Mensual alterno de configuración`, frecuencia **Mensual**,
Activo. Revisa y confirma con el modal existente. No necesita crear periodos.

1. Abre **Configuración de nómina → Bruce → Asignar o cambiar calendario** en dos
   pestañas antes de guardar nada.
2. En ambas selecciona el calendario alterno y motivo:
   `Cambio de prueba para verificar el historial y la protección entre pestañas.`
3. Guarda y confirma sólo la primera pestaña. Debe cambiar el calendario, sin dinero.
4. Guarda la segunda sin recargarla. Debe mostrar que la configuración cambió y pedir
   recargar; no debe crear un segundo historial del mismo cambio.
5. Vuelve a abrir el formulario actualizado y restaura **NOM-MENSUAL-TEST** con motivo:
   `Restaurar calendario mensual original después de verificar la protección entre pestañas.`
6. En **Historial**, deben verse asignación inicial, cambio y restauración, cada uno
   con usuario/fecha/motivo y nombres originales. No se borran registros anteriores.

## 7. Permisos y cierre esperado

- Usuario de lectura salarial: puede consultar y exportar, no asignar calendarios.
- Usuario sin lectura laboral/salarial: no ve el módulo ni accede por URL o Excel.
- Docentes, tutores y soporte de sus portales no reciben acceso a esta configuración.
- Un calendario inactivo no puede asignarse; si después se desactiva, el vínculo se
  conserva y la consulta informa Inactivo. No se oculta su historial.
- Diferencias entre frecuencia salarial y calendario muestran una advertencia; no
  convierten importes ni producen pagos.

Resultado final: Bruce conserva sueldo/bono/asignaciones/borrador anteriores, tiene
calendario mensual actual e historial auditable, filtros/Excel coherentes y ninguna
cuenta financiera modificada. Reporta estos resultados para publicar después la guía
confirmada. Ocasionales, porcentajes, incrementos, cálculo semanal/quincenal y pago
multicuenta siguen pendientes; este ensayo no aplica ninguna nómina.
