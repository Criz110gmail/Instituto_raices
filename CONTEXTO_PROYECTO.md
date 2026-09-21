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
- La lista de módulos de la navegación lateral tiene desplazamiento vertical propio en
  escritorio y móvil; la marca y el estado de servicios quedan fijos y el módulo activo
  se lleva automáticamente a una posición visible.
- El menú filtra módulos con las autoridades de la sesión: basta lectura o
  administración para catálogos operativos, mientras Roles y Usuarios exigen
  administración. El ingreso `/admin` abre el primer módulo autorizado; las consultas
  y exportaciones revalidan el permiso en el controlador además de Spring Security.
  Las autoridades se actualizan para el usuario en su siguiente inicio de sesión.
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
- El selector de grado del alta de grupos dejó de depender de la fotografía inicial de
  la página: consulta sin caché únicamente los grados activos ofrecidos por el plantel
  seleccionado. Un panel accesible y compatible con ambos temas distingue los estados
  de espera, carga, disponibilidad, ausencia de oferta y error.

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
- Las sesiones caducadas ahora redirigen al login con un aviso específico, incluso
  cuando el primer síntoma es un token CSRF vencido al guardar un formulario. Las
  consultas asíncronas de autocompletado y grados también detectan la redirección.
- Los errores de permisos continúan en la pantalla 403 y el cierre normal elimina
  `JSESSIONID` para evitar falsos positivos. Se verificaron solicitudes `GET` y `POST`
  con una sesión inválida: ambas respondieron 302 hacia `/login?sesionExpirada`; la
  suite completa ejecutó 155 pruebas sin fallos y la aplicación quedó `UP`.

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

## Política confirmada — control individual y pagos familiares

- Toda cuota y todo cargo pertenecen a una inscripción y, por consecuencia, a un solo
  alumno. No existe un saldo familiar que oculte cuánto corresponde a cada hijo.
- Un tutor podrá realizar en una etapa futura un único pago para varios hijos. Ese pago
  conservará un solo ingreso, pero el administrador distribuirá el importe mediante
  aplicaciones independientes a los cargos de cada alumno.
- El administrador determina importe, periodo y vencimiento. Una cuota mensual usa un
  día de vencimiento de 1 a 31 y en meses más cortos se ajustará al último día; una cuota
  única usa una fecha exacta.
- Las cuotas pueden configurarse con generación manual o automática. Los conceptos de
  servicio, material u otro permiten modelar cobros extraordinarios sin mezclarlos con
  la colegiatura.
- Cambiar una cuota sólo afectará emisiones futuras. Los cargos ya emitidos conservarán
  su descripción, importe y vencimiento históricos.

## Decisiones — conceptos de cobro y cuotas por alumno

- Flyway V11 crea `ConceptoCobro` y `CuotaAlumno` de forma aditiva y agrega cuatro
  permisos técnicos sin concederlos a roles existentes.
- Un concepto pertenece a la institución, tiene código único, categoría y reglas para
  permitir beca, descuento o recargo; no guarda un precio común para todos los alumnos.
- La cuota guarda el importe individual, moneda, frecuencia `UNICA` o `MENSUAL`, rango,
  vencimiento, estado y `generacionAutomatica`. No pueden existir cuotas activas
  superpuestas para el mismo concepto e inscripción.
- Sólo conceptos e inscripciones vigentes admiten cuotas activas. Una cuota suspendida
  o finalizada no puede generar automáticamente y una finalizada queda inmutable.
- Conceptos se administran con alcance institucional. Cuotas respetan el plantel de la
  inscripción. Las búsquedas de inscripción y concepto usan autocompletado remoto,
  resultados acotados e índices GIN `pg_trgm`.
- Ambos módulos incluyen formularios responsivos con temas claro/oscuro, filtros y
  paginación en PostgreSQL y Excel por bloques reutilizando los mismos filtros.

## Verificación de conceptos y cuotas

- Docker compiló 234 fuentes Java de producción y ejecutó 151 pruebas sin fallos ni
  errores. Trece verificaciones adicionales cubren conceptos, cuotas individuales,
  vencimientos, solapamientos, estados, automatización, alcance y autocompletados.
- Flyway aplicó V11 sobre el volumen existente, Hibernate validó el esquema y detectó
  24 repositorios. Actuator respondió `UP`.
- Listados, formularios y exportación filtrada respondieron autenticados; el XLSX empezó
  con `504b0304`. Las tablas `concepto_cobro` y `cuota_alumno` quedaron vacías y el
  catálogo alcanzó 30 permisos técnicos; no se asignaron permisos a roles.
- El siguiente paso era Flyway V12 con `Cargo`, pero una mejora solicitada de identidad
  de sesión ocupó esa versión de forma aditiva. `Cargo` continúa ahora en Flyway V13,
  con alta manual y generación automática
  idempotente desde cuotas. Pagos, aplicaciones, caja y tesorería siguen fuera de esta
  etapa.

## Decisiones — identidad visible y fotografía de usuario

