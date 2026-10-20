# Instrucciones para agentes

Antes de trabajar en este repositorio, lee por completo `AGENT.md`. Ese archivo contiene
el estado actual, las decisiones técnicas, las restricciones permanentes, la forma de
arranque en otra computadora y el siguiente paso acordado.

Lee también `CONTEXTO_PROYECTO.md` para el historial de verificaciones y
`modelo_entidades_sistema_escolar_v1.txt` antes de ampliar el dominio.

Trabaja exclusivamente dentro de este repositorio, preserva los cambios del usuario y
nunca leas, publiques ni confirmes en Git el contenido real de `.env`.

## Regla permanente de organización de módulos

- En módulos tipo catálogo, la entrada del menú abre únicamente el listado con
  tabla, filtros, paginación, Excel y acciones autorizadas.
- El alta se abre con un botón **Nuevo registro** (o el nombre específico, por
  ejemplo **Nuevo puesto**) y cada fila tiene **Editar**. Alta y edición utilizan
  una pantalla de formulario separada: no incrustar la captura debajo de la tabla.
- Guardar, Cancelar y Volver al listado conservan filtros, tamaño y página de origen;
  los errores permanecen en el formulario sin perder la captura.
- Reutilizar diseño, botones, ayudas, estados, confirmaciones con Cancelar y temas
  claro/oscuro responsivos de los módulos existentes; no crear una paleta diferente.
- Las acciones y sus rutas respetan permisos; ocultar un botón no sustituye validar
  autorización en servidor. Sólo apartarse de este patrón si el usuario lo pide
  explícitamente para un flujo específico. No refactorizar todos los módulos previos
  sin autorización: aplicar la regla en las nuevas implementaciones o correcciones.
