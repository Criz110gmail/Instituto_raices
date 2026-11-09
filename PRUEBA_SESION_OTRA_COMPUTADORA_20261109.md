# Sesión: prueba confirmada y verificación pendiente en otra computadora

## Resultado confirmado el 09/11/2026

El usuario confirmó que todas las pruebas manuales propuestas con duración de cinco
minutos funcionaron en esta computadora:

- Aviso al quedar dos minutos y vencimiento al terminar el plazo.
- Continuar sesión renueva el tiempo y conserva el formulario sin guardarlo.
- Navegar y consultar información renuevan la sesión.
- Tras vencer, se solicita acceso y no se permiten operaciones protegidas.
- Renovación y cierre con varias pestañas, regreso desde una pestaña oculta y
  comportamiento ante desconexión.

La confirmación corresponde a esta instalación; no demuestra la causa del problema
anterior en la otra computadora ni sustituye una prueba con duración de 30 minutos.
No se generaron pagos para esta comprobación. No se repitieron pruebas automáticas
en esta revisión: `mvn` no está disponible en el host.

## Qué falta

1. Repetir la comprobación en la otra computadora, donde antes fallaba el contador.
2. Restablecer la duración habitual de 30 minutos y comprobar aviso y vencimiento.

Al documentar, `src/main/resources/application.properties` tiene un cambio local del
usuario a `server.servlet.session.timeout=5m`. Se conserva sin modificar. No dejar
cinco minutos como configuración definitiva por accidente.

## Pasos para la otra computadora

No se necesita trasladar los empleados de prueba ni la base de nómina. Utiliza una
cuenta autorizada disponible y consultas sin registrar pagos.

1. Actualiza el código y compara `git rev-parse --short HEAD` entre computadoras.
   Antes de actualizar, revisa cambios locales para preservar la captura del usuario.
2. Confirma la carpeta, proyecto, archivos Compose y URL que utilizas normalmente.
   Si arrancas con `-p` o archivos `-f`, conserva esos argumentos en los comandos
   siguientes; no crees otro proyecto por accidente.
3. Para la primera comprobación, configura temporalmente:

   ```properties
   server.servlet.session.timeout=5m
   ```

4. Reconstruye y recrea sólo la aplicación:

   ```bash
   docker compose up -d --build --force-recreate app
   ```

5. Espera el arranque, cierra todas las pestañas del sistema, abre una ventana de
   incógnito e inicia sesión. El contador debe comenzar cerca de 05:00. Si no
   corresponde, verifica la instalación y posibles sobrescrituras de duración
   antes de continuar. No publiques variables, contraseñas ni el contenido de `.env`.
6. Durante la prueba no reinicies la aplicación. Reiniciarla invalida las sesiones.

| Prueba | Qué hacer | Resultado esperado |
| --- | --- | --- |
| Aviso | Dejar una sola pestaña en Empleados sin navegar ni filtrar | Cerca de tres minutos de inactividad aparece el aviso, con dos minutos restantes |
| Renovar | En un formulario escribir un texto sin guardar, esperar el aviso y pulsar Continuar sesión | Se cierra el modal, vuelve cerca de 05:00 y conserva el texto sin guardarlo; al esperar vuelve a avisar |
| Trabajo activo | Consultar otro módulo o aplicar un filtro cada minuto durante más de cinco minutos | Las consultas renuevan el plazo y no hay cierre anticipado |
| Vencimiento | No renovar ni realizar consultas funcionales | Al agotarse el plazo pide acceso; una URL protegida o un intento de guardar no permite continuar sin autenticación |
| Dos pestañas | Renovar desde A y observar B; después cerrar sesión desde A | B sincroniza el plazo y pierde acceso al comprobar la sesión o intentar trabajar |
| Pestaña oculta | Dejarla oculta más de cinco minutos, sin actividad desde otras pestañas | Al regresar reconoce el vencimiento, sin regalar otros cinco minutos |
| Sin conexión | Con el aviso abierto, desconectar temporalmente la red del navegador y pulsar Continuar sesión | Informa el problema sin afirmar que renovó; al reconectar verifica si sigue vigente o pide acceso |

Escribir, mover el ratón y las consultas automáticas del contador no renuevan el
plazo. Sí lo hacen las peticiones funcionales y Continuar sesión. El aviso no guarda
formularios. En una pestaña oculta la detección visual puede esperar a que se vuelva
a mostrar; el acceso protegido debe seguir respetando el vencimiento del servidor.

Si persiste el fallo, como diagnóstico adicional y con los mismos argumentos Compose:

```bash
docker compose build --no-cache app
docker compose up -d --force-recreate app
```

No borrar volúmenes ni ejecutar `docker compose down -v`. Reconstruir sin caché no
limpia las pestañas del navegador ni prueba por sí mismo cuál era la causa.
Si vuelve a fallar, anotar versión, URL, duración mostrada, hora de la última acción,
momento del aviso/cierre, pestañas abiertas y si hubo reinicio o desconexión.

## Volver a la duración habitual

Restablecer `server.servlet.session.timeout=30m`, reconstruir la aplicación e iniciar
sesión nuevamente. Con una sola pestaña y sin actividad, esperar el aviso cerca del
minuto 28 y comprobar renovación. En otra espera completa, no renovar y comprobar
vencimiento cerca del minuto 30. No marcar este punto como aprobado hasta confirmarlo.

## Siguiente trabajo acordado en continuidad

Puede avanzarse con la revisión de filtros dentro de pantallas de detalle mientras
la otra computadora no esté disponible. No retomar la conversión global de selectores:
el usuario decidió conservarlos. Después quedan las verificaciones de liberación y
respaldos/recuperación. La apertura del documento familiar aprobado desde el expediente
oficial todavía requiere confirmación específica; su presencia ya fue confirmada.
