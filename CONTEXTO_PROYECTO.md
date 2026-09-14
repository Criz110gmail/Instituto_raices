# Contexto del proyecto

## Decisiones confirmadas — primera etapa

- Proyecto Maven `sistema-administrativo-escolar`, paquete base `escuela`.
- Java 21 y Spring Boot 4.1.1.
- Interfaz web con Thymeleaf.
- PostgreSQL es la base de datos y usa un volumen persistente en Docker Compose.
- Flyway es el único responsable del esquema; Hibernate usa `ddl-auto=validate`.
- Spring Data JPA, Spring Security, Bean Validation, Lombok, Spring Mail y Actuator.
- Credenciales y secretos se reciben por variables de entorno.
- La imagen Docker compila el proyecto para no depender de Maven en el equipo.
- Código organizado por módulo funcional; las entidades no se expondrán directamente
  en futuros formularios o respuestas.
- La primera etapa se limita al núcleo institucional y académico: Institucion,
  Plantel, NivelEducativo, PlantelNivel, Grado, CicloEscolar, PeriodoAcademico y Grupo.
- Los identificadores son `Long`; todas las entidades tienen fechas y actores de
  auditoría, además de versión para concurrencia optimista.
- `creado_por_id` y `actualizado_por_id` son anulables y no tendrán FK hasta crear
  el módulo Usuario.
- PeriodoAcademico subdivide un ciclo por nivel y no representa meses de cobro.
- El plantel, grado o grupo actual de un alumno se derivará de su inscripción vigente;
  no se duplicará como fuente principal cuando se implemente ese módulo.
- La seguridad inicial deja públicas la portada y la salud; las demás rutas quedan
  protegidas hasta implementar usuarios, roles y permisos.
- No se generan todavía las demás entidades del modelo.

## Verificación de la etapa

- La construcción multietapa de Docker compila con Java 21 y ejecuta las pruebas Maven.
- Resultado: 2 pruebas ejecutadas, sin fallos ni errores.
- Flyway validó y aplicó `V1__crear_nucleo_institucional_academico.sql` en PostgreSQL 17.
- Hibernate inicializó correctamente con `ddl-auto=validate` y detectó 8 repositorios JPA.
- El endpoint Actuator respondió `UP` y la portada Thymeleaf respondió correctamente.
- Limitación del entorno verificado: Maven no está instalado en el host y el puerto 8080
  ya estaba ocupado. La instancia comprobada quedó levantada mediante Docker Compose en
  `http://localhost:18080`, usando `APP_PORT=18080`.

## Decisiones — capa de aplicación del núcleo

- Cada una de las ocho entidades del núcleo tiene DTO de entrada, DTO de respuesta,
  mapper, interfaz de servicio e implementación transaccional.
- Los DTO de entrada son records inmutables y aplican Bean Validation; las respuestas
  usan identificadores para las relaciones y no exponen entidades JPA.
- Códigos, país, moneda y correo se normalizan antes de comparar y persistir.
- Las relaciones propietarias (institución, plantel, nivel, ciclo y grado) no pueden
  reasignarse mediante una actualización; se crea un registro nuevo cuando el historial
  requiere otra relación.
- Las actualizaciones exigen la versión recibida por el cliente y usan el control
  optimista de JPA. Los servicios hacen flush antes de construir la respuesta.
- Los registros históricos no se eliminan físicamente; las entidades que lo admiten
  se desactivan.
- La selección de ciclo predeterminado bloquea la institución y desmarca el anterior
  dentro de la misma transacción.
- No se permiten periodos fuera del ciclo, solapados, duplicados o modificados dentro
  de ciclos cerrados. Reducir las fechas de un ciclo no puede dejar periodos fuera.
- Un grupo sólo puede operar si plantel, institución, nivel y grado están activos, el
  ciclo no está cerrado y existe una oferta educativa activa para su nivel.
- Las excepciones de negocio, ausencia, duplicidad y conflicto de versión tienen un
  manejador MVC global y una vista segura común.

## Verificación de la capa de aplicación

- Compilación Docker correcta de 73 archivos Java de producción.
- 7 pruebas ejecutadas sin fallos: portada, ciclo predeterminado y reglas de periodos.
- Spring inició correctamente, validó Flyway V1 y `ddl-auto=validate`.
- El endpoint de salud permaneció en estado `UP` en el puerto externo 18080.

## Decisiones — interfaz, paginación y exportación

- Todo módulo nuevo debe incluir desde su primera pantalla: filtros ejecutados en base
  de datos, paginación real y exportación Excel con los mismos filtros.
- Los listados del núcleo consultan páginas de 10, 25, 50 o 100 registros mediante
  Spring Data `Pageable` y `JpaSpecificationExecutor`; nunca recuperan el catálogo
  completo para mostrar una pantalla.
- La exportación usa Apache POI `SXSSFWorkbook` y recorre el resultado filtrado en
  bloques, evitando mantener todo el archivo o todos los registros en memoria.
- El objeto `FiltroCatalogo` es compartido por pantalla y exportador para impedir que
  el Excel difiera de la consulta visible.
- Se creó una consola administrativa común para las ocho entidades actuales, con
  búsqueda textual, estado, tamaño de página, navegación y estados vacíos.