- Flyway V12 agrega una referencia opcional desde `Usuario` hacia `Archivo`; no altera
  roles, permisos ni credenciales.
- La administración de usuarios permite cargar o reemplazar JPEG/PNG de hasta 5 MB y
  volver al avatar genérico. Se valida firma, formato, dimensiones y SHA-256 antes de
  relacionar el archivo; la fotografía sustituida se marca como retirada sin eliminar
  físicamente el archivo privado.
- El encabezado compartido muestra el nombre de usuario autenticado y obtiene su foto
  actual mediante una ruta autenticada sin caché. La cuenta temporal de recuperación y
  los usuarios sin fotografía reciben siempre el avatar genérico.
- El diseño funciona en temas claro/oscuro y, en móvil, distribuye las acciones en un
  segundo renglón para mantener visible el nombre de usuario.

## Verificación de identidad de sesión

- Docker compiló 239 fuentes Java y ejecutó 167 pruebas sin fallos ni errores. Ocho
  pruebas nuevas cubren validación real de imagen, reemplazo, retiro, almacenamiento,
  integración del controlador, entrega del avatar genérico y publicación segura del
  nombre de sesión, incluso cuando no existe autenticación.
- Flyway validó doce migraciones y aplicó V12 sobre el volumen existente; Hibernate
  validó el esquema y la aplicación inició correctamente en `http://localhost:18080`.
- La revisión pública confirmó el arranque y el flujo de sesión caducada. No se usaron
  credenciales del `.env`, no se cargaron fotografías y no se modificaron usuarios.
- Una revisión posterior al login detectó que el dialecto Thymeleaf no proporcionaba
  `#authentication`; se sustituyó por `IdentidadSesionAdvice`, evitando el error de
  renderizado sin agregar otra dependencia.

## Decisiones — cargos individuales y generación idempotente

- Flyway V13 crea `Cargo` y agrega `CARGO_LEER` y `CARGO_ADMINISTRAR` sin modificar
  roles existentes. Cada obligación pertenece a una inscripción y, por consecuencia,
  conserva el alumno y plantel al que corresponde.
- El alta manual permite registrar cobros extraordinarios con concepto, descripción
  histórica, periodo, emisión, vencimiento, importe y periodo académico opcional.
- El generador consulta por bloques de 100 únicamente cuotas activas, automáticas, con
  concepto activo e inscripción vigente. Las cuotas mensuales se convierten en un cargo
  por mes y el día 29, 30 o 31 se recorta al último día del mes cuando corresponde.
- La clave `AUTO:<institución>:<cuota>:<periodo>` tiene restricción única y se inserta
  mediante `ON CONFLICT DO NOTHING`; repetir o ejecutar concurrentemente el proceso no
  duplica cargos. Una cuota única se emite una sola vez.
- Los cargos emitidos son inmutables. Sólo pueden cancelarse con bloqueo, versión
  optimista y motivo obligatorio; la fila y sus importes históricos permanecen.
- El catálogo filtra y pagina en PostgreSQL, aplica alcance institucional o por plantel
  y exporta con Apache POI exactamente el mismo filtro en bloques. Las pantallas de alta,
  generación y detalle/cancelación son responsivas y compatibles con tema claro/oscuro.
- El autocompletado opcional de periodo académico se acota por la inscripción y usa un
  índice GIN `pg_trgm`; no carga todos los periodos en el formulario.

## Verificación de cargos

- Docker compiló 253 fuentes Java y ejecutó 175 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren meses cortos, separación por inscripción, idempotencia,
  aislamiento institucional y cancelación histórica.
- Flyway validó trece migraciones y aplicó V13 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 25 repositorios y la aplicación inició correctamente.
- Actuator respondió `UP` en `http://localhost:18080`. No se generaron cargos reales ni
  se modificaron cuotas existentes durante la verificación.
- Se corrigió el enlace del formulario de cuota única: el DTO descarta siempre el día
  mensual cuando la frecuencia es `UNICA`, y descarta la fecha exacta cuando es
  `MENSUAL`. Esto evita que un valor visual predeterminado produzca una validación falsa.
- La validación de vigencia de cuota informa ahora cuál fecha quedó fuera y muestra el
  rango exacto permitido por la intersección entre inscripción y ciclo escolar.
- El expediente de inscripción incorpora una ficha visual del alumno con fotografía
  actual o iniciales de respaldo, matrícula, nacimiento, CURP, plantel, ciclo y grado.
  La descarga de la foto valida primero el acceso a la inscripción y usa `no-store`.
- El siguiente paso es `TipoBeca`, `BecaAlumno` y `AjusteCargo` en Flyway V14. Pagos,
  aplicaciones, caja y tesorería continúan fuera de esta etapa.

## Decisiones — becas individuales y ajustes históricos

- Flyway V14 crea `TipoBeca`, `BecaAlumno` y `AjusteCargo`, junto con seis permisos
  técnicos que no se conceden automáticamente a roles existentes.
