# Nómina: fondos insuficientes en una de dos fuentes

Estado: **CONFIRMADA por el usuario el 29/10/2026**, incluida confirmación final
después de Cancelar del modal sin movimientos. BOR-000006/NPI-7 PAGADO5806.45:
CAJA1806.45 y banco4000; saldos finales10956.40/274.29/cajacorte1150.
No repetir ni cancelar este pago. V144 publica guía9pasos del caso concluido.
El recorrido original siguiente se conserva como referencia histórica, no retoma.
No ejecutar pagos con herramientas.
Casos de noviembre de Bruce ya terminados: no cancelar ni repetir BOR-000004/5 o NPI-6.

## Datos comprobados, sólo lectura, el 29/10/2026

Bruce/DOC-PRUEBA-02 tiene sueldo12000 mensual del01/11/2026 al31/12/2026.
Transporte80 termina30/11 y bono500 empieza01/01/2027; diciembre no tiene esos
conceptos. Sin periodo ni reserva de Bruce en diciembre. Motor real calcula
01/12–15/12:12000*15/31=$5806.45; no asumir6000 por llamarse quincena.
BBVA_COBROS4274.29, CAJA12762.85, CAJA-CORTE-TEST1150. Si cambian, recalcular
distribución y faltante; no ajustar saldos manualmente para forzar el ejemplo.

## Recorrido

1. Administración → Finanzas → Cuentas financieras: anotar saldos y último movimiento.
2. Personal y nómina → Periodos de nómina → Nuevo periodo: código
   DIC-2026-FONDOS-TEST, Inicio01/12/2026, Fin15/12/2026, Pago15/12/2026.
   Revisar periodo → confirmar modal. Reutilizar si ya existe; no superponer reservas.
3. Personal y nómina → Nóminas → Nuevo borrador: seleccionar periodo, Opciones de
   selección → Elegir empleados específicos → Preparar nómina del periodo.
   Buscar Bruce y marcar sólo su fila Incluir. Sueldo/neto5806.45; adicionales0,
   deducciones0. No guardar si no coincide: revisar configuración/fechas.
4. Nombre Diciembre — fondos insuficientes de Bruce; motivo Comprobar que una fuente
   sin fondos bloquee todo el pago sin afectar ninguna cuenta. Guardar borrador
   revisado → confirmar. Anotar folio automático.
5. Ver ese borrador → Autorización y pagos por empleado → Resumen → Preparar pago
   colectivo. Fuente1 CAJA806.45; Agregar otra cuenta, fuente2 BBVA_COBROS5000.
   Medios automáticos Efectivo/Transferencia, motivo financiero autorizado de egreso
   de nómina, referencia DIC-BRUCE-SIN-FONDOS-TEST y motivo de la prueba anterior.
6. Revisar distribución: debe bloquear banco, disponible4274.29/solicitado5000/
   faltan725.71, No se movió dinero. No debe abrir revisión válida ni confirmar pago.
7. Cuentas y movimientos: tres saldos intactos, ningún egreso nuevo de ese borrador.
   Nómina sigue Borrador, sin pago registrado. La existencia del borrador sí es normal.
   Este ensayo prueba validación previa, no carrera de saldos entre revisión/confirmación.
8. En el mismo formulario corregir CAJA1806.45 y banco4000; no crear otro borrador.
   Revisar distribución: total5806.45, caja posterior10956.40 y banco274.29 (estimados).
9. Confirmar y registrar pagos abre modal. **Pulsar Cancelar dentro del modal**.
   No confirmar pago definitivo. Cuentas12762.85/4274.29/1150 y sin movimientos nuevos.
10. Conservar el borrador pendiente; reportar aviso, saldos y cancelación del modal.
    No eliminar ni cancelar el borrador: servirá para el siguiente ensayo si se acuerda.

No declarar probado el rollback durante una concurrencia por este resultado. Ese
escenario y la aplicación final son pruebas independientes todavía pendientes.