- Dirección visual: centro de control académico en azul tinta y cian, alta legibilidad,
  tabla de escritorio, tarjetas móviles y navegación lateral adaptable.
- La interfaz contempla escritorio, tablet, móvil y ampliación de texto, sin depender
  de JavaScript para la navegación responsiva.
- Las credenciales administrativas temporales se reciben mediante variables de entorno
  y se retirarán cuando se implemente el módulo definitivo de usuarios y roles.

## Verificación de interfaz y exportación

- Compilación de 80 archivos Java y 7 pruebas sin fallos.
- Catálogo autenticado renderizado con filtros y tamaño de página en PostgreSQL real.
- Exportación filtrada generada con respuesta HTTP 200 y libro XLSX válido.
- Aplicación y PostgreSQL permanecen saludables en `http://localhost:18080`.

## Decisiones — temas, acceso y primer mantenimiento

- La consola y el acceso permiten elegir tema claro u oscuro; la preferencia se guarda
  localmente en el dispositivo y, si aún no existe, respeta el tema del sistema operativo.
- Se reemplazó el login predeterminado por una pantalla propia de Nexo Escolar,
  responsiva, accesible y consistente con la consola administrativa.
- El flujo de autenticación sigue bajo Spring Security y redirige a la consola después
  de un acceso correcto.
- Se implementó el mantenimiento completo de Institucion: alta, edición y desactivación
  lógica, con Bean Validation, protección CSRF, versión optimista y mensajes de resultado.
- Institucion es el primer formulario porque es la raíz obligatoria para planteles,
  niveles y ciclos. El mismo patrón visual y técnico se reutilizará en esos catálogos.

## Verificación de esta iteración

- Compilación de 83 archivos Java y 7 pruebas sin fallos.
- Login público, selector de tema y formulario autenticado renderizados correctamente.
- Aplicación y PostgreSQL permanecen saludables en el puerto externo 18080.

## Decisiones — configuración segura y mantenimiento estructural

- Existe un `.env` local, excluido del control de versiones, que concentra las
  credenciales de PostgreSQL y del administrador temporal, además del perfil y puerto.
- Docker Compose exige explícitamente esas variables y ya no contiene contraseñas de
  respaldo; si falta una variable sensible, el despliegue falla de forma inmediata.
- La contraseña del rol PostgreSQL del volumen existente se migró para coincidir con
  `.env`, sin recrear ni eliminar los datos persistentes.
- Se completó el mantenimiento de Plantel, NivelEducativo y PlantelNivel: alta, edición,
  desactivación lógica, validación, protección CSRF y control optimista de versión.
- Los formularios conservan el diseño responsivo y los temas claro/oscuro. Los listados
  siguen paginados y cada módulo mantiene su exportación Apache POI con los mismos filtros.

## Verificación de esta iteración

- Compilación de 89 archivos Java y 7 pruebas sin fallos.
- PostgreSQL y la aplicación reiniciaron usando exclusivamente las variables de `.env`.
- Salud `UP`; formularios de planteles, niveles y oferta respondieron HTTP 200.
- La exportación filtrada de oferta educativa respondió HTTP 200 con un XLSX válido.

## Decisiones — mantenimiento de grados

- Se completó el mantenimiento de Grado con alta, edición y desactivación lógica.
- El formulario selecciona primero la institución y filtra los niveles educativos que
  le pertenecen, pero el dominio conserva únicamente la relación Grado-NivelEducativo.
- En edición, la institución y el nivel propietario se muestran bloqueados para evitar
  la reasignación de relaciones históricas.
- El controlador comprueba en servidor que el nivel seleccionado pertenezca a la
  institución indicada, además de delegar las reglas de unicidad y actividad al servicio.
- El listado de grados habilitó las acciones **Nuevo registro** y **Editar** sin modificar
  su paginación, filtros o exportación existentes.

## Verificación del mantenimiento de grados

- Compilación Docker correcta de 91 archivos Java de producción.
- 11 pruebas ejecutadas sin fallos ni errores, incluidas 4 del controlador de grados.
- PostgreSQL quedó saludable y `/actuator/health` respondió `UP`.
- El formulario autenticado de grado respondió HTTP 200 y renderizó correctamente.
- La exportación filtrada de grados respondió HTTP 200 con firma XLSX válida.

## Decisiones — errores integrados en formularios

- Los cinco mantenimientos disponibles capturan errores esperables al crear, actualizar
  y desactivar: reglas de negocio, concurrencia optimista e integridad de base de datos.
- El mismo formulario se vuelve a renderizar con los valores capturados y un mensaje
  accesible, responsivo y compatible con los temas claro y oscuro.
- Los mensajes específicos del dominio se conservan. Las restricciones SQL se traducen
  según su categoría sin mostrar consultas, nombres internos ni detalles de PostgreSQL.
- Los recursos inexistentes y errores inesperados continúan en el manejador global para
  no ocultar fallos de programación o navegación inválida.
- Este comportamiento es obligatorio para los mantenimientos futuros de ciclos,
  periodos académicos y grupos.

## Verificación del manejo de errores