- Una beca se asigna a una inscripción y a un solo concepto. Puede ser porcentaje o
  monto fijo, tiene vigencia y estado, y no puede solaparse con otra beca activa para
  el mismo alumno y concepto.
- Al emitir un cargo manual o automático se localiza la beca vigente y se registra un
  ajuste separado con base, porcentaje y monto congelados. El porcentaje se redondea
  `HALF_UP` a dos decimales y el monto fijo nunca reduce el cargo por debajo de cero.
- Modificar, suspender o finalizar una beca sólo afecta cargos futuros. Un concepto no
  puede retirar la autorización de becas mientras conserve asignaciones activas.
- Los ajustes manuales admiten descuento, recargo y corrección según la política del
  concepto. Son inmutables y se corrigen creando una reversa igual y de efecto opuesto.
- Los tres módulos filtran y paginan en PostgreSQL, exportan XLSX por bloques con los
  mismos filtros, respetan alcance institucional/plantel y conservan temas claro/oscuro.

## Verificación de becas y ajustes

- Docker compiló 285 fuentes Java y ejecutó 181 pruebas sin fallos ni errores. Las seis
  pruebas nuevas cubren redondeo, monto fijo, idempotencia, límites, política del
  concepto y reversas históricas.
- Flyway validó catorce migraciones y aplicó V14 sobre PostgreSQL 17. Hibernate validó
  el esquema y detectó 28 repositorios; Actuator respondió `UP`.
- No se crearon becas, cargos ni ajustes reales durante la verificación.
- El siguiente paso requiere confirmar porcentaje o monto, días de gracia, periodicidad
  y límite de los recargos automáticos. Hasta entonces sólo se permiten manualmente.

## Decisiones — políticas de recargo automático

- Flyway V15 crea `PoliticaRecargo`, agrega su referencia y una clave idempotente a
  `AjusteCargo`, y suma `POLITICA_RECARGO_LEER` y
  `POLITICA_RECARGO_ADMINISTRAR` sin concederlos a roles existentes.
- Cada concepto admite una política configurable: porcentaje o monto fijo, de 0 a 365
  días completos de gracia, aplicación única o mensual y límite opcional por monto o
  porcentaje del importe original.
- El primer recargo se genera después de la fecha del cargo y de todos los días de
  gracia. En recurrencia mensual se conserva el día equivalente y se ajusta al final
  de los meses cortos.
- El cálculo es simple y no capitaliza recargos. Usa el importe original ajustado por
  becas, descuentos y correcciones ajenas a mora; si esa base queda en cero, no genera.
- Cada periodo usa la clave `RECARGO:<política>:<cargo>:<periodo>` y se inserta con
  `ON CONFLICT DO NOTHING`. Una reversa reduce el acumulado usado para el límite, pero
  no habilita el cobro duplicado del mismo periodo.
- La consola incluye alta, edición y desactivación de políticas, generación por fecha de
  corte e institución/plantel, listado filtrado y paginado y Excel por bloques con los
  mismos filtros. El concepto no puede desactivarse ni dejar de permitir recargos si
  conserva una política activa.

## Verificación de recargos automáticos

- Docker compiló 299 fuentes Java y ejecutó 185 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren porcentaje, base descontada, días de gracia, meses cortos,
  tope prorrateado, idempotencia y saldo base cero.
- Flyway validó quince migraciones y aplicó V15 sobre PostgreSQL 17. Hibernate validó
  el esquema y las consultas, detectó 29 repositorios y Actuator respondió `UP`.
- PostgreSQL confirmó 40 permisos técnicos. `politica_recargo` y los ajustes vinculados
  a políticas permanecieron vacíos; no se modificaron datos operativos.
- El siguiente paso es acordar e implementar primero `CuentaFinanciera` y después la
  recepción, validación y aplicación de pagos. Un pago familiar podrá distribuirse entre
  varios hijos, pero cada aplicación quedará ligada al cargo y alumno correspondiente.

## Decisiones — cuentas financieras para recepción de pagos

- Flyway V16 crea `CuentaFinanciera` y agrega `CUENTA_FINANCIERA_LEER` y
  `CUENTA_FINANCIERA_ADMINISTRAR` sin ampliar roles existentes.
- Una cuenta pertenece a una institución y opcionalmente a un plantel; sin plantel es
  compartida y sólo puede administrarse con alcance institucional. Los usuarios de
  plantel administran únicamente cuentas de sus planteles autorizados.
- Los tipos iniciales son caja, banco e inversión. Caja no conserva banco, número ni
  CLABE; banco e inversión requieren nombre de la institución financiera y al menos un
  identificador. La moneda coincide con la predeterminada de la institución.
- El saldo inicial es no negativo y su fecha no puede ser futura. Podrá editarse mientras
  todavía no haya movimientos; una etapa posterior deberá bloquear moneda y apertura
  después del primer movimiento y realizar correcciones mediante movimientos trazables.
- Número de cuenta y CLABE completos sólo se muestran en el formulario administrativo.
  El listado y el Excel reutilizan los mismos filtros paginados y muestran únicamente
  los cuatro últimos caracteres.
