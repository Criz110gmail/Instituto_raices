# Prueba de Conceptos por empleado — V123

Prueba humana PENDIENTE. La verificación técnica no confirma este recorrido por el
propietario. No generar nómina ni modificar cuentas, salarios o las asignaciones
laborales de Bruce. No repetir registros de pruebas anteriores sin comprobarlos.

## Alcance y reglas

Menú **Administración → Personal y nómina → Conceptos por empleado**.
Ruta `/admin/conceptos-empleado`. El catálogo Conceptos de nómina describe qué es
el concepto; aquí se asignan empleado, importe fijo por nómina y vigencia inclusiva.
No se captura sueldo base ni porcentaje. El importe es por periodo de nómina
elegible, no una cantidad mensual para prorratear automáticamente. Elegibilidad y
prorrateos requieren el diseño de preparación de nómina, todavía pendiente.

Sólo habituales activos en nuevas selecciones. Ocasionales, préstamos, impuestos,
cálculos, autorización y pago quedan fuera. Aportación patronal es costo de escuela,
no una deducción del neto. La desactivación o edición del catálogo no reescribe las
asignaciones anteriores: se conservan código, nombre, tipo, moneda e importe.

Requiere administrador institucional persistido activo con Empleados, Datos
laborales y permisos privados salariales de lectura/administración. No hay nuevas
concesiones. Familias, docentes, recuperación y alcance de plantel no obtienen acceso.

## 1. Preparar el caso sin duplicar expedientes

En **Personal y nómina → Empleados**, busca Bruce Wayne, código DOC-PRUEBA-02.
Abre su expediente y confirma actividad, fecha de ingreso laboral 01/10/2026 y
situación ACTIVO. No cambies sus salarios, jornada, planteles, usuario ni clases.
Si falta actividad o ingreso, corrígelo con autorización en Datos laborales.

En **Conceptos de nómina**, busca BON-PUNT-TEST, Bono de puntualidad de prueba,
Percepción/Habitual/Activo. Ya existe del ensayo confirmado: NO crearlo otra vez.
DESC-TEST es Ocasional y por eso no se ofrece para asignar aquí. No convertirlo ni
alterar sus datos para este ejemplo. APORT-TEST habitual puede consultarse después.

## 2. Crear bono con confirmación y Cancelar

En **Conceptos por empleado → Asignar concepto**, captura:

| Campo | Valor |
|---|---|
| Empleado | Seleccionar DOC-PRUEBA-02 · Bruce Wayne de la ayuda |
| Concepto habitual | Seleccionar BON-PUNT-TEST de la ayuda |
| Importe fijo por nómina | 500.00 |
| Inicio | 01/01/2027 |
| Último día incluido | Vacío |
| Motivo | Bono habitual de prueba desde enero de 2027, sin generar nómina. |

Al enfocar ambas ayudas aparecen hasta diez registros y desde tres caracteres se
busca en servidor. Espera la consulta: muestra moneda/tipo/efecto y habilita
**Revisar asignación**. Importe es texto monetario, sin incremento por rueda del mouse.
Al quitar foco se presenta como moneda; el servicio recibe el decimal canónico.

Pulsa **Revisar asignación → Cancelar** dentro del modal: no guarda y conserva la
captura. Repite y pulsa **Confirmar y guardar**. Se abre el detalle. En Resumen:
Bruce, bono histórico, $500.00 MXN, inicio01/01/2027, fin vacío, Próxima mientras no
llegue esa fecha. No se sumó dinero a cuentas ni se cambió el sueldo base.

Fechas de este ensayo son futuras respecto de22/10/2026. Si se ejecuta después de
enero, usa fechas futuras y registra la diferencia antes de compararlas.

## 3. Tabla, filtros y Excel

Pulsa **Volver al listado**. Enfoca filtro Empleado sin escribir: hasta diez opciones;
si Bruce no está entre ellas, escribe `Bru` y selecciónalo. Enfoca Concepto y elige
BON-PUNT-TEST; busca desde `BON` si es necesario. Filtro Estado Próximas, Tipo
Percepción, Desde01/01/2027, Hasta31/01/2027; pulsa **Aplicar filtros**.