- Compilación Docker correcta de 92 archivos Java de producción.
- 16 pruebas ejecutadas sin fallos ni errores.
- Se verificó que un duplicado mantiene el mismo formulario y los datos introducidos.
- Se probaron las traducciones seguras de unicidad SQL y concurrencia optimista.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — mantenimiento de ciclos escolares

- Se completó el mantenimiento de CicloEscolar con alta y edición de institución,
  código, nombre, fechas, estado y selección predeterminada.
- La institución propietaria queda bloqueada durante la edición y se conserva la versión
  para concurrencia optimista.
- El ciclo usa sus estados `PLANIFICADO`, `ABIERTO` y `CERRADO`; no tiene desactivación
  lógica porque su ciclo de vida se representa mediante esos estados.
- Al marcar un ciclo como predeterminado, el servicio bloquea la institución y desmarca
  el anterior dentro de la misma transacción.
- Los errores de código duplicado, fechas inválidas, periodos fuera del nuevo rango,
  concurrencia o integridad permanecen en el mismo formulario.

## Verificación del mantenimiento de ciclos escolares

- Compilación Docker correcta de 94 archivos Java de producción.
- 20 pruebas ejecutadas sin fallos ni errores.
- El formulario autenticado de ciclo escolar respondió HTTP 200 y renderizó sus estados.
- La exportación filtrada de ciclos respondió HTTP 200 con firma XLSX válida.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — mantenimiento de periodos académicos

- Se completó el mantenimiento de PeriodoAcademico con alta y edición de ciclo, nivel,
  código, nombre, tipo, orden, fechas, estado y observaciones.
- La institución auxiliar filtra ciclos y niveles, pero no se duplica como relación del
  dominio. En edición se bloquean institución, ciclo y nivel.
- La pantalla limita las fechas al rango del ciclo como ayuda; el servicio conserva la
  validación definitiva de rango, solapamientos, duplicados y ciclo cerrado.
- Todos los errores esperables se muestran dentro del formulario conservando la captura.

## Verificación del mantenimiento de periodos académicos

- Compilación Docker correcta de 96 archivos Java de producción.
- 24 pruebas ejecutadas sin fallos ni errores.
- El formulario autenticado respondió HTTP 200 y renderizó tipos, estados y rango.
- La exportación filtrada respondió HTTP 200 con firma XLSX válida.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — mantenimiento de grupos y cierre de catálogos

- Se completó el mantenimiento de Grupo con alta, edición y desactivación lógica.
- La institución filtra planteles y ciclos; el plantel limita los grados a niveles de su
  oferta activa. Los ciclos cerrados y relaciones inactivas no se ofrecen en altas.
- En edición se bloquean plantel, ciclo y grado para conservar relaciones propietarias.
- Nombre, turno, código, aula, capacidad y estado activo permanecen editables.
- El servidor revalida institución, oferta activa, ciclo, grado, turno, capacidad y
  unicidad; los errores esperables permanecen en el mismo formulario.
- Con este módulo, los ocho catálogos del núcleo institucional y académico cuentan con
  mantenimiento conforme a su ciclo de vida, además de filtros, paginación y Excel.

## Verificación del mantenimiento de grupos

- Compilación Docker correcta de 99 archivos Java de producción.
- 28 pruebas ejecutadas sin fallos ni errores.
- El formulario autenticado de grupo respondió HTTP 200 y renderizó sus selectores.
- La exportación filtrada respondió HTTP 200 con firma XLSX válida.
- PostgreSQL permaneció saludable y `/actuator/health` respondió `UP`.

## Decisiones — base definitiva de usuarios, roles y permisos

- Flyway V2 agrega `Usuario`, `Rol`, `Permiso`, `RolPermiso`, `UsuarioRol` e
  `InvitacionUsuario` sin modificar la migración V1 ni los datos existentes.
- Los usuarios y roles pertenecen a una institución; sus nombres, correos y códigos se
  comparan normalizados dentro de ese límite institucional.
- Los permisos son un catálogo técnico global. V2 siembra 18 permisos para el núcleo
  actual y para administrar usuarios y roles.
- Las asignaciones de rol tienen alcance `INSTITUCION`, `PLANTEL` o
  `VINCULOS_TUTOR`. El plantel es obligatorio exclusivamente para el alcance de plantel
  y debe pertenecer a la institución del usuario y del rol.
- Las invitaciones son revocables, vencen como máximo en siete días y son de un solo
  uso. Sólo se persiste SHA-256 del token; la contraseña se persiste codificada con
  BCrypt y nunca se envía ni almacena en texto plano.
- Se mantienen auditoría, concurrencia optimista y desactivación lógica. El login sigue
  usando temporalmente las credenciales de `.env` para evitar un bloqueo administrativo
  antes de crear y comprobar el primer usuario real.

## Verificación de la base de seguridad

- Compilación Docker correcta de 130 archivos Java de producción.
- 35 pruebas ejecutadas sin fallos ni errores; seis cubren alcances multiinstitución,
  token protegido, contraseña codificada y consumo único de invitación, y una protege
  el acceso del administrador temporal al coexistir con BCrypt.
