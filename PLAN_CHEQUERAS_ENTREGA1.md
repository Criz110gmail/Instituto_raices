# Siguiente entrega propuesta: catálogo de chequeras

## Punto de partida

La prueba de neto cero directo de Diego terminó y fue aprobada el 03/11/2026.
BOR-000015 no debe modificarse ni repetirse. Chequeras todavía no está implementado.
Este documento detalla el siguiente alcance, no acredita funcionalidad disponible.
Referencia: PLAN_REDISENO_NOMINA_20261026.md, sección9.

## Qué verá el usuario

En una cuenta bancaria, una pestaña **Chequeras** permitirá consultar las chequeras
ligadas a esa cuenta. No aparece en cajas. No habrá una cuenta financiera de tipo
Cheque ni un saldo independiente: el dinero pertenece a la cuenta bancaria.

El listado tendrá filtros alineados, estado, paginación, Excel y acciones discretas.
**Nueva chequera** y **Editar** abrirán formularios separados. Guardar mostrará resumen
y modal con Cancelar; errores conservarán los datos y volver conservará los filtros.
Respetar temas claro/oscuro, autocompletado10/3 donde corresponda y posición de filtros.

## Datos iniciales del catálogo

- Cuenta bancaria de origen, fija desde la cuenta consultada.
- Nombre para identificar la chequera.
- Folio inicial y folio final, inclusivos.
- Estado activa/inactiva.
- Motivo de registro o cambio y auditoría de usuario/fecha.

Validar cuenta bancaria, institución y alcance de plantel; rango válido y no
superpuesto con otras chequeras de la misma cuenta, incluso inactivas. Dos pestañas
no pueden crear rangos incompatibles. Conservar auditoría, no eliminar historia.
No presentar folios como emitidos mientras no exista una emisión registrada.

## Seguridad pendiente de concretar antes de implementar

El plan acordado dejó el permiso de chequeras por definir. Recomendación:
**Chequeras — Consultar** y **Chequeras — Administrar**, independientes de registrar
pagos, además del acceso autorizado a su cuenta. No conceder permisos automáticamente
a roles existentes. Validación en servidor para pantalla, URL directa, ayuda y Excel.

## Qué no hace aún esta primera entrega

Crear o editar una chequera no mueve dinero, reserva cheques ni paga nómina.
La siguiente entrega integrará cheque/beneficiario/fecha/importe y folio con el
pago, consumiéndolo atómicamente. La emisión descontará disponibilidad interna
una sola vez, sin afirmar que el banco ya cobró el cheque. Cancelar conservará
folio y motivo, sin reutilización automática y sin devoluciones ficticias.

El rediseño de préstamos se mantiene como entrega posterior e independiente.
