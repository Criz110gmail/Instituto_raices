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
- `escuela.alumno`: expediente institucional de alumnos.
- `escuela.config`: seguridad y auditoría JPA.
- `escuela.common`: excepciones, respuesta de auditoría y utilidades compartidas.
- Cada módulo contiene DTO, mappers, repositorios y servicios transaccionales.
- `db/migration`: esquema gestionado exclusivamente por Flyway.

Los controladores enlazan formularios validados con DTO propios y nunca exponen ni
reciben entidades JPA directamente.

## Consola administrativa

La ruta `/admin` contiene los ocho catálogos académicos, Alumnos y los módulos de Roles
y permisos y Usuarios.
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