- Flyway validó dos migraciones y aplicó V2 sobre el volumen existente.
- PostgreSQL 17 contiene las seis tablas nuevas y los 18 permisos sembrados.
- Hibernate validó el esquema, detectó 14 repositorios y la aplicación respondió `UP`
  en `http://localhost:8080`.
- Se verificó una autenticación HTTP real con las credenciales cargadas internamente en
  el contenedor, sin imprimirlas; el acceso respondió correctamente.

## Decisiones — módulo visible de roles y permisos

- `Roles y permisos` se agregó como noveno módulo de la consola administrativa y como
  primera sección visible de Seguridad.
- El listado filtra código, nombre y descripción directamente en PostgreSQL, pagina sin
  cargar el catálogo completo y exporta un XLSX con los mismos filtros.
- El formulario crea y edita roles por institución, permite seleccionar los 18 permisos
  técnicos y muestra validaciones, duplicados, concurrencia e integridad sin salir de la
  pantalla.
- Los roles se desactivan lógicamente. Flyway V3 agregó el indicador `activo` a
  `RolPermiso`, de modo que retirar o volver a conceder permisos conserva la relación y
  su auditoría en lugar de eliminarla.

## Verificación del módulo de roles y permisos

- Compilación Docker correcta de 133 archivos Java de producción.
- 39 pruebas ejecutadas sin fallos ni errores, incluidas las reglas de sincronización
  histórica y el controlador del formulario de roles.
- Flyway validó y aplicó V3 sobre el volumen existente; Hibernate validó el esquema.
- Listado y formulario respondieron autenticados y el formulario presentó los permisos
  técnicos disponibles.
- La exportación filtrada respondió correctamente con firma XLSX válida y la aplicación
  permaneció `UP` en `http://localhost:8080`.

## Decisiones — módulo visible de usuarios y asignaciones

- `Usuarios` se agregó a la sección Seguridad con búsqueda por usuario, correo o
  institución, filtro de estado, paginación real y exportación Excel por bloques.
- El alta siempre crea usuarios `INVITADO`; institución, username y correo se validan y
  no se permite reasignar la institución al editar.
- La pantalla de edición administra estados y roles con alcance `INSTITUCION`,
  `PLANTEL` o `VINCULOS_TUTOR`. Usuario, rol y plantel se validan contra la misma
  institución y las asignaciones se desactivan o reactivan sin borrado físico.
- La administración puede generar una invitación de 48 horas. Una nueva invitación
  revoca la anterior, el enlace se muestra una sola vez y sólo se persiste su hash.
- `/activar-cuenta` es una pantalla pública protegida por CSRF que valida confirmación y
  longitud, consume el token una vez y guarda la contraseña con BCrypt.
- El administrador temporal de `.env` continúa siendo el único usuario autenticable
  hasta crear, activar y verificar el primer administrador persistido.

## Verificación del módulo de usuarios

- Compilación Docker correcta de 139 archivos Java de producción.
- 47 pruebas ejecutadas sin fallos ni errores, incluidas duplicidad, estados,
  aislamiento de asignaciones, controlador administrativo y activación.
- Listado y formulario de alta respondieron autenticados; la pantalla de activación
  respondió sin sesión y la exportación produjo una firma XLSX válida.
- Se corrigió y volvió a probar una condición nula de Thymeleaf detectada durante la
  verificación real.
- PostgreSQL permaneció en Flyway V3, la aplicación respondió `UP` y la tabla `usuario`
  siguió vacía: no se dejaron registros de prueba.

## Decisiones — autenticación de usuarios persistidos

- `UsuarioSistemaDetailsService` reemplazó el proveedor exclusivamente temporal y
  autentica usuarios `ACTIVO` mediante el hash BCrypt guardado al consumir la
  invitación.
- Un username único se resuelve directamente. Cuando existe en más de una institución,
  el acceso exige `CODIGO_INSTITUCION\usuario` para eliminar ambigüedad.
- Las autoridades se derivan únicamente de asignaciones activas, roles activos y
  permisos activos. Las rutas administrativas exigen su permiso técnico correspondiente.
- El administrador de `.env` permanece como acceso de recuperación y recibe todos los
  permisos técnicos, sin guardar ni publicar su contraseña.
- La cuenta real `criz110` quedó activa, con credencial BCrypt y rol administrador de
  alcance institucional; la contraseña en texto plano no se leyó ni se conservó.

## Verificación de la autenticación persistida

- Compilación Docker correcta de 140 archivos Java de producción.
- 50 pruebas ejecutadas sin fallos ni errores; tres cubren usuario persistido, permisos,
  usernames multiinstitución y acceso temporal de recuperación.
- Spring registró `usuarioSistemaDetailsService` como proveedor global de autenticación.
- Flyway validó V1, V2 y V3, la aplicación inició correctamente y salud respondió HTTP
  200. El acceso de recuperación respondió HTTP 200 sin imprimir sus credenciales.
- Falta únicamente que el propietario confirme en el navegador su contraseña elegida
  para `criz110`; el sistema sólo conserva el hash y no puede reconstruirla.

## Decisiones — aislamiento multiinstitución y multiplantel

- El principal de Spring Security conserva el identificador del usuario, su institución,
  la presencia de alcance institucional y los planteles de sus asignaciones activas.
