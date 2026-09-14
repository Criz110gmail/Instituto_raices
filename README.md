# Sistema administrativo escolar

Base del sistema multi-plantel construida con Java 21, Spring Boot 4.1.1,
Thymeleaf, PostgreSQL, Flyway y Maven.

## Inicio rápido con Docker

1. Revisa las credenciales locales de `.env` y cámbialas antes de publicar el sistema.
2. Ejecuta `docker compose up --build`.
3. Abre `http://localhost:18080` (o el puerto definido en `APP_PORT`).
4. Comprueba la salud en `http://localhost:18080/actuator/health`.

Docker Compose exige todas las variables declaradas en `.env`; no existen contraseñas
alternativas dentro de `compose.yaml`. `.env.example` sólo documenta las variables y
`.env` está excluido del control de versiones.

Flyway crea el esquema al iniciar y Hibernate solamente lo valida mediante
`ddl-auto=validate`. El volumen `postgres_data` conserva la base de datos.
El volumen `private_files` conserva archivos privados fuera de PostgreSQL y fuera de
las rutas públicas de la aplicación. En respaldos o traslados entre computadoras deben
copiarse tanto `postgres_data` como `private_files`; las fotografías nunca deben subirse
al repositorio Git.

### Respaldo manual de datos y fotografías

Git no incluye el contenido de los volúmenes. En una ventana sin usuarios, detén la
aplicación y genera los dos respaldos del mismo corte. El contenedor temporal sólo lee
`private_files`; no elimina el volumen:

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

No ejecutes `docker compose down -v`: `-v` elimina los volúmenes persistentes. Para
producción todavía debe automatizarse el respaldo y probarse la restauración completa.
La carpeta anterior está fuera del repositorio, pero debe copiarse después a un destino
privado externo. Los respaldos, volcados SQL y fotografías reales no deben confirmarse
en Git.

## Desarrollo local

Se requiere Java 21, Maven y PostgreSQL. Configura `DB_URL`, `DB_USERNAME` y
`DB_PASSWORD`, y luego ejecuta:

```bash
mvn spring-boot:run
```

La portada, el login, la activación, el restablecimiento de contraseña y el endpoint de
salud son públicos. Las demás rutas autentican usuarios activos de PostgreSQL y
comprueban los permisos de sus roles. Las credenciales `ADMIN_BOOTSTRAP_USERNAME` y
`ADMIN_BOOTSTRAP_PASSWORD` de `.env` se conservan como acceso temporal de recuperación.

## Estructura inicial

- `escuela.institucion`: institución, planteles y oferta educativa.
- `escuela.academico`: niveles, grados, ciclos, periodos y grupos.
- `escuela.seguridad`: usuarios, roles, permisos, alcances e invitaciones de acceso.
- `escuela.alumno`: expediente de alumnos y vínculos históricos con tutores.
- `escuela.tutor`: expediente institucional de tutores y cuenta de acceso opcional.
- `escuela.config`: seguridad y auditoría JPA.
- `escuela.common`: excepciones, respuesta de auditoría y utilidades compartidas.
- Cada módulo contiene DTO, mappers, repositorios y servicios transaccionales.
- `db/migration`: esquema gestionado exclusivamente por Flyway.

Los controladores enlazan formularios validados con DTO propios y nunca exponen ni
reciben entidades JPA directamente.

Las relaciones con catálogos de alto volumen se resuelven mediante autocompletado
remoto: la búsqueda inicia con tres caracteres, respeta el alcance institucional y
devuelve un máximo de 20 coincidencias. Alumnos, tutores y usuarios cuentan con índices
GIN `pg_trgm` desde Flyway V8; no se cargan tablas completas dentro de formularios.

## Consola administrativa

La ruta `/admin` contiene los ocho catálogos académicos, Alumnos, Tutores, Vínculos
alumno–tutor y los módulos de Roles y permisos y Usuarios.
Todos los listados consultan la
base de datos con filtros y paginación; el botón **Exportar Excel** aplica exactamente
los mismos filtros y genera el archivo con Apache POI en modo streaming.

Para recuperación administrativa, configura `ADMIN_BOOTSTRAP_USERNAME` y
`ADMIN_BOOTSTRAP_PASSWORD` en `.env`.

La base del módulo definitivo de seguridad ya existe en Flyway V2 y en la capa de
dominio. Incluye aislamiento por institución, roles con permisos técnicos, alcances
institucionales o por plantel e invitaciones de un solo uso cuyo token sólo se guarda
como hash. El inicio de sesión acepta usuarios activos persistidos y carga como
autoridades sus permisos vigentes. Si un username se repite en varias instituciones,
se ingresa como `CODIGO_INSTITUCION\usuario`; si es único, basta el username.