- La pantalla es responsiva, compatible con tema claro/oscuro, filtra planteles por
  institución y oculta los campos bancarios al seleccionar caja.

## Verificación de cuentas financieras

- Docker compiló 309 fuentes Java y ejecutó 199 pruebas sin fallos ni errores. Las
  pruebas nuevas cubren normalización, duplicados, plantel ajeno, reglas de caja/banco,
  moneda, fecha, desactivación, formulario y alcance institucional/plantel.
- Flyway validó dieciséis migraciones y aplicó V16 sobre PostgreSQL 17. Hibernate validó
  el esquema, detectó 30 repositorios y Actuator respondió `UP`.
- PostgreSQL confirmó 42 permisos técnicos y cero cuentas financieras. No se crearon ni
  modificaron datos operativos durante la verificación.
- El navegador confirmó que la ruta administrativa exige sesión y conserva el aviso de
  sesión caducada; no se utilizaron credenciales para crear registros de prueba.
- El siguiente paso es V17 con registro de `Pago`, comprobantes privados y solicitudes
  de distribución. Permanecerán pendientes la validación, las aplicaciones que afectan
  saldos y la generación del movimiento financiero único por pago.

## Decisiones — pagos pendientes, comprobantes y distribución solicitada

- Flyway V17 crea `Pago`, `ComprobantePago` y `SolicitudAplicacionPago`, además de los
  permisos `PAGO_LEER` y `PAGO_REGISTRAR` sin modificar los roles existentes.
- El alta registra efectivo o transferencia exclusivamente como `PENDIENTE_VALIDACION`.
  Folio e idempotencia son únicos por institución; fecha futura, moneda ajena, tutor,
  plantel o cuenta incompatibles se rechazan antes de persistir.
- Una transferencia requiere al menos un comprobante. Se aceptan como máximo cinco
  archivos JPEG, PNG o PDF de 10 MB cada uno, comprobando su firma binaria, nombre,
  tamaño y SHA-256. Los bytes viven en `private_files` y la descarga autorizada usa
  `no-store` y disposición de archivo adjunto.
- Efectivo puede declarar una cuenta CAJA y transferencia una cuenta BANCO o INVERSION;
  la cuenta es opcional hasta que la validación elija el destino real.
- Un pago puede proponer distribución para varios hijos del mismo tutor. Cada cargo se
  valida contra responsabilidad financiera vigente, institución, moneda, estado y saldo;
  la suma solicitada no supera el pago y el remanente permanece visible sin asignar.
- Ni el pago pendiente ni sus solicitudes modifican saldos o cargos. Todavía no existen
  `AplicacionPago`, movimientos, devoluciones, cortes ni retiros.
- El listado filtra y pagina en PostgreSQL y exporta exactamente los mismos filtros con
  Apache POI por bloques. El alta y detalle son responsivos, compatibles con ambos temas
  y usan búsquedas remotas acotadas para tutores, cuentas y cargos.

## Verificación de pagos pendientes

- Docker compiló 328 fuentes Java de producción y ejecutó 209 pruebas sin fallos ni
  errores. Diez pruebas nuevas cubren pago para varios hijos, remanente, comprobantes,
  firma falsa, autorización financiera, tipo de cuenta, formulario y redirección.
- Flyway validó diecisiete migraciones y aplicó V17 sobre PostgreSQL 17. Hibernate validó
  el esquema, detectó 33 repositorios y Actuator respondió `UP`.
- No se registraron pagos, comprobantes ni distribuciones operativas durante la
  verificación. La siguiente etapa es V18: validar o rechazar pagos, crear aplicaciones
  por cargo y publicar un solo movimiento de ingreso por pago validado, todo de manera
  atómica e idempotente.

## Decisiones — validación, aplicaciones e ingreso de pagos

- Flyway V18 crea `AplicacionPago`, `MotivoFinanciero` y `MovimientoFinanciero`, y suma
  `PAGO_VALIDAR` sin asignarlo automáticamente a roles existentes.
- Sólo una cuenta de usuario persistida puede decidir sobre dinero. El acceso de
  recuperación puede consultar, pero no validar ni rechazar pagos.
- Validar exige una cuenta activa, accesible para el plantel, con la moneda y tipo
  compatibles. El pago y la cuenta se bloquean antes de procesar los cargos en orden
  estable para proteger la secuencia y evitar aplicaciones concurrentes excesivas.
- Cada solicitud se vuelve a comprobar contra el saldo y la responsabilidad financiera
  vigentes. Si el saldo disminuyó, se aplica sólo el importe disponible; la diferencia y
  cualquier parte no solicitada permanecen como monto disponible del pago.
- Se publica exactamente un movimiento `COBRO/INGRESO` por el importe total del pago.
  La clave `COBRO:PAGO:<id>`, la relación única con pago y las aplicaciones ligadas a su
  solicitud protegen los reintentos. Toda la operación comparte una transacción.
