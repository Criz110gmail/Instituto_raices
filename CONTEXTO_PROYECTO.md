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