El alcance del rol también se aplica a los datos: un usuario institucional sólo ve su
institución y uno de plantel sólo ve los planteles, ofertas y grupos asignados. Los
selectores, accesos directos por ID y exportaciones Excel respetan la misma regla. Los
roles y usuarios sólo pueden administrarse con alcance institucional; un intento fuera
del alcance responde con una pantalla 403 integrada al diseño.

Las entidades auditables toman automáticamente el identificador del usuario persistido
de la sesión para completar `creado_por_id` y `actualizado_por_id`. Las operaciones del
usuario de recuperación o de procesos sin cuenta persistida mantienen esos actores en
`null` y nunca registran credenciales.

El sistema registra el último acceso correcto y protege las cuentas persistidas contra
intentos repetidos: cinco contraseñas incorrectas consecutivas bloquean durante 15
minutos. Un acceso correcto reinicia el contador. Los bloqueos administrativos sin fecha
son permanentes hasta que un administrador cambie el estado, y el login nunca revela si
el usuario existe, está inactivo o está bloqueado.

La consola y el login incluyen temas claro y oscuro con preferencia persistente en el
dispositivo. Instituciones, planteles, niveles, oferta educativa y grados ya permiten
crear, editar y desactivar registros; los ciclos escolares permiten crear y editar su
vigencia, estado y selección predeterminada, y los periodos académicos permiten administrar
su calendario por ciclo y nivel. Los grupos permiten alta, edición y desactivación con
selectores que respetan la oferta educativa. Los errores de validación, reglas de negocio,
concurrencia y restricciones de integridad se muestran dentro del mismo formulario sin
perder los valores capturados ni exponer detalles técnicos de PostgreSQL.

Todos los listados y formularios administrativos incluyen un botón **Cerrar sesión**.
El control envía un `POST /logout` protegido con CSRF e invalida la sesión antes de
regresar al inicio.

Roles y permisos permite crear, editar y desactivar perfiles por institución, además de
seleccionar sus permisos técnicos. Los cambios de permisos se conservan mediante
activación o desactivación lógica de la relación y el listado mantiene filtros,
paginación y exportación Excel.

Usuarios permite crear identidades invitadas por institución, editar sus datos,
administrar estados y asignar roles con alcance institucional, por plantel o por
vínculos de tutor. La invitación genera un enlace de 48 horas que se muestra una sola
vez; la pantalla pública permite establecer una contraseña protegida sin exponer el
token ni la contraseña en la base de datos.

Para usuarios activos, la administración también puede generar un enlace independiente
de recuperación que vence en 30 minutos. El token sólo se guarda como SHA-256, se
consume una vez y la contraseña nueva se codifica con BCrypt. Este flujo limpia bloqueos
temporales pero respeta los bloqueos administrativos permanentes. Mientras no se
configure un servidor SMTP y correos reales, el enlace se entrega manualmente por un
canal verificado.

Alumnos permite registrar y mantener el expediente personal e institucional con
matrícula y CURP únicas por institución, filtros en base de datos, paginación, Excel y
desactivación lógica. Requiere conceder `ALUMNO_LEER` y/o `ALUMNO_ADMINISTRAR` a los
roles correspondientes. El plantel, grado y grupo vigentes no se almacenan en este
expediente: se derivarán de las inscripciones para conservar el historial correctamente.
Los formularios de alumnos, ciclos y periodos usan fechas ISO compatibles con los
controles nativos del navegador tanto al crear como al editar.

En la edición del alumno puede cargarse una fotografía JPEG o PNG de hasta 5 MB. El
sistema valida el contenido real y sus dimensiones, calcula SHA-256, conserva cada
reemplazo en el historial y sirve las imágenes mediante una ruta autenticada con control
institucional y caché deshabilitada. PostgreSQL sólo contiene metadatos y relaciones.

Tutores permite registrar datos personales, contacto, domicilio y ocupación, además de
vincular opcionalmente una cuenta de usuario de la misma institución. Incluye filtros en
base de datos, paginación, Excel y desactivación lógica. Requiere conceder
`TUTOR_LEER` y/o `TUTOR_ADMINISTRAR` a los roles correspondientes. La relación concreta
con alumnos, parentesco, autorizaciones y vigencias se administrará en `AlumnoTutor`.

Vínculos alumno–tutor conserva el parentesco, contacto principal, responsabilidad
financiera, autorizaciones y vigencias. Impide relaciones superpuestas y más de un
contacto principal vigente; revocar conserva el historial y retira el acceso. Requiere
`VINCULO_TUTOR_LEER` y/o `VINCULO_TUTOR_ADMINISTRAR`. Las cuentas con alcance
`VINCULOS_TUTOR` sólo pueden consultar relaciones propias, activas y vigentes.
