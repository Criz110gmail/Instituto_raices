# Prueba de puestos y condiciones salariales — V116

Implementación disponible; aceptación humana todavía pendiente. No es nómina ni
pago, ni calcula impuestos, deducciones, proporcionales o conversiones de sueldo.
No usar empleados reales sin autorización; no reactivar a María que quedó en baja.

## 1. Autorizar el acceso privado

1. Con administrador institucional identificado entra a **Administración → Seguridad
   y soporte → Roles y permisos**. No usar acceso de recuperación.
2. Edita el rol autorizado para esta prueba, verificando todas las cuentas afectadas.
   Marca **Empleados**, **Datos laborales de empleados** y el módulo nuevo
   **Puestos y condiciones salariales**. El último no se asignó automáticamente.
3. Pulsa **Guardar cambios**, revisa el modal y confirma; Cancelar no aplica cambios.
4. Cierra sesión e inicia nuevamente con la cuenta administrativa autorizada.
   Tener permiso docente o laboral general no concede acceso salarial.

## 2. Elegir un empleado activo existente

1. **Administración → Personal y nómina → Empleados** → busca Bruce Wayne
   (`DOC-PRUEBA-02` si conserva ese código) → **Ver expediente**.
2. En **Datos laborales**, verifica actividad laboral confirmada y fecha de ingreso
   legítima. Perfil docente activo y expediente Activo NO acreditan actividad laboral.
3. Si está Por confirmar, completa actividad, puesto textual e ingreso únicamente si
   son datos correctos y autorizados, usando Guardar información laboral y su modal.
   Si está en baja, no lo reactives para este ejemplo: utiliza otro empleado ficticio
   activo autorizado. No cambiar cuentas, materias, foto ni adscripciones.
4. Abre **Condiciones salariales → Abrir condiciones salariales**. Si la pestaña no
   aparece, revisa permisos y nueva sesión, no inventes otro empleado.

El puesto textual previo de Datos laborales se conserva como referencia. El puesto
de cada condición se selecciona del catálogo y se congela con ese sueldo; no se
sobrescribe automáticamente el texto previo o el historial laboral.

## 3. Registrar el puesto

1. Abre **Administración → Personal y nómina → Puestos laborales** para consultar
   todos los puestos, filtrar y exportar. También puedes entrar desde condiciones,
   **Registrar condición → Administrar puestos**, en otra pestaña sin cerrar el expediente.
2. En Buscar puestos escribe `DOC-PRI-TEST`, aplica filtros y reutiliza el registro
   si ya existe y coincide. No duplicarlo.
3. Si no existe pulsa **Nuevo puesto**: abre un formulario separado del listado.
   Captura Código `DOC-PRI-TEST`, Nombre `Docente de primaria`,
   Descripción `Puesto de prueba de condiciones salariales`, Puesto activo marcado.
4. Pulsa **Crear puesto**. En el modal comprueba código/nombre/estado; pulsa primero
   **Cancelar** para verificar que no se registra y permanecen los datos en el formulario.
   Luego crea y confirma: regresa al listado conservando sus filtros y página.
5. Búscalo y revisa estado **Activo** en verde. **Exportar Excel** respeta búsqueda
   y estado. Al editar nombre/código después, las condiciones previas conservan su
   fotografía histórica. Inactivo impide nuevas condiciones, no elimina las existentes.
   Usa **Editar** en la fila para abrir su formulario; **Guardar cambios**, **Cancelar**
   y **Volver al listado** conservan búsqueda, estado, página y tamaño de origen.

## 4. Condición inicial mensual

Vuelve al expediente salarial → **Registrar condición**. Para el ejemplo, en
institución MXN y antes del01/11/2026:

| Campo | Valor |
|---|---|
| Puesto | Seleccionar `DOC-PRI-TEST · Docente de primaria` desde la ayuda |
| Sueldo bruto base | 12000.00 |
| Base del sueldo | Mensual |
| Frecuencia de pago | Mensual |
| Inicio de vigencia | 01/11/2026 |
| Fin previsto | Vacío |
| Motivo | Condición inicial mensual de prueba, sin generación de nómina. |

Diez puestos activos al enfocar; desde tres caracteres se consulta. Si no aparece
el puesto recién creado, vuelve a enfocar: se refresca la ayuda. Moneda fija según
institución, no recibida de un campo editable. Base y frecuencia son cosas distintas:
no divide automáticamente un mensual entre cuatro o dos ni calcula netos.

1. Captura el importe y quita foco: debe verse en moneda, sin variar al usar scroll.
2. Pulsa **Revisar y registrar condición**. Revisa el modal con empleado, puesto,
   importe, base, frecuencia, fechas y motivo.
