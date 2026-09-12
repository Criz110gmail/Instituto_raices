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

La portada, el login, la activación de cuenta y el endpoint de salud son públicos. Las
demás rutas autentican usuarios activos de PostgreSQL y comprueban los permisos de sus
roles. Las credenciales `ADMIN_BOOTSTRAP_USERNAME` y `ADMIN_BOOTSTRAP_PASSWORD` de
`.env` se conservan como acceso temporal de recuperación.

## Estructura inicial

- `escuela.institucion`: institución, planteles y oferta educativa.
- `escuela.academico`: niveles, grados, ciclos, periodos y grupos.
- `escuela.seguridad`: usuarios, roles, permisos, alcances e invitaciones de acceso.
- `escuela.config`: seguridad y auditoría JPA.
- `escuela.common`: excepciones, respuesta de auditoría y utilidades compartidas.
- Cada módulo contiene DTO, mappers, repositorios y servicios transaccionales.
- `db/migration`: esquema gestionado exclusivamente por Flyway.

Los controladores enlazan formularios validados con DTO propios y nunca exponen ni
reciben entidades JPA directamente.

## Consola administrativa

La ruta `/admin` contiene los ocho catálogos académicos y los módulos de Roles y
permisos y Usuarios.
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

La consola y el login incluyen temas claro y oscuro con preferencia persistente en el
dispositivo. Instituciones, planteles, niveles, oferta educativa y grados ya permiten
crear, editar y desactivar registros; los ciclos escolares permiten crear y editar su
vigencia, estado y selección predeterminada, y los periodos académicos permiten administrar
su calendario por ciclo y nivel. Los grupos permiten alta, edición y desactivación con
selectores que respetan la oferta educativa. Los errores de validación, reglas de negocio,
concurrencia y restricciones de integridad se muestran dentro del mismo formulario sin
perder los valores capturados ni exponer detalles técnicos de PostgreSQL.

Roles y permisos permite crear, editar y desactivar perfiles por institución, además de
seleccionar sus permisos técnicos. Los cambios de permisos se conservan mediante
activación o desactivación lógica de la relación y el listado mantiene filtros,
paginación y exportación Excel.

Usuarios permite crear identidades invitadas por institución, editar sus datos,
administrar estados y asignar roles con alcance institucional, por plantel o por
vínculos de tutor. La invitación genera un enlace de 48 horas que se muestra una sola
vez; la pantalla pública permite establecer una contraseña protegida sin exponer el
token ni la contraseña en la base de datos.
