# Prueba del diseñador de página

Estado: implementación y verificaciones automáticas terminadas; falta aceptación manual del usuario.

Para fotos, álbumes, carrusel y otros recursos usa también [Prueba de biblioteca visual](PRUEBA_BIBLIOTECA_VISUAL.md). Estos ajustes ahora se hacen en ventanas del diseñador.
Actualizado: 11/11/2026. Esta guía sustituye las instrucciones del editor anterior para la pantalla principal de Diseño web.

La idea es sencilla: a la izquierda eliges qué parte de la página quieres cambiar; en el centro ves la página y seleccionas sus objetos; a la derecha cambias sus propiedades o agregas componentes. Guardar crea una copia de trabajo. Publicar es una acción aparte.

## 1. Abrir la página completa

1. Inicia sesión con un usuario que tenga **Diseño web - Editar**.
2. Entra al menú **Diseño web** (`/admin/diseno-web`).
3. Comprueba los tres paneles: **Estructura**, página central y **Componentes / Propiedades / Recursos**.
4. Si el borrador guardado pertenece al editor anterior, se abre una edición local de la página publicada completa. El borrador anterior sigue disponible en **Versiones → Cargar borrador guardado**. Esta apertura no guarda ni publica automáticamente.
5. En la página escolar original deben existir Encabezado, Portada, Franja del eslogan, Quiénes somos, **Un mundo por explorar**, Galería, Ubicaciones, Accesos a portales y Pie de página. Un diseño personalizado puede tener otras secciones.
6. Selecciona **Un mundo por explorar** en el árbol. Deben verse su título y las tarjetas Oferta académica, Talleres e Inscripciones, con sus fondos y decoraciones.
7. Selecciona **Portada**: revisa fotografía, título, fondo, sol y decoraciones. Selecciona **Encabezado**: revisa logo y navegación.

Si quieres volver a la página que está publicada, abre **Versiones → Cargar página publicada** y confirma **Cargar para editar**. Los cambios locales se sustituyen; las revisiones guardadas se conservan.

## 2. Cambiar un texto sin perder el diseño

1. Selecciona Portada y haz clic sobre **PEQUEÑAS PERSONAS. GRANDES POSIBILIDADES.** en la página.
2. Debe resaltarse ese texto y abrirse **Propiedades**. El árbol debe mostrar el mismo objeto.
3. En **Contenido → Texto** escribe `Pequeños pasos, grandes descubrimientos.`
4. Debe cambiar sólo ese texto. La foto, el fondo y las demás secciones deben seguir presentes.
5. Pulsa **Deshacer**: vuelve el texto anterior. Pulsa **Rehacer**: reaparece el cambio.
6. También puedes hacer doble clic en un texto para editarlo directamente. Si un título tiene palabras de distinto color, cada fragmento puede seleccionarse por separado; el árbol permite elegir el grupo completo.

## 3. Mover, cambiar tamaño y alinear

1. Con un objeto seleccionado, arrastra el tirador turquesa que aparece encima para moverlo.
2. Usa los tiradores de sus bordes para cambiar su tamaño.
3. En Propiedades revisa **Posición y tamaño**. Los cambios se reflejan en la página.
4. Activa **Cuadrícula** y conserva **Ajustar** para probar la ayuda al acomodar objetos.
5. Selecciona dos objetos con **Mayús + clic** y prueba Izquierda, Centro o Arriba. Deben alinearse los objetos seleccionados.
6. Prueba Deshacer y Rehacer para recuperar la posición anterior.

Los objetos del diseño original conservan su composición al moverlos. Las secciones nuevas pueden usar lienzo libre o un acomodo vertical, horizontal o en cuadrícula.

### Copiar, pegar, quitar y girar sin abrir Propiedades

1. Haz un clic sobre un objeto para seleccionarlo. En Windows/Linux pulsa **Ctrl+C** y luego **Ctrl+V**; en Mac usa **⌘C** y **⌘V**. Debe aparecer otra copia, un poco desplazada, que puedas mover por separado.
2. Si copias una tarjeta o contenedor, también se copian sus elementos internos. Selecciona otra sección o contenedor antes de pegar para colocar la copia ahí. Puedes pegar varias veces.
3. La barra que aparece junto al objeto también incluye **Copiar**, **Pegar** y **Quitar**. Los nombres aparecen al poner el mouse sobre sus iconos.
4. Pulsa **Quitar** desde esa barra. Primero prueba Cancelar: el objeto sigue ahí. Repite y confirma Quitar: desaparece. Deshacer debe recuperarlo.
5. Arrastra el **tirador circular ↻** para girarlo directamente. Mantén Mayús para avanzar en pasos de 15°. Comprueba el resultado en Giro y utiliza Deshacer para regresar. Escape durante el arrastre lo cancela.
6. Para editar el texto directamente haz **doble clic**. Mientras escribes, copiar y pegar trabaja con el texto; para volver a operar con el objeto sal del campo o pulsa Escape.
7. Guarda el borrador y abre su vista previa: debe conservar la posición y el giro. En Celular/Tableta el giro corresponde a esa vista, sin alterar el estilo de Computadora.

