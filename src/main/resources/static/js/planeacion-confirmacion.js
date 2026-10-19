(() => {
    if (typeof document === 'undefined') return;
    const dialog = document.querySelector('[data-planeacion-confirm-dialog]');
    if (!dialog) return;
    const actions = {
        guardar: ['¿Guardar la planeación?', 'Se conservará esta captura como una planeación editable.', 'Guardar no la envía a revisión ni la publica. Podrás seguir editándola.', 'Confirmar guardado'],
        descartar: ['¿Descartar esta planeación?', 'La planeación dejará de estar disponible para continuar su captura.', 'Se conservarán el motivo y el historial. No se borrarán sus antecedentes.', 'Confirmar descarte'],
        revision: ['¿Iniciar la revisión?', 'La planeación pasará de Enviada a En revisión.', 'El maestro no podrá editarla mientras se encuentre en revisión.', 'Confirmar revisión'],
        publicar: ['¿Publicar esta planeación?', 'Se publicará el contenido revisado y se conservará una nueva versión histórica con su PDF.', 'La edición seguirá bloqueada. Para cambiarla después será necesaria una reapertura administrativa con motivo.', 'Confirmar publicación'],
        reabrir: ['¿Reabrir esta planeación?', 'El maestro podrá volver a editar y deberá enviar nuevamente su planeación para revisión.', 'Las versiones publicadas anteriores se conservarán; el motivo quedará registrado en el historial.', 'Confirmar reapertura'],
        ajustes: ['¿Solicitar ajustes al maestro?', 'La planeación pasará a Requiere ajustes y el maestro verá las correcciones que capturaste.', 'Podrá editarla, guardar las correcciones y volver a enviarla a revisión.', 'Confirmar solicitud']
    };
    const accept = dialog.querySelector('[data-pc-accept]');
    let pending = null, submitter = null, trigger = null, submitted = false;
    const text = (selector, value) => { dialog.querySelector(selector).textContent = value; };
    const formatDate = value => /^\d{4}-\d{2}-\d{2}$/.test(value) ? value.split('-').reverse().join('/') : value;
    const valid = form => {
        const reason = form.querySelector('[name=motivo]');
        if (reason) {
            reason.setCustomValidity(reason.required && !reason.value.trim() ? 'Escribe el motivo o las correcciones solicitadas.' : '');
        }
        return form.noValidate || form.reportValidity();
    };
    document.querySelectorAll('form[data-planeacion-confirm]').forEach(form => {
        form.querySelector('[name=motivo]')?.addEventListener('input', event => event.target.setCustomValidity(''));
        form.addEventListener('submit', event => {
            if (event.defaultPrevented) return;
            if (submitted) { event.preventDefault(); return; }
            if (form.dataset.planConfirmationReady === 'true') { submitted = true; return; }
            event.preventDefault();
            const kind = form.dataset.planeacionConfirm, config = actions[kind];
            if (!config || !valid(form)) return;
            pending = form; submitter = event.submitter || null; trigger = document.activeElement;
            text('[data-pc-title]', config[0]); text('[data-pc-description]', config[1]);
            text('[data-pc-note]', config[2]); accept.textContent = config[3]; accept.disabled = false;
            const reason = form.querySelector('[name=motivo]')?.value.trim() || '';
            dialog.querySelector('[data-pc-reason-row]').hidden = !reason; text('[data-pc-reason]', reason);
            if (kind === 'guardar') {
                const group = form.querySelector('[data-plan-group]');
                text('[data-pc-group]', group?.selectedOptions[0]?.textContent.trim() || 'Sin grupo');
                const start = form.querySelector('[name=fechaInicio]').value, end = form.querySelector('[name=fechaFin]').value;
                text('[data-pc-week]', `${formatDate(start)} al ${formatDate(end)}`);
                const subjects = [...form.querySelectorAll('[data-plan-subjects] input:checked')].map(input => {
                    const label = input.closest('label').querySelector('span');
                    return [...label.childNodes].filter(node => node.nodeType === 3).map(node => node.textContent).join('').trim();
                });
                text('[data-pc-subjects]', subjects.join(', '));
            }
            dialog.showModal();
        });
    });
    dialog.querySelector('[data-pc-cancel]').addEventListener('click', () => dialog.close());
    dialog.addEventListener('close', () => { pending = null; submitter = null; trigger?.focus(); });
    dialog.addEventListener('click', event => {
        const rect = dialog.getBoundingClientRect();
        if (event.target === dialog && (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom)) dialog.close();
    });
    accept.addEventListener('click', () => {
        if (!pending || accept.disabled || submitted) return;
        const form = pending, button = submitter;
        if (!valid(form)) { dialog.close(); return; }
        accept.disabled = true; form.dataset.planConfirmationReady = 'true'; dialog.close();
        try { if (button) form.requestSubmit(button); else form.requestSubmit(); }
        finally { delete form.dataset.planConfirmationReady; if (!submitted) accept.disabled = false; }
    });
})();
