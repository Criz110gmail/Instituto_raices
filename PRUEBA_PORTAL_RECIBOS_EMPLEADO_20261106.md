# Acceso opcional del empleado a sus recibos — etapa 2

El usuario confirmó además el recorrido docente con Bruce Wayne: listado propio,
PDF RN-P-000008 pagado por $6,193.55 con puesto, RN-P-000005 cancelado y regreso
al portal de maestros. RN-P-000006 pagado $80.00 indica que su fotografía no
conserva puesto. Incorporado a la Guía de procesos mediante V164.

Estado: el usuario confirmó el 06/11/2026 que pasó las pruebas; indicó expresamente
que al quitar el acceso dejó de ver el módulo y al volver a habilitarlo lo vio de
nuevo. La prueba de la cuenta docente compartida también fue confirmada como se
describe arriba. Las pruebas técnicas usan datos ficticios. Este
ejemplo utiliza el cierre de Sofía que ya existe: no volver a pagar ni modificar
préstamos, nóminas o cuentas financieras.

**Continuidad del intento del 06/11:** el primer alta de Sofía guardó el usuario
`sofia.prueba.recibos` en estado invitado, pero se perdió la pantalla del enlace por
un error. No volver a habilitar ni crear otra cuenta. En la pantalla de acceso de
Sofía pulsa **Generar enlace de contraseña**, confirma y copia el nuevo enlace de
activación de 48 horas; invalida la invitación anterior. Continúa en el apartado 2.

## 1. Habilitar desde Empleados

1. Con el administrador autorizado, abre **Personal y nómina → Empleados**.
2. Busca **EMP-PREST-CERO-01 · Sofía Prueba Neto Cero** y entra en Editar.
3. Abre la pestaña **Acceso**, sección **Accesos del empleado**, y **Administrar acceso a sus recibos**.
   Requiere administración de Empleados y de condiciones salariales, además del
   alcance institucional correspondiente. No se gestiona en Usuarios administrativos.
4. Debe indicar **Desactivados / Sin cuenta** si nunca se habilitó. Revisa el nombre
   de usuario sugerido; puedes cambiarlo si existe. Usa, por ejemplo,
   `sofia.prueba.recibos`. Anota el usuario que realmente guardes.
5. Captura un correo de contacto. En capacitación puedes usar
   `sofia.prueba@example.invalid`: no recibirá correos; el sistema entrega un enlace.
6. Pulsa **Habilitar acceso a recibos**. Revisa empleado, usuario y alcance en el
   modal. Pulsa **Cancelar**: conserva la captura y no crea la cuenta.
7. Repite y confirma. Copia de forma privada el enlace mostrado antes de salir.
   Dura 48 horas y sólo sirve una vez. Habilitar no cambia ningún saldo ni pago.

## 2. Activar y consultar como Sofía

1. Abre el enlace en otro perfil o ventana privada para conservar tu sesión admin.
2. Tú defines y confirmas la contraseña; no la compartas con el agente.
3. Abre **http://localhost:8080/personal/acceso**. Entra con el usuario guardado.
   Si existe ambigüedad institucional, usa `CÓDIGO_INSTITUCIÓN\usuario`, por ejemplo
   `INSTITUTO_RAICES\sofia.prueba.recibos` si ése es el código de tu institución.
4. Debe aparecer **Mis recibos de nómina** con los recibos de Sofía únicamente.
   No aparecen empleados ajenos, menú administrativo ni cuentas financieras.
5. Localiza el cierre **Neto cero confirmado** de la prueba anterior. El PDF debe
   mostrar las percepciones por nombre e importe que suman **$100.00**, deducciones
   por nombre e importe que suman **$100.00** y **neto $0.00**, con logo y sus datos.
   No debe mostrar cuenta bancaria, responsable, folio del préstamo ni su saldo.
6. Prueba búsqueda, estado y fechas del periodo cubierto que muestre el recibo.
   Fuera del periodo: lista vacía; rango invertido: aviso conservando filtros.
7. Cierra sesión y vuelve a entrar: debe abrir al primer intento en su portal.

## 3. Desactivar sin borrar historia

1. Mantén abierta la sesión de Sofía. Desde admin vuelve al acceso del empleado.
2. Pulsa **Desactivar acceso a recibos**. Cancelar no cambia nada; luego confirma.
3. En la ventana de Sofía intenta recargar el listado o volver a abrir el PDF.
   Debe denegarse el acceso aunque la sesión fuera anterior a la desactivación.
4. La cuenta dedicada queda inactiva y no puede iniciar sesión. Sus recibos y
   empleados siguen en la consulta administrativa; no cambian saldos financieros.
5. Si deseas dejarla habilitada, reactiva desde el mismo formulario. Conserva el
   mismo usuario; si ya había activado, conserva la contraseña. **Generar enlace
   de contraseña** crea un enlace privado de 30 minutos y un solo uso; no envía correo.

## 4. Cuenta docente compartida, sólo si ya tienes un maestro vinculado

No crees otra nómina para este punto. Usa un empleado con perfil docente y cuenta
existente. Al habilitar recibos debe reutilizar su usuario sin pedir otra cuenta ni
cambiar su contraseña. Tras cerrar y volver a iniciar sesión docente aparece
**Mis recibos**; el portal personal ofrece **Volver a maestros**.

Desactivar recibos bloquea la consulta personal, pero el portal docente continúa
activo. Generar un enlace para la contraseña compartida requiere también administrar
Maestros y el aviso explica que afecta ambos accesos. Si primero se creó cuenta
personal y después se habilita la docente, se reutiliza la misma cuenta por esa
acción explícita. Nunca fusionar personas sólo porque coincida su nombre o correo.

## Resultado para documentar

Confirma por separado: alta/cancelación del modal, activación y primer login,
recibo propio/PDF, filtros, desactivación con sesión abierta y, si aplica, cuenta
docente compartida. Registrar sólo los puntos que realmente pruebes. No publicar
como CONFIRMADA en la Guía de procesos antes de tu aceptación.
