# Comprobantes de compras — prueba manual pendiente

## Qué vamos a comprobar

Guardar los archivos que respaldan una compra y poder consultarlos después, sin
volver a pagarla. Si retiramos un archivo, quedará en el historial con su motivo.
El XML permite consultar la factura y, sólo en un borrador, revisar sus partidas
antes de decidir si reemplazamos las capturadas.

Estado: implementación V167 con verificación técnica; pendiente aceptación manual
del propietario. Esta guía no autoriza confirmar egresos de prueba ni cancelar compras.

## 1. Entrar y elegir la compra

Entra como administrador con permiso de administrar Compras.
Ruta: **Compras → Ver compra → Comprobantes**.

Puedes consultar compras existentes. Para probar importación, utiliza un borrador
ficticio nuevo; no uses una compra operativa confirmada. Si necesitas crear uno:

| Campo | Valor |
| --- | --- |
| Proveedor, cuenta y categoría | Opciones autorizadas de esta base |
| Fecha | Hoy |
| Referencia | `COMP-DOC-TEST-01` |
| Descripción de partida | `Papel ficticio para probar comprobantes` |
| Cantidad | `2` |
| Precio unitario | `100.00` |
| Impuesto | `16` |

Guarda y confirma únicamente el **borrador**. Debe mostrar subtotal $200.00,
impuestos $32.00 y total $232.00. **No pulses Confirmar y registrar egreso.**
Anota folio y saldos de referencia; adjuntar documentos no debe cambiarlos.

## 2. Guardar una compra sin comprobante

1. En Comprobantes pulsa **Clasificar respaldo**.
2. Selecciona **Sin comprobante** y trata de guardar sin justificación: debe exigirla.
3. Escribe `Compra ficticia de capacitación sin comprobante fiscal.`
4. Pulsa **Revisar y guardar**. Prueba **Cancelar** en el modal: no debe guardar.
5. Revisa nuevamente y confirma. Debe aparecer la clasificación y su historial.
6. Para continuar, cambia a **Ticket o recibo**. El historial anterior se conserva.

## 3. Adjuntar PDF y fotografía

1. Pulsa **+ Adjuntar comprobante**. Se abre un formulario separado.
2. Elige PDF, un PDF ficticio y descripción `Ticket ficticio de papelería`.
3. Pulsa **Revisar y adjuntar**, prueba Cancelar y después confirma.
4. En la tabla pulsa **Abrir archivo ↗**: debe abrir en otra pestaña.
5. Repite con Fotografía y una imagen ficticia JPEG, PNG, HEIC o HEIF.
6. La imagen guardada debe poder abrirse como JPEG optimizado.

PDF hasta 10 MB; fotografía hasta 20 MB de entrada; XML hasta 2 MB. Un archivo vacío,
formato incorrecto o PDF ilegible no debe guardarse. Tras un error se conservan los
textos; el navegador exige seleccionar de nuevo el archivo. No se puede adjuntar
otra vez el mismo contenido en esta compra, ni siquiera si se retiró.

## 4. Adjuntar XML CFDI de prueba

Archivo sintético incluido: `src/test/resources/compras/cfdi-prueba.xml`.
No es una factura auténtica ni debe utilizarse como comprobante operativo.

1. En Adjuntar comprobante selecciona **XML CFDI**.
2. Selecciona ese archivo y descripción `XML ficticio de prueba`.
3. En Justificación escribe `XML ficticio para capacitación; los RFC de ejemplo pueden diferir del proveedor y la institución.`
4. Revisa, cancela primero y después confirma.
5. Pulsa **Ver datos CFDI**.

Debe mostrar UUID `AAAAAAAA-AAAA-4AAA-8AAA-AAAAAAAAAAAA`, versión 4.0,
subtotal $200.00, total $232.00 MXN, un concepto de dos unidades a $100.00,
IVA $32.00 por concepto y su resumen global, y un UUID relacionado.
No se suman otra vez los impuestos globales a los impuestos de conceptos.

Las pestañas son **Resumen**, **Conceptos**, **Impuestos y relaciones** e
**Importar al borrador** (esta última sólo con permiso de administrar).
Comprueba que Abrir XML original conserva el contenido seleccionado.

La aplicación hace lectura estructural local. No valida sellos, autenticidad,
estado SAT, deducibilidad ni el esquema XSD completo; no timbra documentos.
Soporta lectura de CFDI 4.0 y 3.3 con UUID timbrado. No consulta sitios externos al leer.

## 5. Clasificar como factura y probar diferencias

