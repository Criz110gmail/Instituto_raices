# Calendarios y periodos de nómina — Prueba V125

Estado: **pendiente de aceptación por el propietario**. Las verificaciones técnicas no
sustituyen este recorrido. No repitas las pruebas confirmadas de Bruce ni cambies sus
sueldos, planteles o bono de $500. Este ejercicio sólo organiza fechas: no genera nómina,
retenciones, recibos ni movimientos en cuentas.

## 1. Entrar y crear el calendario

Inicia sesión en el portal administrativo con tu cuenta institucional habitual y permisos
de Empleados, Datos laborales y administración salarial. El acceso de recuperación y los
portales de maestros/familias no administran estos datos. No concedas permisos por la prueba.

Entra a **Administración → Personal y nómina → Calendarios de nómina**. El menú abre
una tabla con filtros, no el formulario. Pulsa **Nuevo calendario** y captura:

| Campo | Captura |
| --- | --- |
| Código | `NOM-MENSUAL-TEST` |
| Nombre | `Nómina mensual de prueba` |
| Descripción | `Calendario de ensayo para organizar periodos, sin calcular ni pagar nómina.` |
| Frecuencia | Mensual |
| Calendario activo | Marcado |

Pulsa **Revisar calendario**. Comprueba el resumen y pulsa **Cancelar** dentro del modal:
debe cerrarse sin guardar ni borrar lo capturado. Vuelve a pulsar **Revisar calendario**
y ahora **Confirmar y guardar**. Debe volver al listado con estado **Activo**, verde.
La frecuencia todavía no está protegida porque no tiene periodos.

Si ese código ya existe de un ensayo previo, consulta el registro antes de continuar;
no dupliques el mismo escenario ni borres historial. Usa un sufijo nuevo si necesitas repetirlo.

## 2. Probar búsqueda, retorno y Excel del calendario

En la tabla de **Calendarios de nómina**, enfoca **Calendario** sin escribir: deben
aparecer hasta diez opciones disponibles de tu institución. Si hay menos, aparecen las
existentes. Escribe `NOM` o más caracteres y selecciona el calendario creado. Escribir
una o dos letras no consulta coincidencias. No basta escribir: selecciona la sugerencia.

Elige Estado **Activos**, Frecuencia **Mensual** y pulsa **Aplicar filtros**. Debe verse
tu calendario. Pulsa **Ver →**, revisa las pestañas Resumen, Historial y Gestión; después
**Volver al listado**. Deben mantenerse filtros, tamaño y página de origen. Pulsa
**Exportar Excel**: debe contener los mismos registros que esos criterios, no todo el catálogo.

## 3. Crear enero de 2027

Entra a **Administración → Personal y nómina → Periodos de nómina → Nuevo periodo**.
El formulario nuevo no debe seleccionar un calendario ni fechas por ti, aunque hayas
filtrado la lista antes. Enfoca **Calendario activo**, selecciona `NOM-MENSUAL-TEST` y
espera a que aparezca **Mensual** en Frecuencia del calendario. Captura:

| Campo | Captura |
| --- | --- |
| Código del periodo | `ENE-2027-TEST` |
| Nombre del periodo | `Enero de 2027 de prueba` |
| Inicio del periodo | **01/01/2027** |
| Fin del periodo | **31/01/2027** |
| Fecha de corte de novedades | **28/01/2027** |
| Fecha prevista de pago | **31/01/2027** |
| Motivo de registro | `Programación mensual de prueba de enero; todavía sin preparar ni pagar nómina.` |

Inicio y fin son los días cubiertos, ambos incluidos. Corte es el límite administrativo
previsto para novedades; no cierra por sí solo la captura ni ejecuta tareas automáticas
en esta etapa. Pago previsto es la fecha planeada, no un pago realizado.

Pulsa **Revisar periodo**. Verifica calendario y las cuatro fechas en `dd/mm/aaaa`.
Primero pulsa **Cancelar** en el modal: no debe guardarse. Abre otra vez la revisión y
pulsa **Confirmar y guardar**. En el detalle → Resumen debes ver **Programado**, **31 días**,
las fechas anteriores y el motivo. Si hoy todavía es 2026, la situación por fechas es
**Próximo**. Después cambiará a En curso o Transcurrido según la fecha institucional:
eso NO significa que esté calculado, cerrado o pagado. Historial muestra usuario/fecha.

## 4. Verificar reglas de fechas y superposición

Vuelve a **Periodos de nómina → Nuevo periodo** y selecciona el mismo calendario.
Usa código `TRASLAPE-2027-TEST`, nombre `Traslape que no debe registrarse`, inicio
**15/01/2027**, fin **14/02/2027**, corte **10/02/2027**, pago **14/02/2027**, motivo
`Prueba de bloqueo de fechas ya cubiertas.` Revisa y confirma: debe indicar que el
calendario ya tiene un periodo que coincide con esas fechas. No debe crear el registro
ni perder la captura. Incluso empezar el **31/01/2027** se cruza con enero porque ambos
extremos están incluidos. El siguiente periodo puede comenzar el **01/02/2027**.

También deben rechazarse fin anterior al inicio, corte anterior al inicio y pago anterior
al corte. Corrige o vuelve sin guardar estos intentos. El corte sí puede quedar después
del fin del periodo si la escuela así lo requiere, siempre que pago sea igual o posterior.
La frecuencia no rellena fechas ni fuerza meses de una duración fija: permite periodos
parciales explícitos; su eventual prorrateo todavía no está implementado.

