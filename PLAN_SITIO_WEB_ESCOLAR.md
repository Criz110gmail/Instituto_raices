## Arrastre y alineación precisa del diseñador — 11/11/2026

Cuadrícula por superposición pointer-events:none sobre los fondos (no background
oculto por hijos), por sección seleccionada y con origen del contenedor real. Paso
4/8/16/24/32px;16por defecto. Ajuste absoluto a cuadrícula y guías rosas al borde/
centro más cercano, incluidos bordes cruzados de objetos de distinto tamaño. Tolerancia
6px de pantalla convertida a píxeles de página según zoom; geometría pura en
NexoDisenador.ajustarMovimiento, cubierta por pruebas. Alt libera ajuste; Mayús
bloquea eje. Cachea rectángulos al iniciar para no medir todo el DOM en cada movimiento.

Iconos/imágenes/formas seleccionados admiten arrastre directo; textos conservan doble
clic para editar y tirador para mover. Captura de puntero diferida hasta superar3px,
clic posterior suprimido sólo tras mover/girar, Escape/pointercancel restaura. Se
conserva multiselección Mayús/Ctrl/Cmd. Lectura X/Y durante arrastre y tras soltar;
tiradores legibles a cualquier escala. Zoom enfoca selección vertical/horizontal sin
cambiar modelo ni marcar borrador modificado. Vista sin guías omite overlays. No se
modifican render público ni esquema/permisos; sigue V174. Caché20261111-precision.

Guía:PRUEBA_POSICION_PRECISA.md. Aceptación manual pendiente. QA usa fixture local;
no guardar ni publicar datos operativos para probar las posiciones.

## Paleta de objetos escolares — 11/11/2026

Componentes → Objetos escolares abre paleta visual separada de Elementos, con36
ilustraciones originales SVG, búsqueda sin acentos y categorías Útiles/Aprendizaje/
Creatividad/Vida escolar/Decoraciones. Clic o drag al lienzo; mismo destino, límites,
confirmaciones, copiar/pegar, deshacer, giro y estilos por dispositivo existentes.
Propiedades → Contenido permite cambiar dibujo/color y descripción accesible;
decorativo por defecto. No acepta SVG ni URLs de usuario: sólo clave cerrada validada
en backend; tipo icono (div sin texto/hijos), campos icono/decorativo en JSONB actual.
Recurso estático local /images/sitio-objetos-escolares.svg, GET público exacto y sin
fotos ni datos privados. SVG externo por use, currentColor, proporciones64x64; mismo
recurso y CSS en SSR/lienzo. Sin migraciones ni nuevos permisos. Caché20261111-objetos.
Generador de ilustraciones:scripts/generar_objetos_escolares.py; test verifica paridad
de claves Java/JS/sprite. No cambiar a SVG arbitrario cargado desde propiedades.

Guía manual:PRUEBA_OBJETOS_ESCOLARES.md. No guardar ni publicar datos operativos
para QA; usar fixture/base temporal. Aceptación manual pendiente.

## Siete diseños de galería y visor ampliado — 11/11/2026

Se amplía la biblioteca visual con Cuadrícula, Mosaico, Muro de fotos, Tarjetas,
Carrusel, Foto destacada y Tira panorámica. Propiedades → Contenido muestra tarjetas
visuales para elegir diseño, columnas2..5 en diseños estáticos, descripciones,
efectos ninguno/acercar/elevar/monocromo y transiciones deslizar/fundido/zoom para
Carrusel y Foto destacada. El cambio conserva álbum y orden. JSONB, sin migración
posterior a174; backend valida enums, columnas y tiempo. SSR y lienzo usan el mismo
markup y sitio-galeria.css/js. Cache de diseñador:20261111-galerias.

Clic en foto abre diálogo ampliado con navegación, miniaturas, zoom, contador,
teclado/Escape y gesto táctil; no modifica el borrador. En editor el iframe envía
sólo IDs a padre del mismo origen, que verifica componente/álbum/foto activos y
construye URL privada propia: visor ocupa pantalla completa. Runtime rechaza URLs
externas y esquemas noHTTP(S). Conserva permisos, CSRF y recursos protegidos.
Autoplay se pausa al abrir visor; respeta foco, visibilidad y movimiento reducido.
Tira sin desbordamiento centra fotos y oculta controles innecesarios.

Verificación:1111Java,177JavaScript y9pruebas aisladas PostgreSQL aprobadas.
Navegador con fixture: siete diseños, orden conservado, transiciones, vista ampliada,
flechas/miniaturas/teclado/zoom/Escape; guardar y recargar conserva Mosaico/efecto/
descripciones; público móvil390sin desbordamiento. Guía:PRUEBA_GALERIAS_DISENOS.md.
Aceptación manual pendiente. No publicar ni sustituir el borrador operativo al probar.

## Biblioteca visual integrada — 11/11/2026

Implementada: recursos sin salir del diseñador, álbumes/carga múltiple/reutilización/
orden por arrastre, carrusel/cuadrícula y ventanas de Contacto/Ubicaciones/Publicaciones.
1110Java/175JS e integración PostgreSQL temporal8pruebas aprobadas. Sin migración
nueva después de174. Aceptación manual: PRUEBA_BIBLIOTECA_VISUAL.md.

## Estado vigente — 11/11/2026

El diseñador de secciones y componentes está implementado y verificado técnicamente.
Incluye página original completa, selección y edición por objeto, nuevas secciones,
logo/menús, paleta arrastrable, tamaños/alineación, vistas responsivas y recuperación
de revisiones. No modifica automáticamente la publicación ni la configuración del
inicio. Falta aceptación manual con PRUEBA_DISENADOR_PAGINA.md. El editor anterior
permanece para leer publicaciones históricas. Sin nueva migración después de V174.

