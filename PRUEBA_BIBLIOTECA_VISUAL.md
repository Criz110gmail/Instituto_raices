# Prueba: fotografías y recursos dentro del diseñador

Actualizado: 11/11/2026. Implementado y verificado técnicamente; falta aceptación manual del usuario.

Ahora trabajas sin salir del lienzo. Recursos abre una biblioteca visual: puedes crear un álbum, arrastrar varias fotos y colocarlo en tu página. **Usar este álbum** lo agrega al diseño; **Guardar borrador** conserva el trabajo; **Publicar** lo hace visible.

## 1. Abrir la biblioteca

1. Entra a **Diseño web** y recarga la pantalla.
2. A la derecha abre **Recursos → Álbumes y galerías**.
3. Debe aparecer una ventana titulada **Historias en imágenes**, con tus álbumes y miniaturas. No debe abrir un módulo con tabla, filtros o Excel.
4. En celular primero abre **＋ Componentes / propiedades**, después Recursos.

## 2. Crear un álbum y subir varias fotos

1. Pulsa **＋ Nuevo álbum**.
2. Cambia Nombre del álbum a `Festival de primavera`.
3. Si quieres, abre **Añadir una descripción · opcional** y escribe `Momentos para compartir con nuestra comunidad.`
4. Arrastra tres fotografías PNG/JPEG a la zona de carga. También puedes hacer clic ahí y seleccionar varias en el explorador.
5. Espera el resultado de la carga. Deben aparecer las tres miniaturas; cada archivo muestra su resultado.
6. Cambia sus descripciones, por ejemplo: `Bienvenida al festival`, `Actividades en equipo` y `Cierre de la jornada`.
7. Arrastra la tercera tarjeta antes de la primera. Debe cambiar el orden. También puedes utilizar las flechas debajo de cada foto.

Cada imagen admite hasta 5 MB. Se conservan los límites existentes: 100 imágenes en la biblioteca, 20 álbumes y 100 fotos por álbum. Si un archivo falla, las fotos cargadas correctamente permanecen disponibles y el problema se muestra junto al archivo.

## 3. Usar el álbum en tu página

1. Pulsa **Usar este álbum**.
2. La ventana se cierra y la galería queda seleccionada en el lienzo. En la página escolar original ocupa el lugar del aviso de galería vacía, conservando el encabezado de la sección.
3. Si abriste la biblioteca desde una galería que ya existía, se actualiza esa galería.
4. También puedes agregar **Galería** desde Componentes: abre la biblioteca para elegir o crear su álbum.
5. El resto de la página debe conservarse.

## 4. Probar el carrusel

1. Con la galería seleccionada, abre **Propiedades → Contenido → Diseño de galería**.
2. Pulsa la tarjeta **Carrusel**.
3. Usa las flechas y los puntos para recorrer las fotos. El contador debe indicar 1 / 3, 2 / 3 y 3 / 3, con las descripciones y el orden que elegiste.
4. Si quieres, activa **Pasar fotografías automáticamente**. Se muestra el tiempo entre fotos, de 3 a 15 segundos.
5. Mientras diseñas, el pase automático permanece detenido para facilitar la selección de objetos. Compruébalo después de guardar en **Vista previa guardada**: debe avanzar y permitir Pausar/Reproducir. Se detiene al interactuar con el carrusel o cuando queda fuera de la vista.
6. En Celular revisa que la foto se ajuste al ancho y que puedas recorrerla con las flechas o deslizando horizontalmente.
7. Regresa a Cuadrícula y comprueba la otra presentación sin volver a subir fotos.

## 5. Cancelar una edición sin perder el álbum

Haz este paso después de haber pulsado Usar este álbum en el punto 3.

1. Selecciona la galería y pulsa **Elegir o editar fotografías**. Debe abrir el álbum que utiliza ese objeto.
2. Cambia el nombre a `ESTE CAMBIO SE CANCELA` y reordena las fotos.
3. Pulsa **Cancelar**.
4. Abre otra vez: debe conservar el nombre Festival de primavera y el orden anterior a esa edición.
5. Los archivos que ya subiste siguen disponibles en Imágenes, aunque canceles los cambios del álbum. No necesitas volver a subirlos.

## 6. Reutilizar, retirar y recuperar fotografías

1. Dentro del álbum pulsa **Elegir de biblioteca**.
2. Selecciona una o varias miniaturas y pulsa **Agregar fotos al álbum**. Deben agregarse sin subir archivos otra vez. Una imagen que ya está activa no se repite.
3. Prueba el botón × de una tarjeta. Primero Cancelar: la foto continúa ahí. Repite y confirma **Retirar fotografía**.
4. Abre **Retiradas · 1**. La foto debe estar disponible para recuperarla.
5. Pulsa **Recuperar fotografía** y vuelve con **Ver fotos del álbum**.
6. **Aplicar al diseño** incorpora esta edición al lienzo. Las publicaciones anteriores no cambian.

## 7. Guardar y revisar

1. Pulsa **Guardar borrador**. Primero prueba Cancelar; después repite y confirma.
2. Recarga el diseñador y comprueba que el álbum, sus descripciones, el orden y la presentación permanecen.
3. Abre **Vista previa guardada**, con el mismo ancho de ventana que usaste para diseñar. Revisa el carrusel y el resto de la página.
4. La página publicada conserva su revisión anterior hasta que publiques expresamente la nueva.
5. Si deseas hacerla visible, pulsa Publicar y revisa la ruta que indica el modal antes de confirmar. La prueba técnica no publica por ti.

## 8. Los demás recursos también se editan aquí

- **Imágenes:** selecciona el logo o una imagen del lienzo y pulsa Elegir o subir imagen. Abre la biblioteca con miniaturas; elige una y pulsa Usar imagen. La posición del objeto se conserva.
- **Ubicaciones:** Recursos → Ubicaciones abre una ventana. Puedes elegir un plantel activo propio y escribir su nombre público, dirección y horario. Aplicar al diseño actualiza los datos; Agregar al diseño coloca el componente de ubicaciones en la página.
- **Contacto:** Recursos → Contacto y WhatsApp abre los ajustes de correo, teléfono, número internacional, mensaje y privacidad. No abre otro módulo ni envía mensajes automáticamente.
- **Publicaciones:** Estructura → Versiones → Ver publicaciones abre las versiones publicadas en la misma biblioteca, con fecha y acceso para consultarlas.

## Verificaciones técnicas realizadas

- 1,111 pruebas Java y 177 JavaScript aprobadas.
- Integración PostgreSQL temporal: guardado y publicación del carrusel, fotografías propias, aislamiento de planteles, recursos protegidos y rechazo de fotos duplicadas sin crear otra revisión.
- Navegador con datos ficticios: carga simultánea de tres archivos, orden por arrastre, reutilización, retiro y recuperación, Cancelar, selección de Carrusel, flechas, guardado/recarga, reproducción automática y pausa.
- Revisión visual en escritorio y celular de 390 px, temas claro y oscuro. Contacto y Ubicaciones dentro del editor.
- Se conserva V174: no requiere una migración nueva. No se cambiaron los permisos ni se publicó o sustituyó el borrador operativo durante la prueba técnica.

## Nuevos diseños y vista ampliada

Consulta `PRUEBA_GALERIAS_DISENOS.md` para probar los siete diseños, efectos y visor de fotografías.
