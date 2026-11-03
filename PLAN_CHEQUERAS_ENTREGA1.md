# Catálogo de chequeras — primera entrega implementada

## Continuidad V153 — 03/11/2026

Integrado pago nominativo por empleado con chequera/folio/fecha/beneficiario,
revisión protegida y modal Cancelar. Emisión consume folio y registra egreso
atómicamente; cancelación exige no cobrable y fondos recuperados/liberados,
devuelve a las fuentes originales y conserva el folio sin reutilización.
Colectivo sigue efectivo/transferencias; cheques se entregan por empleado.
No imprime ni transmite al banco. Sin cambios automáticos de permisos o saldos.
Prueba humana pendiente: PRUEBA_NOMINA_CHEQUE_20261103.md. Catálogo ya aprobado
no debe recapturarse. Después: rediseño de préstamos y recibos según plan vigente.

## Punto de partida

La prueba de neto cero directo de Diego terminó y fue aprobada el 03/11/2026.
BOR-000015 no debe modificarse ni repetirse. Catálogo implementado con V152.
El usuario confirmó la prueba funcional: PRUEBA_CHEQUERAS_ENTREGA1_20261103.md.
Pendiente revisar los ajustes visuales solicitados después de esa confirmación.
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

## Seguridad aprobada por el usuario — 03/11/2026

Nombres aprobados: **Chequeras - Consultar** y **Chequeras - Administrar**.

- Consultar: ver chequeras, rangos, estados e historial, buscar y exportar Excel;
  no permite modificar datos.
- Administrar: además de consultar, crear, editar los datos permitidos y activar
  o desactivar chequeras, con confirmación y auditoría.

Ninguno autoriza por sí solo emitir pagos o cancelarlos. Una emisión ligada a nómina
también requerirá el permiso de pagos y acceso a la cuenta bancaria correspondiente.
No conceder permisos automáticamente a roles existentes. Validación en servidor
para pantalla, URL directa, ayuda y Excel, con alcance institucional y de plantel.
Permisos implementados como CHEQUERA_LEER y CHEQUERA_ADMINISTRAR, en paquetes de
roles separados para impedir que seleccionar Consultar conceda Administrar.
El usuario debe asignarlos expresamente y renovar sesión; no se cambian sus roles
ni se conceden accesos automáticamente al desplegar.

## Qué no hace aún esta primera entrega

Crear o editar una chequera no mueve dinero, reserva cheques ni paga nómina.
La siguiente entrega integrará cheque/beneficiario/fecha/importe y folio con el
pago, consumiéndolo atómicamente. La emisión descontará disponibilidad interna
una sola vez, sin afirmar que el banco ya cobró el cheque. Cancelar conservará
folio y motivo, sin reutilización automática y sin devoluciones ficticias.

El rediseño de préstamos se mantiene como entrega posterior e independiente.
