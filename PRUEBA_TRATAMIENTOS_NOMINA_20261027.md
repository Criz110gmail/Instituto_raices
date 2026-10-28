# Pruebas propuestas — No aplicar, Posponer y pendientes de nómina

Estado: **pendiente de ejecución manual y confirmación del usuario**. Las pruebas
automatizadas aisladas no significan que el usuario ya haya aprobado estos casos.
No ingresar datos con herramientas ni alterar julio para preparar este ejemplo.

## Julio ya está cerrado; no repetirlo

El usuario confirmó julio, cancelación NPI-1 y nuevo pago NPI-2 por $14,187.15 el
28/10/2026. No incluir otra vez, crear borrador, cancelar ni pagar julio. El siguiente
ensayo ordinario es pago individual sin borrador (apartado7 del manual de flujo base);
después continuar estos tratamientos. Antes de usar agosto revisa si ese ensayo ocupó
su cobertura: elige otro periodo libre sin solapamientos si corresponde, ajustando el
cálculo por días reales. No usar importes de agosto a ciegas para otro mes.

## Qué incorpora esta entrega

- **Nóminas → Ver → Empleados → Ver desglose → Decisiones y pendientes de este
  periodo**. Listado de la copia original, tratamiento por concepto y auditoría.
- **Cambiar decisión** abre captura separada; Revisar muestra el neto inmediato;
  Confirmar abre un modal con Cancelar. Motivo obligatorio, versión y permisos.
- No aplicar: no genera percepción pendiente por lo excluido. No extingue préstamos
  ni otras deudas subyacentes. Posponer: conserva una obligación del periodo original.
- En **Pagos por empleado**, resumen separado de colectivo inmediato, pospuesto y
  no aplicado; filtros Pendiente/Pagado/Pospuesto/No aplicar y Excel equivalente.
- **Ver conceptos pospuestos** muestra las obligaciones complementarias por empleado
  y periodo, con filtro/Excel. No se suman a otro colectivo ni se recalculan.
- Un pago completo pospuesto se resuelve desde la fila **Pospuesto → Ver pago e
  intentos → Registrar resultado → Pagar desde una o varias cuentas**.
- Un pendiente complementario positivo permite Preparar pago individual, distribuir
  fuentes y confirmar. Cancelar ese pago recuperado reabre el mismo pendiente.

### Límites visibles, no funciones ocultas

Las deducciones pospuestas quedan con signo negativo como **recuperación pendiente**,
no como dinero para entregar. La liquidación especializada de una obligación neta
cero/negativa sigue pendiente: no genera egresos ficticios ni se mezcla con otro mes.
Cuotas de préstamos mantienen su flujo especializado, no se cambian desde estas
decisiones. Chequeras, rediseño de préstamos, recibos nuevos y permisos de operación
separados siguen fuera de esta entrega. Los nuevos accesos reutilizan los permisos
privados salariales/financieros existentes; no se conceden permisos a roles.

## Caso A — Excluir un concepto sin perder su configuración

Usar administrador persistido. Después de cerrar julio, comprobar que agosto no
tenga ya un borrador/reserva de Diana; si lo hay, no duplicarlo ni cancelarlo a ciegas.

1. **Personal y nómina → Periodos de nómina → Nuevo periodo**.
2. Código `AGO-2027-DEC-TEST`, inicio01/08/2027, fin31/08/2027, pago31/08/2027.
   Si existe un periodo de agosto compatible, reutilizarlo; no crear solapamientos.
3. Revisar y confirmar. Sin calendario.
4. **Personal y nómina → Nóminas → Nuevo borrador**. Elegir agosto, **Opciones de
   selección → Elegir empleados específicos → Preparar nómina del periodo**.
5. Filtrar Diana e **Incluir** sólo su fila. Resultado, si su configuración no cambió:

| Partida | Agosto |
|---|---:|
| Sueldo base | $14,000.00 |
| Transporte | $80.00 |
| Apoyo quincenal | $300.00 |
| EXTRA250 de julio | No corresponde en agosto |
| Deducción semanal proporcional | $442.86 |
| Neto original | **$13,937.14** |

6. Guardar como `Agosto — prueba de decisiones`, motivo `Revisar exclusiones y
   posposiciones sin modificar la configuración de Diana.` Anotar folio BOR.
7. En **Ver del borrador → Empleados → Ver desglose**, abrir **Decisiones y
   pendientes de este periodo**.
8. En Transporte, pulsar **Cambiar decisión**, seleccionar **No aplicar en este
   periodo**, motivo `No otorgar transporte en esta nómina de prueba.`
9. **Revisar decisión**: inmediato **$13,857.14**. Pulsar Confirmar tratamiento y
   probar **Cancelar** del modal: no debe guardar ni mover dinero.
10. Confirmar después. Ver Transporte80 como original, decisión No aplicar, motivo
    y evento con usuario/fecha; neto del desglose **$13,857.14**, Transporte0 aplicado.
11. En **Empleados → Diana → Configuración de nómina**, comprobar que Transporte80
    continúa configurado desdejulio. No modificarlo.
12. Regresar al mismo borrador y decisión de Transporte; elegir **Aplicar**, motivo
    `Restablecer transporte para continuar la prueba de posposición.` Confirmar.
13. Neto nuevamente **$13,937.14**, conservando los dos eventos de auditoría.
    Exportar decisiones a Excel y comprobar originales/tratamientos/motivos/historial.

## Caso B — Posponer300 de percepción y pagarlos después

Continuar sobre agosto, sin cambiar sueldo ni conceptos del empleado.

1. En **Nóminas → Ver agosto → Empleados → Ver desglose → Decisiones y pendientes
   de este periodo**, localizar el apoyo quincenal, cuyo cálculo del mes es300.