- Rechazar exige motivo y no crea movimientos ni aplicaciones. Los comprobantes se
  conservan. Los cálculos y pantallas de Cargo ya muestran saldo y estado considerando
  aplicaciones; no se puede cancelar un cargo pagado ni modificar la apertura de una
  cuenta que ya tenga movimientos.
- El expediente de pago ofrece una consola responsiva de decisión, búsqueda remota de
  cuenta destino, distribución efectiva, remanente y resumen del movimiento, compatible
  con temas claro y oscuro.

## Verificación de validación de pagos

- Docker compiló 345 fuentes Java y ejecutó 216 pruebas sin fallos ni errores.
- Flyway validó dieciocho migraciones y aplicó V18 sobre PostgreSQL 17. Hibernate validó
  el esquema y detectó 36 repositorios; Actuator respondió `UP`.
- PostgreSQL confirmó 45 permisos técnicos. Las tablas de pagos, aplicaciones y
  movimientos permanecieron vacías; no se crearon ni modificaron datos operativos.
- El siguiente paso es V19 con libro paginado de movimientos y saldo actual por cuenta,
  filtros y Excel equivalente. Operaciones manuales, traspasos, devoluciones y reversos
  siguen fuera de esta entrega.

## Decisiones — libro financiero y operaciones manuales

- Flyway V19 incorpora el permiso `MOVIMIENTO_FINANCIERO_LEER`, índices para las
  consultas frecuentes y un libro inmutable con paginación PostgreSQL. La vista filtra
  por cuenta, plantel, dirección, clase y fechas, calcula totales de página, muestra el
  saldo vigente de una cuenta seleccionada y exporta los mismos filtros por bloques.
- Flyway V20 incorpora `MOTIVO_FINANCIERO_LEER`,
  `MOTIVO_FINANCIERO_ADMINISTRAR` y `MOVIMIENTO_FINANCIERO_REGISTRAR`; ningún permiso
  nuevo se asigna automáticamente. También siembra motivos comunes por institución.
- El catálogo de motivos admite naturaleza `INGRESO`, `EGRESO` o `AMBOS`. El motivo
  técnico `COBROS_ESCOLARES` permanece reservado y activo para no romper la publicación
  de pagos validados.
- Una operación manual exige cuenta activa por autocompletado, motivo activo compatible,
  fecha no futura ni anterior a la apertura, institución/plantel autorizados y usuario
  persistido. El acceso de recuperación no puede registrar dinero.
- El servicio bloquea la cuenta, obtiene la última secuencia, calcula saldos y confirma
  con idempotencia. No admite saldo negativo. Los movimientos `OPERACION` no se editan
  ni eliminan; una corrección futura utilizará un movimiento reverso relacionado.

## Verificación de V19 y V20

- Docker compiló 364 fuentes Java y ejecutó 223 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren catálogo reservado, normalización, saldo secuencial, fondos
  insuficientes, naturaleza incompatible y bloqueo del acceso de recuperación.
- Flyway validó veinte migraciones y aplicó V20 sobre PostgreSQL 17. Hibernate validó
  el esquema, detectó 36 repositorios y `/actuator/health` respondió `UP`.
- No se creó ningún movimiento monetario durante la verificación automática.
- El propietario debe conceder los permisos de V20 y probar la interfaz. Después, el
  siguiente paso es V21 con transferencias atómicas entre cuentas; devoluciones,
  reversas generales, cortes y retiros permanecen fuera de alcance.

## Decisiones — transferencias entre cuentas

- Flyway V21 crea `TransferenciaCuenta`, agrega `transferencia_id` al libro financiero,
  protege con unicidad los lados ingreso/egreso y suma el permiso
  `TRANSFERENCIA_CUENTA_REGISTRAR` sin modificar roles existentes.
- Una transferencia sólo admite cuentas distintas, activas, de la misma institución y
  moneda. Debe respetar ambas fechas de apertura y no puede dejar negativo el origen.
- Las cuentas se buscan mediante autocompletado. El servicio las bloquea siempre por ID
  ascendente para evitar interbloqueos y valida el alcance institucional o de plantel.
- La entidad principal, el egreso de origen y el ingreso de destino se publican en una
  única transacción. Cada movimiento conserva su propia secuencia, saldos y una clave
  derivada de la transferencia; la operación principal también es idempotente.
- `TRASPASO_INTERNO` es un motivo técnico reservado con naturaleza `AMBOS`. El traspaso
  no representa ingreso ni gasto operativo y una comisión bancaria se registra aparte.
- V21 sólo registra transferencias aplicadas. El estado `REVERTIDA` prepara una futura
  reversa conjunta, pero no existe acción de reversión en esta entrega.

## Verificación de transferencias

- Docker compiló 373 fuentes Java y ejecutó 229 pruebas sin fallos ni errores. Las seis
  pruebas añadidas cubren ambos movimientos, saldos, bloqueo ascendente, fondos,
  monedas, cuenta repetida, acceso de recuperación y motivo técnico reservado.
- Flyway validó 21 migraciones y aplicó V21 sobre PostgreSQL 17. Hibernate validó el
  esquema y detectó 37 repositorios. No se generaron transferencias operativas.
