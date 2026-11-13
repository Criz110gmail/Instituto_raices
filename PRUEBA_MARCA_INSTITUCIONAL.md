# Marca institucional y diseño predeterminado

## Qué cambió

Los accesos, el menú y las pantallas que mostraban Nexo ahora usan `static/images/logo1.png`. La imagen conserva su proporción y conserva la transparencia del PNG, sin fondo ni placa añadida. Se retiró el nombre del producto de los títulos y textos de esas pantallas.

Los reportes financieros PDF/Excel y los respaldos de logotipo de expedientes, fichas, comprobantes y recibos usan ese mismo archivo. Las planeaciones incluyen el logo y ya no muestran Nexo en el pie. Los logotipos institucionales que se hayan cargado expresamente siguen siendo válidos; esta instalación no tiene uno que sustituya al respaldo.

## Revisar el acceso y el menú

1. Recarga el sistema. Abre el acceso de Administración: debe aparecer el logo completo de la institución.
2. Alterna Claro/Oscuro. El logo debe conservar sus colores, sin deformarse y directamente sobre el fondo del diseño.
3. Entra y revisa dos listados distintos. El menú debe mostrar la misma imagen, sin Nexo ni la letra N como marca.
4. Revisa tu perfil y la activación/recuperación de acceso cuando corresponda: también usan la imagen institucional.

## Diseño predeterminado de Administración

Verde bosque `#214936`, fondo claro `#F5F7F2` y acento verde `#72A629`, con adaptación oscura automática y contraste en botones.

Si ya tenías una personalización publicada, se conserva. Para elegir esta combinación: **Diseño → Portales e identidad → Administración → Personalizar → Combinaciones y colores recientes → Verde institucional**. Revisa **Acceso / Dentro del sistema**, guarda y publica.

**Recuperar diseño inicial** también utiliza ahora la paleta genérica predeterminada de Administración. Recuperar sigue creando un borrador y requiere publicar. No se publicaron colores ni se alteraron tus versiones durante esta actualización.

## Revisar reportes

1. Genera un PDF financiero y exporta su Excel. Revisa el logo, la cabecera, la primera fila y los importes.
2. Abre un comprobante y un recibo de nómina disponibles; el logo debe conservar su proporción.
3. Abre un expediente/ficha y una planeación. En la planeación debe aparecer el logo arriba y el pie sin Nexo.
4. Los archivos descargados anteriormente conservan su contenido; genera o descarga nuevamente el reporte para revisar la marca nueva.

Verificación: 1125 pruebas Java y208 JavaScript aprobadas. Siete pruebas adicionales de integración/reportes aprobaron marca, proporción Excel, PDFs y accesos. Muestras ficticias de tesorería y planeación revisadas visualmente, sin modificar importes ni registros operativos.

Actualización 13/11/2026: se retiraron fondos, rellenos, bordes y sombras del logo en todas las pantallas y vistas previas. La imagen de Jasper también es transparente. El color de la página del PDF o de las celdas del Excel sigue siendo el del reporte; el logo no añade un rectángulo propio.

Actualización de marca genérica: el logo usa el texto alternativo «Logotipo de la institución». Se quitó el nombre escrito debajo del logo del login. Las plantillas iniciales del sitio y los ejemplos activos son genéricos. Los nombres legales en reportes y las instituciones configuradas siguen proviniendo de los datos de cada instalación. Las migraciones SQL aplicadas y el historial de pruebas no se reescriben.