Debe aparecer sólo su bono de500. El rango incluye vigencias que coinciden con el
intervalo; no es fecha de creación. Exporta Excel y comprueba mismo registro,
importe500, monedaMXN y fechas dd/MM/yyyy. Abre **Ver** y vuelve: criterios y página
se conservan, aunque la etiqueta concatene código y nombre. La selección filtra por ID.
Prueba limpiar sólo un campo con × y después **Limpiar** para todos los filtros.

## 4. No duplicar fechas

Desde el listado pulsa **Asignar concepto** y elige el mismo empleado y concepto.
Captura500, inicio01/01/2027 sin fin y motivo `Duplicado que no debe guardarse`.
Confirma el modal: debe impedirlo por vigencias superpuestas, conservar captura
y no registrar otro bono. Cancelar del formulario vuelve sin guardar.

## 5. Programar nuevo importe

En **Conceptos por empleado → Ver** del bono de500, pestaña **Gestión → Programar
cambio**. Empleado/concepto permanecen identificados. Captura importe600, inicio
01/02/2027, fin vacío, motivo `Aumento de bono de prueba desde febrero`.
Revisa modal y confirma. Debe existir una vigencia nueva de600 desde01/02/2027;
la de500 termina31/01/2027 y conserva su importe y motivo originales.

No se permite intercalar cambios cuando ya existe una programación posterior ni
alterar el importe de una fila histórica. Códigos/tipo de la nueva vigencia se
congelan del catálogo consultado al confirmar; la anterior conserva su fotografía.

## 6. Cancelar cambio futuro y restaurar

Abre **Ver** del bono600 → **Gestión → Cancelar programación futura**. Motivo
`Cancelar aumento de prueba; conservar bono500`. Pulsa **Revisar cancelación**.
Primero prueba **Cancelar** del modal; sigue Próxima. Vuelve a revisar y confirma:
bono600 Cancelada, conserva fechas e historial; bono500 recupera fin vacío.

Historial de cada fila muestra fecha/hora local, usuario y motivo. No se borran
registros ni se genera devolución. Una pantalla atrasada no puede repetir la
cancelación ni alterar una vigencia restaurada. Se cancela sólo la última
programación que todavía no inicia; una vigente no se elimina como si nunca existió.

## 7. Dos pestañas y validaciones

Si se desea verificar otro cambio, abre **Programar cambio** del bono500 en A y B
antes de guardar. En A programa600 desde01/02/2027 y confirma. En B intenta700
desde la misma fecha con el formulario antiguo: debe avisar que la vigencia cambió
sin sobrescribir A. Cancela después sólo el cambio600 futuro y verifica restauración.

Prueba obligatorios vacíos, importe0/negativo/más de dos decimales, fin anterior
al inicio y fecha pasada: deben impedir guardar con validación. Texto escrito en
un filtro sin seleccionar opción debe pedir selección, no ampliar la consulta.

## 8. Terminar una vigencia actual — caso independiente opcional

Para probarlo sin afectar el bono de enero, usa otro concepto habitual de ensayo
autorizado y otro registro que inicie hoy. En su **Ver → Gestión → Terminar vigencia
actual**, captura último día hoy y motivo. Revisa el modal; Cancelar conserva todo.
Confirmar registra el fin inclusivo, mantiene historial y estado Vigente durante
ese último día. Al día siguiente se verá Anterior. No extiende un fin registrado;
si hay una programación posterior debe resolverse antes. No afecta sueldo base.

## Cierre esperado antes de documentar como confirmado

Bono500 Próxima desde01/01/2027 sin fin, bono600 Cancelada; sin duplicados activos,
sin cambios a salarios/cuentas. Confirmar con propietario filtros/Excel, modales y
restauración antes de incorporar el caso a Guía de procesos. Después: analizar
calendarios y preparación de nómina, definiendo periodos y elegibilidad sin asumir
cálculos fiscales o aplicar conceptos ocasionales automáticamente.
