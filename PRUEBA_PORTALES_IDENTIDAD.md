# Portales e identidad: prueba completa

## Qué vamos a comprobar

Cada portal puede tener los colores de tu escuela, tanto al iniciar sesión como en el menú y las acciones después de entrar. Puedes revisar el resultado antes de publicarlo y compartir su enlace. Cambiar Familias no debe cambiar Administración, Maestros ni Personal. Personal incluye Mis recibos de nómina.

## 1. Habilitar el módulo

1. Con un administrador de roles, abre **Roles y permisos** y edita el rol de la persona que hará la prueba.
2. Activa **Portales e identidad - Administrar** para editar, guardar, publicar y recuperar versiones. **Portales e identidad - Consultar** sirve para ver las tarjetas y copiar enlaces; no permite editar.
3. Guarda y confirma. Cierra sesión y vuelve a entrar con ese usuario para cargar sus derechos.
4. El rol necesita alcance de **toda la institución**.
5. Entra a **Diseño → Portales e identidad**.

Los permisos nuevos no se asignan automáticamente a los roles. No hace falta conceder Diseño web para administrar este módulo.

## 2. Revisar las tarjetas y enlaces

Deben aparecer Administración, Familias, Maestros y Personal · Mis recibos. Cada tarjeta muestra la vista publicada y su estado.

1. Pulsa **Copiar enlace** en Familias y pégalo en otra pestaña. Debe identificar tu institución y abrir el acceso familiar.
2. Prueba también **Abrir portal**.
3. No compartas el enlace de vista previa administrativa: comparte el que entrega la tarjeta.
4. Si el enlace contiene `localhost` o `127.0.0.1`, sólo funciona en esta computadora. Para compartirlo fuera se necesita una dirección pública de la instalación.

La instalación puede definir `APP_PORTALES_URL_PUBLICA` con su URL pública real; si no está definida se usa la dirección desde la que abres el sistema. No se pide contraseña ni se agrega un permiso al compartir un enlace.

## 3. Personalizar Familias sin publicar todavía

1. En Familias pulsa **Personalizar**.
2. En **Color principal**, escribe `#194F3D`.
3. En **Fondo exterior**, escribe `#F5F8EF`.
4. En **Botones y acentos**, escribe `#F5C542`.
5. Revisa que la zona visual sea verde y el botón amarillo con texto oscuro.
6. Pulsa primero el campo que quieras cambiar y después un color de la paleta. La etiqueta **Editando** indica qué color estás cambiando. Los otros dos deben conservarse.
7. Prueba **Combinaciones y colores recientes**. La opción **Usar colores institucionales** copia los colores del sitio web publicado de tu institución, cuando existe.
8. Prueba `#XYZ` en un hexadecimal: debe marcarlo como inválido y conservar el último color válido de la vista. Corrígelo antes de guardar.
9. Cambia entre **Computadora / Celular** y **Claro / Oscuro** en la vista previa.
10. El nombre y el logo se toman de la institución al guardar. Si no tiene logo institucional, se puede reutilizar un logo visible ya publicado en Diseño web; si tampoco existe se muestra la marca de respaldo. El logo se conserva con cada versión.

Editar los colores todavía no cambia la pantalla pública. Los formularios de la vista previa están deshabilitados: sirven para revisar el diseño.

## Deshacer mientras editas

1. Cambia un color y pulsa **Ctrl+Z** (Windows/Linux) o **Cmd+Z** (Mac). Debe volver al color anterior.
2. Pulsa **Ctrl+Shift+Z / Cmd+Shift+Z** o **Rehacer** para recuperar el cambio. En Windows/Linux también funciona Ctrl+Y.
3. Prueba los botones **Deshacer / Rehacer**. Cuando no hay más pasos, el botón queda deshabilitado.
4. Aplica una combinación: Deshacer debe recuperar los tres colores anteriores en un solo paso. También funciona con colores institucionales y Restablecer diseño original.
5. Si deshaces y luego haces un cambio distinto, ya no se puede rehacer el cambio descartado.

Este historial conserva hasta 100 cambios mientras tienes abierta la edición. No guarda ni publica nada. Al guardar o recargar comienza una nueva edición; para recuperar algo guardado usa **Historial de versiones**.