1. Regresa a Comprobantes → Clasificar respaldo → **Factura CFDI**.
2. Sin un XML vigente no debe permitir guardar esa clasificación.
3. Con el XML guardado, si hay diferencias, captura la justificación antes de guardar.
4. Debe mostrar advertencias de RFC emisor/proveedor, RFC receptor/institución,
   moneda, tipo, subtotal o total cuando no coincidan.
5. Si se cambia el borrador después y aparecen diferencias sin justificar, confirmar
   el egreso queda bloqueado hasta revisar el respaldo. No es necesario confirmar
   ningún egreso para completar esta prueba manual documental.

El sistema permite conservar diferencias justificadas; no cambia el proveedor,
la cuenta ni las partidas al adjuntar el XML.

## 6. Importar partidas sólo después de revisarlas

En el XML del borrador, entra a **Importar al borrador**.

1. Revisa la vista previa: Papel ficticio de prueba, cantidad 2, precio 100, impuesto 16%.
2. Verifica total actual y total propuesto.
3. Escribe `Reemplazar las partidas ficticias por la vista previa del XML.`
4. Pulsa Revisar importación y **Cancelar**: las partidas originales deben seguir iguales.
5. Repite y confirma: las partidas del borrador se reemplazan por las de la vista previa.
6. Total $232.00, compra en Borrador y sin nuevo movimiento financiero.

Sólo se importan conceptos representables sin pérdida en la captura actual:
sin descuentos ni retenciones, como máximo 100 partidas, cantidades/precios/tasas
con precisión compatible e impuestos compatibles. Un CFDI más complejo se conserva
como respaldo y solicita captura manual. No se convierte moneda automáticamente.
Una compra confirmada o cancelada no permite sustituir partidas.

## 7. Duplicados y archivos inválidos

- Adjunta otra vez el mismo XML en la misma compra: debe rechazarlo.
- Intenta vincular el mismo UUID en otro borrador de la misma institución: debe rechazarlo.
- Un archivo de texto renombrado como PDF o XML incompleto no debe guardarse.
- No se admiten XML con DTD o entidades externas.
- En dos pestañas, cambia la clasificación en A y trata de guardar la versión antigua
  en B: debe pedir recargar. Lo mismo aplica a importar con una versión vieja del borrador.

## 8. Retirar sin borrar

1. En el PDF o la fotografía pulsa **Retirar**.
2. Escribe `Archivo ficticio retirado para comprobar el historial.`
3. Prueba Cancelar en el modal; el documento debe continuar vigente.
4. Repite y confirma. Con filtro Vigentes ya no aparece; con Retirados o Todos sí.
5. Debe conservarse el motivo y poder abrirse el archivo histórico.
6. Para retirar el único XML vigente de un respaldo clasificado CFDI, primero cambia
   la clasificación a Ticket o recibo o Sin comprobante. No debe dejar Factura CFDI
   sin XML vigente.
7. El UUID retirado no se libera para vincularlo de nuevo a otra compra.

## 9. Filtros, Excel, regreso y diseño

En Comprobantes combina Tipo de archivo, Estado del documento y búsqueda por
nombre, descripción o UUID. Aplica y limpia: debe mantenerte junto a los filtros.
Exportar Excel debe respetar esos filtros; exporta metadatos, no los archivos.
Abre un formulario o Datos CFDI y vuelve: se conservan los filtros del expediente.
Volver al historial debe conservar los filtros del listado de Compras de origen.
Comprueba tema claro/oscuro y ventana estrecha; las tablas tienen desplazamiento
horizontal propio, sin desbordar toda la página.

## 10. Permisos y cierre

- Un usuario con sólo consulta puede ver el expediente, abrir archivos, consultar
  datos CFDI y exportar, pero no adjuntar, clasificar, retirar ni importar.
- Probar una URL de captura directamente tampoco debe permitir administrar.
- Otra institución o plantel fuera del alcance no puede abrir archivos por URL.
- En una compra cancelada el expediente queda sólo de consulta.
- Los saldos y movimientos deben permanecer iguales durante toda esta prueba.

No marcar esta prueba como confirmada hasta la aceptación del propietario.

## Referencias técnicas

Se reutilizan `archivo`, `AlmacenamientoArchivo`, `ProcesadorFotografia`,
los permisos `COMPRA_*`, el alcance institucional/plantel y las confirmaciones
existentes. V167 añade sólo tablas e índices documentales; no hace backfill
de comprobantes ni modifica saldos, compras históricas o permisos.
Estructura de referencia del SAT: https://wwwmat.sat.gob.mx/cs/Satellite?blobcol=urldata&blobkey=id&blobtable=MungoBlobs&blobwhere=1461173215510&ssbinary=true
