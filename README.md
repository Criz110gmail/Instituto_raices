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

La seguridad inicial permite la portada, el login y el endpoint de salud. Las demás
rutas requieren las credenciales `ADMIN_BOOTSTRAP_USERNAME` y
`ADMIN_BOOTSTRAP_PASSWORD` configuradas en `.env`.

## Estructura inicial

- `escuela.institucion`: institución, planteles y oferta educativa.
- `escuela.academico`: niveles, grados, ciclos, periodos y grupos.
- `escuela.config`: seguridad y auditoría JPA.
- `escuela.common`: excepciones, respuesta de auditoría y utilidades compartidas.
- Cada módulo contiene DTO, mappers, repositorios y servicios transaccionales.
- `db/migration`: esquema gestionado exclusivamente por Flyway.

Los controladores enlazan formularios validados con DTO propios y nunca exponen ni
reciben entidades JPA directamente.

## Consola administrativa

La ruta `/admin` contiene los ocho catálogos actuales. Todos los listados consultan la
base de datos con filtros y paginación; el botón **Exportar Excel** aplica exactamente
los mismos filtros y genera el archivo con Apache POI en modo streaming.

Mientras se desarrolla el módulo definitivo de usuarios, configura
`ADMIN_BOOTSTRAP_USERNAME` y `ADMIN_BOOTSTRAP_PASSWORD` en `.env` para acceder.

La consola y el login incluyen temas claro y oscuro con preferencia persistente en el
dispositivo. Instituciones, planteles, niveles, oferta educativa y grados ya permiten
crear, editar y desactivar registros. Los errores de validación, reglas de negocio,
concurrencia y restricciones de integridad se muestran dentro del mismo formulario sin
perder los valores capturados ni exponer detalles técnicos de PostgreSQL.