- El propietario debe asignar `TRANSFERENCIA_CUENTA_REGISTRAR`, volver a iniciar sesión
  y probar con datos controlados. La siguiente etapa recomendada es V22 con devoluciones
  de pagos; las reversas generales quedan separadas.

## Decisiones — devoluciones de pagos

- Flyway V22 crea `DevolucionPago`, relaciona los ajustes de `AplicacionPago` y agrega
  un vínculo único desde `MovimientoFinanciero`. También crea `PAGO_DEVOLVER` y el motivo
  técnico reservado `DEVOLUCION_PAGO`, sin ampliar roles existentes.
- Sólo se devuelve un pago `VALIDADO`. El pago conserva ese estado porque la devolución
  representa salida real de dinero, no cancelación. Fecha, cuenta, moneda, alcance,
  fondos, monto acumulado, versión e idempotencia se validan dentro de la transacción.
- El disponible es monto del pago menos aplicaciones netas menos devoluciones ejecutadas.
  Si resulta insuficiente, el usuario selecciona aplicaciones vigentes. Para mantener
  historial inmutable, una aplicación se revierte completa y se reaplica su remanente
  cuando la liberación requerida es parcial.
- La entidad de devolución, los ajustes de cargos y exactamente un movimiento
  `DEVOLUCION/EGRESO` se publican juntos. El formulario permanece en el expediente ante
  errores y usa autocompletado acotado para la cuenta de origen.
- El expediente de pago muestra aplicado, devuelto, disponible e historial. El acceso
  de recuperación no puede devolver fondos y se requiere una cuenta de usuario persistida.

## Verificación de devoluciones

- Docker compiló 382 fuentes Java y ejecutó 235 pruebas sin fallos ni errores. Las seis
  pruebas añadidas cubren egreso único, devolución parcial aplicada, selección
  insuficiente, límite acumulado, fondos de cuenta y motivo técnico reservado.
- Flyway validó 22 migraciones y aplicó V22 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 38 repositorios y `/actuator/health` respondió `UP`.
- No se ejecutaron devoluciones ni otros movimientos monetarios durante la verificación.
- El propietario debe asignar `PAGO_DEVOLVER`, iniciar una sesión nueva y probar con
  datos controlados. Después, la etapa recomendada es V23 para reversas trazables de
  operaciones manuales y transferencias; cancelaciones, cortes y retiros quedan separados.

## Decisiones — reversas financieras

- Flyway V23 crea `ReversionFinanciera`, agrega la relación desde los movimientos
  compensatorios y suma `MOVIMIENTO_FINANCIERO_REVERTIR` sin modificar roles existentes.
- El flujo general sólo acepta `OPERACION` manual y transferencias `APLICADA`. Cobros,
  devoluciones y aplicaciones de pago mantienen sus procesos propios; ningún original
  se edita o elimina y una restricción impide una segunda reversa.
- La reversa manual publica un movimiento `REVERSO` opuesto con el mismo importe, motivo
  financiero, alcance y referencia. Deshacer un ingreso requiere saldo actual suficiente.
- La reversa de transferencia bloquea ambas cuentas por ID ascendente y publica de forma
  atómica el retorno al origen y la salida del destino. La cuenta receptora debe conservar
  el importe y la transferencia cambia a `REVERTIDA` en la misma transacción.
- La cabecera conserva objetivo, fecha, motivo, actor e idempotencia. Se validan cuenta
  activa, alcance, fecha original, aperturas y actor persistido; recuperación no autoriza.
- El libro identifica movimientos revertidos y reversos compensatorios, y presenta la
  acción sólo cuando corresponde. Los errores permanecen en el formulario.

## Verificación de reversas financieras

- Docker compiló 392 fuentes Java y ejecutó 242 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren reversa simple, pareja inversa, saldos, clases no admitidas,
  fondos gastados en la receptora, acceso de recuperación y reintento idempotente.
- Flyway validó 23 migraciones y aplicó V23 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 39 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó cero cabeceras y cero movimientos de reversa; no se movió dinero
  real durante la verificación automática.
- El propietario debe asignar `MOVIMIENTO_FINANCIERO_REVERTIR`, iniciar una sesión nueva
  y probar con datos controlados. Después se recomienda V24 para cortes de caja y
  conciliación; cancelaciones de pagos y retiros especializados quedan separados.

## Decisiones — cortes de caja

- Flyway V24 crea `CorteCaja` y agrega `CORTE_CAJA_LEER` y
  `CORTE_CAJA_ADMINISTRAR` sin modificar roles existentes. El flujo aplica únicamente a
  cuentas `CAJA`; no incluye bancos, inversiones, cancelaciones de pagos ni retiros.
- La apertura captura el momento del servidor, actor persistido, saldo vigente y último
  folio de la cuenta. Existe un solo corte abierto por caja y claves institucionales de
  idempotencia distintas para apertura y cierre.
