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
- Último commit confirmado antes de estos cambios locales: `84ea37a` — `recuperación
  segura de contraseña`.
- La rama local estaba sincronizada con `origin/main` antes de crear este documento.
- Nunca guardes tokens de GitHub, contraseñas o el contenido real de `.env` en Git.
- En equipos con varias cuentas de GitHub, conserva la configuración de credenciales
  a nivel local del repositorio y usa `credential.useHttpPath=true`.

## Tecnología y arquitectura

- Java 21, Maven y Spring Boot 4.1.1.
- Spring MVC con Thymeleaf; no es una SPA.
- PostgreSQL 17 mediante Docker Compose y volumen persistente `postgres_data`.
- Flyway administra exclusivamente el esquema; Hibernate usa `ddl-auto=validate`.
- Spring Data JPA, Bean Validation, Spring Security, Lombok, Actuator y Spring Mail.
- Apache POI 5.4.1 con `SXSSFWorkbook` para exportaciones Excel de bajo consumo de
  memoria.
- Paquete base: `escuela`.
- Módulos principales: `escuela.institucion`, `escuela.academico`, `escuela.admin`,
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
- La situación académica vigente de un alumno se derivará de su inscripción futura;
  no debe duplicarse como otra fuente de verdad.

## Interfaz terminada

- Consola administrativa disponible bajo `/admin`.
- Navegación lateral para los ocho catálogos.
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
- Los formularios incluyen Bean Validation, CSRF, mensajes de resultado y versión
  optimista, manteniendo los temas y el diseño responsivo.
- Los errores esperables al crear, actualizar o desactivar se muestran en el mismo
  formulario sin perder la captura: reglas de negocio, concurrencia y restricciones de
  integridad. Nunca se presentan SQL ni detalles internos de PostgreSQL al usuario.

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
  limitados a la institución. `VINCULOS_TUTOR` no concede acceso administrativo.
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
- Matrícula y CURP —cuando existe— son únicas por institución. No se guarda plantel,
  grado o grupo actual: esa trayectoria se derivará de las inscripciones futuras.
- Los listados y los once formularios administrativos muestran `Cerrar sesión`. El
  botón envía `POST /logout` con CSRF, invalida la sesión y regresa al inicio.

## Verificación confirmada

- Compilación correcta de 163 archivos Java de producción.
- 85 pruebas Maven sin fallos ni errores.
- Flyway V1 a V5 validados y aplicados correctamente sobre el volumen existente.
- Hibernate validó el esquema y detectó 16 repositorios.
- PostgreSQL y la aplicación iniciaron correctamente con credenciales tomadas de `.env`.
- `/actuator/health` respondió `UP`.
- Los formularios autenticados de instituciones, planteles, niveles, oferta educativa y
  grados respondieron HTTP 200.
- La exportación filtrada generó un libro XLSX válido con HTTP 200.
- El cierre de sesión respondió HTTP 302 y la misma cookie fue redirigida al login al
  intentar regresar a `/admin`.
- La última instancia local verificada quedó en `http://localhost:8080`.

## Siguiente paso acordado

Después de que el propietario asigne `ALUMNO_LEER` y `ALUMNO_ADMINISTRAR` a su rol y
valide el módulo en el navegador, implementar `Tutor`: migración aditiva, dominio,
aislamiento institucional y mantenimiento completo con filtros, paginación y Excel.
Todavía no crear `AlumnoTutor`, inscripciones, cobros ni tesorería.

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
