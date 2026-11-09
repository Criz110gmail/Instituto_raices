# Pago anticipado desde inscripción — prueba manual

## Resultado confirmado por el usuario

- Noviembre y diciembre de ANT-MENS-TEST se pagaron en un solo pago validado
  (PAG-20261109-000031, $1,000). Esos meses ya no aparecen disponibles; enero
  de 2027 sí aparece por $500. Caja quedó en $1,415.11.
- Sin los permisos requeridos el usuario de prueba no pudo entrar; con ellos sí.
- En dos pestañas, un pago ficticio pendiente de enero bloqueó la consulta antigua.
  El usuario canceló ese pago, lo vio en el historial y enero volvió a estar disponible.
- El usuario confirmó que todos los puntos de la prueba de dos pestañas pasaron.
- El usuario confirmó la prueba de acceso fuera del plantel y la revisión visual en
  ambos temas y pantalla angosta. La prueba manual queda cerrada.

La prueba realizada cubre el alcance entre planteles; no se afirmó una prueba
manual adicional con una institución distinta.

## Qué vamos a comprobar

Preparar varias mensualidades de un alumno en una sola captura. Consultar no crea
cargos. Confirmar la selección genera únicamente los faltantes y abre el formulario
de pago. Registrar el pago lo deja pendiente; validar es lo que publica el ingreso.

## Preparar el ejemplo

En esta base existen INS_2026_001 (Jose Pedro), INS_2026_002 (Capitan) e
INS_2026_003 (Black). Elige una inscripción vigente con tutor responsable financiero.
No cambies cuotas operativas ya utilizadas. Si necesitas datos de prueba, agrega
un concepto ficticio distinto de los habituales y una cuota para esa inscripción.

Permisos necesarios: Inscripciones, Adeudos de alumnos y Pagos recibidos; para
crear la cuota también Cuotas por alumno y Conceptos de cobro. El acceso por URL
verifica también los permisos, la institución y el plantel.

Ejemplo: concepto ANT-MENS-TEST, nombre Mensualidad de prueba anticipada, cuota
mensual de $500 MXN entre 01/11/2026 y 31/01/2027, vencimiento día 10. Verifica que
las fechas estén dentro del ciclo y la vigencia de la inscripción antes de guardar.
Desde Inscripciones → Editar → Cobranza → Preparar cobro puedes abrir la captura
de la cuota con la inscripción precargada. Selecciona frecuencia Mensual.

## 1. Consultar sin crear cargos

1. En Inscripciones busca la inscripción y pulsa Editar.
2. Abre Cobranza → Preparar pago anticipado.
3. Captura Desde 01/11/2026 y Hasta 31/01/2027.
4. Pulsa Consultar cargos y mensualidades.
5. Identifica las tres mensualidades de prueba por $500 cada una.

Deben mostrarse únicamente los cargos disponibles para pagar, con estado Por generar
o Existente. Los liquidados, cancelados, sustituidos y los que tienen un pago en revisión
no aparecen en esta selección; pueden consultarse en la cobranza de la inscripción. Consultar no
modifica cargos ni saldos. Una mensualidad conserva el periodo y monto completos;
este flujo no prorratea al elegir una fecha intermedia.

## 2. Seleccionar y cancelar

Marca sólo noviembre y diciembre del concepto de prueba: total $1,000 MXN.
Pulsa Generar faltantes y preparar pago. El modal debe mostrar número de cargos,
total y explicación. Pulsa Cancelar: conserva la selección sin generar cargos.
Comprueba también que intentar continuar sin seleccionar muestre un mensaje.

## 3. Confirmar y preparar el pago

Revisa otra vez y confirma. Debe abrirse el formulario habitual de pago con:

- La institución, plantel y tutor responsable precargados.
- Dos aplicaciones, una por cada mensualidad seleccionada.
- Total $1,000 y moneda MXN.
- Cuenta pendiente de seleccionar y medio de pago editable conforme al flujo habitual.

En Cobranza deben existir los dos cargos seleccionados. Enero no debe generarse.
No deben aparecer cargos nuevos en otro alumno. Todavía no hay pago registrado
ni movimiento financiero. Cancelar el formulario de pago regresa a la cobranza;
los cargos ya generados se conservan como importes por cobrar.

## 4. Comprobar repetición y cambios concurrentes

Vuelve a consultar el mismo rango: noviembre y diciembre deben aparecer Existentes,
sin duplicados; enero continúa Por generar. Si consultas desde dos pestañas y
confirmas la misma selección desde una, la otra debe pedir revisar nuevamente al
cambiar los cargos. Si cambia cuota, beca o saldo, una vista antigua no se confirma.
El servidor comprueba la selección: no acepta claves ajenas ni más de cien cargos.

## 5. Registrar y validar un solo pago

Sólo sobre el ejemplo ficticio y si quieres cerrar el recorrido financiero:

1. Prepara noviembre y diciembre existentes.
2. Elige una cuenta caja autorizada y medio Efectivo; anota su saldo inicial B.
3. Captura fecha actual, referencia ANT-INS-TEST-1000 y observaciones de prueba.
4. Registra como pendiente: debe existir un pago con dos solicitudes y sin ingreso.
5. En el detalle revisa la distribución y valida una sola vez.
6. Comprueba dos aplicaciones de $500 y un único ingreso de $1,000; saldo B+$1,000.
7. Ambos cargos quedan liquidados y ya no son seleccionables para otro anticipo.

No mezclar el ejemplo con cargos históricos de otros conceptos. Si el alumno tiene
beca, el total puede variar: comprueba el importe mostrado antes de confirmar.

## 6. Diseño y permisos

Comprueba fechas y etiquetas alineadas, tabla con desplazamiento propio en móvil,
total seleccionado visible, modal Cancelar/Confirmar y ambos temas. Regresar desde
la captura debe llevar a la pestaña Cobranza de la inscripción.

Un usuario sin alguno de los tres permisos requeridos no debe poder entrar ni
confirmar por URL. Tampoco un usuario fuera de la institución o plantel.
Una selección vencida (20 minutos) pide consultar de nuevo.

## Evidencia técnica

Pruebas automatizadas con datos ficticios: vista previa sin escrituras, mes individual,
cargo manual existente, configuración cambiada, selección ajena, concurrencia de dos
sesiones sin duplicados, formularios reales, CSRF, permisos y un pago con dos cargos
que produce un solo movimiento al validar. Pendiente aceptación manual del propietario.
