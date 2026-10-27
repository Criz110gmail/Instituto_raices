# Prueba manual — Configuración de nómina en el empleado (entrega 2)

26/10/2026. **Pendiente de aceptación humana.** No genera nómina ni movimientos
financieros. No continuar el ejemplo antiguo de calendarios/configuración integrada.

## Antes de empezar

Esta computadora tiene a Diana (empleado6) y dos asignaciones confirmadas:
APOYO_TECNICO_40,10000,01–15/06/2027; APOYO_TECNICO_48,14000,desde16/06/2027.
No volver a crearlas, no cambiar sus salarios para este ensayo. En otra computadora
verificar primero los datos locales. No usar nombres como clave de identidad.

Actualización27/10/2026: el usuario terminó los casos manuales viables de entrega1:
selector de fechas, concurrencia de asignación con Elena, acceso salarial autorizado
y privacidad con Laboral permitido/Salarios omitido, incluida URL/Excel. No repetir.
La separación sólo consulta/administración sigue pendiente: la interfaz actual
concede paquetes completos por módulo. No atribuir al usuario un POST de fecha
inválida forzado; esa validación de servidor tiene cobertura automatizada.

Las pruebas ya confirmadas no se vuelven a marcar pendientes: historial y Excel,
consulta por fecha, bloqueo de asignación intermedia, puesto inactivo, código
duplicado, vacíos/horas0/169/sueldo0, dos pestañas editando catálogo y selección
de puesto modificada antes de confirmar. El usuario confirmó también salir sin
crear otra asignación y conservar ambas originales.

## 1. Habilitar únicamente a quien deba configurar nómina

En **Administración → Seguridad y soporte → Roles y permisos**, editar el rol
administrativo autorizado. Ubicar **Empleados — Configuración de nómina**.
Activar su casilla de módulo, revisar y confirmar el modal. Mantener Empleados,
Datos laborales y Puestos y condiciones salariales. En este ensayo usar el rol
QA_SALARIOS_CONSULTA del usuario **pruebas**, con s; pese al nombre no es sólo
lectura. No modificar PRUEBA_PERSONAL_BASE del usuario **prueba**, sin s.

La migración no concede estos permisos a ningún rol. Cerrar sesión y entrar de
nuevo tras modificar el rol para renovar las autoridades. No usar recuperación,
portal familiar ni portal docente para administrar nómina.

## 2. Revisar el menú y abrir el expediente

1. Abrir **Personal y nómina → Puestos laborales**: debe ser el listado del catálogo,
   sin submenús Jornadas ni Plantillas de puesto y sueldo. Sus históricos no se borran.
2. Menú principal: Puestos → Conceptos de nómina → Empleados → Periodos → Nóminas.
   La configuración individual ya no se captura desde un menú separado.
3. Entrar a **Empleados**, buscar el código de Diana y pulsar **Ver expediente**.
4. Abrir **Configuración de nómina** dentro del expediente.
5. La tarjeta Sueldo base muestra únicamente lo aplicable **hoy**. Como las
   asignaciones de Diana son futuras, puede indicar Sin sueldo base aplicable hoy.
   Usar Ver puesto e historial para comprobar sus fechas. No crear un sueldo cero.
6. La tabla muestra los conceptos adicionales de ese empleado con filtros/Excel.

## 3. Crear conceptos de ensayo sin duplicar los existentes

Entrar a **Personal y nómina → Conceptos de nómina**. Buscar cada código antes de
crear. Usar Nuevo registro, guardar con revisión/modal y regresar al listado.

| Código | Nombre | Tipo | Clasificación del catálogo |
|---|---|---|---|
| TRANS-RED-TEST | Transporte de prueba de rediseño | Percepción | Habitual |
| DED-RED-TEST | Deducción semanal de prueba | Deducción | Habitual |
| QUIN-RED-TEST | Apoyo quincenal de prueba | Percepción | Habitual |
| EXTRA-RED-TEST | Apoyo extraordinario de prueba | Percepción | Ocasional |

