# Continuidad del proyecto — Nexo Escolar

Este archivo permite continuar el desarrollo desde otra computadora o una conversación
nueva sin perder las decisiones tomadas. Antes de modificar código, lee también
`CONTEXTO_PROYECTO.md`, `README.md` y `modelo_entidades_sistema_escolar_v1.txt`.

## Objetivo y forma de trabajar

Construir un sistema administrativo escolar profesional, multiinstitución y
multiplantel. La comunicación con el propietario del proyecto debe ser en español.
Trabaja exclusivamente dentro de la carpeta del repositorio y conserva los cambios
existentes. No elimines datos, volúmenes Docker ni archivos sin autorización explícita.

Cuando el usuario diga «continúa» o «siguiente paso», retoma la sección **Siguiente
paso acordado** de este documento. Primero inspecciona el estado real del repositorio;
no vuelvas a implementar componentes que ya existan.

## Repositorio y estado confirmado

- Repositorio privado: `Criz110gmail/Instituto_raices`.
- Remoto esperado: `https://Criz110gmail@github.com/Criz110gmail/Instituto_raices.git`.
- Rama principal: `main`.
- Último commit confirmado en `main` y `origin/main`: `b7814f3` — `bitácora central e inmutable de auditoría para operaciones sensibles`.
- V31 ya está confirmada en Git. V32 (cancelación controlada de pagos) es el cambio
  local actual; el agente nuevo debe confirmar `git status` y `git log` antes de
  continuar y no debe reconstruir V1–V32.
- Nunca guardes tokens de GitHub, contraseñas o el contenido real de `.env` en Git.
- En equipos con varias cuentas de GitHub, conserva la configuración de credenciales
  a nivel local del repositorio y usa `credential.useHttpPath=true`.

## Tecnología y arquitectura

- Java 21, Maven y Spring Boot 4.1.1.
- Spring MVC con Thymeleaf; no es una SPA.
- PostgreSQL 17 mediante Docker Compose y volumen persistente `postgres_data`.
- Flyway administra exclusivamente el esquema; Hibernate usa `ddl-auto=validate`.
- Los archivos privados se guardan en el volumen Docker `private_files`; PostgreSQL
  conserva únicamente metadatos, checksum y relaciones.
- Para respaldar o mover una instalación se necesitan juntos `postgres_data` y
  `private_files`; nunca versionar las fotografías en Git.
- Git transporta código y migraciones, pero no transporta PostgreSQL ni los archivos
  de los volúmenes. Clonar el repositorio en otra computadora crea una instalación sin
  los datos operativos anteriores, salvo que se restauren ambos respaldos.
- Spring Data JPA, Bean Validation, Spring Security, Lombok, Actuator y Spring Mail.
- Apache POI 5.4.1 con `SXSSFWorkbook` para exportaciones Excel de bajo consumo de
  memoria.
- Paquete base: `escuela`.
- Módulos principales: `escuela.institucion`, `escuela.academico`, `escuela.seguridad`,
  `escuela.alumno`, `escuela.tutor`, `escuela.inscripcion`, `escuela.cobranza`,
  `escuela.finanzas`, `escuela.comunicacion`, `escuela.auditoria`, `escuela.admin`,
  `escuela.config` y `escuela.common`.
- Cada entidad usa identificador `Long`, auditoría y versión para concurrencia optimista.
- Los controladores y formularios usan DTO; nunca deben enlazarse directamente con
  entidades JPA.
- Los registros históricos se desactivan; no se eliminan físicamente.

## Modelo implementado en la primera etapa

El núcleo institucional y académico contiene ocho entidades:

1. `Institucion`
2. `Plantel`
3. `NivelEducativo`
4. `PlantelNivel` (oferta educativa)
5. `Grado`
6. `CicloEscolar`
7. `PeriodoAcademico`
8. `Grupo`

Para las ocho entidades ya existen migración Flyway, entidades, repositorios, DTO de
entrada y salida, mappers, servicios transaccionales y reglas de negocio. La migración
actual es `V1__crear_nucleo_institucional_academico.sql`.

Reglas importantes ya aplicadas:

- Los códigos, país, moneda y correo se normalizan antes de persistir o comparar.
- No se reasignan relaciones propietarias durante una actualización.
- Toda actualización exige la versión recibida y valida concurrencia optimista.
- Un ciclo predeterminado desmarca el anterior dentro de una transacción con bloqueo.
- Los periodos no pueden salir del ciclo, solaparse, duplicarse ni modificarse si el
  ciclo está cerrado.
- Un grupo sólo opera con institución, plantel, nivel y grado activos, ciclo abierto y
  una oferta educativa activa para ese plantel y nivel.
- La situación académica vigente de un alumno se deriva de `Inscripcion` y
  `AsignacionGrupo`; no debe duplicarse como otra fuente de verdad.

## Interfaz terminada

- Consola administrativa disponible bajo `/admin`.
- Navegación lateral para los ocho catálogos.
- La navegación lateral desplaza únicamente la lista de módulos cuando supera la
  altura disponible; marca, encabezado y estado de servicios permanecen visibles. El
  módulo activo se centra automáticamente dentro del área desplazable.
- La navegación sólo incluye módulos para los que la sesión tenga permiso `*_LEER` o
  `*_ADMINISTRAR`; Roles y Usuarios requieren su permiso administrativo. `/admin`
  redirige al primer módulo autorizado y el controlador vuelve a validar el permiso
  antes de consultar o exportar. Los cambios de rol se reflejan al iniciar una sesión
  nueva, porque las autoridades se cargan durante el login.
- Diseño profesional en azul tinta y cian, totalmente responsivo para escritorio,
  tableta y móvil.
- Tema claro/oscuro seleccionable y persistido en el dispositivo.
- Login personalizado de Nexo Escolar; se sustituyó el formulario predeterminado de
  Spring Security.
- Listados de los ocho módulos con filtros ejecutados en PostgreSQL, estados vacíos y
  paginación real de 10, 25, 50 o 100 filas.
- Las pantallas nunca deben recuperar el catálogo completo para mostrar un listado.
- Los ocho módulos exportan Excel con Apache POI. La exportación reutiliza el mismo
  `FiltroCatalogo` que la pantalla y recorre los datos filtrados por bloques.
- Mantenimiento completo —alta, edición y desactivación lógica— para:
  `Institucion`, `Plantel`, `NivelEducativo`, `PlantelNivel` y `Grado`.
- `CicloEscolar` cuenta con alta, edición, estados y selección transaccional del ciclo
  predeterminado; no usa desactivación porque su ciclo de vida se expresa por estado.
- `PeriodoAcademico` cuenta con alta y edición de ciclo, nivel, tipo, fechas, estado y
  observaciones, validando rangos, duplicados, solapamientos y ciclos cerrados.
