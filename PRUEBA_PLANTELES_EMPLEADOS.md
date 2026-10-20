# Planteles de empleados: organización y cambio de principal

## Lo confirmado por el propietario el 20/10/2026

Se confirmó la tabla final del recorrido manual: A principal y B adicional del
01/10/2026 al 19/10/2026; B principal y A adicional desde el 20/10/2026 sin fin.
También reportó el bloqueo de dos principales simultáneos y después pudo continuar.
No repetir altas ni finalizar otra vez esas cuatro asignaciones. Después se confirmó
el cambio programado A principal/B adicional desde21/10 y el propietario aprobó la
claridad del aviso y de la vista. Recorrido principal cerrado el20/10/2026; quedan
independientes baja laboral, permisos privados y variantes no ensayadas.

## Organización de la pestaña

Desde el ajuste del20/10 se muestra arriba un resumen del principal actual y del
próximo principal con fecha efectiva. En la revisión de sólo lectura del ejemplo
EMP-ADM-PRUEBA-01 se encontró el cambio ya guardado: Parian principal/B adicional
desde21/10 y las etapas anteriores finalizadas20/10. Por eso intentar elegir de nuevo
Parian para21/10 devuelve que ya es principal. No repetir la operación ni cancelarla.
Hasta20/10 las tarjetas actuales muestran B principal; las nuevas están en Próximas.
El aviso aclara que no hace falta otra confirmación. Posteriormente el propietario
confirmó «sí ya quedó bien» y solicitó documentar. Esa aceptación visual no prueba
variantes adicionales ni anticipa la observación humana del día siguiente.

En Administración → Personal y nómina → Empleados busca al empleado existente,
abre su expediente y entra a Planteles.

- Asignaciones actuales: cubren hoy según la zona horaria de la institución;
  primero principal, después adicionales.
- Próximas asignaciones: comienzan después de hoy. Sección desplegable.
- Ver asignaciones anteriores: terminaron antes de hoy. Cerrada al entrar; el tipo
  Principal/Adicional indica lo que fue en esa etapa, no su función actual.
- Cada grupo consulta páginas de20 registros; no se carga todo el historial.
- Finalizar esta asignación está dentro de cada tarjeta aplicable, cerrado al
  inicio. No se necesita usarlo para el nuevo cambio automático de principal.

## Procedimiento del asistente — recorrido principal documentado

Este ejemplo deja datos e historial: úsalo sólo en capacitación o con autorización.
No lo ejecuta el agente. Reutiliza el empleado y sus asignaciones de la prueba anterior.
El caso ya guardado se consulta, no se repite; los pasos de captura se conservan como
manual para nuevas operaciones autorizadas. Cancelación/concurrencia también tienen
cobertura técnica, sin presumir aceptación humana independiente de cada variante.

1. Administración → Personal y nómina → Empleados → localizar empleado → expediente
   → Planteles. Comprueba B principal y A adicional en Asignaciones actuales.
2. Abre Cambiar plantel principal.
3. Fecha efectiva: 21/10/2026 si hoy es20/10/2026. Si lo pruebas más tarde, elige una
   fecha futura posterior al inicio de ambas asignaciones. No se permite retroactividad.
4. Nuevo plantel principal: enfoca la ayuda, selecciona A entre hasta10 opciones o
   busca desde3caracteres. No basta con escribir su nombre sin seleccionar resultado.
5. Mantén marcada Continuará en el plantel anterior como adicional.
6. Motivo: Reorganización administrativa: el plantel A será principal y el B
   continuará como adicional.
7. Pulsa Visualizar cambio de principal. Revisa el modal: principal reemplazado B;
   nuevo principal A; desde21/10/2026; cierre anterior20/10/2026; B continuará como
   adicional. Si las vigencias tienen fin previsto, también se muestra su fin.
8. Pulsa Cancelar. No debe guardar, debe conservar los campos y las tarjetas deben
   permanecer iguales. Escape o clic en el fondo también cancelan, sin POST.
9. Vuelve a Visualizar cambio de principal y, sólo si coincide, pulsa Confirmar
   cambio de principal. Se registra todo junto o no se registra ningún cambio.
10. Como la fecha es futura, hasta20/10 las etapas actuales siguen vigentes y las dos
    nuevas aparecen en Próximas asignaciones: A principal/B adicional desde21/10.
    Desde21/10 pasarán a actuales y las etapas que terminan20/10 irán al historial.
11. No cambies el reloj ni edites fechas por BD para verificar el paso del día.
    Comprueba las fechas guardadas y el orden de las secciones; el cambio temporal
    se ha cubierto técnicamente y puede observarse después sin registrar otra operación.
12. Con permiso de Auditoría, Administración → Seguridad y soporte → Auditoría:
    busca la acción EMPLEADO_PRINCIPAL_CAMBIADO; debe conservar usuario, motivo,
    fecha de operación, fecha efectiva y planteles. No crea cuentas, grupos o sueldos.

Resultado si ambas etapas previas no tenían fin: seis asignaciones en total.
Dos originales finalizadas19/10, dos de la etapa siguiente finalizadas20/10 y dos
nuevas desde21/10 sin fin. No deben existir seis asignaciones vigentes simultáneas.

## Reglas para no confundir al usuario

- Si desmarca Continuará..., el plantel anterior termina y no se crea una nueva
  adscripción adicional en él. No lo desmarques en el ejemplo anterior.
- Puede seleccionar otro plantel activo de la institución aunque todavía no tenga
  una adscripción; si ya es adicional, el sistema conserva esa etapa y registra la nueva.
- Debe existir un principal para la fecha efectiva. Si no hay, primero configura la
  asignación inicial con Agregar plantel laboral.
- No se cambia al mismo que ya es principal para esa fecha.
- No se puede partir una asignación el mismo día que comienza porque dejaría una
  etapa anterior inválida. Elige al menos el día siguiente; no se elimina su historia.
- Si se cruza con otras asignaciones programadas se rechaza, sin alterar sus fechas.
- Si otra persona cambia las asignaciones tras visualizar, vuelve a visualizar.
- Cada confirmación valida permisos, institución, versiones y solapamientos otra vez.
- Historial no es edición libre. Fechas finales inclusivas; la siguiente etapa empieza
  al día siguiente. No se cambian asignaciones académicas ni permisos del empleado.

V113 incorpora el procedimiento a Guía de procesos → Personal → Varios planteles y
cambio programado de principal, ruta /admin/guias/empleado-planteles-cambio-principal.
Incluye ocho etapas, ejemplos, resultados, límites y exportación Excel con los mismos
filtros. Otra guía de siete etapas cubre alta de empleado administrativo sin perfil
docente. V112 conserva planeaciones; ninguna de estas guías ejecuta operaciones.