Descripción: `Concepto de ensayo de configuración; sin cálculo fiscal automático.`
Dejar activos. Aquí no se captura dinero; el importe corresponde a cada empleado.

## 4. Transporte mensual de Diana

1. Volver a **Empleados → Diana → Ver expediente → Configuración de nómina**.
2. Pulsar **Agregar concepto**. Se abre un formulario separado; empleado protegido.
3. En Percepción o deducción, enfocar: hasta10 sugerencias; escribir3 caracteres
   o más para buscar TRANS-RED-TEST y seleccionar su sugerencia exacta.
4. Esperar Tipo/moneda y que se habilite Revisar y guardar concepto.

| Campo | Valor |
|---|---|
| Importe de referencia | 50.00 |
| ¿Cuándo se aplica? | Se repite durante su vigencia |
| Este importe corresponde a | Un mes |
| Aplicar desde | 01/06/2027 |
| Último día incluido | Vacío |
| Motivo | Transporte mensual autorizado para prueba desde junio de 2027. |

5. Pulsar **Revisar y guardar concepto**: comprobar empleado, concepto,50MXN,
   mensual, fechas y motivo. Primero **Cancelar**: conserva captura y no guarda.
6. Revisar de nuevo y confirmar. Regresa a la configuración; una fila50/Mensual,
   desde01/06/2027 sin fin, Próxima. Nada de dinero se mueve.

## 5. Programar aumento del transporte a80

1. Desde la tabla anterior, pulsar **Ver** de TRANS-RED-TEST.
2. Abrir pestaña **Gestión → Programar cambio de importe o frecuencia**.
3. Mantener concepto; importe80, frecuencia Un mes, Aplicar desde01/07/2027,
   fin vacío. Motivo: `Aumento del apoyo de transporte a80 desde julio de2027.`
4. Revisar, probar Cancelar y luego confirmar.
5. Volver a configuración. Debe mostrar:

| Concepto | Importe | Frecuencia | Inicio | Fin |
|---|---:|---|---|---|
| Transporte | 50 | Mensual | 01/06/2027 | 30/06/2027 |
| Transporte | 80 | Mensual | 01/07/2027 | Sin fin |

No se sobrescribe el50 anterior. Abrir Ver → Historial para revisar actores,
fechas y motivo. Una nómina guardada no cambia por esta programación.

## 6. Agregar deducción semanal independiente

En la configuración de Diana, **Agregar concepto → DED-RED-TEST**. Importe100,
recurrente, **Una semana**, inicio01/06/2027, sin fin y motivo:
`Deducción semanal fija de prueba; no es impuesto calculado ni préstamo.`
Revisar modal y confirmar. Debe verse100/ Semanal. No sumar50mensuales y100semanales
como si fueran una misma frecuencia; el neto y las conversiones serán entrega3.

Después agregar **QUIN-RED-TEST**, importe150, recurrente, **Una quincena**,
inicio01/06/2027,sin fin,motivo `Apoyo quincenal fijo de prueba desde junio2027.`
Revisar y confirmar. Debe verse150/Quincenal. Así se prueban las tres frecuencias,
sin calcular todavía un neto ni asumir mensual/2 o mensual/4.

## 7. Concepto de una sola vez

Se necesita un periodo programado que comience después del ingreso de Diana.
Si ya existe uno julio2027, comprobar fechas y reutilizarlo. Si no existe:

1. **Personal y nómina → Periodos de nómina → Nuevo periodo**.
2. El nuevo formulario ya no solicita calendario. Captura únicamente código,
   inicio, fin y pago previsto.
3. Código JUL-2027-RED-TEST, inicio01/07/2027, fin31/07/2027 y pago31/07/2027.
4. Revisar y confirmar. Si ya existe esa cobertura, reutilizar el periodo. No crear
   calendarios para eludir validaciones. Para continuar hasta el pago ordinario,
   seguir `PRUEBA_NOMINA_FLUJO_BASE_20261027.md`; el cálculo nuevo se valida allí.

