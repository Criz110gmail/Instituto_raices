(() => {
    function initialize() {
        const dialog = document.querySelector('[data-session-profile]');
        const trigger = document.querySelector('[data-session-profile-open]');
        if (!dialog || !trigger || dialog.dataset.initialized) return;
        dialog.dataset.initialized = 'true';
        // Mantener el modal fuera de cabeceras/contenedores con estilos de formulario.
        document.body.append(dialog);
        const closeButton = dialog.querySelector('[data-session-profile-close]');
        const status = dialog.querySelector('[data-profile-status]');
        const details = dialog.querySelector('[data-profile-details]');
        const fields = [...dialog.querySelectorAll('[data-profile-field]')];
        let pending;

        function reset() {
            details.hidden = true;
            fields.forEach(field => { field.textContent = ''; });
            status.hidden = false;
            status.textContent = 'Consultando tu información…';
            status.classList.remove('is-error');
        }
        function closed() {
            pending?.abort();
            pending = undefined;
            document.body.classList.remove('session-profile-open');
            trigger.setAttribute('aria-expanded', 'false');
            reset();
            trigger.focus();
        }
        function close() { if (dialog.open) dialog.close(); }
        closeButton.addEventListener('click', close);
        dialog.addEventListener('close', closed);
        dialog.addEventListener('cancel', event => { event.preventDefault(); close(); });
        dialog.addEventListener('click', event => {
            if (event.target !== dialog) return;
            const bounds = dialog.getBoundingClientRect();
            if (event.clientX < bounds.left || event.clientX > bounds.right
                    || event.clientY < bounds.top || event.clientY > bounds.bottom) close();
        });
        trigger.addEventListener('click', async () => {
            if (dialog.open) return;
            reset();
            dialog.showModal();
            document.body.classList.add('session-profile-open');
            trigger.setAttribute('aria-expanded', 'true');
            closeButton.focus();
            const request = new AbortController();
            pending = request;
            try {
                const response = await fetch('/admin/perfil/resumen', {
                    credentials: 'same-origin', cache: 'no-store', signal: request.signal,
                    headers: { Accept: 'application/json' }
                });
                if (!response.ok || response.redirected
                        || !response.headers.get('content-type')?.includes('application/json')) throw new Error('perfil');
                const data = await response.json();
                if (pending !== request || !dialog.open) return;
                fields.forEach(field => { field.textContent = data[field.dataset.profileField] || 'No registrado'; });
                details.hidden = false;
                status.hidden = true;
            } catch (error) {
                if (error.name === 'AbortError' || pending !== request || !dialog.open) return;
                status.textContent = 'No pudimos consultar tu perfil. Cierra esta ventana y vuelve a intentarlo; si tu sesión terminó, inicia sesión nuevamente.';
                status.classList.add('is-error');
            }
        });
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initialize);
    else initialize();
})();
