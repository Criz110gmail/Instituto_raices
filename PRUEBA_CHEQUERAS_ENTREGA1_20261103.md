# Prueba de chequeras: registrar y organizar folios sin pagar

Estado: implementación V152, **prueba funcional confirmada por el usuario**.

El usuario reportó que todas las pruebas pasaron y solicitó ajustes visuales.
Lectura de sólo consulta: chequera1 de BBVA_COBROS, nombre BBVA — Chequera de prueba,
folios1001–1050, activa, versión1 y dos entradas de historial. No repetir el alta
para comprobar el diseño. El catálogo sigue sin emitir cheques ni mover dinero.

Revisión visual pendiente de los ajustes posteriores: botones Editar/Chequeras
uniformes en Cuentas financieras; menú común Datos de la cuenta/Chequeras; ayuda
de folios separada; Editar y Ver historial alineados. Comprobar con el registro
existente, sin volver a confirmar ni cambiar sus folios.

## Qué comprueba esta prueba

Registrarás una chequera de prueba para BBVA, revisarás sus filtros y comprobarás
que no se pueden repetir folios en esa cuenta. No se emitirán cheques ni se moverá
dinero. No modifiques nóminas ni sus pagos para realizar este ejemplo.

## 1. Dar acceso de forma explícita

En **Roles y permisos**, edita el rol administrativo que utilizarás. Conserva sus
permisos actuales y agrega **Chequeras - Administrar**. Ese permiso ya permite
consultar las chequeras; no es obligatorio marcar ambos. También necesita acceso
a **Cuentas financieras**. Guarda con confirmación y cierra/inicia sesión.

No se asignan permisos automáticamente al desplegar. Si no ves Chequeras, revisa
el rol y renueva sesión; no cambies al usuario de institución ni uses recuperación.

## 2. Abrir la cuenta correcta

Entra a **Cuentas financieras** y localiza **BBVA_COBROS**. En esa fila pulsa
**Chequeras**. También está la pestaña **Chequeras** al editar la cuenta bancaria.

Debe identificarse la sección y la cuenta BBVA. Las cuentas **CAJA** y
**CAJA-CORTE-TEST** no deben ofrecer Chequeras, ni permitir entrar por URL directa.

Si aún no has hecho capturas en este módulo, la lista estará vacía.

## 3. Probar Cancelar y registrar la chequera

Pulsa **Nueva chequera**:

| Campo | Valor |
| --- | --- |
| Cuenta bancaria | BBVA_COBROS, fija y no editable |
| Nombre de la chequera | `BBVA — Chequera de prueba` |
| Folio inicial | **1001** |
| Folio final | **1050** |
| Chequera activa | Marcada |
| Motivo | `Registro de chequera ficticia para probar el catálogo sin emitir cheques.` |

Son folios ficticios para probar esta etapa; no significan que se haya emitido
ningún documento físico. En uso real, registrar los números del talonario del banco.

1. Pulsa **Revisar y guardar chequera**.
2. Comprueba cuenta, nombre, rango, estado y motivo en el modal.
3. Pulsa **Cancelar dentro del modal**: conserva la captura y no guarda.
4. Vuelve a revisar y ahora confirma.

Debe regresar al listado y mostrar **1001 — 1050**, **50 folios**, **Activa**.
La cantidad indica números incluidos en el rango, no cheques emitidos ni pagados.

## 4. Filtros, autocompletado y Excel

- En Chequera, enfoca el campo: debe mostrar las primeras diez sugerencias como
  máximo. Al escribir tres caracteres busca en el servidor; selecciona la opción.
- Búsqueda adicional: `prueba`.
- Buscar folio en el rango: **1025**.
- Estado: **Activas**.
- Por página: **10**.

Pulsa **Aplicar filtros**. Debe aparecer la chequera y mantenerse la posición en
los filtros. Exporta Excel: debe corresponder a los filtros y mostrar los mismos
folios, cantidad y estado. Folio **1051** no encuentra esa chequera. Pulsa **Limpiar**:
debe quitar los filtros sin subir al inicio.

## 5. Bloquear un rango superpuesto

Con filtros limpios, pulsa **Nueva chequera** y captura:

- Nombre: `BBVA — Prueba de rango repetido`.
- Folio inicial: **1050**; final: **1100**.
- Motivo: `Comprobar que el folio 1050 no puede pertenecer a dos chequeras.`

Revisa y confirma. Debe rechazarlo porque **1050** ya está incluido en la primera.
Debe conservar la captura en el formulario. Cancela para volver; no cambies el
rango para crear otra chequera si no necesitas hacerlo.

## 6. Editar y comprobar historial

En la primera fila pulsa **Editar**. Cambia el nombre a
`BBVA — Chequera de prueba revisada`, conserva 1001–1050 y escribe como motivo
`Actualizar el nombre de prueba y conservar su historial.` Guarda con confirmación.

Abre **Ver historial**: deben aparecer registro inicial y cambio, con usuario,
fecha, nombre, folios, estado y motivo. Volver a chequeras conserva los filtros
con los que abriste esa fila.

## 7. Dos pestañas y estado inactivo

1. Abre **Editar** de la misma chequera en pestañas **A** y **B**, sin guardar aún.
2. En A desmarca Chequera activa y escribe `Desactivar chequera de prueba.` Confirma.
3. En B cambia el nombre y el motivo, sin recargar, y confirma.
4. B debe avisar que la chequera cambió y pedir recargar, sin sobrescribir A.
5. En el listado usa estado **Inactivas**; debe aparecer el registro.
6. El intento 1050–1100 del paso5 debe seguir rechazándose aunque esté inactiva.
7. Recarga una edición nueva, activa la chequera y registra el motivo
   `Reactivar al terminar las pruebas del catálogo.`

Al terminar debe quedar activa y su historial debe conservar los cambios. No se
borra una chequera ni se libera su rango por desactivarla.

## 8. Comprobar el permiso de consulta

Usa un usuario de pruebas con acceso a Cuentas financieras y **Chequeras - Consultar**,
sin Chequeras - Administrar. No quites los permisos del administrador principal.
Renueva la sesión del usuario de pruebas.

Debe poder consultar listado, filtros, historial y Excel. No debe ver Nueva chequera
ni Editar en el catálogo de chequeras. Copiar la URL de alta/edición del administrador
debe devolver acceso denegado. Consultar no puede guardar por una URL directa.

## 9. Comprobar que no se movió dinero

Si no hubo otros movimientos, deben permanecer:

| Cuenta | Saldo |
| --- | ---: |
| BBVA_COBROS | **$274.29** |
| CAJA | **$1,195.11** |
| CAJA-CORTE-TEST | **$1,150.00** |

No hay ingresos, egresos ni nuevos pagos de nómina por crear o editar chequeras.
La próxima entrega conectará la emisión y cancelación de cheques con los pagos;
esta prueba sólo valida el catálogo. La funcionalidad fue confirmada; la aceptación
visual de los últimos ajustes es independiente y no requiere nuevos registros.