- `Grupo` cuenta con alta, edición y desactivación lógica, con selectores dependientes y
  validación de oferta educativa activa.
- Al crear grupos, los grados se consultan bajo demanda para el plantel seleccionado,
  sin caché ni carga completa inicial. La pantalla informa claramente si está
  consultando, si encontró grados, si falta oferta educativa activa o si ocurrió un
  error de conexión.
- `Alumno` y `Tutor` cuentan con expediente institucional, alta, edición,
  desactivación lógica, filtros, paginación y Excel. El tutor puede vincularse de
  manera opcional con una cuenta de usuario de su misma institución.
- Los formularios incluyen Bean Validation, CSRF, mensajes de resultado y versión
  optimista, manteniendo los temas y el diseño responsivo.
- Los errores esperables al crear, actualizar o desactivar se muestran en el mismo
  formulario sin perder la captura: reglas de negocio, concurrencia y restricciones de
  integridad. Nunca se presentan SQL ni detalles internos de PostgreSQL al usuario.
- Las relaciones con catálogos de alto volumen usan autocompletado remoto, nunca un
  `<select>` con todos los registros. La búsqueda inicia con tres caracteres, respeta
  permisos y alcance institucional y devuelve como máximo 20 coincidencias.

Archivos clave de la interfaz:

- `src/main/java/escuela/admin/controller/CatalogoAdminController.java`
- `src/main/java/escuela/admin/service/CatalogoConsultaService.java`
- `src/main/java/escuela/admin/service/ExcelCatalogoService.java`
- `src/main/java/escuela/admin/dto/FiltroCatalogo.java`
- `src/main/java/escuela/admin/dto/ModuloCatalogo.java`
- `src/main/resources/templates/admin/catalogo.html`
- `src/main/resources/static/css/admin.css`
- `src/main/resources/static/css/forms.css`
- `src/main/resources/static/js/theme.js`

Los controladores y formularios de mantenimiento ya terminados sirven como patrón:

- `InstitucionAdminController` + `InstitucionForm` + `institucion-form.html`
- `PlantelAdminController` + `PlantelForm` + `plantel-form.html`
- `NivelEducativoAdminController` + `NivelEducativoForm` + `nivel-form.html`
- `PlantelNivelAdminController` + `PlantelNivelForm` + `oferta-form.html`
- `GradoAdminController` + `GradoForm` + `grado-form.html`
- `CicloEscolarAdminController` + `CicloEscolarForm` + `ciclo-form.html`
- `PeriodoAcademicoAdminController` + `PeriodoAcademicoForm` + `periodo-form.html`
- `GrupoAdminController` + `GrupoForm` + `grupo-form.html`

## Requisitos no negociables para módulos nuevos

Todo módulo que se construya debe incluir desde su primera entrega:

1. Diseño profesional e innovador, accesible y 100 % responsivo.
2. Compatibilidad completa con temas claro y oscuro.
3. Consultas paginadas en base de datos; nunca cargar todos los registros para un
   listado.
4. Filtros enviados desde la pantalla y aplicados en la consulta JPA.
5. Exportación `.xlsx` con Apache POI usando exactamente los mismos filtros.
6. Exportación por bloques/streaming para soportar crecimiento futuro.
7. DTO de formulario separado de la entidad JPA.
8. Bean Validation, CSRF, reglas de negocio, auditoría y control optimista de versión.
9. Desactivación lógica cuando el dominio la permita; no borrado físico.
10. Pruebas proporcionales al riesgo y verificación real en Docker antes de entregar.
11. Errores de negocio, concurrencia e integridad mostrados dentro del mismo formulario.
12. Toda relación con un catálogo de alto volumen debe usar autocompletado remoto con
    consulta indexada, alcance de seguridad, mínimo de tres caracteres y resultado
    acotado; nunca cargar la colección completa al abrir el formulario.

## Seguridad y variables de entorno

- `.env` existe sólo localmente y está ignorado por `.gitignore`.
- `.env.example` sí se versiona, pero contiene únicamente valores descriptivos.
- `compose.yaml` exige todas las variables; no contiene contraseñas de respaldo.
- `application.yml` exige `DB_PASSWORD` y tampoco conserva una contraseña predeterminada.
- Variables necesarias:
  `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SPRING_PROFILES_ACTIVE`,
  `APP_PORT`, `ADMIN_BOOTSTRAP_USERNAME` y `ADMIN_BOOTSTRAP_PASSWORD`.
- Nunca copies al repositorio las credenciales del equipo anterior. En una computadora
  nueva crea `.env` a partir de `.env.example` y genera contraseñas nuevas.
- Los usuarios activos de PostgreSQL ya pueden iniciar sesión. El administrador de
  `.env` se conserva temporalmente como acceso de recuperación controlado.
- Una sesión caducada redirige a `/login?sesionExpirada` y muestra un aviso explícito.
  Esto cubre navegación, envíos `POST` cuyo token CSRF venció y consultas asíncronas;
  los errores de permisos reales conservan la pantalla 403. El cierre normal elimina
  la cookie `JSESSIONID` para no producir falsos avisos de caducidad.

## Arranque en una computadora nueva

```bash
git clone https://github.com/Criz110gmail/Instituto_raices.git
cd Instituto_raices
cp .env.example .env
```

Edita `.env` localmente, define contraseñas seguras y selecciona un puerto disponible;
el proyecto se verificó usando `APP_PORT=18080`. Después ejecuta:

```bash
docker compose up --build -d
docker compose ps
curl http://localhost:18080/actuator/health
```

La imagen Docker compila el proyecto y ejecuta las pruebas Maven, por lo que Maven no
es obligatorio en el host. Docker sí debe estar instalado y en ejecución.

## Archivos privados, respaldo y decisión pendiente de producción

- La configuración actual debe conservarse durante desarrollo: el volumen nombrado
  `private_files` se monta como `/data/nexo-escolar` y la aplicación recibe esa ruta en
  `FILE_STORAGE_ROOT`. El volumen sobrevive a reinicios, recreaciones de contenedores y
  a `docker compose down` sin opciones destructivas.
- Nunca ejecutar `docker compose down -v` ni eliminar volúmenes sin autorización y un
  respaldo comprobado. Esa opción elimina `postgres_data` y `private_files`.
- Un respaldo recuperable debe capturar en el mismo corte PostgreSQL y los archivos;
  la base guarda la relación entre alumno y clave física. Los dos artefactos deben
  copiarse fuera de Docker y fuera de la computadora, cifrados y con acceso limitado.
- Comandos manuales de referencia, ejecutados desde la raíz en una ventana de
  mantenimiento. Se detiene sólo la aplicación para impedir cargas durante el corte;
  PostgreSQL y los volúmenes no se eliminan:

```bash
mkdir -p ../respaldos_instituto_raices
docker compose stop app
docker compose exec -T postgres sh -c \
  'pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB"' \
  > ../respaldos_instituto_raices/base_datos.sql
docker compose run --rm --no-deps app \
  tar -C /data/nexo-escolar -czf - . \
  > ../respaldos_instituto_raices/imagenes_privadas.tar.gz
docker compose start app
tar -tzf ../respaldos_instituto_raices/imagenes_privadas.tar.gz | head
```

- La carpeta `../respaldos_instituto_raices` queda fuera del repositorio, pero todavía
  está en la misma computadora: luego debe copiarse a un destino privado externo. Los
  `.sql`, los archivos comprimidos y cualquier fotografía real deben
  permanecer fuera de Git. Antes de confiar en el mecanismo se debe documentar y probar
  también la restauración en una instalación aislada.
- Decisión pendiente antes de producción: para una sola instancia puede mantenerse el
  volumen con respaldo automatizado externo. Un bind mount sólo se usará si la operación
  necesita una ruta del host para su agente de respaldo; será una ruta dedicada como
  `/srv/nexo-escolar/private-files`, nunca una carpeta dentro del repositorio. Si se
  despliegan varias instancias, se evaluará almacenamiento de objetos privado compatible
  con S3/MinIO. Cambiar de backend no debe alterar la abstracción de almacenamiento ni
  guardar rutas físicas en PostgreSQL.
- Pendiente operativo: automatizar respaldos, definir retención/cifrado/destino externo,
  comprobar restauraciones y acordar RPO/RTO antes de usar datos reales en producción.

## Base de seguridad implementada

- La migración `V2__crear_seguridad_roles_permisos.sql` crea `Usuario`, `Rol`,
  `Permiso`, `RolPermiso`, `UsuarioRol` e `InvitacionUsuario` de forma aditiva.
- Existen entidades, repositorios, DTO, mappers y servicios transaccionales para crear y
  modificar usuarios invitados y roles, asignar permisos, asignar roles con alcance y
  activar usuarios mediante invitaciones.
- Se preserva la separación entre instituciones en usuarios, roles y planteles.
- V2 siembra 18 permisos técnicos. Las invitaciones sólo guardan SHA-256 del token, son
  de un solo uso y la contraseña se codifica con BCrypt.
- `UsuarioSistemaDetailsService` autentica usuarios activos de PostgreSQL con su hash
  BCrypt y obtiene autoridades de asignaciones, roles y permisos activos.
- Un username único puede usarse directamente. Si se repite entre instituciones, el
  identificador es `CODIGO_INSTITUCION\usuario`.
- El usuario temporal de `.env` sigue disponible como recuperación y recibe todos los
  permisos técnicos existentes. No retirar este puente hasta comprobar la operación y
  recuperación con usuarios persistidos.
- `Roles y permisos` ya es un módulo visible en la consola. Incluye filtros en base de
  datos, paginación, Excel, alta, edición, desactivación lógica y selección de los 18
  permisos técnicos con errores dentro del mismo formulario.
- La migración V3 agrega `activo` a `RolPermiso` para conservar el historial al retirar
  o volver a conceder permisos; V2 y V3 ya fueron aplicadas y no deben editarse.
- `Usuarios` ya es visible en Seguridad con filtros PostgreSQL, paginación, Excel, alta
  de invitados, edición, estados, asignaciones de rol y alcances. Las asignaciones se
  desactivan y reactivan lógicamente.
- `/activar-cuenta` es público y permite consumir una invitación de 48 horas para fijar
  una contraseña BCrypt. El enlace se muestra una sola vez al administrador y la base
  conserva únicamente SHA-256 del token.
- El principal autenticado conserva usuario, institución y planteles asignados. Los
  listados, Excel, selectores y operaciones por ID aplican ese alcance en servidor.
- El alcance institucional ve sólo su institución; el de plantel restringe planteles,
  oferta y grupos a los asignados. Los catálogos académicos compartidos permanecen
  limitados a la institución. `VINCULOS_TUTOR` no concede acceso administrativo; con
  permiso de lectura sólo permite consultar relaciones propias, activas y vigentes.
- La creación de instituciones queda reservada al acceso de recuperación, y administrar
  roles o usuarios exige alcance institucional para impedir escalamiento de privilegios.
- Los permisos o alcances insuficientes muestran una pantalla 403 propia, responsiva y
  compatible con los temas claro y oscuro.
- Spring Data Auditing obtiene el actor desde `UsuarioPrincipal`: las altas guardan
  `creado_por_id` y los cambios guardan `actualizado_por_id`. El acceso de recuperación
  y los procesos sin usuario persistido conservan actor nulo mediante un listener que
  evita conservar por error el actor anterior.
- Los eventos de autenticación registran `ultimo_acceso_en`, reinician el contador al
  entrar correctamente e incrementan sólo contraseñas erróneas de cuentas identificables.
- Cinco fallos consecutivos producen un bloqueo automático de 15 minutos. Al vencer,
  un acceso correcto reactiva la cuenta; un bloqueo administrativo sin fecha no vence.
- Usuarios desconocidos, ambiguos, invitados, inactivos y el acceso de recuperación no
  acumulan intentos. La pantalla conserva un mensaje de error uniforme.
- La migración V4 crea `RecuperacionPassword`, separada de las invitaciones. Un
  administrador puede generar para un usuario activo un enlace de 30 minutos que se
  muestra una sola vez; otro enlace revoca el anterior y la base sólo conserva SHA-256.
- `/restablecer-password` permite fijar una nueva contraseña BCrypt, consume el token,
  limpia intentos y bloqueos temporales, pero nunca retira un bloqueo administrativo.
- Todavía no existe entrega por correo: no hay SMTP configurado y los correos actuales
  pueden no ser reales. El administrador entrega el enlace por un canal verificado.
- La migración V5 crea `Alumno` y agrega los permisos `ALUMNO_LEER` y
  `ALUMNO_ADMINISTRAR`. No concede permisos nuevos automáticamente a roles existentes.
- `Alumnos` ya tiene aislamiento institucional, listado filtrado y paginado en
  PostgreSQL, Excel por bloques, alta, edición y desactivación lógica.
- El propietario confirmó en el navegador que `criz110` ya puede ver y operar el
  módulo Alumnos con los permisos asignados a su rol.
- Matrícula y CURP —cuando existe— son únicas por institución. No se guarda plantel,
  grado o grupo actual: esa trayectoria se derivará de las inscripciones futuras.
- Los campos `LocalDate` de alumnos, ciclos y periodos declaran formato ISO explícito
  para que los controles HTML de fecha carguen correctamente al editar.
- La migración V6 crea `Tutor` y agrega `TUTOR_LEER` y `TUTOR_ADMINISTRAR` sin
  concederlos automáticamente a roles existentes. Una cuenta de usuario sólo puede
  vincularse con un tutor y debe pertenecer a la misma institución.
