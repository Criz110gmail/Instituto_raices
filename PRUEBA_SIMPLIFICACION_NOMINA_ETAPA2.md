# Captura integrada por empleado — etapa 2

Propuesta pendiente de aceptación humana; no publicar todavía como guía confirmada.
No repetir el egreso BOR-000001 ni usar a Carlos para altas duplicadas. Los ensayos
automatizados usaron otra base ficticia, nunca la base operativa.

## Dónde está

Personal y nómina → Configuración de nómina → buscar empleado → Configurar nómina.
También está dentro de Ver configuración. El listado conserva filtros, fecha y página;
la captura abre aparte. Cancelar y volver conservan ese contexto.

La pantalla tiene Puesto y sueldo, Calendario y Conceptos adicionales. Motivo y
confirmación quedan debajo de las secciones, con detalle previo y Cancelar en el modal.
Por defecto no vuelve a registrar sueldo ni calendario: activa sólo lo que cambie.

## Revisión sin modificar datos

1. Abrir Carlos existente. Consultar enero2027 y reconocer el sueldo12000 y bono500.
2. Abrir Configurar nómina: las opciones de cambiar sueldo/calendario están desmarcadas.
   No confirmar un nuevo sueldo para ver el diseño.
3. Revisar las tres pestañas en claro/oscuro y móvil. En Conceptos adicionales, agregar
   y quitar una fila sólo de captura. Eliminar una fila NO cancela un concepto guardado.
4. Volver sin guardar: los registros originales y la nómina aplicada permanecen iguales.
5. En Conceptos recurrentes se ven todos los registros y sus vigencias, incluso futuros.
   El resumen del listado sigue consultando únicamente la fecha indicada. El estado
   Próxima/Vigente/Anterior de cada concepto corresponde a hoy, no al corte del resumen.
6. Vigencias e historial reúne las condiciones salariales originales y cambios de
   calendario, paginados; auditoría/gestión avanzada conserva sus rutas anteriores.

## Ejemplo nuevo sólo si se autoriza preparar otro empleado de prueba

Usar empleado activo distinto, con ingreso laboral confirmado y puesto del catálogo.
No crea jornada ni plantilla de remuneración: sueldo se registra directamente en la
fuente salarial existente, sin un segundo concepto que duplique la percepción base.
La captura directa NO hereda jornada/horas de una plantilla anterior. Si se requiere
conservar ese esquema específico, consultar la herramienta histórica antes de cambiarlo.

1. Activar Puesto y sueldo: elegir puesto con ayuda10/3, importe12000, base mensual,
   frecuencia mensual, inicio01/01/2027 y fin31/12/2027, siempre hoy/futuro e ingreso válido.
2. Activar Calendario y elegir uno mensual activo. Es referencia ACTUAL, sin vigencia
   futura propia; todavía no restringe el generador por calendario (etapa3).
3. En Conceptos adicionales: bono500 desde01/01/2027; otra deducción250 desde01/06/2027,
   cada una con fecha final opcional. No llamarla préstamo: el control de saldo es etapa4.
4. Motivo claro. Revisar modal: empleado, cambios seleccionados, tipo, importes y fechas.
   Cancelar conserva captura. Confirmar guarda todo o nada, sin nóminas ni movimientos.
5. Consultar15/05/2027: deducción0;15/06/2027: deducción250. El sueldo sigue12000,
   percepción base única. La lista de programación muestra ambos conceptos completos.
6. Para cambiar un importe existente: Ver configuración → Conceptos recurrentes →
   Programar cambio. Elegir inicio posterior a hoy y al inicio previo; la vigencia previa
   termina el día anterior. No se modifica su importe histórico. Desde la captura de
   cambio no se puede convertir esa fila en otro concepto ni otro empleado.

Las fechas del ejemplo son válidas a la entrega24/10/2026. Si ya pasaron, usar fechas
futuras coherentes; nunca saltarse las validaciones para reescribir historia.

## Verificación técnica de esta entrega

Suite993 Java,115 JS, IT PostgreSQL17 de Configuración ampliado: sueldo directo,
calendario y dos conceptos en una transacción; deducción desde junio; cambio de bono
con cierre anterior; rechazo de segundo envío y referencia a otro empleado; fallo por
solapamiento después de intentar sueldo/calendario revierte TODA la transacción y sus
historiales. Render y POST reales, CSRF, lectura sin edición, captura conservada y retorno
con filtro/página. Dos pruebas nuevas de render/contrato incluidas en la suite993.
Sin migración nueva:V131 vigente/V132 libre. No cambia el generador ni una nómina aplicada.

QA visual con HTML real generado por pruebas y datos ficticios: escritorio1280 y
móvil390, temas claro/oscuro, filas dinámicas/renumeración/autocompletado/contexto,
formato monetario, modal legible y Cancelar conservando la captura sin enviar.
Sigue etapa3: conectar programación por periodo/calendario sin alterar historial.