2. **Cambiar decisión → Posponer**, motivo `Entregar los300 del apoyo quincenal
   posteriormente, conservando el periodo de agosto.` Revisar y confirmar con modal.
3. Debe mostrar **a pagar ahora $13,637.14**, pospuesto300. No se movió dinero.
4. Volver al borrador y abrir **Pagos por empleado**. Revisar **Colectivo · a pagar
   ahora $13,637.14** y pospuesto300. No confundirlo con el neto original de13,937.14.
5. Anotar saldo actual de la cuenta elegida. **Preparar pago colectivo**: distribuir
   exactamente13,637.14 en una cuenta con fondos, o varias cuya suma sea esa cantidad.
   Capturar medio compatible, motivo financiero de egreso, referencia `AGO-DEC-TEST`
   y motivo `Pago inmediato de agosto; apoyo300 pospuesto.`
6. Revisar distribución, probar Cancelar en la confirmación y después confirmar.
   Sólo deben salir13,637.14. La nómina conserva pendientes; no se pagaron esos300.
7. **Pagos por empleado → Ver conceptos pospuestos**: filtrar Diana y Pendiente.
   Debe aparecer una obligación de **$300**, periodo original agosto. Exportar Excel:
   misma fila, periodo, importe y estado; buscar un nombre inexistente devuelve0filas.
8. Pulsar **Preparar pago individual**, probar Cancelar del modal y confirmar después.
   Se abre la distribución del pendiente: total **$300**, no sueldo base otra vez.
9. Completar fuente con300, motivo financiero compatible, referencia `AGO-APOYO-300`
   y justificación `Entrega posterior del apoyo pospuesto de agosto.`
10. Revisar y confirmar el pago. La cuenta disminuye300; pendiente aparece Pagado;
    nómina de agosto queda sin pendientes si no hay otras excepciones.
11. Total del empleado entre ambas operaciones: **13,637.14+300=13,937.14**.
    Confirmar que no se agregó ese importe a septiembre ni cambió la configuración.

## Caso C — Cancelar el pago del pendiente y volverlo a pagar

1. **Nóminas → Ver agosto → Pagos por empleado → Ver conceptos pospuestos**.
2. En la obligación300, **Ver liquidación → Ver pago e intentos → Registrar
   resultado**, usar **Cancelar pago** sólo si se recuperó realmente el dinero.
3. Capturar motivo financiero de ingreso, referencia `RECUP-APOYO-300`, motivo
   `Fondos recuperados para probar reapertura del apoyo pospuesto.` Confirmar.
4. Cuenta aumenta300 exactamente. Se conservan pago y compensación. La obligación
   original vuelve Pendiente300 y agosto vuelve a tener pendientes; el pago inmediato
   de13,637.14 sigue vigente, no se devuelve ni se duplica.
5. **Ver conceptos pospuestos → Pendiente → Preparar pago individual**; reutiliza
   la liquidación original. Nuevo pago con referencia distinta `REPOSICION-APOYO-300`.
6. Confirmar300: cuenta disminuye300, obligación Pagado, nuevo intento y conservación
   del anterior cancelado. Nunca vuelve a pagar13,637.14 ni cambia su periodo.

## Caso D — Posponer todo el pago del empleado

Usar septiembre independiente; no alterar el agosto aplicado.

1. Crear/reutilizar periodo `SEP-2027-DEC-TEST`,01/09/2027–30/09/2027,
   pago30/09/2027. Confirmar que no exista otra reserva de Diana para ese rango.
2. **Nóminas → Nuevo borrador → septiembre → Elegir empleados específicos**.
   Incluir sólo Diana. Referencia:14,000+80+300−428.57=**$13,951.43**.
3. Guardar y abrir **Empleados → Ver desglose → Decisiones y pendientes**.
4. **Decidir sobre todo el empleado → Posponer**, motivo `Posponer el pago completo
   de septiembre para entregarlo individualmente después.` Revisar: inmediato0;
   confirmar con modal. No combinar con decisiones individuales de conceptos.
5. Volver a **Pagos por empleado**: colectivo a pagar0, pospuesto13,951.43.
   No intentar financiar un colectivo de0. En **Decisiones y conceptos pospuestos**,
   capturar motivo y pulsar **Confirmar decisiones sin mover dinero**; probar
   Cancelar y confirmar después. Revisión congelada, sin cambios en cuentas.
6. Filtrar estado **Pospuesto**: una fila de Diana. **Ver pago e intentos → Registrar
   resultado → Pagar desde una o varias cuentas**.
7. Distribuir exactamente13,951.43, referencia `SEP-POSPUESTO-TEST`, motivo financiero
   de egreso y justificación. Revisar y confirmar: sólo esa salida, mismo periodo,
   pasa a pagado. No se suma a octubre.

## Controles adicionales

- Motivo vacío: no guarda. Cancelar/Escape del modal: sin POST definitivo.
- Abrir decisión en dos pestañas; guardar primera y confirmar segunda: debe pedir
  recargar sin sobrescribir ni duplicar eventos.
- Después de confirmar/aplicar: cambiar decisión por URL/POST no debe modificarla.
- Neto cero/negativo: bloquear la decisión y conservar importes/auditoría previos.
- Recalcular con decisiones explícitas activas: pide restablecerlas a Aplicar antes;
  no las descarta silenciosamente. Revisiones anteriores se conservan.
- Usuario sólo consulta salarial: ve información autorizada y Excel, no administra.
  Sin salario no accede por URL. Sin permiso financiero no paga ni cancela.
- Claro/oscuro, celular/escritorio: filtros alineados, acciones con borde discreto,
  formulario separado, tabla legible y sin desbordar la página.
- No marcar estos casos en la guía confirmada hasta la validación del usuario.
