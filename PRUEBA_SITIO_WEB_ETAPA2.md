> Recorrido parcial histórico. Para probar todas las funciones usa
> [PRUEBA_SITIO_WEB_COMPLETO.md](PRUEBA_SITIO_WEB_COMPLETO.md), que incluye galería,
> ubicaciones, contacto y WhatsApp implementados en V173.

# Editor y publicación de la página escolar — etapa 2

## Qué hace

Cada escuela guarda su identidad, colores, logotipo, portada y textos en su propia
base/configuración. Guardar crea un borrador con historial. Publicar es una acción
separada: las familias ven la última publicación y no los cambios que sigues preparando.
No modifica el diseño de los módulos administrativos ni registra dinero.

## Entrada y permisos

1. Como administrador de roles, habilita **Diseño web - Editar** en el rol que usará
   el editor. Para publicar, agrega también **Diseño web - Publicar**.
2. La cuenta debe ser administrativa, identificable y con alcance **Toda la institución**.
   Una cuenta limitada a un plantel no administra el sitio de toda la escuela.
3. Cierra y vuelve a iniciar sesión para actualizar permisos.
4. Abre **Diseño web** desde el menú administrativo. Ruta: `/admin/diseno-web`.

## 1. Preparar el borrador

1. Pulsa **Editar identidad y contenido**.
2. En **Identidad**, revisa el nombre público y captura el eslogan autorizado.
3. Usa el selector de color o el código hexadecimal. Ejemplo principal `#2b5031`
   y acento `#ffc83e`. Un principal demasiado claro o un acento demasiado oscuro
   se rechazan porque impedirían leer los textos.
4. En **Contenido**, escribe bienvenida, quiénes somos (misión/visión/valores),
   oferta, talleres e información de inscripciones. Todos los textos son obligatorios.
5. Pulsa **Revisar y guardar borrador**. Prueba primero Cancelar y después confirma.
6. El borrador debe quedar guardado. Su revisión aparece en Historial.
7. Intenta borrar un campo obligatorio: se identifica en rojo y se abre su sección.
   Un error de servidor conserva los datos capturados.

## 2. Logotipo y portada

1. Guarda el borrador antes de salir del formulario.
2. En **Imágenes**, pulsa **Cargar imagen**.
3. Selecciona PNG o JPEG autorizado para publicación (hasta 5 MB y 25 megapíxeles).
4. Revisa y confirma. Cargar no publica la imagen.
5. De vuelta al editor, abre **Imágenes**, selecciona el logotipo y/o portada.
6. Si hay portada, escribe su descripción para lectores de pantalla.
7. Guarda el borrador. Abre **Vista previa guardada** y verifica imagen y descripción.
   Puedes elegir Sin imagen para quitarla de una nueva versión sin borrar el historial.

Los archivos son copias específicas del sitio y no se toman del expediente privado.
Se optimizan a PNG de hasta 2000 px y se descartan metadatos originales. Máximo
100 imágenes por institución en esta entrega.

## 3. Revisar y publicar

1. Regresa a Diseño web y abre **Ver borrador**.
2. Revisa textos, colores, imágenes y menú tanto en computadora como en pantalla angosta.
3. En el resumen, pulsa **Revisar y publicar versión**.
4. Prueba Cancelar: la publicación anterior sigue igual.
5. Repite y confirma. La página pública se consulta en `/sitio/CODIGO_INSTITUCION`;
   utiliza el enlace **Abrir página pública** para evitar errores al escribirlo.
6. Edita y guarda otra frase sin publicar. La vista previa cambia, la página pública no.
7. Publica de nuevo para actualizarla. Las versiones anteriores siguen en Historial.
8. Intenta publicar dos veces la misma revisión: el sistema lo impide.

## 4. Dos pestañas y permisos

1. Abre Editar en las pestañas A y B con el mismo borrador.
2. Cambia y guarda en A. En B, cambia algo y confirma guardar.
3. B debe avisar que el borrador cambió y conservar tu captura sin sobrescribir A.
4. Un usuario con Editar pero sin Publicar puede guardar/ver borrador, pero no publicar.
5. Un usuario sin Editar no entra ni pegando la dirección.
6. Otro usuario institucional no ve imágenes ni revisiones privadas de tu institución.
7. Comprueba claro/oscuro, teclado y botones del modal Cancelar/Confirmar.

## Página principal de una instalación

Cada instalación puede definir `APP_SITIO_CODIGO` con el código exacto de su institución.
Al recrear la aplicación, `/` muestra la última publicación de ese código. Primero
publica y comprueba `/sitio/CODIGO`. Con código vacío se conserva la portada base;
no se elige automáticamente la primera institución. Este ajuste es de despliegue,
no cambia código ni mezcla datos entre clientes. No publicar `.env` en Git.

## Alcance y evidencia

Esta entrega cubre editor, textos, logotipo/portada, colores, borrador, revisiones y
publicación. Galería, WhatsApp y ubicaciones administrables corresponden a la etapa 3.
La apariencia del sistema administrativo se configura en una entrega posterior.

Pruebas técnicas: compilación y suite general; integración real en PostgreSQL aislado
con instituciones ficticias, validación, CSRF, imágenes, revisiones, aislamiento,
dos guardados concurrentes, publicación y render Thymeleaf real. Revisión visual
con pantallas renderizadas en escritorio y 390 px, temas claro y oscuro. La aceptación
manual del propietario sigue pendiente.