En **Empleados → Diana → Configuración de nómina → Agregar concepto**:
EXTRA-RED-TEST, importe250, **Una sola vez, en un periodo**. Se oculta frecuencia
recurrente y aparece la ayuda de Periodo. Seleccionar el de julio; inicio/fin se
completan y protegen automáticamente. Motivo `Apoyo único autorizado para julio2027.`
Revisar y confirmar. Debe mostrar250/Una sola vez y el código del periodo, sin
convertirlo a250mensuales ni repetirlo en otros periodos. Su consumo/pago se habilita
con el motor posterior; esta etapa sólo guarda la configuración.

## 8. Filtros, Excel y retorno

1. En la configuración de Diana, enfocar Concepto de nómina: ayuda10/3.
2. Seleccionar TRANS-RED-TEST. Buscar por ID, no por el texto concatenado.
3. Frecuencia Mensual, estado Próximas, Desde01/06/2027/Hasta31/07/2027.
4. Aplicar filtros: dos vigencias de transporte. Excel: exactamente esas dos,
   importes50/80, frecuencia, fechas, estado y motivos; no incluir deducción/extra.
5. Abrir Ver y Volver a configuración: conservar filtros y página.
6. Limpiar; filtrar Semanal: sólo deducción100; Una sola vez: sólo extra250.
   Quincenal: sólo apoyo150.
7. Rango Desde posterior a Hasta: error claro, sin consulta engañosa.
8. Si hay conceptos legados, se muestran como **Por nómina · anterior**, no mensual
   inferido. Para convertir uno, programar cambio con nueva frecuencia y fecha
   futura explícitas; conservar original y nunca generar otro sueldo base.

## 9. Cancelar una programación futura sin borrar historia

Desde Ver del transporte80 → Gestión → Cancelar programación futura. Capturar
motivo `Retirar incremento de prueba de transporte.` y Revisar cancelación.
Cancelar primero el modal: no cambia nada. Después confirmar.

Esperado:80 queda Cancelada,50 recupera su fin abierto; auditoría conserva ambas.
Repetir el POST no debe provocar otra restauración. Para dejar el aumento original
activo se necesitaría programarlo nuevamente con otro registro; no hacerlo sin
acordar cuál será el resultado final del ensayo.

Terminar vigencia se prueba con un concepto **vigente hoy**, no con las filas
futuras de Diana: empleado de ensayo activo e ingreso confirmado, nuevo concepto
desde hoy, sin fin; Ver → Gestión → Dejar de aplicar, último día hoy o futuro,
motivo y modal. Ese día se incluye; desde el siguiente deja de aplicar. No borrar.
Los conceptos únicos conservan su cobertura; no se recorta arbitrariamente el periodo.

## 10. Seguridad y diseño

- Sin el módulo Configuración de nómina: no pestaña; URL, contexto y Excel no
  autorizados, aunque tenga acceso a datos personales. La prueba manual de sólo
  lectura queda fuera: los roles de interfaz conceden paquetes completos. No
  prometer que el nombre QA_SALARIOS_CONSULTA impida administrar.
- No permitir concepto Sueldo base ni préstamo Con saldo como concepto adicional.
- Rechazar importe0/negativo/decimales extra, fechas inválidas, duplicados y versiones
  obsoletas. El catálogo no cambia el tipo/nombre histórico de una fila guardada.
- Revisar claro/oscuro,390px y escritorio, controles alineados, ayudas no rojas,
  monto seguro sin cambio por scroll y modales con Cancelar/Escape.
- Las configuraciones nuevas quedan en Revisión si se intenta usar el motor mensual
  anterior. No intentar pagar usando ese motor: requiere entrega3.
- Cuentas y movimientos deben permanecer intactos durante todo este ensayo.

Reportar cada resultado antes de incorporarlo como proceso probado a la guía del
sistema. Este documento propone el ensayo; no declara aceptación humana.