- El cierre bloquea la cuenta y el corte, resume por secuencia los movimientos posteriores
  a la apertura y conserva folio final, cantidad, ingresos, egresos y saldo esperado. La
  secuencia evita perder movimientos capturados tarde con una fecha operativa anterior.
- El efectivo se declara mediante conteo ciego; el saldo esperado se muestra sólo tras
  cerrar. La diferencia es declarado menos esperado y, cuando no es cero, la justificación
  es obligatoria tanto en servicio como en PostgreSQL.
- El cierre conserva responsables y observaciones, es inmutable y no genera movimientos
  monetarios. Una caja inactiva puede cerrar un corte pendiente, pero no abrir otro.
- El listado filtra y pagina en PostgreSQL, exporta los mismos filtros por bloques y usa
  autocompletado remoto limitado a cajas activas. El alcance de plantel nunca expone cajas
  institucionales ni cortes de otros planteles.

## Verificación de cortes de caja

- Docker compiló 409 fuentes Java y ejecutó 249 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren apertura, exclusividad, cálculo por folios, diferencia obligatoria,
  idempotencia de cierre, tipo de cuenta y bloqueo del acceso de recuperación.
- Flyway validó 24 migraciones y aplicó V24 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 40 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó V24 exitosa, dos permisos nuevos y cero cortes. No se crearon cortes
  ficticios ni movimientos monetarios durante la verificación.
- El propietario debe asignar ambos permisos, iniciar una sesión nueva y probar apertura,
  movimientos, conteo, cierre con y sin diferencia y Excel. Después se recomienda V25
  para estados de cuenta y reportes financieros operativos.

## Decisiones — estados de cuenta y reportes financieros

- Flyway V25 agrega `REPORTE_FINANCIERO_CONSULTAR` y tres índices de apoyo; no crea
  entidades de saldo ni asigna el permiso a roles existentes. Los reportes se reconstruyen
  desde los libros inmutables de cargos, ajustes, aplicaciones y movimientos.
- El estado de cuenta administrativo exige un alumno por autocompletado y admite corte,
  plantel y situación. Calcula importe original, ajustes efectivos, aplicaciones netas,
  saldo y vencimiento. Un adeudo parcial vencido se clasifica como `VENCIDO`; un cargo
  cancelado permanece visible pero no suma al exigible.
- Tesorería admite cuenta, plantel de operación, periodo y agrupación diaria, mensual o
  anual. Usa `fechaOperacion` interpretada en la zona horaria institucional. Los
  traspasos y reversas de transferencia se excluyen de ingresos/egresos operativos y se
  reportan aparte para evitar doble conteo.
- Apertura y cierre se calculan desde saldo inicial y movimientos sólo cuando el filtro
  representa cuentas completas. Un filtro por plantel de operación no prorratea cuentas
  institucionales ni inventa un saldo atribuible.
- Los usuarios con alcance de plantel sólo consultan cargos y cuentas de sus planteles;
  no reciben cuentas compartidas institucionales. Ambos reportes pagan el costo de la
  consulta en PostgreSQL, tienen paginación y exportan XLSX por bloques con los mismos
  filtros. No publican ni modifican movimientos monetarios.
- Identificadores inexistentes o filtros de negocio inválidos muestran el error dentro
  de la pantalla. Las vistas son responsivas y compatibles con temas claro y oscuro.

## Verificación de reportes financieros

- Docker compiló 424 fuentes Java y ejecutó 254 pruebas sin fallos ni errores. Las cinco
  pruebas nuevas cubren fecha local institucional, estado vacío, pertenencia del alumno,
  periodo invertido y conversión de fechas a instantes en la zona institucional.
- Flyway validó 25 migraciones y aplicó V25 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 40 repositorios y la aplicación inició en el puerto 8080.
- PostgreSQL confirmó V25, los tres índices y el permiso nuevo, y aceptó las consultas
  nativas de estado de cuenta, agrupación de tesorería y saldo de apertura. La validación
  fue de solo lectura y no creó movimientos, cargos ni saldos ficticios.
- El propietario debe asignar `REPORTE_FINANCIERO_CONSULTAR`, iniciar una sesión nueva y
  revisar ambas pestañas y sus Excel con datos existentes. Después se recomienda acordar
  V26 para eventos escolares y destinatarios como base del futuro portal del tutor;
  avisos y notificaciones quedan para una etapa posterior.

## Decisiones — eventos escolares

- Flyway V26 crea `EventoEscolar` y `DestinatarioEvento`, y agrega
  `EVENTO_ESCOLAR_LEER` y `EVENTO_ESCOLAR_ADMINISTRAR` sin modificar roles existentes.
- Los estados son `BORRADOR`, `PUBLICADO` y `CANCELADO`. Sólo se edita un borrador;
  publicar es explícito y cancelar exige motivo, conserva el historial y nunca elimina
  el registro. Los tipos iniciales son junta, festival, suspensión, actividad y otro.
- El alcance puede ser institución, plantel o selección. Una selección puede combinar
  niveles, grados, grupos y alumnos con semántica de unión; se validan institución,
  oferta, ciclo, plantel, inscripción vigente y ausencia de duplicados.