- Los diez listados y sus exportaciones Excel agregan en PostgreSQL la especificación de
  alcance, además de los filtros de búsqueda y estado existentes.
- Formularios, selectores, altas, ediciones, cambios de estado y desactivaciones validan
  el alcance en servidor. Alterar manualmente un ID o una relación no permite cruzar de
  institución o usar un plantel no asignado.
- Un alcance `PLANTEL` puede consultar catálogos académicos compartidos de su institución,
  pero planteles, oferta y grupos quedan limitados a sus planteles. `VINCULOS_TUTOR` no
  habilita módulos administrativos.
- Administrar usuarios y roles requiere alcance institucional. Crear otra institución
  queda reservado al acceso de recuperación para evitar escalamiento entre inquilinos.
- Se agregó una pantalla 403 propia, accesible, responsiva y compatible con tema claro
  y oscuro para permisos o alcances insuficientes.

## Verificación del aislamiento de datos

- Compilación Docker correcta de 142 archivos Java de producción.
- 55 pruebas ejecutadas sin fallos ni errores; cinco cubren aislamiento institucional,
  planteles permitidos, acceso directo denegado, tutores y recuperación global.
- La aplicación inició con PostgreSQL saludable, Flyway V3 vigente y el proveedor de
  usuarios persistidos activo. Salud, recuperación y la pantalla 403 respondieron.
- La base de datos no fue recreada ni se modificaron los datos existentes.

## Decisiones — actor de auditoría autenticado

- `EntidadAuditable` usa `@CreatedBy` y `@LastModifiedBy` además de las fechas que ya
  administraba Spring Data Auditing.
- `AuditorAware<Long>` obtiene el identificador directamente de `UsuarioPrincipal`, sin
  consultar de nuevo la base ni utilizar username como clave de auditoría.
- El acceso temporal de recuperación y una operación sin usuario persistido devuelven
  actor vacío. `ActorAuditoriaEntityListener` también limpia explícitamente el actor al
  persistir o actualizar para no conservar por error una atribución anterior.
- No fue necesaria una migración porque `creado_por_id` y `actualizado_por_id` ya
  existían como columnas anulables en todas las tablas auditables.

## Verificación del actor de auditoría

- Compilación Docker correcta de 143 archivos Java de producción.
- 59 pruebas ejecutadas sin fallos ni errores; cuatro cubren usuario persistido,
  recuperación, sesión anónima y limpieza de un actor anterior.
- La aplicación inició correctamente, validó Flyway V3 y salud respondió HTTP 200.
- No se generaron registros artificiales ni se modificaron datos para la verificación.

## Decisiones — intentos fallidos y bloqueo temporal

- Eventos de éxito y credenciales incorrectas de Spring Security delegan el registro a
  un servicio transaccional; no se modificó el formulario ni su mensaje uniforme.
- Un acceso correcto de usuario persistido actualiza `ultimo_acceso_en`, limpia
  `intentos_fallidos` y retira un bloqueo temporal vencido.
- Cada contraseña incorrecta incrementa el contador bajo bloqueo pesimista. El quinto
  fallo cambia el estado a `BLOQUEADO` y fija `bloqueo_hasta` a 15 minutos.
- Un bloqueo con `bloqueo_hasta = null` es administrativo y no vence automáticamente.
  Tras vencer un bloqueo temporal, un fallo nuevo comienza otra vez desde uno.
- No se modifican cuentas desconocidas, usernames ambiguos, invitados, inactivos ni el
  usuario de recuperación. Esto evita filtrar la existencia o estado de una cuenta.

## Verificación de protección de acceso

- Compilación Docker correcta de 146 archivos Java de producción.
- 65 pruebas ejecutadas sin fallos ni errores; seis nuevas cubren éxito, quinto fallo,
  expiración, bloqueo administrativo, usuario desconocido y autenticación tras vencer.
- La aplicación inició correctamente con PostgreSQL saludable, Flyway V3 vigente y el
  proveedor `usuarioSistemaDetailsService` activo.
- No se provocaron fallos deliberados sobre `criz110` ni se alteraron sus credenciales.

## Decisiones — recuperación segura de contraseña

- Flyway V4 agrega `RecuperacionPassword` como flujo independiente de
  `InvitacionUsuario`; no cambia una cuenta activa al estado `INVITADO`.
- Desde la edición de un usuario activo, la administración genera un enlace de 30
  minutos que se muestra una sola vez. Emitir otro revoca cualquier enlace pendiente.
- El token aleatorio tiene 256 bits y la base conserva exclusivamente su SHA-256. La
  contraseña nueva se guarda con BCrypt y el token queda consumido en la misma
  transacción.
- Completar la recuperación limpia intentos fallidos y bloqueos automáticos temporales.
  Un bloqueo administrativo permanente nunca puede retirarse mediante el enlace.
- La pantalla pública `/restablecer-password` valida longitud y confirmación, conserva
  los errores dentro del formulario y funciona con los temas claro y oscuro.
- La entrega actual es administrada: el proyecto no tiene SMTP configurado y no se
  presupone que los correos capturados sean reales. No se envían contraseñas en texto
  plano.

