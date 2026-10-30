# No aplicar todo el pago de un empleado

Estado: **confirmado por el usuario el 30/10/2026**. Guía integrada V147.

## En palabras sencillas

«Este pago no corresponde en este periodo». Se guarda un motivo, no sale dinero
y no queda pendiente para después. Posponer, en cambio, significa «se lo pagaré
después». Si todos fueron excluidos, al confirmar las decisiones queda
**Confirmada sin pagos**, no como borrador. No se borra la configuración del empleado.
Esta acción registra una decisión autorizada; no determina obligaciones laborales.

## Evidencia del caso

- BOR-000010: Preparación · DIC-2026-DOS-PESTANAS-TEST.
- Luis Prueba Posposición, empleado 8 / fila 11.
- Cobertura 16–31/12/2026. Cálculo original $557.42:
  sueldo $516.13 + transporte $41.29, deducciones $0.
- Todo el pago del empleado → No aplicar en este periodo, con motivo.
- Confirmar decisiones sin mover dinero: usuario ve Confirmada sin pagos.
- Lectura técnica: tratamiento NO_APLICAR, neto efectivo0; sin intentos de pago
  ni pendientes complementarios de BOR10. Estado interno PAGADA no significa un
  pago registrado; prevalece la distinción de negocio confirmada sin pagos.
- El usuario confirma Ver conceptos pospuestos vacío y ningún movimiento financiero.
- CAJA $3,762.85; BBVA_COBROS $274.29; caja de corte $1,150.00, sin cambios.
- Sueldo mensual1000 y transporte80mensual de diciembre conservados. Apoyos10/11
  EXTRA-RED-TEST de10 ambos cancelados. No tocar BOR9 ni pagos anteriores.
- No afirmar aceptación humana separada de Cancelar en modal: hubo confusión durante
  el ensayo; el cierre confirmado es la exclusión definitiva sin pagos ni pendientes.

## Organización solicitada

Nóminas → Ver → Detalle por empleado → Ver desglose → Decisiones y pendientes de
este periodo tiene pestañas Resumen, Conceptos e Historial de decisiones.
Resumen muestra qué se paga/pospone; Conceptos contiene las partidas y Cambiar
decisión; Historial conserva usuario, fecha, acción legible y motivo, con paginación.
Los enlaces de paginación regresan a Historial, sin ocultarlo al volver.

Pagos por empleado y Conceptos pospuestos conservan posición al aplicar filtros.
Restauración después de la carga y pageshow, sin mover una pestaña oculta ni bloquear
la consulta si el navegador impide sessionStorage; script versionado evita caché vieja.

## Qué sigue

Permisos de nómina confirmados por el usuario el 30/10/2026: prueba sin salarios,
pruebas con salarios/configuración, retirar sólo configuración y restaurarla;
pantallas, enlaces y Excel protegidos. Guía V148. No repetir sin necesidad.
Sigue nómina con varios empleados y cancelar sólo uno de sus pagos; después empleado
sin sueldo base con conceptos y bloqueo de neto cero/negativo. Ver
RETOMAR_NOMINA_20261030.md antes de preparar datos en otra computadora.