# Sitio público de Instituto Raíces

Acordado el 09/11/2026. Entrada pública `/`, dentro de la aplicación Spring Boot y
Thymeleaf existente. No usar otro hosting ni sustituir los portales.

## Identidad aprobada

Logo original: `src/main/resources/static/images/logo.png`, conservar sin alterarlo.
Eslogan exacto: **SEMBRANDO PRINCIPIOS, TRANSFORMANDO CULTURA.**
Paleta derivada del logo: verde hoja protagonista, verde profundo, café y fondo marfil.
Diseño público propio; no modificar colores administrativos.

Corrección del propietario: la primera propuesta fue demasiado formal. Dirección
vigente: profesional y divertida, letras sans amigables, verde lima/amarillo y
acentos turquesa, fondos con útiles escolares (libros, regla, crayolas, aviones de
papel), formas orgánicas y fotografía de niños como protagonista. Bienvenida con
semilla que brota, logo y eslogan animados. No retomar titulares serif ni estética
editorial seria. Rediseño implementado, pendiente aceptación del propietario.

## Arquitectura de contenido

Página responsiva con Inicio, Quiénes somos (misión, visión, valores), Oferta
académica (talleres), Inscripciones, Galería y Ubicaciones; footer con identidad,
contactos, privacidad y accesos a Familias, Maestros, Personal y Administración.
Menú móvil desplegable. No publicar datos, fotografías, niveles, direcciones,
requisitos, fechas o afirmaciones institucionales inventadas.

Administración futura: categoría Diseño web con Identidad y apariencia, Portada,
Contenido institucional, Oferta y talleres, Inscripciones, Galería y Ubicaciones.
Catálogos con listado/filtros/paginación/Excel y formularios separados. Mantener
patrones administrativos, ayudas, confirmaciones con Cancelar, origen y captura.
WhatsApp: número internacional y mensaje inicial editables en administración;
botón público sólo con número válido publicado. No incrustar número ficticio.
Planteles existentes se vinculan expresamente para publicación; nunca exponer
información escolar privada ni elegir automáticamente la primera institución.

Guardar borrador, vista previa y publicación explícita; permisos de edición y
publicación comprobados en servidor, historial, versión para evitar sobreescrituras.
Imágenes públicas específicas, validadas y optimizadas; no publicar archivos del
expediente privado. Fotos de menores seleccionadas expresamente para difusión.
Futuros textos como texto seguro, sin HTML/script libre; colores validados y
contraste comprobado. Contacto público sin capturar inscripción ni datos médicos.

## Etapas y aceptación

1. **Identidad e inicio público.** Estructura responsiva, logo original, eslogan,
   menú/anclas/accesos y animación de bienvenida. Logo revelado con progreso real
   de recursos esenciales 0–100 %, tiempo máximo, omitir, una vez por sesión y
   respeto a movimiento reducido. Contenido visible sin JS o si falla la carga.
   Portada gráfica de identidad y banner institucional `images/imagen.png`, aportado
   por el usuario durante esta etapa, con verde lima y amarillo como referencia.
   No simular campus ni extraer automáticamente datos de contacto del banner.
   Sin nuevas tablas ni módulos administrativos en esta entrega.
2. **Editor y publicación.** Diseño web, identidad/colores/portada y textos,
   borrador/vista previa/publicación, oferta/talleres/inscripciones. Definir esquema
   tras leer modelo de dominio; migraciones, permisos y aislamiento institucional.
3. **Galería y contacto.** Álbumes, carga/orden/retiro, ubicaciones vinculadas,
   WhatsApp editable y enlaces de contacto. Encuadres para móvil/escritorio.
4. **Cierre.** Pruebas de permisos/alcance/publicación/concurrencia/archivos,
   revisión visual en escritorio y celular, navegación con teclado, carga lenta,
   imágenes fallidas y movimiento reducido, documentación y aceptación humana.

## Estado al 10/11/2026

Etapas1–3 implementadas y desplegadas en la instalación local18080 con V172/V173.
Incluye identidad,textos,logo/portada,galería,ubicaciones,contacto,privacidad y
WhatsApp,borrador,vista previa,publicación e historial por institución.

Comprobaciones técnicas de cierre realizadas:1097Java,148JS,integración PostgreSQL
separada y revisión visual en escritorio/390px,claro/oscuro. Respaldo validado previo
a despliegue y datos financieros intactos. Prueba manual integral pendiente de
aceptación del propietario en PRUEBA_SITIO_WEB_COMPLETO.md. No se han publicado
datos operativos ni concedido permisos automáticamente.

La raíz se vincula explícitamente con APP_SITIO_CODIGO;cada escuela usa la misma
versión del código y su propia configuración. Identidad administrativa/portales
continúa como entrega separada;sesión en la otra computadora conserva su pendiente.

## Ampliación autorizada: editor visual — 10/11/2026

V174 incorpora diseño estructurado JSONB en borradores y publicaciones. El usuario
autorizó un editor visual con paleta y arrastre. Se agregan 14 bloques, cuatro
puntos de partida, edición directa, propiedades y vista por dispositivo. Se
conserva renderizado público en servidor, historial, permisos y publicación explícita.
Sin HTML/scripts arbitrarios ni carga de recursos externos por animaciones.
La apariencia de publicaciones anteriores permanece mientras no se publique una nueva.
Prueba humana vigente: PRUEBA_EDITOR_VISUAL_SITIO.md; los recorridos de catálogos
de PRUEBA_SITIO_WEB_COMPLETO.md siguen aplicando.
