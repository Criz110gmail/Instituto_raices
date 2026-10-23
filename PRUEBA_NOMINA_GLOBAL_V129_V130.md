# Nómina del periodo: revisar, guardar y aplicar desde una cuenta

Implementación V129/V130. Prueba humana pendiente; no equivale a aceptación del
propietario. No es cálculo fiscal ni transferencia bancaria automática.

## Qué cambia

**Administración → Personal y nómina → Preparación de nómina → Nuevo borrador**
ahora pide el periodo, no preparar un borrador por cada empleado. Selecciona todos
los elegibles del periodo, aunque haya más de 25 o 100. La tabla muestra 25 por página;
los totales corresponden a toda la selección. Puedes excluir empleados o elegir
**Opciones de selección → Elegir empleados específicos**, que comienza sin seleccionar.

Tres acciones distintas:

1. **Preparar nómina del periodo:** consulta privada, sin egreso.
2. **Guardar borrador revisado:** conserva empleados, importes y revisión, sin egreso.
3. **Gestión → Revisar y aplicar nómina:** elige cuenta, revisa y confirma un egreso
   consolidado; queda Aplicada y sólo consulta.

La aplicación necesita administrar salarios y **MOVIMIENTO_FINANCIERO_REGISTRAR**,
además del acceso laboral privado existente y una cuenta administrativa institucional
activa. No se otorgan permisos automáticamente. Los portales no reciben acceso.

## 1. Continuar el ensayo ya guardado, sin repetir altas

Ya existe un borrador de Bruce Wayne, DOC-PRUEBA-02, enero de 2027 revisión A:
sueldo $12,000 + BON-PUNT-TEST $500 = **$12,500 MXN**, revisión 1. Su número/folio
real se consulta en el listado; no se presupone un ID.

No vuelvas a crear Bruce, su sueldo, bono, calendario o enero. No uses febrero
FEB-2027-TEST: quedó Cancelado en la prueba anterior.

1. Entra en **Administración → Personal y nómina → Preparación de nómina**.
2. Filtra por enero y abre el borrador existente con **Ver**.
3. En **Resumen**, comprueba $12,000 de sueldo, $500 de percepciones y $12,500
   propuestos. En **Empleados → Ver desglose**, identifica sueldo y bono.
4. En **Historial**, consulta la revisión 1, regresa y vuelve a consultarla. Debe
   abrir Empleados cada vez; no crea revisiones ni movimientos.
5. Anota su folio y conserva el borrador: no lo canceles para forzar otra preparación.

## 2. Probar la nueva preparación global sin generar dinero

1. Regresa al listado de **Preparación de nómina** y pulsa **Nuevo borrador**.
2. En **Periodo programado**, enfoca la ayuda y selecciona **ENE-2027-TEST**.
   Muestra diez opciones al foco; desde tres caracteres consulta coincidencias.
3. Deja **Todos los elegibles del periodo** y pulsa **Preparar nómina del periodo**.
4. Revisa el total general y conteos. Bruce ya pertenece al borrador anterior, por
   eso aparecerá en **Mostrar → No incluidos**, con motivo de inclusión previa;
   no debe duplicarse. Si nadie más tiene configuración compatible, habrá cero
   incluidos y Guardar estará deshabilitado: es correcto, no inventar empleados.
5. Si hay otros elegibles, anota total y número de incluidos. Abre su desglose;
   excluye uno y comprueba que disminuya exactamente su importe y una persona.
   Cambia filtros/página y vuelve: debe continuar excluido. Inclúyelo de nuevo.
6. Filtra un solo empleado. El total de arriba NO cambia: es de toda la selección.
   **Exportar consulta a Excel** contiene Resumen global y Consulta con los filtros
   de la tabla, no sólo las 25 filas visibles.
7. Los casos **Requieren revisión** explican el motivo; no se calculan como cero.
   Sueldos/cambios/ingresos parciales, frecuencia no mensual o fuentes incompatibles
   no se convierten/prorratean automáticamente.
8. Pulsa **Descartar vista previa**, prueba **Cancelar** en su modal y después
   confirma el descarte. El borrador previo y las cuentas deben seguir intactos.

La vista es privada del administrador y vence a las dos horas; no es un borrador
definitivo. Si caduca o cambian las fuentes, prepara otra. Crear empleados después
no los agrega silenciosamente a la vista abierta.

## 3. Comprobar guardar y revisar varios empleados

Cuando haya al menos dos empleados elegibles que NO estén ya preparados para el
periodo, repite el punto 2. No cambies los datos aceptados de Bruce para conseguirlo.
Para un caso de capacitación controlado usa empleados y configuraciones de prueba
creados expresamente, con ingreso anterior al mes y sueldo mensual durante todo él.

Ejemplo aritmético si los únicos seleccionados son:

| Empleado de prueba | Sueldo mensual | Percepción habitual | Propuesto |
| --- | ---: | ---: | ---: |
| A | $12,000 | $500 | $12,500 |
| B | $8,000 | $0 | $8,000 |
| Total | $20,000 | $500 | **$20,500** |

En **Opciones de selección → Elegir empleados específicos** incluye únicamente A y
B. El importe exacto exige que no tengan otras deducciones/percepciones configuradas.
No uses una cantidad esperada si el desglose muestra otros conceptos vigentes.

1. Revisa totales y desgloses. Nombre y motivo se proponen; puedes personalizarlos
   en **Nombre y observaciones** sin volver a capturar importes.
2. Pulsa **Guardar borrador revisado**. El modal muestra periodo, empleados y total.
   **Cancelar** sólo cierra el modal y no guarda. Confirma una vez después de revisar.
