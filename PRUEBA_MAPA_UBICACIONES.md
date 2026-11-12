# Mapa de ubicación en la página web

## Qué vamos a comprobar

Que una familia encuentre la dirección de la escuela, vea el mapa y pueda abrir las indicaciones para llegar. El menú **Ubicaciones** debe llevar a esa sección al final de la página.

## 1. Colocar el mapa

1. Recarga **Diseño web** para cargar las nuevas herramientas.
2. Abre **Recursos → Colocar mapa en Ubicaciones**.
3. Se selecciona el mapa en la sección Ubicaciones, conservando su encabezado. Se usa la dirección registrada del plantel: Parián 1974, Jardines del Country, Guadalajara, Jalisco, 44210, México.
4. En **Propiedades** revisa la dirección completa, el título, el acercamiento y el diseño. Puedes elegir **Dirección junto al mapa** o **Mapa ancho con dirección arriba**.
5. El acceso rápido oculta los avisos anteriores de ubicación pendiente y la decoración que competía con el mapa. No elimina esos objetos: puedes deshacer el cambio.
6. Vuelve a pulsar el acceso rápido: debe seleccionar el mismo mapa, sin duplicarlo.

Si hay varios planteles con dirección, agrega **Componentes → Contenido → Mapa** y elige el plantel en Propiedades. Cada mapa tiene su propia dirección editable. No se inventan horarios ni teléfonos.

## 2. Revisar la navegación y el diseño

1. Activa **Vista sin guías**.
2. Pulsa **Ubicaciones** en el encabezado: debe desplazarse a la sección del mapa dentro de la vista previa.
3. Comprueba la dirección y el marcador. El mapa puede tardar en cargar porque consulta Google Maps.
4. Pulsa **Cómo llegar**: debe abrir las indicaciones para esa dirección en otra pestaña.
5. Revisa la vista de celular: la dirección queda arriba y el mapa debajo, sin salirse del ancho.
6. Vuelve a la edición: puedes seleccionar, mover y ajustar el mapa como los demás componentes. El mapa se explora en Vista sin guías para evitar que interfiera con el arrastre.

## 3. Guardar y publicar

1. Guarda el borrador. Primero prueba Cancelar en la confirmación; luego vuelve a guardar y confirma.
2. Recarga el editor y comprueba que conserva dirección, diseño y posición.
3. Publica con su confirmación cuando el resultado sea correcto.
4. Abre la página pública y prueba Ubicaciones y Cómo llegar.
5. En esta computadora, revisa tanto `/` como `/sitio/INSTITUTO_RAICES`: deben mostrar la misma publicación.

Guardar borrador no publica. La actualización del código no coloca ni publica automáticamente objetos en el diseño operativo.

## Verificación técnica realizada — 12/11/2026

191 pruebas JavaScript y 1117 pruebas Java aprobadas. Integración en base temporal: guardado y publicación del mapa verificados. En navegador de prueba se comprobaron inserción sin duplicados, guardar/cancelar, recarga, navegación desde el menú, ambos diseños y vista móvil de 390 px. Google Maps cargó el marcador de Parián 1974. La aceptación en la página operativa queda pendiente de realizar los pasos anteriores.

## Propiedades: comprobar que no se pierde el lugar

1. Selecciona el mapa y baja en Propiedades hasta **Diseño del mapa**.
2. Cambia entre **Dirección junto al mapa** y **Mapa ancho con dirección arriba**. El panel y el lienzo deben quedarse en el mismo lugar; el selector conserva el foco.
3. Abre **Bordes y acomodo**, vuelve a **Texto y color** y cambia **Fondo transparente**. El grupo que abriste debe seguir abierto.
4. Prueba también editar un texto y cambiar una opción de una sección. No debe saltar al inicio mientras sigues trabajando en el mismo objeto.
5. Seleccionar otro objeto sí muestra las propiedades de ese nuevo objeto desde el principio. Agregar un objeto o seleccionarlo desde el árbol puede llevar el lienzo a ese objeto intencionalmente.

### Publicación revisada el 12/11/2026

Se comprobó la publicación real15, ya realizada por el propietario. El mapa existe, carga la ubicación de Parián1974 y aparece igual en `/` y `/sitio/INSTITUTO_RAICES`. No fue necesario guardar ni publicar otra vez. Se revisó también en celular. Si una pestaña que quedó abierta muestra otra vista, recárgala por completo y comprueba la URL; no agregues otro mapa para intentar corregirlo.