## 5. Crear febrero y comprobar filtros por fechas

En **Periodos de nómina → Nuevo periodo** selecciona el calendario mensual y captura:

| Campo | Captura |
| --- | --- |
| Código / nombre | `FEB-2027-TEST` / `Febrero de 2027 de prueba` |
| Inicio / fin | **01/02/2027** / **28/02/2027** |
| Corte / pago previsto | **25/02/2027** / **28/02/2027** |
| Motivo | `Programación mensual de prueba de febrero.` |

Confirma el modal. Debe registrarse sin conflicto, con **28 días**. En **Periodos de
nómina** filtra Calendario por `NOM-MENSUAL-TEST`, Estado **Programados**, Frecuencia
**Mensual**, desde **01/01/2027** hasta **31/01/2027** y pulsa **Aplicar filtros**:
sólo debe aparecer enero. El rango busca periodos que coincidan con las fechas;
no exige que sus extremos sean exactamente los del filtro. Con rango **31/01/2027**
a **01/02/2027** deben verse enero y febrero. También puedes seleccionar un periodo
con su ayuda independiente. Limpia esa selección si quieres buscar los dos.

Pulsa **Ver →** y **Volver al listado**: se conservan criterios. Prueba **Exportar Excel**
con rango de enero: una fila, 31 días y fechas 01/01/2027–31/01/2027. Si no hay filas
por combinación de filtros, debe mostrar un vacío explicativo, no un error.

## 6. Edición, motivo e historial

Desde **Periodos de nómina**, localiza enero y pulsa **Editar**. El calendario histórico
no se puede cambiar; el motivo del cambio comienza vacío para que expliques esta gestión.
Cambia el nombre a `Enero de 2027 revisado de prueba`, conserva las cuatro fechas y
captura `Se aclara el nombre del periodo de prueba sin cambiar su cobertura.`
Pulsa **Revisar cambios → Confirmar y guardar**. Resumen conserva el motivo original y
muestra Última gestión; Historial contiene el alta y el cambio con usuario y fecha.
No debe considerarse superposición consigo mismo.

Para probar dos pestañas, abre **Editar** del mismo periodo en dos pestañas antes de
guardar. Guarda el primer cambio con motivo. Intenta guardar un nombre diferente en
la segunda: debe avisar que cambió en otra pestaña y no sobrescribir. Recarga antes
de nuevos cambios. No es necesario crear datos de empleados para esta prueba.

## 7. Calendario histórico y disponibilidad

En **Calendarios de nómina**, busca `NOM-MENSUAL-TEST` y pulsa **Editar**. Su frecuencia
debe estar protegida porque ya tiene periodos. Puedes cambiar descripción/nombre, pero
los periodos conservan la copia del calendario al registrarse. No cambies la frecuencia
mediante base de datos: para una frecuencia diferente crea otro calendario.

Desmarca **Calendario activo** y confirma el resumen. En **Periodos de nómina → Nuevo
periodo**, ese calendario ya no debe aparecer en la ayuda de captura. Sigue disponible
en filtros históricos; sus periodos pueden consultarse y cancelarse, pero no crearse
ni editarse hasta reactivar el calendario. Reactívalo desde **Calendarios de nómina →
Editar** para dejar el ensayo activo. Desactivar NO cancela enero/febrero.

## 8. Cancelar febrero conservando el historial

Entra a **Periodos de nómina**, busca febrero y pulsa **Ver → Gestión**. En **Cancelar
este periodo**, captura `Se cancela únicamente la programación de febrero de prueba;
no existe nómina ni pago asociado.` Pulsa **Revisar cancelación**. Comprueba calendario,
fechas y motivo. Primero pulsa **Cancelar** dentro del modal: febrero sigue Programado.
Repite y ahora pulsa **Confirmar cancelación**.

Debe quedar **Cancelado**, sólo consulta, con historial y sin posibilidad de editar,
reactivar o borrar. Filtra **Cancelados**: aparece febrero. Filtra **Programados**:
enero sigue ahí, febrero no. La cuenta financiera no cambia. Aunque se cancelen todos
los periodos de un calendario, su frecuencia sigue protegida por el historial.

Si necesitas reemplazar un cancelado, utiliza Nuevo periodo con **otro código**. Las
fechas de un cancelado quedan libres, pero su código no se reutiliza. Esto no es una
restauración ni elimina el registro anterior; no hagas el reemplazo si sólo deseas
conservar el cierre del ejemplo.

## 9. Resultado y siguiente etapa

Esperado: calendario mensual **Activo**, enero **Programado** y febrero **Cancelado**;
historial de todas las gestiones, filtros/Excel coherentes y modales Cancelar/Confirmar.
No cambian empleados, sueldo base, bono de Bruce, adeudos, pagos ni cuentas.

Prueba también vistas de escritorio/móvil y temas claro/oscuro con los mismos estilos.
Reporta cualquier diferencia antes de dar el caso por confirmado. Después de tu
confirmación se incorporará el recorrido a **Guía de procesos**. No se publica como
prueba confirmada automáticamente. La siguiente etapa será diseñar la **preparación
de nómina**, elegibilidad por vigencias y revisión explícita de periodos parciales y
cambios de sueldo/conceptos dentro del periodo, sin asumir prorrateo, impuestos o pagos.