- `Tutores` incluye aislamiento institucional, filtros y paginación en PostgreSQL,
  Excel por bloques, alta, edición y desactivación lógica. Los errores permanecen en
  el formulario y la fecha de nacimiento usa formato ISO.
- El propietario confirmó que `criz110` ya tiene los permisos y puede ver Tutores.
- La migración V7 crea `AlumnoTutor` y agrega `VINCULO_TUTOR_LEER` y
  `VINCULO_TUTOR_ADMINISTRAR` sin concederlos automáticamente. Administra parentesco,
  contacto principal, responsabilidad financiera, autorizaciones y vigencias sin
  borrar el historial.
- El propietario confirmó que `criz110` ya tiene los permisos y validó el módulo de
  vínculos. Flyway V8 agrega búsquedas indexadas con `pg_trgm` para alumnos, tutores y
  usuarios.
- Flyway V9 crea `Archivo`, `AlumnoFotografia` y `fotografia_archivo_id`. El expediente
  permite cargar JPEG/PNG de hasta 5 MB, reemplazar o retirar la foto y consultar el
  historial sin exponer el almacenamiento privado.
- Al editar un alumno, la pantalla inicia con una ficha técnica visual: fotografía
  destacada, nombre, matrícula, institución, estado y datos rápidos. La carga, retiro e
  historial de fotografías forman parte de esa cabecera; debajo permanece el
  formulario completo del expediente.
- Los listados y los trece formularios administrativos muestran `Cerrar sesión`. El
  botón envía `POST /logout` con CSRF, invalida la sesión y regresa al inicio.
- Flyway V12 agrega `fotografia_archivo_id` a `Usuario`. La edición de usuario permite
  cargar o reemplazar JPEG/PNG de hasta 5 MB y restaurar el avatar genérico; los bytes
  permanecen en `private_files` y sólo se sirven mediante rutas autenticadas.
- El encabezado compartido muestra siempre la fotografía —personalizada o genérica— y
  el nombre del usuario autenticado. En móvil conserva ambos datos mediante un segundo
  renglón responsivo y funciona en los temas claro y oscuro.
- Flyway V10 crea `Inscripcion` y `AsignacionGrupo`, y agrega los permisos
  `INSCRIPCION_LEER` e `INSCRIPCION_ADMINISTRAR` sin concederlos automáticamente.
- `Inscripciones` ya administra la trayectoria por alumno, plantel, ciclo y grado.
  Los traslados o promociones cierran el registro anterior y crean uno nuevo; los
  cambios de grupo cierran la asignación vigente sin sobrescribir el historial.
- El módulo valida institución, oferta activa, ciclo, vigencias, solapamientos y
  capacidad bajo bloqueo transaccional. Su listado pagina y filtra en PostgreSQL y la
  exportación XLSX reutiliza exactamente esos filtros y recorre los datos por bloques.
- El detalle de inscripción muestra una ficha técnica compacta del alumno con su
  fotografía privada actual, matrícula, nacimiento, CURP, plantel, ciclo y grado. La
  fotografía se entrega mediante la inscripción después de validar su alcance.
- Flyway V11 crea `ConceptoCobro` y `CuotaAlumno`, con cuatro permisos técnicos sin
  autoasignación. Los conceptos definen categorías y ajustes permitidos; no contienen
  un precio global.
- Cada cuota pertenece a una inscripción y, por tanto, a un solo alumno. Configura
  importe, moneda, frecuencia única o mensual, vigencia, vencimiento, estado y si la
  generación futura de cargos será automática o manual.
- Flyway V13 crea `Cargo` y los permisos `CARGO_LEER` y `CARGO_ADMINISTRAR`, sin
  concederlos automáticamente a roles existentes. Un cargo emitido pertenece siempre
  a una inscripción/alumno y conserva importe, descripción, periodo y vencimiento.
- Los cargos extraordinarios se emiten manualmente. El generador automático recorre
  cuotas por bloques, respeta institución/plantel y usa una clave única por cuota y
  periodo con `ON CONFLICT DO NOTHING`; reintentos y ejecuciones concurrentes no
  duplican obligaciones. Los cargos no se editan: sólo se cancelan con versión y motivo.
- Un pago futuro podrá cubrir varios hijos, pero deberá conservar aplicaciones separadas
  por cada cargo/alumno. Nunca se administrará un saldo familiar indistinto.
- Flyway V14 crea `TipoBeca`, `BecaAlumno` y `AjusteCargo`, además de seis permisos sin
  autoasignación. Cada beca pertenece a una inscripción y concepto, utiliza porcentaje
  o monto fijo y no puede solaparse con otra beca activa equivalente.
- Al emitir un cargo se aplica la beca vigente con redondeo `HALF_UP` a dos decimales y
  se congela su base, porcentaje y monto como ajuste histórico. Cambiar la beca sólo
  afecta cargos futuros. Los ajustes manuales y sus reversas son movimientos separados;
  nunca se sobrescribe ni elimina el original.
- Flyway V15 crea `PoliticaRecargo` y enlaza cada recargo automático con su política y
  una clave de generación única. Existe una sola política editable por concepto.
- El administrador elige porcentaje o monto fijo, días completos de gracia,
  periodicidad única o mensual, límite sin tope, por monto o por porcentaje del cargo,
  y puede pausar tanto la política como su generación automática.
- El recargo es simple: se calcula sobre el importe original más o menos ajustes que no
  sean recargos, por lo que nunca genera interés sobre recargos anteriores. Empieza el
  día posterior al vencimiento y a los días de gracia completos configurados.
- El generador procesa cargos por bloques de 100, admite corte institucional o por
  plantel y usa `ON CONFLICT DO NOTHING`; repetirlo no duplica un periodo. Los ajustes
  emitidos conservan importe, base, porcentaje, fecha y política históricos.
- Una reversa resta del acumulado usado para el límite, pero la clave histórica impide
  volver a cobrar automáticamente el mismo periodo.
- Flyway V16 crea `CuentaFinanciera` y dos permisos sin autoasignación. Cada cuenta
  pertenece a una institución y puede ser compartida o limitarse a un plantel.
- Los tipos iniciales son `CAJA`, `BANCO` e `INVERSION`. Caja no admite datos bancarios;
  banco e inversión requieren institución financiera y al menos número o CLABE.
- La moneda se limita por ahora a la predeterminada de la institución. El saldo inicial
  no puede ser negativo y su fecha no puede estar en el futuro. Mientras no existan
  movimientos, estos valores pueden corregirse con versión optimista.
- Los listados y Excel muestran únicamente los cuatro últimos caracteres de cuenta o
  CLABE. Los valores completos sólo aparecen en el formulario administrativo autorizado.