La copia de objetos sirve dentro de esta sesión del diseñador. Copiar texto en campos conserva los atajos habituales del sistema operativo. Estas acciones no publican automáticamente.

## 4. Crear una sección y agregar componentes

1. En Estructura pulsa **＋ Nueva sección**.
2. Nombre: `Actividades de prueba`; Tipo: **Contenido**; Acomodo: **Lienzo libre**.
3. Pulsa **Crear sección**. Debe aparecer al final de la página y en el árbol.
4. Abre **Componentes** y arrastra **Título** a la sección. También puedes seleccionar la sección y pulsar Título en la paleta.
5. Cambia el texto a `Aprender también es explorar`.
6. Agrega Texto, Imagen, Botón, Forma y un Contenedor o Tarjeta. Cada elemento debe poder seleccionarse, moverse y tener sus propias propiedades.
7. En Texto y color prueba tamaño de letra, paleta, fondo, borde y redondeado. En Animación prueba Aparecer o Deslizar.
8. Duplica un objeto con **Duplicar**; comprueba que pueda editarse por separado.
9. Usa las flechas ↑ / ↓ de Propiedades para cambiar el orden entre elementos del mismo contenedor. En el árbol puedes arrastrar un objeto a otro contenedor y arrastrar secciones para reordenarlas.
10. Prueba **Quitar**: Cancelar conserva el elemento; confirmar lo quita de esta edición. Deshacer lo recupera.

## 5. Cambiar logo y opciones del menú

1. Selecciona Encabezado y haz clic en el logotipo.
2. En Propiedades debe mostrarse como Logotipo, con Imagen y Descripción accesible.
3. Pulsa **Elegir o subir imagen** en las propiedades del logo. Se abre la biblioteca. Arrastra una imagen PNG/JPEG de prueba a la zona de carga, o selecciónala con un clic. Máximo 5 MB.
4. Con el logo seleccionado, elige la miniatura y pulsa **Usar imagen**. Debe cambiar el logo y conservarse el resto del encabezado.
5. Selecciona el texto **Quiénes somos** del menú. Cambia Texto a `Nuestra escuela`; debe conservar el destino `#nosotros`.
6. Comprueba que puedas modificar el destino del enlace desde sus propiedades. Los enlaces del menú no conceden permisos a los módulos del sistema.
7. Para agregar una opción, selecciona el objeto **Menú** en el árbol y pulsa **＋ Agregar entrada al menú**. Configura su texto y destino.
8. Recupera los cambios de prueba con Deshacer si no quieres conservarlos.

## 6. Comprobar que el editor coincide con la página

1. Arriba selecciona **Esta ventana**. Ahora ésa es la opción inicial. El rótulo indicará, por ejemplo, **1720 px de página · Escala 63 %**.
2. Esos 1720 px son el ancho real que tendría la página en tu ventana. El 63 % sólo permite verla entre los paneles: no cambia la posición guardada de los objetos.
3. Selecciona el logo. En Horizontal · X, disminuye 24 respecto al valor anterior, o usa el tirador para desplazarlo a la izquierda.
4. Guarda y confirma el borrador. Abre **Vista previa guardada** en otra pestaña del mismo navegador, sin cambiar el tamaño de ventana ni su zoom. Compara la misma revisión: el logo, menú, textos y demás objetos deben ocupar las mismas posiciones y tamaños, considerando la escala reducida del editor.
5. El aviso de vista previa aparece flotando abajo; puedes cerrarlo con ×. No desplaza el contenido de la página.
6. Si no hay ninguna publicación, abajo aparece **Aún sin publicar**. La página de inicio original no es la vista previa de tu borrador. Guardar no reemplaza esa página; para comparar lo guardado utiliza Vista previa guardada.
7. Una publicación existente conserva su revisión anterior hasta que publiques expresamente la nueva. El enlace indica la revisión publicada. Compara la misma revisión y el mismo ancho.
8. Para probar otros tamaños usa los valores explícitos **Computadora · 1720 px**, **Computadora · 1440 px**, **Portátil · 1280 px**, **Portátil · 1100 px**, Tableta o Celular. La página se adapta al ancho elegido; es normal que el acomodo sea distinto entre una pantalla ancha y otra estrecha.