Si ya publicaste y quieres regresar: **Historial de versiones → Recuperar como borrador → Colores y vista previa → Publicar diseño**. Confirma la publicación. Así vuelves al diseño anterior conservando todas las versiones.

## 4. Guardar y publicar

1. Pulsa **Guardar borrador** y primero **Cancelar** en el modal. Debes seguir con los mismos colores capturados.
2. Guarda otra vez y confirma.
3. Recarga: deben conservarse colores, nombre y logotipo guardados.
4. Desde la tarjeta abre el portal público: todavía debe mostrar su diseño anterior.
5. Pulsa **Publicar diseño**, revisa el modal y primero cancela. El portal público debe conservarse.
6. Publica nuevamente y confirma.
7. Abre el enlace de Familias en una pestaña nueva: debe mostrar los colores publicados.
8. Comprueba las otras tres tarjetas: deben conservar su diseño.

Si hay cambios sin guardar, el editor indica **Guarda el borrador antes de publicar**. Publicar usa únicamente la versión guardada y revisada.

## 5. Recuperar una combinación

1. Cambia otro color, guarda y publica para tener dos versiones.
2. Abre **Historial de versiones**.
3. Usa **Ver versión** para revisar una anterior.
4. Pulsa **Recuperar como borrador**. Primero cancela, después vuelve a hacerlo y confirma.
5. La versión recuperada debe aparecer como nuevo borrador. La publicación actual todavía se conserva.
6. Revisa y publica para aplicar esa combinación.
7. Prueba también **Restablecer diseño original**. No aplica el cambio de inmediato: debes guardar y publicar después de revisar.

## 6. Probar derechos y dos pestañas

- Usuario con Consultar: ve las cuatro tarjetas y copia enlaces; no ve Personalizar y la URL de edición rechaza el acceso.
- Usuario sin ambos permisos: no ve el módulo y no puede entrar pegando su URL.
- Usuario con alcance de plantel: no puede administrar identidad institucional.
- Abre la misma edición en A y B. Guarda un cambio en A. Intenta guardar en B: debe informar que cambió el diseño y pedir recargar, sin sobrescribir A.
- Si un borrador cambia después de abrir la publicación, la confirmación antigua debe rechazarse.

## 7. Comprobar el acceso por institución

Abre el enlace compartido con una cuenta de esa institución. El usuario se identifica dentro de la institución del enlace; esto evita confundir usuarios con el mismo nombre de diferentes escuelas. Las contraseñas y permisos existentes se conservan. Después de un error de acceso o al cerrar sesión debe mantenerse la identidad correspondiente. Las URLs habituales también muestran el diseño publicado de la institución configurada en esta instalación.

## Verificación técnica realizada

12/11/2026:1124 pruebas Java y196JavaScript aprobadas; cinco pruebas aisladas de integración aprobadas. Se comprobó separación entre dos instituciones y entre portales, permisos/CSRF, guardado concurrente, publicación e historial, logo PNG propio, acceso con nombres repetidos, error y cierre de sesión, vistas reales y acceso público sin crear sesión. Navegador con datos ficticios: cuatro tarjetas, copiar enlace, paleta y hexadecimal, contraste, original/historial, modal de guardado Cancelar/Confirmar, vista celular390px sin desbordamiento y colores en los cuatro portales. No se publicaron diseños operativos ni se concedieron permisos automáticamente. La aceptación manual del propietario queda pendiente.

Actualizado en Docker y salud UP. Antes de comenzar, habilita el permiso indicado en el punto 1 y vuelve a iniciar sesión. Las pantallas públicas conservan el diseño anterior hasta que guardes y publiques tu primera personalización.

## Recuperar y comprobar los colores dentro del sistema

Si recuperas una versión que tiene los mismos colores que la actual, se verá igual. El historial ahora muestra los códigos de color para que puedas distinguirlas.