- Las fechas locales se convierten a `Instant` con la zona horaria institucional y deben
  quedar dentro de un ciclo no cerrado. La administración institucional exige alcance
  institucional y el usuario de plantel queda limitado a sus planteles.
- El listado filtra y pagina en PostgreSQL y exporta XLSX por bloques. La selección usa
  autocompletado remoto indexado y acotado, no listas completas. Crear y actualizar
  conservan la captura ante errores; publicación y cancelación muestran los errores en
  la ficha del evento.
- V26 constituye la fuente administrativa de eventos para el futuro portal del tutor.
  No implementa todavía el portal, avisos libres, notificaciones internas, correo ni
  WhatsApp.

## Verificación de eventos escolares

- Docker compiló 447 fuentes Java y ejecutó 261 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren zona horaria, rango invertido, selección vacía, grupo de otro
  ciclo, publicación única y cancelación histórica con motivo normalizado.
- Flyway validó 26 migraciones y aplicó V26 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 41 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó la migración V26, sus dos tablas y sus dos permisos. No se crearon
  eventos ni destinatarios ficticios durante la verificación.
- El propietario debe asignar ambos permisos, iniciar una sesión nueva y probar creación,
  edición, publicación, cancelación, filtros, autocompletado y Excel. Tras confirmar y
  subir V26, debe acordarse V27 para la primera entrega del portal del tutor con hijos
  vinculados, estado de cuenta derivado y eventos publicados aplicables.

## Decisiones — primer portal del tutor

- Flyway V27 agrega `PORTAL_TUTOR_ACCEDER` y un índice parcial para eventos publicados;
  no crea tablas duplicadas ni asigna el permiso a roles existentes.
- El portal vive en `/portal`, separado de administración. La cuenta debe estar enlazada
  con un tutor activo y cada consulta limita alumnos y fotografías a vínculos activos y
  vigentes. El acceso de recuperación y los identificadores ajenos se rechazan.
- El selector familiar muestra matrícula, parentesco, fotografía y situación académica
  vigente derivada de inscripción y asignación de grupo. La ausencia de inscripción se
  presenta explícitamente.
- Finanzas se habilita sólo si el vínculo es responsable financiero y además permite ver
  finanzas. El estado de cuenta reutiliza la reconstrucción de cargos, ajustes y abonos,
  con paginación, sin persistir saldos ni permitir operaciones monetarias.
- La agenda resuelve únicamente eventos publicados del mismo ciclo que alcancen al alumno
  por institución, plantel, nivel, grado, grupo o destino individual. Presenta próximos
  eventos y hasta 30 días de historial reciente, paginados de diez en diez.
- La vista es responsiva, compatible con tema claro y oscuro, incorpora fotografía
  privada y cierre de sesión. Un usuario sólo de portal que entra por `/admin` es enviado
  al portal en lugar de recibir una pantalla de acceso denegado.
- El diseño se separa intencionalmente del sistema administrativo: usa una identidad
  familiar cálida, lenguaje directo, selector visual de hijos, accesos rápidos, agenda
  cronológica, resumen simple de pagos y una barra inferior tipo aplicación en móvil.
  No expone términos técnicos como expediente, libros contables o permisos al tutor.
- Se incorporó `/familias` como acceso visual exclusivo para tutores. El formulario
  reutiliza la autenticación segura existente, pero los aciertos, errores y cierres de
  sesión conservan el recorrido familiar; `/login` continúa reservado visualmente para
  el personal. Una visita sin sesión a `/portal` se dirige a `/familias`.

## Verificación del primer portal del tutor

- Docker compiló 453 fuentes Java y ejecutó 270 pruebas sin fallos ni errores. Las siete
  pruebas nuevas cubren recuperación, tutor no vinculado, familia vacía, restricción y
  autorización financiera, alumno ajeno y protección de fotografía.
- Dos pruebas adicionales fijan las vistas independientes de acceso para personal y
  familias.
- Flyway validó 27 migraciones y aplicó V27 sobre PostgreSQL 17. Hibernate validó el
  esquema, detectó 41 repositorios y `/actuator/health` respondió `UP`.
- PostgreSQL confirmó el permiso y el índice; además ejecutó las consultas completas de
  vínculos e intersección de eventos con identificadores inexistentes, sólo en lectura.
  No se crearon tutores, vínculos, cargos o eventos ficticios.
- La imagen posterior al rediseño conservó salud `UP`; `/familias` respondió HTTP 200
  y una solicitud anónima a `/portal` respondió HTTP 302 hacia `/familias`.
- El propietario debe asignar `PORTAL_TUTOR_ACCEDER` a un rol con alcance
  `VINCULOS_TUTOR`, iniciar una sesión nueva con una cuenta enlazada y comprobar `/portal`.
  Después de confirmar y subir V27 se recomienda acordar V28 para Avisos institucionales
  y de plantel; las notificaciones internas quedan para una etapa posterior.