## 7. Computadora, tableta y celular

1. Selecciona el título de la sección nueva y cambia arriba de Esta ventana o Computadora a **Celular**.
2. Reduce Tamaño de letra, por ejemplo a 22, y acomoda el componente para esa vista.
3. Regresa a Esta ventana o Computadora: su tamaño base debe conservarse. El texto del contenido es el mismo en todas las vistas.
4. En Celular usa **Restablecer esta vista** para quitar sus ajustes específicos.
5. Comprueba Tableta y **Vista sin guías**. Para continuar editando usa **Volver a diseñar**.
6. En una pantalla pequeña, Estructura y Componentes / propiedades se abren como paneles. Ciérralos con × para regresar a la página.

Los objetos nuevos de posición libre se acomodan en flujo en pantallas pequeñas hasta que les indiques una posición propia para esa vista. Revisa las tres vistas antes de publicar.

## 8. Guardar y recuperar

1. Pulsa **Guardar borrador** y primero **Cancelar**. No debe crearse una revisión.
2. Repite y confirma **Guardar borrador**. Debe mostrarse la nueva revisión guardada.
3. Recarga: deben conservarse los objetos, textos y posiciones guardados.
4. Abre **Vista previa guardada**. Comprueba el diseño completo sin herramientas de edición.
5. Cambia algo sin guardar y usa **Versiones → Cargar borrador guardado**. Cancelar conserva la edición; confirmar recupera la última copia guardada.
6. En Versiones revisa una copia anterior con **Ver**. **Restaurar** solicita confirmación y crea un nuevo borrador con esa copia; no borra el historial ni cambia lo publicado.
7. Si se cierra la pantalla con cambios locales, al volver con la misma cuenta, navegador y revisión puede ofrecer **Recuperar cambios**. No sustituye guardar en el servidor.

## 9. Publicar, sólo cuando quieras hacer visible la prueba

1. Requiere **Diseño web - Publicar** además de Editar.
2. Guarda primero. Publicar debe impedir publicar una edición con cambios sin guardar.
3. Pulsa **Publicar ↗**. El modal indica la ruta que recibirá esa versión y si también está vinculada al inicio de esta instalación.
4. Primero prueba Cancelar. Luego confirma sólo si deseas que la prueba quede visible.
5. Abre **Página publicada** y comprueba logo, menús, sección Un mundo por explorar y los componentes que guardaste.

La ruta pública de una escuela es `/sitio/CODIGO`. El inicio `/` sólo usa esa escuela cuando la instalación tiene configurado su código público; no se elige una escuela automáticamente. Publicar no modifica esa configuración.

## 10. Permisos y cambios simultáneos

- Sin Editar no debe poder abrirse ni guardarse el diseñador. Sin Publicar no debe poder publicarse, tampoco enviando la petición directamente.
- Abre el mismo borrador en A y B. Guarda en A. Intenta guardar en B: debe avisar que cambió el borrador, conservar la captura de B y no reemplazar lo guardado en A.
- No deben aceptarse enlaces con JavaScript, HTML ejecutable ni imágenes privadas de otra institución.
- Las páginas y revisiones anteriores siguen consultándose con su diseño original.

## Evidencia técnica ya verificada

- 1,110 pruebas Java y 175 JavaScript aprobadas, sin fallos.
- Integración real en PostgreSQL temporal: guardado, publicación, restauración, concurrencia, CSRF, alcance institucional y acceso a imágenes de componentes visibles; se rechaza exponer imágenes de ramas ocultas.
- Navegador con datos ficticios: página completa, selección de texto, logo y menú; arrastre y tamaño, paleta, alineación múltiple, Deshacer/Rehacer, guardar/cancelar/recargar y vista de 390 px sin desbordamiento.
- No se publicó ni sustituyó el borrador de la base operativa durante estas verificaciones. Falta ejecutar y confirmar esta guía con el usuario.

Comparación adicional: 249 objetos visibles medidos en navegador entre el lienzo y el render SSR a 1720, 1280 y 390 px, sin diferencias de posición o tamaño superiores a 0.5 px. Vista previa guardada también comparada a 1720 px; animaciones pausadas únicamente en los datos ficticios de QA.