- Una cuenta institucional compartida sólo puede administrarse con alcance institucional;
  una cuenta de plantel también respeta los planteles asignados al usuario.
- Flyway V17 crea `Pago`, `ComprobantePago` y `SolicitudAplicacionPago`, además de
  `PAGO_LEER` y `PAGO_REGISTRAR` sin conceder permisos automáticamente.
- Efectivo y transferencia se registran exclusivamente en `PENDIENTE_VALIDACION`.
  La transferencia exige al menos un comprobante privado; el efectivo lo admite de
  forma opcional. Se aceptan hasta cinco JPEG, PNG o PDF de 10 MB cada uno, validados
  por firma y guardados fuera de las rutas públicas.
- Un pago pertenece a un tutor y puede distribuirse de forma solicitada entre cargos de
  varios hijos sobre los que conserve responsabilidad financiera vigente. Cada cargo
  aparece una sola vez, usa la moneda del pago y no puede solicitar más que su saldo
  actual; la suma tampoco puede superar el pago.
- La distribución solicitada no modifica cargos ni crea aplicaciones. La cuenta
  declarada es opcional y se limita a caja para efectivo o banco/inversión para
  transferencia. Folio e idempotencia son únicos por institución.
- Pagos cuenta con filtros y paginación PostgreSQL, Excel por bloques con los mismos
  filtros, alta responsiva, resumen en tiempo real y detalle/descarga protegidos.
- Flyway V18 crea `AplicacionPago`, `MotivoFinanciero` y `MovimientoFinanciero`, y agrega
  `PAGO_VALIDAR` sin concederlo automáticamente a los roles existentes.
- Sólo una cuenta administrativa persistida e identificable puede validar o rechazar;
  el acceso temporal de recuperación no autoriza movimientos de dinero.
- Validar bloquea primero pago y cuenta, y después los cargos en orden de identificador.
  Recalcula el saldo de cada cargo, reduce automáticamente una solicitud si otro pago
  consumió parte del adeudo y conserva el resto como monto disponible del pago.
- La validación publica un único ingreso por el total del pago, con secuencia y saldos
  de cuenta protegidos por bloqueo. Pago, aplicaciones y movimiento se confirman o se
  revierten juntos; las restricciones por pago, solicitud e idempotencia impiden dobles
  ingresos o aplicaciones al reintentar.
- Rechazar exige motivo y conserva comprobantes sin afectar cargos ni cuentas. Los
  saldos de Cargo, su situación pagada/parcial y los autocompletados ya descuentan las
  aplicaciones efectivas. Un cargo con aplicaciones no puede cancelarse y una cuenta
  con movimientos ya no permite cambiar moneda, fecha ni saldo inicial.
- Flyway V19 agrega `MOVIMIENTO_FINANCIERO_LEER`, índices de consulta y el libro
  inmutable paginado. Permite filtrar por cuenta mediante autocompletado, plantel,
  dirección, clase y fechas; muestra el saldo vigente de la cuenta y exporta a Excel
  exactamente los filtros visibles.
- Flyway V20 agrega `MOTIVO_FINANCIERO_LEER`, `MOTIVO_FINANCIERO_ADMINISTRAR` y
  `MOVIMIENTO_FINANCIERO_REGISTRAR`, sin concederlos automáticamente a roles.
- Motivos financieros es un catálogo institucional de ingreso, egreso o ambos. El
  motivo `COBROS_ESCOLARES` es reservado: no puede desactivarse, renombrarse ni cambiar
  a egreso porque sostiene la publicación de pagos validados.
- El registro manual publica únicamente movimientos `OPERACION`, usa una clave de
  idempotencia por formulario, bloquea la cuenta, toma la última secuencia, calcula
  saldo anterior/posterior y rechaza egresos que producirían saldo negativo.
- La cuenta se busca por autocompletado. El plantel, la institución, el motivo activo,
  la fecha de apertura, la zona horaria y el actor persistido se validan antes de
  guardar. El acceso de recuperación no puede publicar dinero.
- Los movimientos son inmutables: V20 no permite editarlos ni eliminarlos. Las
  correcciones se resolverán mediante reversas en una etapa posterior.
- Flyway V21 crea `TransferenciaCuenta`, agrega la relación desde los dos movimientos
  `TRASPASO`, el motivo técnico `TRASPASO_INTERNO` y el permiso
  `TRANSFERENCIA_CUENTA_REGISTRAR`, sin asignarlo automáticamente a roles.
- Una transferencia exige cuentas distintas, activas, autorizadas, de la misma
  institución y moneda. La fecha debe ser válida para ambas aperturas y la cuenta de
  origen debe conservar saldo no negativo.
- El servicio bloquea las dos cuentas por identificador ascendente, aunque origen y
  destino vengan en orden contrario. Crea en una sola transacción la transferencia, un
  egreso de origen y un ingreso de destino, cada uno con secuencia y saldo propios.
- La transferencia tiene idempotencia institucional y cada lado es único en la base de
  datos. El acceso de recuperación no puede mover fondos. V21 no implementa todavía la
  reversa; `REVERTIDA` queda reservado para una etapa posterior.
- Flyway V22 crea `DevolucionPago`, enlaza sus ajustes de aplicaciones y su movimiento
  único de egreso, y agrega `PAGO_DEVOLVER` sin asignarlo automáticamente a roles.
- Una devolución sólo parte de un pago validado, no cambia ese estado y nunca se modela
  como cancelación. Valida fecha, moneda, alcance, saldo de cuenta, límite total del pago,
  versión e idempotencia institucional. El acceso de recuperación no puede ejecutarla.
- El monto disponible se calcula como pago menos aplicaciones netas menos devoluciones
  ejecutadas. Si no basta, el usuario selecciona aplicaciones vigentes: cada una se
  revierte completa y, cuando sólo se necesita liberar una parte, se crea una nueva
  aplicación por el remanente. El original nunca se edita ni elimina.
- La devolución, los ajustes de cargos y un solo movimiento `DEVOLUCION/EGRESO` se
  confirman en la misma transacción. `DEVOLUCION_PAGO` es un motivo técnico reservado.
- El expediente muestra saldos, historial y formulario responsivo. La cuenta se busca
  por autocompletado remoto; cualquier validación o restricción vuelve a la misma
  pantalla conservando los datos capturados.
- Flyway V23 crea `ReversionFinanciera`, enlaza sus movimientos compensatorios y agrega
  `MOVIMIENTO_FINANCIERO_REVERTIR` sin asignarlo automáticamente a roles existentes.
- Sólo las operaciones manuales y transferencias aplicadas se revierten por este flujo.
  Cobros, devoluciones, aplicaciones de pagos y otros movimientos técnicos conservan
  sus procesos específicos. El original nunca se edita ni elimina.
