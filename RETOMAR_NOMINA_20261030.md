# Retomar nómina en otra computadora

Actualizado el **30/10/2026**. Este documento resume el punto más reciente; no
reemplaza la lectura completa de AGENT.md ni las restricciones del repositorio.

## Dónde nos quedamos

El usuario confirmó **todas las pruebas de permisos de nómina** y pidió documentar
el cierre antes de cambiar de computadora. No pidió nuevos cambios funcionales,
pagos, commit ni push. Guía integrada V148:
**Nómina: quién puede ver sueldos y conceptos del empleado**.

Prueba confirmada:

- `prueba` (sin s final), rol `PRUEBA_PERSONAL_BASE`: consulta empleados y datos
  laborales, sin sueldos, puesto/historial salarial ni configuración de nómina.
  Acceso directo y Excel de salarios/configuración denegados. Excel general de
  empleados permitido sin importes privados.
- `pruebas` (con s final), rol `QA_SALARIOS_CONSULTA`: acceso salarial y de nómina.
  Simulación de Luis 16–31/12/2026: $516.13 + $41.29 = $557.42.
- Administrador retiró sólo **Empleados — Configuración de nómina** del rol de
  ensayo; nueva sesión de pruebas conservó salarios y perdió configuración,
  incluidos enlaces directos y Excel. Restauró el permiso y verificó acceso.
- Lectura posterior confirma ambos roles en su estado original. No cambiar roles
  automáticamente por herramientas. No se solicitaron ni registraron contraseñas.
- El rol llamado consulta salarial también contiene permisos ADMINISTRAR: NO
  afirmar que se probó un rol de sólo lectura ni permisos de pago independientes.

## Qué sigue: tres ejemplos del plan de cierre

1. **Nómina con varios empleados**, pagar juntos y cancelar únicamente el pago de
   uno; los demás pagos deben conservarse. Es el siguiente ejemplo acordado.
2. **Empleado sin sueldo base pero con conceptos**: cálculo y aviso claros.
3. **Neto cero o negativo**: bloqueo explicado, sin egresos ficticios. Verificar el
   comportamiento actual; no declarar implementada una liquidación contable en cero.

No se han ejecutado estos tres ejemplos. Antes de entregar pasos, revisar datos,
permisos, periodos, reservas, cuentas y rutas reales de la computadora nueva. Elegir
fechas cercanas, no meses lejanos que obliguen a avanzar mucho el calendario.

Pendiente editorial separado de esos tres ensayos: el caso de posponer TODO Luis y
pagarlo después está confirmado en su manual, pero aún falta su guía integrada
específica. No requiere volver a pagar NPI-9 para documentarlo.

Chequeras, rediseño de préstamos, recibos nuevos y permisos de operación separados
son entregas posteriores, no pruebas ya aprobadas. La confirmación secuencial desde
dos pestañas sí se probó; una carrera de transacciones realmente simultáneas no.

## Evidencia que NO debe repetirse ni borrarse

| Registro local de referencia | Estado al cambiar de equipo |
|---|---|
| BOR-000008 / NPI-9, Luis en noviembre | Pago completo pospuesto y después pagado individualmente, $1,000 desde CAJA |
| BOR-000009, Luis 01–15/12/2026 | Borrador, revisión 1 $483.87 y revisión 2 $522.58; conservar sin pagar, actualizar ni cancelar |
| BOR-000010, Luis 16–31/12/2026 | Confirmada sin pagos: No aplicar todo el pago original de $557.42; cero pagos y pendientes |
| Luis, concepto TRANS-RED-TEST | $80 mensuales, 01–31/12/2026, activo; sueldo asignado $1,000 mensual |
| Luis, EXTRA-RED-TEST, asignaciones locales 10 y 11 | Ambas canceladas, conservar historial |

BOR10 tiene estado técnico PAGADA, pero tratamiento NO_APLICAR, importe efectivo0
y ningún pago: al usuario se explica **Confirmada sin pagos**, no dinero entregado.
El bloqueo de propuesta desactualizada quedó confirmado: A abierta antes de agregar
un apoyo desde B, confirmar A sin recargar rechazó el guardado; sólo después se
canceló el apoyo temporal. Revisiones de BOR9 intactas y sin revisión3.

Saldos verificados sólo en lectura al cierre local:

| Cuenta | Saldo |
|---|---:|
| CAJA | $3,762.85 |
| BBVA_COBROS | $274.29 |
| CAJA-CORTE-TEST | $1,150.00 |

Los IDs y saldos son referencias de esta base, no valores para forzar en la otra.
Buscar por códigos y volver a leer saldos. No cambiar saldo inicial ni duplicar
pagos para reconstruir el ejemplo. DOC-PRI-TEST del catálogo está en $1,000; Bruce
conserva su sueldo histórico de $12,000: no confundir catálogo con copia histórica.

## Interfaz ya entregada

- Patrón de catálogo: listado, filtros, Excel y alta/edición en formulario separado.
- Acciones de filtros/tablas `module-action`, sin sustitución global. Turquesa en
  acciones específicas solicitadas. Temas claro/oscuro y respuesta móvil.
- Decisiones sobre conceptos: **Resumen / Conceptos / Historial de decisiones**.
  Historial conserva pestaña al paginar y muestra acciones en palabras legibles.
- Pagos por empleado y conceptos pospuestos conservan filtros visibles después de
  consultar. Script versionado `preparacion-nomina.js?v=20261030-filtros2`.
- Regreso de revisión al historial del borrador; desglose vuelve a su revisión.
- Detalle, alta y cambio de concepto muestran ruta, empleado y sección actual;
  **← Volver a configuración** conserva filtros/página/tamaño.

## Cómo retomar con seguridad

1. Leer AGENTS.md, **AGENT.md completo** y CONTEXTO_PROYECTO.md. Leer el modelo de
   entidades antes de ampliar dominio. Revisar cambios locales antes de editar.
2. Seguir el apartado de arranque de AGENT.md. Nunca leer, imprimir, publicar ni
   confirmar contenido real de `.env`. No reconstruir credenciales desde logs.
3. **Git no transporta PostgreSQL ni los archivos subidos.** Un pull actualiza código
   y migraciones, no usuarios, roles, empleados, borradores, pagos ni comprobantes.
   Preguntar o inspeccionar la base nueva antes de asumir que existen los registros.
4. Si se requiere trasladar la misma base, acordar un respaldo/restauración separado
   y seguro. No sobrescribir ni vaciar una base existente, ni subir respaldos a Git.
5. Dar el siguiente ejemplo con rutas reales y lenguaje sencillo: para qué sirve,
   qué capturar, qué debe verse, qué no cambia y dónde detenerse. Documentar sólo
   como confirmados los resultados que el usuario efectivamente valide.

No se hicieron commit/push por esta solicitud: los documentos y la migración deben
viajar mediante el flujo Git que realice el usuario.

## Mensaje sugerido para el agente en el otro equipo

> Lee AGENTS.md, AGENT.md completo, CONTEXTO_PROYECTO.md y RETOMAR_NOMINA_20261030.md.
> Ya terminamos y confirmamos los permisos de nómina. Faltan tres ejemplos del plan
> de cierre; sigue nómina con varios empleados y cancelar sólo un pago. Primero
> revisa la base disponible: puede ser diferente. No repitas los pagos terminados,
> no modifiques BOR9/BOR10 si existen, ni leas .env. Dame el siguiente ejemplo con
> rutas reales, palabras sencillas y fechas cercanas; no operes pagos por mí.
