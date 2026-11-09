(() => {
    const iniciar = () => {
        const tipo = document.querySelector('[data-document-type]');
        const archivo = document.querySelector('[data-document-file]');
        const actualizarTipo = () => {
            if (!tipo || !archivo) return;
            archivo.accept = {PDF: '.pdf,application/pdf', IMAGEN: '.jpg,.jpeg,.png,.heic,.heif,image/jpeg,image/png,image/heic,image/heif', XML: '.xml,application/xml,text/xml'}[tipo.value] || '';
        };
        tipo?.addEventListener('change', () => { archivo.value = ''; actualizarTipo(); }); actualizarTipo();
        const clasificacion = document.querySelector('[data-document-classification]');
        const motivo = document.querySelector('[data-document-reason]');
        const requerido = () => { if (motivo) motivo.required = clasificacion?.value === 'SIN_COMPROBANTE'; };
        clasificacion?.addEventListener('change', requerido); requerido();
        const filtro = document.querySelector('[data-document-filter]');
        const clave = 'compra-documentos-scroll:' + location.pathname;
        const guardar = () => { try { sessionStorage.setItem(clave, String(window.scrollY)); } catch (_) {} };
        filtro?.addEventListener('submit', guardar);
        document.querySelector('[data-document-clear]')?.addEventListener('click', guardar);
        document.querySelectorAll('.purchase-documents .pagination a').forEach(a => a.addEventListener('click', guardar));
        if (filtro) {
            try {
                const posicion = sessionStorage.getItem(clave); sessionStorage.removeItem(clave);
                if (posicion !== null && Number.isFinite(Number(posicion))) requestAnimationFrame(() => requestAnimationFrame(() => window.scrollTo(0, Number(posicion))));
            } catch (_) {}
        }
    };
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', iniciar); else iniciar();
})();