- Una operación manual produce un `REVERSO` con dirección contraria, mismo importe y
  relación única con el original. Si deshace un ingreso, la cuenta debe conservar saldo.
- Una transferencia se revierte completa: bloquea ambas cuentas por ID ascendente,
  exige fondos en la receptora, publica el egreso de retorno y el ingreso en el origen
  dentro de una sola transacción, y cambia la transferencia a `REVERTIDA`.
- La cabecera de reversa conserva tipo, objetivo, fecha, motivo, actor e idempotencia.
  No existe segunda reversa. La fecha no puede ser futura ni anterior al original o a
  la apertura de las cuentas. El acceso de recuperación no puede mover fondos.
- El libro muestra acciones únicamente para objetivos elegibles, estado revertido y el
  movimiento original compensado. El formulario es responsivo y conserva errores.
- Flyway V24 crea `CorteCaja` y agrega `CORTE_CAJA_LEER` y
  `CORTE_CAJA_ADMINISTRAR` sin asignarlos automáticamente a roles existentes.
- Sólo las cuentas de tipo `CAJA` pueden abrir un corte. La apertura captura hora del
  servidor, usuario persistido, último folio y saldo vigente; una restricción parcial
  impide dos cortes abiertos en la misma caja. Una caja inactiva no abre cortes nuevos,
  pero sí permite cerrar el que ya estuviera pendiente.
- El cierre bloquea primero la cuenta y después el corte, toma el folio final y resume
  ingresos y egresos por secuencia contable. Por ello, un movimiento capturado después
  no se pierde aunque su fecha operativa sea anterior a la apertura.
- El usuario realiza un conteo ciego: la pantalla no revela el saldo esperado antes de
  declarar el efectivo. Al cerrar conserva saldo esperado, efectivo declarado,
  diferencia, responsables y observaciones; cualquier diferencia exige justificación.
- Abrir o cerrar no crea movimientos monetarios. El cierre es inmutable, usa versión
  optimista y claves de idempotencia separadas para soportar reintentos seguros.
- El módulo tiene listado filtrado y paginado en PostgreSQL, búsqueda remota de cajas,
  Excel por bloques, detalle y formularios responsivos. Los usuarios de plantel sólo
  ven cajas y cortes de sus planteles; una caja institucional exige alcance institucional.
- Flyway V25 no crea saldos duplicados: agrega el permiso
  `REPORTE_FINANCIERO_CONSULTAR` y tres índices de apoyo para ajustes, aplicaciones y
  movimientos. El permiso no se asigna automáticamente a roles existentes.
- Reportes financieros ofrece dos vistas administrativas derivadas. El estado de cuenta
  reconstruye por alumno los cargos, ajustes y aplicaciones efectivos hasta una fecha
  de corte, y clasifica pendiente, parcial, pagado, vencido o cancelado. Un cargo parcial
  ya vencido se presenta como vencido y los cancelados conservan historial sin saldo.
- Tesorería agrupa por día, mes o año los ingresos y egresos operativos por cuenta. Los
  traspasos internos y sus reversas no inflan el flujo operativo y se muestran en
  columnas separadas. La fecha se interpreta en la zona horaria institucional.
- Los saldos de apertura y cierre sólo aparecen cuando se consultan cuentas completas.
  Al filtrar por plantel de operación se muestran flujos atribuidos, pero no se inventa
  una división del saldo de cuentas compartidas. El alcance de plantel tampoco expone
  cuentas institucionales.
- Ambos reportes filtran y paginan en PostgreSQL, usan autocompletado remoto para
  alumnos/cuentas y exportan los mismos filtros a XLSX por bloques. Son consultas de
  solo lectura: no publican movimientos ni modifican cargos, pagos o saldos.
- Flyway V26 crea `EventoEscolar` y `DestinatarioEvento`, además de
  `EVENTO_ESCOLAR_LEER` y `EVENTO_ESCOLAR_ADMINISTRAR`; los permisos no se asignan
  automáticamente a roles existentes.
- Un evento nace como `BORRADOR`, sólo el borrador se edita, la publicación es una
  acción explícita y la cancelación conserva fecha y motivo. No existe eliminación
  física. Los eventos se clasifican como junta, festival, suspensión, actividad u otro.
- El alcance puede ser institucional, de plantel o una selección. La selección admite
  nivel, grado, grupo y alumno con semántica de unión, valida pertenencia, oferta,
  inscripción vigente y ciclo, impide duplicados y exige al menos un destinatario.
- Las fechas se capturan en horario local y se guardan como instantes usando la zona
  horaria institucional; deben permanecer dentro del ciclo y no se admiten ciclos
  cerrados. Los usuarios de plantel no pueden administrar eventos institucionales.
- El módulo incluye listado filtrado y paginado en PostgreSQL, exportación XLSX por
  bloques, formulario, ficha, publicación, cancelación y autocompletado remoto de
  destinatarios. Los errores esperables se muestran en la misma pantalla. V26 todavía
  no envía correo, WhatsApp ni notificaciones y aún no es el portal del tutor.
- Flyway V27 agrega `PORTAL_TUTOR_ACCEDER` y un índice parcial para la agenda publicada;
  no asigna el permiso automáticamente ni crea copias de expedientes o saldos.
- `/portal` es una experiencia familiar separada de `/admin`. Una cuenta persistida debe
  corresponder a un tutor activo y sólo obtiene alumnos activos mediante vínculos activos
  y vigentes. Elegir otro identificador o solicitar su fotografía vuelve a validar el
  vínculo; el acceso de recuperación nunca entra al portal.
- La ficha muestra matrícula, parentesco, plantel, ciclo, grado, grupo y fotografía
  actual. Si no existe inscripción vigente se informa sin inventar una situación
  académica. El selector de hijos y toda la pantalla son responsivos y admiten tema
  claro u oscuro.
- La experiencia visual del portal es deliberadamente distinta de administración:
  lenguaje cotidiano, colores cálidos, jerarquía centrada en la fotografía y el nombre
  del hijo, agenda tipo línea de tiempo, pagos resumidos sin vocabulario contable y
  navegación inferior en móvil. El nombre institucional aparece sólo como contexto.
- `/familias` es la entrada dedicada para madres, padres y tutores: conserva el mismo
  motor de autenticación y las mismas credenciales seguras, pero muestra una bienvenida
  propia, dirige al portal después de autenticar y regresa allí al cerrar sesión o al
  equivocarse de contraseña. `/login` permanece como acceso del personal.
- El estado de cuenta se deriva de cargos, ajustes y aplicaciones existentes y sólo se
  muestra cuando el vínculo marca simultáneamente responsabilidad financiera y permiso
  para ver finanzas. La consulta queda limitada al alumno validado y se pagina en bloques
  de diez; el portal no registra pagos ni altera saldos.
