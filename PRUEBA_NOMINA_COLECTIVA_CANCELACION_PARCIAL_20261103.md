# Nómina colectiva: cancelar sólo un pago

Estado: **en proceso**, falta volver a pagar a Pablo y recibir confirmación humana
del cierre. No repetir el pago colectivo ni cancelar el de Marta.

## Punto actual verificado sólo en lectura

- BOR-000011, Noviembre — pago colectivo Marta y Pablo.
- Periodo NOV-2026-CIERRE-TEST, 16–30/11/2026. $500 por empleado, total $1,000.
- Marta, EMP-MULTI-TEST-01, empleado9/fila12: NPI-10 PAGADO por $500.
- Pablo, EMP-MULTI-TEST-02, empleado10/fila13: NPI-11 cancelado (interno REVERTIDO).
- Nómina parcialmente pagada: Marta intacta, Pablo pendiente $500.
- CAJA $3,262.85; BBVA_COBROS $274.29; CAJA-CORTE-TEST $1,150.00.
- No modificar BOR9, BOR10 ni pagos terminados de otros empleados.

## Mejora solicitada antes de retomar

Resumen de Autorización y pagos por empleado ya no repite el botón Ver pagos por
empleado; se usa su pestaña. Registrar resultado del empleado pendiente conserva
únicamente Pagar desde una o varias cuentas como camino de pago. Se retiraron la
captura antigua de una cuenta y su confirmación duplicada de la pantalla; no se
eliminaron servicios históricos ni se modificaron reglas de dinero o permisos.
Rechazar sin salida y Cancelar sólo este pago siguen disponibles cuando corresponda.

Filtrar, Buscar, Consultar y Limpiar conservan posición relativa en los formularios
GET de Personal y nómina marcados, incluidas nóminas, pagos por empleado, periodos,
puestos, empleados, conceptos, simulador y consultas de vigencia. Limpiar conserva
la pestaña; no desplaza si ya está limpio, respeta Ctrl/clic y no interviene en POST,
Excel, enlaces externos o pantallas ajenas. Offset negativo ya no se fuerza a16px.
JS compartido nomina-filtros.js?v=20261103, separado de operaciones de nómina.

## Retomar exactamente aquí

1. Nóminas → Ver BOR-000011 → Autorización y pagos por empleado → Pagos por empleado.
2. Buscar Pablo, seleccionar sugerencia y aplicar filtros. Debe estar Pendiente $500.
3. Ver pago e intentos → Registrar resultado → Pagar desde una o varias cuentas.
4. Una fuente: CAJA $500, Efectivo automático, motivo financiero NOMINA · Nómina.
   Referencia REPAGO-PABLO-NOV-01, motivo Nuevo pago de Pablo después de comprobar
   la cancelación individual. Revisar distribución → confirmar y modal.
5. Antes de confirmar comprobar total sólo $500 y únicamente Pablo, nunca Marta.
6. Después, Pablo debe tener nuevo pago vigente y conservar el cancelado. Marta
   sigue con NPI-10 vigente, sin otra salida por ella. CAJA esperada $2,762.85,
   banco274.29/corte1150 intactos si no hubo otras operaciones.
7. No marcar esta expectativa como confirmada hasta que el usuario la verifique.

Después faltan: empleado sin sueldo base con conceptos (Sofía) y bloqueo de cero/
negativo (Diego). Antes de dar pasos revisar qué capturas ya existen. Estos casos
todavía no están confirmados. No registrar pagos de prueba por herramientas.
