(() => {
    const button = document.querySelector('[data-copy-personal]');
    const input = document.querySelector('#enlace-personal');
    const status = document.querySelector('[data-copy-personal-status]');
    if (!button || !input || !status) return;
    button.addEventListener('click', async () => {
        try {
            if (navigator.clipboard && window.isSecureContext) {
                await navigator.clipboard.writeText(input.value);
            } else {
                input.select();
                if (!document.execCommand('copy')) throw new Error('No se pudo copiar');
            }
            status.textContent = 'Enlace copiado.';
            button.textContent = 'Copiado';
        } catch {
            input.focus();
            input.select();
            status.textContent = 'Selecciona y copia el enlace manualmente.';
        }
    });
})();