- La agenda sólo muestra eventos `PUBLICADO` aplicables al alumno por ciclo y alcance
  institucional, plantel, nivel, grado, grupo o alumno. Incluye próximos eventos y los
  finalizados durante los últimos 30 días, con paginación. No expone borradores ni
  cancelados.
- Flyway V28 crea `Aviso` y agrega `AVISO_LEER` y `AVISO_ADMINISTRAR` sin asignarlos
  automáticamente a roles. Un aviso nace como borrador, sólo el borrador se edita, la
  publicación es explícita y el retiro exige motivo y conserva el historial.
- El alcance inicial de Avisos es institución o plantel. Los avisos generales exigen
  alcance institucional; los de plantel respetan los planteles asignados. El vencimiento
  es opcional y debe ser posterior a la publicación.
- La administración incluye filtros y paginación PostgreSQL, Excel por bloques, alta,
  edición, detalle, publicación y retiro con errores integrados en la pantalla.
- El portal familiar muestra por hijo sólo avisos publicados, ya vigentes, no vencidos
  y aplicables a toda la institución o a su plantel actual, paginados de diez en diez.
  V28 no crea notificaciones internas ni envía correo o WhatsApp.
- Flyway V29 crea `NotificacionUsuario` para la bandeja interna del portal. La
  sincronización ocurre al abrir `/portal`, genera como máximo una fila por usuario y
  origen mediante una clave única, e incorpora eventos publicados de los últimos 30
  días y avisos publicados vigentes que correspondan a cualquiera de sus hijos.
- Sólo se generan notificaciones para vínculos activos, vigentes y con
  `puede_recibir_notificaciones=true`. Eventos respeta institución, plantel, ciclo,
  nivel, grado, grupo o alumno; Avisos respeta institución o plantel.
- La campana muestra pendientes y la bandeja pagina diez filas en PostgreSQL, primero
  las no leídas. Al abrir una notificación se bloquea la fila, se comprueba que sea de
  la cuenta autenticada y se revalida que el evento o aviso siga accesible antes de
  marcarla como leída. No se borran filas ni se generan datos ficticios.
- V29 usa el permiso existente `PORTAL_TUTOR_ACCEDER`; no agrega permisos y no envía
  correo, WhatsApp ni notificaciones push.
- Flyway V30 amplía `NotificacionUsuario` con `pago_id` y los tipos
  `PAGO_VALIDADO` y `PAGO_RECHAZADO`. Al abrir el portal sincroniza resultados de los
  últimos 90 días para el tutor titular del pago, con clave única por usuario, pago y
  resultado; un reintento no duplica filas.
- Las notificaciones financieras exigen tutor, alumno y vínculo activos y vigentes,
  además de responsabilidad financiera, permiso para ver finanzas y permiso para
  recibir notificaciones. La lectura vuelve a validar usuario, institución, estado del
  pago y acceso financiero, y dirige a `#cuenta`.
- V30 no crea pagos, movimientos ni permisos, no cambia el proceso atómico de validación
  o rechazo y continúa sin correo, WhatsApp o push.
- Flyway V31 crea `Auditoria`, el permiso `AUDITORIA_CONSULTAR`, cuatro índices de
  consulta y un trigger PostgreSQL que rechaza cualquier `UPDATE` o `DELETE`. El permiso
  no se asigna automáticamente a roles existentes.
- `/admin/auditoria` permite consultar por institución, fechas, acción, tipo e ID de
  entidad, actor o correlación; pagina en PostgreSQL y exporta exactamente los mismos
  filtros a XLSX por bloques. Sólo admite alcance institucional.
- La bitácora captura en la misma transacción validación/rechazo de pagos,
  devoluciones, transferencias, reversas, cambios de permisos y roles, y
  publicación/cancelación o retiro de eventos y avisos. Los reintentos idempotentes no
  duplican registros.
- Cada petición recibe `X-Correlation-ID`; el registro atribuye el usuario persistido,
  el acceso de recuperación o un proceso de sistema. Nunca conserva contraseñas, hashes,
  tokens, cuentas bancarias, comprobantes ni contenido de archivos.
- Flyway V32 agrega `PAGO_CANCELAR` sin autoasignarlo a roles; registra actor y fecha de
  cancelación, permite la clase de movimiento `ANULACION` con relación única al ingreso
  original y amplía las notificaciones a `PAGO_CANCELADO`.
- Un pago pendiente se puede cancelar con motivo sin mover saldos. Un pago validado por
  error requiere saldo suficiente en la misma cuenta, no puede tener devoluciones
  ejecutadas y se cancela revirtiendo todas sus aplicaciones activas y publicando un
  solo egreso compensatorio. Pago, aplicaciones, movimiento y auditoría se confirman
  juntos. No se borran registros ni se reusa el flujo de devolución real.
- La cancelación exige usuario persistido, `PAGO_CANCELAR`, versión optimista y alcance
  de plantel/cuenta; un reintento con el mismo motivo no duplica movimientos. El pago
  rechazado no se cancela. El portal conserva las notificaciones anteriores como
  historial, pero deja de mostrar las que ya no coinciden con el estado vigente y
  presenta una nueva notificación de cancelación al tutor autorizado.

## Verificación confirmada

- Compilación correcta de 490 archivos Java de producción.
- 288 pruebas Maven sin fallos ni errores.
- Flyway V1 a V32 validados y aplicados correctamente sobre el volumen existente.
- Hibernate validó el esquema y detectó 44 repositorios.
- PostgreSQL y la aplicación iniciaron correctamente con credenciales tomadas de `.env`.
- `/actuator/health` respondió `UP`.
- Los formularios autenticados de instituciones, planteles, niveles, oferta educativa y
  grados respondieron HTTP 200.
- El listado filtrado, el formulario y la exportación de Alumno–Tutor respondieron
  autenticados; el libro comenzó con firma XLSX válida.
- No se crearon vínculos de prueba. El propietario confirmó los permisos y el
  funcionamiento del módulo con `criz110`.
- PostgreSQL confirmó `pg_trgm` y los tres índices GIN. Los formularios autenticados
  mostraron los tres autocompletados y sus endpoints respondieron JSON HTTP 200.
- El volumen `private_files` quedó escribible sólo desde la aplicación, el panel de
  fotografía respondió autenticado y la salud permaneció `UP`. La verificación no
  cargó fotografías ni modificó alumnos existentes.
- La edición autenticada del alumno renderizó la ficha técnica antes del formulario;
  una prueba del controlador fija la etiqueta de institución mostrada en la cabecera.
- El cierre de sesión respondió HTTP 302 y la misma cookie fue redirigida al login al
  intentar regresar a `/admin`.