3. Debe existir UN borrador con los dos empleados y $20,500, no uno por persona.
   Ninguna cuenta cambia. **Empleados** y su Excel contienen ambos desgloses.
4. Desde **Gestión → Revisar actualización**, revisa nueva propuesta, captura motivo
   y confirma sólo si deseas otra revisión. Conserva todos los empleados originales;
   no sobrescribe la revisión 1 ni mueve dinero. Una fuente incompatible bloquea
   toda actualización; no actualiza parcialmente ni asigna cero.
5. En **Historial**, verifica que ambas revisiones conservan sus importes y auditoría.
   Preparar de nuevo el mismo periodo no debe volver a incluir a esas personas.

## 4. Elegir la cuenta y revisar el egreso

Para probar sin afectar una cuenta operativa real, crea una **cuenta exclusiva de
capacitación**, con código identificable y sin mezclar movimientos reales. Registrar
la aplicación es definitivo en esta entrega: todavía no hay reversión especializada
de nómina. No usar dinero ni datos reales sólo para ensayar.

Puedes continuar con el borrador existente de Bruce ($12,500), o con el del punto 3
($20,500), pero NO aplicar ambos suponiendo que son el mismo ejemplo.

| Variante | Cuenta de prueba antes | Nómina | Cuenta después |
| --- | ---: | ---: | ---: |
| Bruce ya guardado | $20,000 | $12,500 | **$7,500** |
| Dos empleados del punto 3 | $30,000 | $20,500 | **$9,500** |

1. Entra en **Administración → Finanzas → Cuentas financieras → Nuevo registro**.
   Crea una cuenta activa de capacitación tipo **Banco** o **Caja**, moneda **MXN**,
   con el saldo inicial de la variante elegida y fecha de apertura de hoy o anterior.
   Anota su código y saldo actual. No modifiques el inicial de una cuenta con historial.
2. En **Administración → Finanzas → Motivos financieros**, crea o identifica un
   motivo activo **Egreso** o **Ambos**, por ejemplo `NOMINA-TEST · Pago de nómina`.
3. Vuelve a **Personal y nómina → Preparación de nómina → Ver** el borrador correcto.
4. Revisa **Resumen**, **Empleados** y **Historial**. En **Gestión**, pulsa
   **Revisar y aplicar nómina**. Si no aparece, verifica ambos permisos con un
   administrador; no uses la cuenta de recuperación.
5. Selecciona la **Cuenta de origen** desde la ayuda y el **Motivo financiero**.
   Captura el motivo administrativo del registro; los importes no son editables.
6. Pulsa **Revisar aplicación**. Confirma periodo, cantidad de empleados, cuenta,
   saldo disponible, total y saldo después de aplicar. Todavía NO existe un egreso.
7. **Cancelar sin aplicar** regresa sin mover dinero. Prueba este retorno y vuelve
   a revisar. No cambies configuración durante la confirmación.

El pago previsto del periodo no es una orden bancaria futura. Aplicar registra la
salida con la fecha y hora institucional actuales; no envía dinero al banco. Sólo
confirmar cuando corresponda registrar una salida efectiva, no un cálculo preliminar.

## 5. Confirmación y controles posteriores

1. En la vista del egreso, marca que revisaste el total y corresponde registrar la
   salida real. Pulsa **Aplicar nómina y registrar egreso**.
2. En el modal, revisa cuenta, empleados, total y saldos. Prueba **Cancelar**:
   debe conservar la vista y no mover dinero. Después vuelve a abrir y confirma.
3. En **Preparación de nómina → Ver → Resumen**, debe decir **Aplicada** y mostrar
   cuenta, monto, fecha, usuario y motivo de aplicación. La revisión queda bloqueada.
4. En **Administración → Finanzas → Movimientos financieros**, filtra la cuenta
   y clase **Nómina**. Debe existir **un egreso consolidado**, por el total del
   borrador, con referencia a su folio. No un egreso duplicado por cada clic.
5. En **Cuentas financieras**, verifica el saldo final de la variante elegida.
   El saldo inicial no se reescribe. Si hubo otros movimientos, concilia sus importes
   antes de atribuir una diferencia a la nómina.
6. En **Preparación de nómina → Ver → Empleados**, el desglose privado sigue por
   empleado. No se publican sueldos individuales en el concepto financiero general.
7. En **Gestión**, ya no deben ofrecer actualizar/cancelar/aplicar otra vez.
   Una segunda pestaña antigua debe rechazar la repetición sin otro egreso.
8. Comprueba filtros de estado **Aplicada**, retorno al listado y Excel con los
   mismos filtros. Guarda las cifras y reporta el resultado para incorporar la guía
   confirmada; no marcar aceptada sólo porque pasaron pruebas automáticas.

## Controles negativos recomendados

- Cuenta con saldo insuficiente: bloquea sin aplicar ni producir saldo negativo.
- Cuenta inactiva, otra institución o moneda: no se puede usar.
- Motivo sólo Ingreso: no aparece como motivo de egreso.
- Cambiar saldo/configuración después de revisar: exige revisar de nuevo, sin egreso.
- Guardar/aplicar simultáneamente desde dos pestañas: sólo una operación definitiva.
- Personal sin permiso financiero: puede usar la preparación autorizada por sus
  permisos, pero no aplicar desde una cuenta.

Fuera de alcance: cálculo de ISR/IMSS/Infonavit, timbrado, dispersión bancaria,
préstamos/incidencias/prorrateos automáticos, pago independiente de aportaciones
patronales, recibos fiscales y reversión especializada de nómina aplicada.