## Verificación de recuperación de contraseña

- Compilación Docker correcta de 154 archivos Java de producción.
- 74 pruebas ejecutadas sin fallos ni errores; las nuevas cubren hash del token,
  revocación, consumo único, BCrypt, bloqueo temporal, bloqueo administrativo,
  validación del formulario y construcción del enlace administrativo.
- Flyway validó cuatro migraciones y aplicó V4 sobre el volumen existente sin eliminar
  datos. Hibernate validó el esquema y detectó 15 repositorios.
- La aplicación quedó `UP` en `http://localhost:8080` y la pantalla pública de
  restablecimiento respondió HTTP 200.
- No se generó un token real ni se modificaron credenciales de `criz110` durante las
  pruebas del despliegue.

## Decisiones — módulo de alumnos

- Flyway V5 agrega `Alumno` y los permisos técnicos `ALUMNO_LEER` y
  `ALUMNO_ADMINISTRAR` de forma aditiva, sin editar migraciones anteriores.
- El expediente pertenece a una institución y conserva identidad, matrícula, CURP
  opcional, nacimiento, ingreso, contacto, domicilio, observaciones y estado activo.
- Matrícula y CURP normalizadas son únicas por institución. Las fechas no pueden ser
  futuras y el ingreso no puede preceder al nacimiento.
- No se duplican plantel, grado o grupo actual; esos datos se derivarán de
  `Inscripcion` y `AsignacionGrupo` cuando se implementen.
- El listado filtra matrícula, nombres, apellidos, CURP y correo en PostgreSQL, pagina
  realmente y exporta los mismos resultados a XLSX por bloques.
- Alta, edición y desactivación lógica aplican alcance institucional, DTO de formulario,
  CSRF, auditoría, versión optimista y errores integrados en la misma pantalla.
- La interfaz agrega la sección Personas y conserva diseño responsivo y temas claro y
  oscuro. Los roles existentes no reciben permisos nuevos automáticamente.

## Verificación del módulo de alumnos

- Compilación Docker correcta de 163 archivos Java de producción.
- 86 pruebas ejecutadas sin fallos ni errores; once nuevas cubren normalización,
  duplicados, fechas, institución inmutable, desactivación, controlador y aislamiento.
- Flyway validó cinco migraciones y aplicó V5 sobre el volumen existente. Hibernate
  validó el esquema y detectó 16 repositorios.
- Listado, formulario y exportación respondieron HTTP 200; el archivo comenzó con firma
  XLSX válida y la aplicación quedó `UP` en `http://localhost:8080`.
- No se crearon alumnos de prueba. Posteriormente el propietario asignó los permisos
  correspondientes y confirmó que `criz110` ya puede ver el módulo Alumnos.
- Se corrigió la edición de fechas: Spring renderizaba `LocalDate` con formato regional
  y el navegador descartaba ese valor en controles `type=date`. Alumnos, ciclos y
  periodos ahora declaran ISO `yyyy-MM-dd`; se verificaron ambas fechas de un alumno
  existente sin modificarlo.

## Decisiones — cierre de sesión visible

- Los listados y todos los formularios administrativos reutilizan un control de sesión
  común con selector de tema y botón `Cerrar sesión`.
- El cierre usa exclusivamente `POST /logout` con el token CSRF agregado por Thymeleaf;
  no depende de navegar manualmente a una ruta GET.
- Spring invalida la sesión y redirige al inicio. Se comprobó que la cookie anterior ya
  no puede abrir `/admin` y termina nuevamente en el login.

## Decisiones — módulo de tutores

- Flyway V6 agrega `Tutor` y los permisos técnicos `TUTOR_LEER` y
  `TUTOR_ADMINISTRAR` de forma aditiva; los roles existentes no reciben privilegios
  nuevos automáticamente.
- El expediente de tutor pertenece a una institución y conserva identidad, teléfonos,
  correo, nacimiento opcional, domicilio, ocupación, contacto laboral y estado activo.
- Un tutor puede vincularse opcionalmente a un `Usuario` activo o invitado de su misma
  institución. Cada cuenta sólo puede representar a un tutor.
- El listado busca por nombre, apellidos, teléfono y correo directamente en PostgreSQL,
  pagina realmente y exporta los mismos filtros a XLSX por bloques.
- Alta, edición y desactivación lógica aplican alcance institucional, DTO de formulario,
  CSRF, auditoría, versión optimista y errores integrados en la misma pantalla.
- La creación automática de una cuenta invitada se pospuso hasta `AlumnoTutor`, donde
  podrá decidirse si el tutor tendrá acceso a alumnos concretos mediante
  `VINCULOS_TUTOR`.

## Verificación del módulo de tutores

- Compilación Docker correcta de 172 archivos Java de producción.
- 99 pruebas ejecutadas sin fallos ni errores; trece nuevas cubren dominio, cuenta
  opcional, aislamiento, controlador, fechas y desactivación lógica.
- Flyway validó seis migraciones y aplicó V6 sobre el volumen existente. Hibernate
  validó el esquema y detectó 17 repositorios.
- El listado y el formulario respondieron autenticados; la exportación comenzó con la
  firma XLSX `504b0304` y la aplicación quedó disponible en `http://localhost:8080`.