- El listado y el formulario de inscripciones respondieron autenticados, y la
  exportación filtrada comenzó con firma XLSX `504b0304`. No se crearon inscripciones
  ni asignaciones durante la verificación.
- Los dos listados y formularios de cobranza respondieron autenticados; la exportación
  filtrada de cuotas comenzó con firma XLSX `504b0304`. Las tablas de conceptos y cuotas
  permanecieron vacías y PostgreSQL confirmó 30 permisos técnicos totales.
- La última instancia local verificada quedó en `http://localhost:8080`.
- PostgreSQL confirmó V24 exitosa, los dos permisos de corte y cero cortes de prueba;
  la verificación automática no generó movimientos ni alteró saldos.
- PostgreSQL confirmó V25 exitosa, sus tres índices y el permiso de reportes. También
  analizó correctamente las consultas nativas de estado de cuenta, tesorería agrupada y
  saldos; la base verificada no contenía filas operativas para esos filtros y no se
  modificaron datos monetarios.
- PostgreSQL confirmó V26 exitosa, las tablas `evento_escolar` y
  `destinatario_evento`, y los dos permisos de eventos. La imagen reconstruida inició
  con salud `UP`; no se crearon eventos ni destinatarios ficticios.
- PostgreSQL confirmó V27, `PORTAL_TUTOR_ACCEDER` y el índice parcial de agenda. Las
  consultas reales de vínculos y segmentación de eventos se ejecutaron en modo lectura
  con identificadores inexistentes; no se crearon ni modificaron datos escolares.
- El nombre visible de sesión se publica al modelo Thymeleaf mediante
  `IdentidadSesionAdvice`; no usar `#authentication`, porque el dialecto de seguridad
  no forma parte de las dependencias actuales. Una prueba de regresión cubre presencia
  y ausencia de autenticación.
- Se corrigió la sintaxis Thymeleaf de los tamaños de página en Tesorería, Estado de
  cuenta, Cortes de caja y Eventos. La expresión compacta `${{10,25,50,100}}` se
  interpretaba como SpEL inválido y cortaba el HTML; una prueba de regresión protege
  las cuatro plantillas.
- PostgreSQL confirmó V28, la tabla `aviso` vacía y los permisos `AVISO_LEER` y
  `AVISO_ADMINISTRAR`. La imagen reconstruida inició con salud `UP`; no se crearon
  avisos ficticios ni se modificaron datos escolares.
- PostgreSQL confirmó V29 exitosa y la tabla `notificacion_usuario` inicialmente vacía.
  La imagen Docker inició por completo y las pruebas nuevas cubren sincronización,
  página normalizada, lectura propia y rechazo de una notificación de otra cuenta.
- PostgreSQL confirmó V30 exitosa, `pago_id`, sus restricciones e índice, y cero
  notificaciones iniciales. Las pruebas nuevas cubren lectura de pago validado y rechazo
  de lectura cuando ya no existe acceso financiero.
- PostgreSQL confirmó V31 exitosa, el permiso `AUDITORIA_CONSULTAR`, cuatro índices y
  `tg_auditoria_inmutable` para impedir cambios o eliminaciones. La tabla permaneció
  vacía: no se inventaron acciones sensibles para verificarla.
- PostgreSQL confirmó V32 exitosa, el permiso `PAGO_CANCELAR`, las columnas de actor y
  fecha y las restricciones de cancelación y movimiento. No se cancelaron pagos reales
  para verificarla; la suite de pruebas cubre los caminos sensibles.

## Siguiente paso acordado

V32 está implementada localmente, compilada y aplicada en el volumen actual. El
propietario debe conceder `PAGO_CANCELAR` a un rol administrativo e iniciar sesión de
nuevo. Probar primero con un pago pendiente de prueba y después con uno validado y
controlado en una cuenta con saldo suficiente, comprobando que el cargo recupera su
saldo, que el libro financiero muestra la compensación y que `/admin/auditoria`
contiene `PAGO_CANCELADO`. También confirmar la notificación en `/portal` con una
cuenta de tutor autorizada. No cancelar un pago operativo real sólo para probar.

Después de revisar y subir V32, el siguiente bloque recomendado es V33 para
conciliación bancaria de cuentas `BANCO`/`INVERSION`, empezando por definir importación
de estados de cuenta, coincidencia de movimientos, discrepancias y cierre sin alterar
el libro inmutable. Los archivos bancarios requerirán almacenamiento privado y reglas
de privacidad antes de implementarse.

Correo y WhatsApp siguen fuera de alcance hasta diseñar consentimiento, proveedor,
reintentos y trazabilidad. Retiros especializados y conciliación bancaria continúan
como flujos posteriores separados.

La estrategia definitiva de almacenamiento privado sigue pendiente para producción,
pero no bloquea el siguiente módulo funcional.

### Punto exacto de reanudación en otra computadora

1. V31 está en `origin/main`; V32 es el cambio local actual. Confirmar si ya fue revisada
   y subida; si no, preservar los cambios locales y no volver a implementarla.
2. Crear el `.env` local desde `.env.example`; nunca pedir, leer ni copiar el contenido
   real del otro equipo. Levantar con `docker compose up --build -d` y comprobar salud.
3. Si se necesita conservar alumnos y fotografías del equipo anterior, Git no basta:
   restaurar base y archivos como una pareja sólo con autorización y con un procedimiento
   probado. No improvisar una restauración sobre datos existentes.
4. Flyway V1–V32 ya fueron aplicadas en el volumen verificado; nunca editarlas. La
   siguiente migración disponible será V33.
5. Primero terminar la prueba funcional de V32 asignando `PAGO_CANCELAR` y usando sólo
   pagos/cuentas controlados. Después definir reglas y formato de conciliación bancaria
   antes de crear V33; no asumir envíos por correo o WhatsApp.
6. Mantener el patrón completo: permisos sin autoasignación, alcance, filtros y
   paginación en PostgreSQL, exportación XLSX por bloques con los mismos filtros,
   formularios responsivos, temas y validación transaccional.
7. No generar movimientos monetarios reales de prueba sin autorización expresa. Para
   validar la interfaz, el propietario debe usar importes y cuentas controlados.

## Disciplina de cambios y entrega

- Inspecciona `git status` antes de editar y preserva cambios ajenos.
- Usa migraciones Flyway nuevas para cambios de esquema; nunca edites una migración ya
  aplicada.
- No uses operaciones destructivas de Git ni elimines el volumen PostgreSQL.
- Actualiza `README.md`, `CONTEXTO_PROYECTO.md` y este archivo cuando cambie el estado o
  el siguiente paso.
- Antes de subir, confirma que `.env` no aparece en `git status`.
- No hagas `push` ni cambies credenciales remotas sin autorización del usuario.
- La respuesta final debe indicar qué se implementó, cómo se verificó y cuál es el
  siguiente paso lógico.
