(() => {
    const form = document.querySelector('[data-campus-change]');
    const dialog = document.querySelector('[data-campus-dialog]');
    if (!form || !dialog) return;
    const summary = dialog.querySelector('[data-campus-summary]');
    const confirm = dialog.querySelector('[data-campus-confirm]');
    const cancel = dialog.querySelector('[data-campus-cancel]');
    const error = form.querySelector('[data-campus-error]');
    const preview = form.querySelector('button[type="submit"]');
    const field = name => form.querySelector(`[name="${name}"]`);
    const signature = () => JSON.stringify([field('fecha').value, field('plantelId').value,
        field('conservarAnterior').checked, field('motivo').value]);
    const date = value => value ? value.split('-').reverse().join('/') : 'Sin fecha de fin';
    let request, snapshot, approved = false, submitting = false;
    const close = () => { dialog.close(); confirm.disabled = false; preview.focus(); };
    const invalidate = () => { request?.abort(); snapshot = null; if (dialog.open) close(); };
    form.addEventListener('input', invalidate);
    form.addEventListener('change', invalidate);
    cancel.addEventListener('click', close);
    dialog.addEventListener('cancel', event => { event.preventDefault(); close(); });
    dialog.addEventListener('click', event => { if (event.target === dialog) close(); });
    confirm.addEventListener('click', () => {
        if (confirm.disabled) return;
        if (!snapshot || snapshot !== signature() || !form.reportValidity()) { close(); return; }
        confirm.disabled = true;
        approved = true;
        try { form.requestSubmit(); } finally { approved = false; }
    });
    form.addEventListener('submit', async event => {
        if (submitting) { event.preventDefault(); return; }
        if (approved && snapshot === signature()) { submitting = true; preview.disabled = true; return; }
        event.preventDefault();
        if (!form.reportValidity()) return;
        error.hidden = true;
        if (!field('plantelId').value) { error.textContent = 'Selecciona el nuevo plantel desde la ayuda de búsqueda.'; error.hidden = false; return; }
        if (!field('motivo').value.trim()) { error.textContent = 'Explica el motivo del cambio.'; error.hidden = false; field('motivo').focus(); return; }
        request?.abort(); const current = new AbortController(); request = current;
        const sent = signature(); preview.disabled = true;
        try {
            const query = new URLSearchParams({fecha: field('fecha').value, plantelId: field('plantelId').value,
                conservarAnterior: String(field('conservarAnterior').checked)});
            const response = await fetch(`${form.dataset.previewUrl}?${query}`, {headers: {'Accept': 'application/json'}, signal: current.signal, cache: 'no-store'});
            if (response.redirected && /\/login(?:\?|$)/.test(response.url)) { window.location.assign(response.url); return; }
            const data = await response.json().catch(() => ({mensaje: 'No fue posible consultar el resumen. Revisa tu sesión y vuelve a intentarlo.'}));
            if (current.signal.aborted || request !== current || sent !== signature()) return;
            if (!response.ok || !data.origenId) throw new Error(data.mensaje || 'No fue posible visualizar el cambio. Revisa tu sesión y vuelve a intentarlo.');
            for (const name of ['origenId','versionOrigen','destinoId','versionDestino']) field(name).value = data[name] ?? '';
            summary.replaceChildren();
            const add = (label, value) => { const dt = document.createElement('dt'), dd = document.createElement('dd'); dt.textContent = label; dd.textContent = value; summary.append(dt, dd); };
            add('Principal que será reemplazado', data.origen);
            add('Fin de las asignaciones anteriores', date(data.cierreAnterior));
            add('Nuevo plantel principal', data.destino);
            add('Desde', date(data.fecha));
            add('Fin del nuevo principal', date(data.finNuevoPrincipal));
            add('Plantel anterior', data.conservarAnterior ? (data.finAnteriorAdicional ? `Continuará como adicional hasta el ${date(data.finAnteriorAdicional)}.` : 'Continuará como adicional, sin fecha de fin.') : 'Terminará su asignación; no continuará como adicional.');
            add('Motivo', field('motivo').value.trim());
            snapshot = sent; confirm.disabled = false; dialog.showModal(); cancel.focus();
        } catch (e) {
            if (e.name !== 'AbortError' && request === current) { error.textContent = e.message; error.hidden = false; }
        } finally { if (request === current) preview.disabled = false; }
    });
})();