1. En **Diseño → Portales e identidad → Administración → Personalizar**, abre **Historial de versiones**.
2. Para volver al aspecto de antes de personalizar, pulsa **Recuperar diseño inicial**. Para una combinación guardada, usa **Recuperar como borrador** en su fila.
3. Confirma. Debes volver a **Colores y vista previa**, donde ya se ve el diseño recuperado. Arriba se distinguen **Editando borrador** y **En público**.
4. El público todavía conserva su versión anterior. Pulsa **Publicar diseño** y confirma para aplicar la recuperación.
5. Revisa tanto el acceso público como el menú después de entrar. Recarga las otras pestañas que ya estaban abiertas.
6. Si personalizas Administración con principal `#194F3D`, fondo `#EFF5EF` y acento `#F5C542`, el menú debe verse verde, la selección amarilla y el texto legible. En oscuro el fondo se adapta para no perder legibilidad.
7. Los avisos rojos, ámbar y verdes de errores o estados conservan su significado. No se cambian permisos ni datos.
8. Publicar Familias, Maestros o Personal modifica sólo ese portal. La identidad interior se consulta según la institución de la sesión, aunque el usuario tenga alcance de plantel y no pueda editar identidad.

Verificación del ajuste: 204 pruebas JavaScript, 1124 Java y cinco de integración aislada aprobadas; recuperación de versión guardada y de diseño inicial, publicación posterior, acceso habitual `/login`, endpoint de colores sin datos personales y separación de instituciones. Navegador con datos ficticios: menú verde/selección amarilla, temas claro y oscuro, historial con colores y recuperación inicial con Cancelar.

## Probar la adaptación automática de claro y oscuro

La combinación que elijas es tu identidad. El sistema prepara tonos para fondos, texto, bordes y botones sin cambiar los códigos que guardaste. Por ejemplo, un amarillo puede aparecer más oscuro en un botón para que su texto se lea; la selección del menú conserva el amarillo.

1. Abre **Diseño → Portales e identidad → Administración → Personalizar**.
2. Usa principal `#194F3D`, fondo del modo claro `#EFF5EF` y acento `#F5C542`. Deja los ajustes avanzados desmarcados: la adaptación automática ya está activa.
3. En la vista previa elige **Dentro del sistema**. Verás un ejemplo de menú, campos, tabla, botones y estados, sin registros reales.
4. Alterna **Claro / Oscuro** y pulsa **Ver modal de confirmación**. Deben leerse tanto **Confirmar** como **Cancelar**. Cancelar sólo cierra el ejemplo.
5. Prueba acentos `#FFFFFF`, `#000000` y `#FFFF00`: no debe aparecer texto blanco sobre un botón blanco. Vuelve a tu color preferido.
6. Revisa **Acceso**, **Dentro del sistema** y **Celular** antes de publicar. Los avisos Activo/Pendiente mantienen sus colores habituales.
7. Opcional: abre **Ajustes avanzados del modo oscuro** y activa **Usar una combinación propia en oscuro**. Elige principal `#220033`, fondo `#090510`, acento `#FFEE00`. Sólo se usa al elegir Oscuro; Claro conserva la combinación principal. Los ajustes de legibilidad siguen activos.
8. Prueba Deshacer/Rehacer con esos campos y con la casilla de personalización. Al desactivarla vuelves a la adaptación automática.
9. Un hexadecimal inválido debe señalar el campo y bloquear el guardado, sin perder lo capturado. Los campos avanzados desactivados no bloquean el formulario.
10. Guarda primero probando Cancelar, confirma el borrador y publica cuando esté listo. Revisa luego el portal real en ambos modos; recarga las pestañas que ya estaban abiertas.
11. Recupera una versión anterior. Si era anterior a estos ajustes usará la adaptación automática; una versión con combinación oscura propia recupera también esos tres colores. La recuperación requiere publicar para cambiar el portal público.

Las vistas de ejemplo no guardan, pagan ni modifican registros. Los ajustes oscuros se conservan en el historial de cada portal y cada institución. El modo Claro/Oscuro sigue siendo una preferencia de quien usa el sistema.

Verificación técnica de esta entrega: 207 pruebas JavaScript, 1124 Java y cinco de integración aislada aprobadas. Actualizada en Docker con salud UP y V176 aplicada. Se conservaron los colores y versiones existentes; no se publicó una personalización durante las pruebas. La aceptación manual del propietario sigue pendiente.
