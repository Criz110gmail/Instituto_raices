# Colocar objetos con precisión en el diseñador

Implementado el 11/11/2026. Pendiente tu aceptación manual.

La cuadrícula y las líneas de ayuda sirven para acomodar el diseño. No se guardan
como parte de la página y no aparecen en la publicación.

## Ejemplo: colocar un icono junto a «Tu espacio, aquí»

1. Entra a **Diseño web** y recarga.
2. En Estructura selecciona **Accesos a portales** y agrega un objeto escolar, o selecciona el que ya colocaste.
3. La barra de precisión permanece visible al desplazarte. Arriba del lienzo deja marcadas **Cuadrícula** y **Ajustar**. El Paso inicial es **16 px**.
4. Haz clic sobre el icono para seleccionarlo. Después arrástralo desde su cuerpo o desde el tirador con su nombre.
5. Acércalo al título: las líneas rosas indican coincidencias entre bordes o centros. Al estar cerca, el objeto se ajusta a esa línea.
6. Mientras lo mueves verás **X/Y**. Al soltarlo, las coordenadas permanecen junto al tamaño del objeto.
7. Para moverlo más libremente, mantén **Alt** mientras arrastras. No se ajustará a la cuadrícula ni a otros objetos.
8. Para conservar una sola dirección, mantén **Mayús** durante el arrastre. Si estás seleccionando varios objetos, Mayús + clic conserva esa función.
9. Si necesitas más detalle, cambia **Paso** a **4 px** o aumenta el zoom a **100 %**. El editor mantiene enfocado el objeto seleccionado al cambiar la escala.
10. Con el objeto seleccionado y fuera de un campo de texto, las flechas lo mueven **1 px**; Mayús + flecha lo mueve **10 px**. Estos movimientos finos no se redondean a la cuadrícula.

Las guías tienen prioridad sobre la cuadrícula cuando estás cerca de una alineación.
Por eso una coordenada puede no ser múltiplo del Paso: se está alineando con otro objeto.

## Comprobaciones

- La cuadrícula debe verse también sobre fondos blancos, fotografías y tarjetas.
- Al soltar el icono debe continuar seleccionado, sin saltar a otra ubicación.
- Un clic sin arrastrar no cambia sus coordenadas.
- Seleccionar texto y hacer doble clic conserva la edición de su contenido. Para mover un texto usa el tirador con su nombre.
- **Vista sin guías** oculta cuadrícula, selección y controles. **Volver a diseñar** los recupera.
- Guarda el borrador, recarga y vuelve a seleccionar el objeto: debe conservar X/Y.
- Compara con **Vista previa guardada**, usando el mismo ancho de página. No necesitas publicar para probarlo.
- No se modifica la página pública hasta que decidas Publicar.

X/Y corresponden a la posición configurada respecto a su contenedor. En los objetos originales de la plantilla pueden representar un desplazamiento desde su posición original. Puedes mover ambos visualmente sin cambiar el acomodo de la sección.

Verificación técnica: 1,112 pruebas Java y 184 JavaScript aprobadas. Navegador con datos ficticios: cuadrícula sobre fondos, arrastre directo al 50 % y 100 %, selección conservada, coordenadas guardadas/recargadas, edición de texto por doble clic, flechas de 1/10 px, paso de 4 px, zoom enfocado y vista sin guías. Barra móvil revisada a 390 px sin desbordamiento.

## Acercar y alejar con los botones

1. Selecciona un objeto y usa **+** en la barra superior del lienzo para acercarte.
2. Usa **−** para alejarte. El selector indica el porcentaje, desde 25 % hasta 300 %.
3. También puedes elegir 150 % o 200 % directamente en el selector.
4. El objeto seleccionado debe seguir visible, con las mismas coordenadas X/Y.
5. Elige **Ajustar al lienzo** para volver a ver la página encajada en el espacio disponible.
6. Cambiar el zoom no debe marcar cambios sin guardar ni modificar la publicación.

Verificación del zoom:186 pruebas JavaScript aprobadas, escritorio y móvil de 390 px.
