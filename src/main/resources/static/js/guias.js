(() => {
    function abrirPaso() {
        if (!/^#paso-\d+$/.test(location.hash)) return;
        const paso = document.getElementById(location.hash.slice(1));
        if (!paso?.matches('.guide-step')) return;
        paso.open = true;
        paso.scrollIntoView({block: 'start', behavior: 'auto'});
    }
    document.addEventListener('click', evento => {
        const enlace = evento.target.closest('a[href^="#paso-"]');
        if (!enlace) return;
        const paso = document.getElementById(enlace.getAttribute('href').slice(1));
        if (paso?.matches('.guide-step')) paso.open = true;
    });
    window.addEventListener('hashchange', abrirPaso);
    abrirPaso();
})();