- La tabla `tutor` permaneció vacía durante esta verificación. Posteriormente el
  propietario asignó ambos permisos y confirmó que `criz110` ya ve Tutores.
- El siguiente módulo acordado es `AlumnoTutor`; después se agregarán archivos y
  fotografía al historial del alumno, y posteriormente las inscripciones.

## Decisiones — vínculos entre alumnos y tutores

- Flyway V7 agrega `AlumnoTutor` y los permisos `VINCULO_TUTOR_LEER` y
  `VINCULO_TUTOR_ADMINISTRAR` sin modificar migraciones anteriores ni ampliar roles de
  forma automática.
- La relación conserva parentesco, contacto principal, responsabilidad financiera,
  autorización de trámites y recogida, consulta financiera, notificaciones,
  observaciones, vigencia y estado activo.
- Alumno y tutor deben pertenecer a la misma institución y estar activos para mantener
  un vínculo activo. No pueden reasignarse al editar un registro histórico.
- No se permiten periodos superpuestos para la misma pareja ni dos contactos
  principales con vigencias superpuestas. La validación bloquea pesimistamente al
  alumno para evitar altas concurrentes incompatibles.
- Revocar desactiva la relación sin eliminarla. Una cuenta con alcance
  `VINCULOS_TUTOR` sólo puede consultar sus propios vínculos activos y vigentes; no
  recibe acceso administrativo ni conserva acceso después de la revocación o vigencia.
- El listado busca por matrícula y nombres de alumno o tutor en PostgreSQL, pagina los
  resultados y exporta los mismos filtros a XLSX por bloques.
- El formulario busca alumnos y tutores mediante autocompletado remoto por institución,
  bloquea ambas personas en edición y mantiene reglas, integridad y concurrencia dentro
  de la misma pantalla.

## Verificación de vínculos entre alumnos y tutores

- Compilación Docker correcta de 182 archivos Java de producción.
- 116 pruebas ejecutadas sin fallos ni errores; diecisiete nuevas cubren dominio,
  solapamientos, contacto principal, revocación, controlador, fechas y alcance del
  tutor.
- Flyway validó siete migraciones y aplicó V7 sobre el volumen existente. Hibernate
  validó el esquema y detectó 18 repositorios.
- El listado con filtro relacional y el formulario respondieron autenticados; la
  exportación comenzó con firma XLSX `504b0304`.
- La tabla `alumno_tutor` permaneció vacía y se confirmaron los dos permisos nuevos; no
  se alteraron alumnos, tutores, usuarios ni roles existentes.
- El siguiente paso acordado es la base privada de archivos y la fotografía del alumno;
  después se implementarán inscripciones.

## Decisión posterior: autocompletado para catálogos de alto volumen

- Se corrigió el formulario de vínculos para que alumno y tutor se seleccionen mediante
  búsqueda remota, y el formulario de tutor usa el mismo componente para la cuenta de
  usuario opcional. Ninguno carga ya las tablas completas al abrirse.
- La búsqueda comienza con tres caracteres, espera 280 ms mientras se escribe, cancela
  solicitudes anteriores y devuelve como máximo 20 elementos sin ejecutar un conteo
  total. Incluye teclado, estados ARIA, botón para limpiar y texto insertado de forma
  segura.
- Los endpoints aplican los permisos y el alcance institucional del servidor. Al buscar
  una cuenta para tutor también se excluyen cuentas asignadas a otro tutor.
- Flyway V8 habilita `pg_trgm`, incorpora columnas generadas normalizadas y crea índices
  GIN para alumnos, tutores y usuarios. La regla del proyecto es utilizar este patrón en
  toda relación futura con un catálogo potencialmente masivo.
- Docker compiló 186 fuentes de producción y ejecutó 120 pruebas sin fallos. Flyway
  avanzó a V8, se confirmaron los tres índices y los tres endpoints devolvieron JSON
  HTTP 200. La aplicación quedó disponible en `http://localhost:8080`.
- La continuidad funcional no cambia: sigue la base privada de archivos y fotografía
  del alumno; posteriormente se implementarán inscripciones.

## Decisiones — archivos privados y fotografía del alumno

- Flyway V9 crea `Archivo`, agrega `fotografia_archivo_id` a `Alumno` y registra cada
  asignación en `AlumnoFotografia`. Una fotografía reemplazada o retirada deja de ser
  la actual, pero su relación histórica y su contenido no se eliminan.
- PostgreSQL guarda institución, clave aleatoria, nombre original saneado, MIME,
  tamaño, SHA-256, estado y auditoría; nunca guarda los bytes del archivo.
- El contenido vive fuera de los recursos web en el volumen Docker privado
  `private_files`. Las claves son generadas por el servidor y el almacenamiento bloquea
  cualquier ruta que intente salir de su raíz.
- La etapa inicial admite únicamente JPEG y PNG de hasta 5 MB y 25 millones de píxeles.
  Se validan firma binaria, lector real, dimensiones y checksum, sin confiar en la
  extensión ni en el MIME enviado por el navegador.