3. Pulsa **Cancelar**: se conserva la captura, no hay condición nueva ni historial.
4. Repite el envío y **Confirmar y guardar**. Recarga: aparece la condición.
5. En **Consultar condiciones → Fecha a consultar** elige01/11/2026 y pulsa
   **Consultar fecha**: $12,000.00 MXN, base Mensual/pago Mensual. Antes del01/11 no
   existe condición aplicable, si no había otra anterior. No es un pago realizado.

Si ya pasó esa fecha, usa un inicio futuro autorizado y adapta todas las fechas del
ensayo; esta versión no permite configurar vigencias retroactivas. Si existen otras
condiciones, no ejecutes el ejemplo a ciegas: revisa primero sus programaciones.

## 5. Programar un aumento sin borrar el sueldo anterior

1. En **Registrar condición** selecciona el mismo puesto; importe13000.00,
   base Mensual, frecuencia Mensual, inicio01/01/2027 y fin vacío.
2. Motivo: `Aumento mensual programado de prueba a partir de enero de 2027.`
3. Pulsa **Revisar y registrar condición**, revisa fechas/importe y confirma.
4. En **Consultar condiciones**, consulta31/12/2026: $12,000.00 MXN. Consulta
   01/01/2027: $13,000.00 MXN. Tabla conserva condición anterior hasta31/12/2026
   y nueva desde01/01/2027, sin fecha de fin. Si ambas fechas son futuras, ambas
   filas dicen Próxima; esto no altera el importe aplicable a cada fecha.
5. Abre **Historial**: registro inicial, cierre de vigencia anterior y registro del
   aumento con actor/fecha/motivo. No editar ni eliminar esos eventos.

No se duplica sueldo por plantel. Cambiar esta configuración no modifica foto,
datos personales, puesto textual previo, usuario, asignaciones docentes o cuentas.

## 6. Filtros, Excel y validaciones

1. Consultar condiciones → buscar `Docente de primaria`, vigencia Próximas,
   Vigente desde01/01/2027 y hasta31/01/2027. Aplica filtros: sólo debe quedar
   la condición de13000 si no hay otras coincidentes. El rango consulta vigencias
   que tocan esas fechas, no la fecha de captura.
2. Exportar Excel: mismas filas, código/nombre del empleado, puesto histórico,
   bruto numérico con dos decimales, moneda, base/frecuencia, fechas y motivo.
3. Intenta inicio anterior a hoy, importe0 o fin anterior al inicio: debe rechazarse
   sin cerrar vigencias anteriores ni crear historial parcial. Corrige o recarga
   después. Si se cambió el estado laboral mientras capturabas, se exige recargar.
4. No se admite otra condición que empiece el mismo día ni insertar un cambio antes
   de una programación posterior ya guardada. No sobrescribe el historial.
5. Dos pestañas con la misma captura/revisión: después de guardar una, confirmar la
   otra debe avisar que cambió información, sin duplicar sueldo o cierres. Recarga
   para conocer el estado real. Prueba sólo si quieres validar concurrencia adicional.

## 7. Cancelar una programación futura (opcional e independiente)

Realizar sólo si se quiere deshacer el aumento de prueba, no después de que ya inició.

1. **Consultar condiciones** → fila de13000 → **Cancelar programación**.
2. Captura motivo `Cancelar aumento programado de prueba; conservar sueldo anterior.`
3. Pulsa **Revisar cancelación**; primero Cancelar el modal no debe cambiar nada.
4. Repite y confirma. La fila de13000 queda Cancelada; condición12000 recupera su
   fin original vacío. Consulta01/01/2027:12000. El historial conserva ambas operaciones.
5. Sólo se permite cancelar la última programación que aún no comienza. Si hay otra
   posterior se pide resolverla primero; condiciones vigentes/históricas no se borran.

## 8. Privacidad y cierre del ensayo

- Cuenta sin módulo salarial: no ve pestaña ni importes ni puede consultar rutas,
  autocompletado, Excel o historial salarial. Laboral general no incluye sueldos.
- Permiso técnico salarial de lectura permite consultar, no guardar; la pantalla
  de Roles sigue concediendo paquetes completos por módulo, no sólo lectura visual.
- Cuentas familiar/docente, soporte académico, recuperación y otra institución no
  acceden al expediente salarial. No otorgar privilegios para intentar eludirlo.
- Cuentas financieras deben conservar exactamente el saldo anterior: no hubo
  egresos, pagos, préstamos, retenciones, nóminas o recibos.
- Aceptación humana pendiente hasta que el propietario confirme resultados. La guía
  de este nuevo proceso se incorporará después de probarlo, no como CONFIRMADA ahora.
