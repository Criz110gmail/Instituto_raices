# Duplicación de pago de nómina desde dos pestañas

Estado: **CONFIRMADA por el usuario el 29/10/2026**. BOR-000007/NPI-8,
único egreso6193.55 desdeCAJA a15:21; saldo10956.40→4762.85, banco274.29 y
corte1150 intactos. B bloqueó con La nómina cambió. Recarga antes de revisar;
no se movió dinero. Historial sóloNPI-8 Pagado, referenciaDIC-BRUCE-DOBLE-A.
No repetir ni cancelar este pago. Guía integrada de este caso por publicar;
recorrido original conservado abajo como evidencia, no como retoma pendiente.
No operar pagos con herramientas. Este cierre no afirma transacciones simultáneas.

## Punto de partida comprobado

Bruce conserva sueldo12000 mensual aplicable hasta31/12/2026. Transporte termina
30/11 y bono empiezaenero. Primera quincena diciembre ya pagada BOR-000006/NPI-7;
no repetirla. En lectura29/10 no existe periodo/reserva para16–31/12/2026.
Neto por díasreales:12000−5806.45=6193.55. CAJA10956.40/banco274.29/corte1150.
Si cambiaron saldos/coberturas, detenerse y revisar, no ajustar para eludir reservas.

## Preparar una sola nómina

1. Personal y nómina → Periodos de nómina → Nuevo periodo:
   DIC-2026-DOS-PESTANAS-TEST, inicio16/12/2026, fin31/12/2026, pago31/12/2026.
   Revisar periodo → confirmar. Reutilizar si ya existe, no duplicar.
2. Personal y nómina → Nóminas → Nuevo borrador → seleccionar ese periodo →
   Opciones de selección → Elegir empleados específicos → Preparar nómina del periodo.
3. Detalle por empleado: buscarBruce, Aplicar filtros y pulsar Incluir en sufila.
   Confirmar1incluido y6193.55, sin adicionales/deducciones. Guardar borrador revisado:
   nombre Diciembre — prueba de dos pestañas de Bruce, motivo Verificar que dos
   confirmaciones del mismo borrador sólo registren un pago. Confirmar y anotarBOR.

## Preparar dos revisiones antes de pagar

4. Abrir Ver del MISMO borrador en dospestañasA/B, misma sesiónadministrador.
   No crear otra nómina ni usar Pago individual independiente.
5. En A: Autorización y pagos por empleado → Resumen → Preparar pago colectivo.
   Sólo fuente CAJA6193.55, medioEfectivo automático, motivo financiero egreso nómina,
   referencia DIC-BRUCE-DOBLE-A, motivo Prueba de primera confirmación del mismo
   borrador desde dos pestañas. Revisar distribución, sin confirmar todavía.
6. En B repetir desde el mismoBOR: CAJA6193.55, referencia DIC-BRUCE-DOBLE-B,
   motivo Prueba de segunda confirmación de una revisión que quedará obsoleta.
   Revisar distribución ANTES de pagarA; no duplicar sólo una captura ya confirmada.
   Ambas propuestas deben mostrar6193.55 y saldoestimado4762.85.

## Confirmar A y después B

7. A → Confirmar y registrar pagos → confirmar modal una vez.
   Debe pagar6193.55, cuentaCAJA4762.85; banco274.29/corte1150 sin cambios.
8. B sin recargar ni volver a revisar → Confirmar y registrar pagos → confirmar
   modal. Servidor debe bloquear revisión obsoleta/nómina ya pagada, sin segunda
   salida. No exigir frase exacta: puede señalar nómina cambió o ya no tiene pagos.
   Si aparece error500, o permite otro pago, detenerse/reportar; no compensar manualmente.
9. Cuentas: CAJA4762.85, no saldo negativo ni otro descuento; otras intactas.
   Movimientos financieros: exactamente1egreso6193.55 de eseBOR. Nóminas → VerBOR →
   Autorización y pagos por empleado → Pagos por empleado → Ver pago e intentos →
   Historial de intentos: sólo1pago vigente y una fuenteCAJA. IntentoB no otro pago.
10. Conservar pago y evidencia; NO cancelar/repetir. Reportar mensaje exactoB,
    conteo de egresos y saldos. Dos confirmaciones secuenciales desde páginas
    obsoletas no equivalen a una carrera de transacciones simultáneas real.
