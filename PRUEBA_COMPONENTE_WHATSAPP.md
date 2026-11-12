# Botón WhatsApp en el diseñador

## Qué hace

Agrega un botón donde tú quieras en la página. Al pulsarlo se abre WhatsApp con el número y mensaje que configuraste; la persona puede revisar el mensaje antes de enviarlo. Cada botón tiene su propia configuración. El contacto flotante que ya existía se administra por separado.

## 1. Agregar y configurar

1. Recarga **Diseño web** después de actualizar la aplicación.
2. Selecciona la sección donde quieres ponerlo.
3. Abre **Componentes → Contenido → WhatsApp**. Arrástralo a la página o pulsa el componente para agregarlo.
4. En **Propiedades → Contenido**, captura:
   - **Texto del botón:** `Habla con nuestra escuela`.
   - **Número de WhatsApp:** tu número real, con código de país. En México escribe `+52` seguido de los diez dígitos. El ejemplo `+52 33 1234 5678` es ficticio: no lo publiques como contacto de tu escuela.
   - **Mensaje inicial:** `Hola, quisiera información de inscripciones y horarios.`
   - **Diseño del botón:** **Icono y texto** o **Sólo icono circular**.
5. Revisa que aparezca **Destino: +...** con el número correcto.
6. Usa el tirador del objeto para moverlo, la cuadrícula para alinearlo y los controles de tamaño, color, fondo y bordes para ajustar su apariencia.
7. Si necesitas otro contacto, copia y pega el botón y cambia su número o mensaje. Cada copia debe conservar su propia configuración.

## 2. Revisar antes de publicar

1. Cambia entre ambos diseños. El scroll y el selector deben quedarse en su lugar.
2. Abre **Vista sin guías**. Comprueba el enlace del botón: debe usar `https://wa.me/` con tu número internacional.
3. Con tu propio número, pulsa el botón y revisa que WhatsApp muestre el destinatario y mensaje correctos. El diseñador no envía mensajes automáticamente.
4. Revisa la vista de celular: el botón debe caber en su contenedor y el texto puede ocupar más de una línea.
5. Prueba dejar el número vacío en un botón visible: puedes conservar un borrador para terminar después, pero no publicar ese botón sin número.
6. Números con letras, enlaces, código inicial cero o longitud inválida deben mostrar error. No se debe habilitar un destino arbitrario.

## 3. Guardar y publicar

1. Pulsa **Guardar borrador**. Primero cancela la confirmación y comprueba que sigues editando; después guarda y confirma.
2. Recarga y revisa que conserva texto, número, mensaje, diseño y posición.
3. Publica cuando esté listo y confirma.
4. Abre la página pública y comprueba nuevamente el botón, tanto en computadora como en celular.

## Verificación técnica realizada — 12/11/2026

1120 pruebas Java y194 JavaScript aprobadas. Integración PostgreSQL temporal: cuatro pruebas aprobadas, incluyendo guardado/publicación y conservación del mensaje. Navegador: arrastre real de paleta a lienzo, captura, cambio de diseño sin perder scroll, Cancelar/Guardar, nueva carga con configuración conservada, enlace codificado y presentación móvil390px. Se usaron datos ficticios; no se enviaron mensajes ni se modificó la publicación operativa.

El formato Click to Chat se revisó en la [ayuda oficial de WhatsApp](https://faq.whatsapp.com/5913398998672934).
