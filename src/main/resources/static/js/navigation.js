(() => {
    const navegacion = document.querySelector('.sidebar nav');
    const moduloActivo = navegacion?.querySelector('a.active');
    if (!navegacion || !moduloActivo) return;

    const rectNavegacion = navegacion.getBoundingClientRect();
    const rectActivo = moduloActivo.getBoundingClientRect();

    if (rectActivo.top < rectNavegacion.top || rectActivo.bottom > rectNavegacion.bottom) {
        const desplazamiento = rectActivo.top - rectNavegacion.top;
        navegacion.scrollTop = Math.max(0, navegacion.scrollTop + desplazamiento
                - (navegacion.clientHeight - rectActivo.height) / 2);
    }
})();