- Cargar, reemplazar y retirar exige `ALUMNO_ADMINISTRAR` y alcance institucional. La
  visualización exige `ALUMNO_LEER` o `ALUMNO_ADMINISTRAR`, vuelve a comprobar el
  alcance por alumno, usa `no-store` y nunca revela una ruta física.
- La edición del alumno incluye vista previa, estado vacío, carga accesible, retiro e
  historial privado. Los errores de archivo, tipo, tamaño o persistencia permanecen en
  el mismo formulario.

## Verificación de archivos y fotografía

- Docker compiló 197 fuentes Java de producción y ejecutó 130 pruebas sin fallos ni
  errores. Diez pruebas nuevas cubren imagen válida, contenido falso, tamaño, historial,
  retiro, bloqueo, rutas privadas y errores integrados en el controlador.
- Flyway aplicó V9 sobre el volumen existente, Hibernate validó el esquema y detectó 20
  repositorios. Las tablas `archivo` y `alumno_fotografia` quedaron disponibles.
- El volumen `private_files` se creó con permisos para el usuario no privilegiado de la
  aplicación. El formulario autenticado mostró el panel multipart y Actuator respondió
  `UP` en `http://localhost:8080`.
- No se cargaron archivos reales ni se modificaron alumnos durante la verificación.
- El siguiente paso acordado es `Inscripcion` y `AsignacionGrupo`; todavía no se deben
  implementar cobros ni tesorería.

## Mejora visual posterior — ficha técnica del alumno

- La edición del alumno ahora abre con una ficha de perfil antes del formulario:
  fotografía destacada, nombre completo, matrícula, institución, estado, nacimiento,
  ingreso, CURP y contacto.
- Cargar, reemplazar, retirar y consultar el historial fotográfico quedó integrado en
  la cabecera. El diseño responde en móvil y conserva los temas claro y oscuro.
- Docker compiló las 197 fuentes de producción y ejecutó 131 pruebas sin fallos ni
  errores. La página autenticada confirmó que la ficha se renderiza antes del
  formulario multipart.
- Esta mejora no altera el dominio ni el siguiente paso: continúan `Inscripcion` y
  `AsignacionGrupo`.

## Decisión operativa pendiente — respaldo de archivos privados

- En desarrollo se conserva el volumen Docker `private_files`; es persistente respecto
  del contenedor, pero no sustituye un respaldo y no viaja mediante Git.
- Para recuperar o trasladar una instalación deben respaldarse juntos PostgreSQL y
  `/data/nexo-escolar`, porque la base conserva metadatos y relaciones mientras el
  volumen contiene los bytes.
- Nunca usar `docker compose down -v` sin autorización y respaldo verificado. Los
  respaldos deben quedar fuera del repositorio y copiarse cifrados a otro equipo o
  servicio privado.
- Antes de producción se definirá automatización, retención, restauración probada y
  destino externo. Para una instancia se admite volumen respaldado; un bind mount debe
  apuntar a una ruta dedicada fuera del repositorio. Para varias instancias se evaluará
  almacenamiento de objetos S3/MinIO.

## Decisiones — inscripciones y asignaciones de grupo

- Flyway V10 crea `Inscripcion` y `AsignacionGrupo`, junto con los permisos
  `INSCRIPCION_LEER` e `INSCRIPCION_ADMINISTRAR`; no modifica roles existentes.
- La inscripción es la fuente de verdad de plantel, ciclo y grado. Un traslado o una
  promoción cierra la trayectoria anterior y crea otra enlazada; una nueva asignación
  de grupo cierra la anterior y conserva ambas.
- El servicio valida misma institución, relaciones activas, oferta del plantel, ciclo
  abierto, fechas dentro del ciclo, ausencia de solapamientos y transiciones de estado.
- La asignación de grupo exige coincidencia exacta de plantel, ciclo y grado, y protege
  la capacidad con bloqueo pesimista para evitar sobrecupo concurrente.
- La consola ofrece alta, edición, cambio/finalización de grupo y continuidad académica,
  con autocompletado remoto de alumnos, alcance institucional o por plantel, temas y
  diseño responsivo.
- El listado filtra y pagina en PostgreSQL. La exportación Apache POI aplica el mismo
  filtro y procesa los resultados por bloques, sin cargar toda la tabla en memoria.

## Verificación de inscripciones y asignaciones

- Docker compiló 213 fuentes Java de producción y ejecutó 138 pruebas sin fallos ni
  errores. Las pruebas del servicio cubren creación, aislamiento, continuidad,
  capacidad, cambio de grupo y estados terminales; el alcance por plantel también se
  verifica de forma aislada.
- Flyway aplicó V10 sobre el volumen existente, Hibernate validó el esquema y detectó
  22 repositorios. Actuator respondió `UP` en `http://localhost:18080`.
- El listado, el formulario y una exportación filtrada respondieron autenticados; el
  archivo comenzó con firma XLSX `504b0304`. No se crearon inscripciones ni
  asignaciones reales durante la verificación.
- El siguiente paso es acordar meses cobrables, verano, vencimientos y actualización
  de cuotas; después iniciar Flyway V11 con `ConceptoCobro` y `CuotaAlumno`. Pagos, caja
  y tesorería permanecen fuera de esta etapa.
