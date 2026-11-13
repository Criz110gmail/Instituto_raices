# Proyecto reutilizable para varias instituciones

## Marca de las pantallas

El logotipo se toma de `src/main/resources/static/images/logo1.png`, con fondo transparente. Los textos alternativos dicen «Logotipo de la institución». El acceso ya no muestra el nombre de una empresa debajo del logo. La combinación de colores predeterminada se llama **Verde institucional**.

Para cambiar la identidad gráfica, reemplaza ese PNG y reconstruye la aplicación con Docker. El texto dibujado dentro de la imagen forma parte del archivo; no es texto fijo de la aplicación.

## Nombres y contenido propios de una instalación

Los nombres legales que aparecen en reportes y los datos de Instituciones se obtienen de la base de datos de esa instalación. Configúralos para cada cliente. Cambiar una imagen no renombra registros ni elimina sus datos.

El contenido que se haya guardado/publicado en Diseño web pertenece a la institución y se edita desde su diseñador. Las plantillas iniciales del repositorio ahora son genéricas, pero no se sustituyeron los textos publicados por el usuario.

## Historial y guía

Las migraciones SQL ya aplicadas conservan sus archivos y checksums. V177 convierte las referencias editoriales de la guía visible en ejemplos genéricos; no cambia instituciones, proveedores, empleados, permisos, saldos ni movimientos. La documentación histórica conserva el registro de las pruebas realizadas.

## Comprobación

1. Recarga el login: sólo debe aparecer el logo, sin nombre escrito debajo.
2. Revisa Administración y otro portal. Los textos alternativos de marca son genéricos.
3. En Personalizar revisa **Verde institucional**.
4. Revisa las plantillas de un sitio de prueba sin publicarlo: sus textos iniciales no mencionan una empresa concreta.
5. En Guía de procesos los ejemplos de institución/proveedor deben ser genéricos. Los importes, pasos y permisos se conservan.

Verificación: 1125 pruebas Java, 208 JavaScript y una prueba integral con dos instituciones aprobadas. Código activo y datos de prueba sin el nombre fijo anterior; migraciones históricas intactas.
