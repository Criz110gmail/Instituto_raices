(() => {
    const form = document.querySelector('[data-update-review]');
    const dialog = document.getElementById('update-confirm-dialog');
    if (!form || !dialog || typeof dialog.showModal !== 'function') return;
    const answer = form.querySelector('[name="respuesta"]');
    const accept = dialog.querySelector('[data-confirm-accept]');
    let button = null, approved = false, submitting = false;
    form.addEventListener('submit', event => {
        if (approved) { if (submitting) event.preventDefault(); else submitting = true; return; }
        event.preventDefault();
        const chosen = event.submitter;
        if (!chosen?.dataset.reviewDecision || !answer.value.trim() || !form.reportValidity()) {
            if (!answer.value.trim()) { answer.setCustomValidity('Escribe una respuesta para la familia.'); answer.reportValidity(); }
            return;
        }
        button = chosen;
        const approve = chosen.dataset.reviewDecision === 'aprobar';
        dialog.querySelector('[data-confirm-title]').textContent = approve ? '¿Aprobar esta propuesta?' : '¿Rechazar esta propuesta?';
        dialog.querySelector('[data-confirm-description]').textContent = approve ? 'La información propuesta se incorporará al expediente oficial del alumno.' : 'La propuesta se rechazará y la ficha oficial conservará sus datos actuales.';
        dialog.querySelector('[data-confirm-response]').textContent = answer.value.trim();
        accept.textContent = approve ? 'Confirmar aprobación' : 'Confirmar rechazo';
        dialog.showModal();
    });
    answer.addEventListener('input', () => answer.setCustomValidity(''));
    dialog.querySelector('[data-confirm-cancel]').addEventListener('click', () => dialog.close());
    dialog.addEventListener('close', () => { if (!approved) button?.focus(); });
    accept.addEventListener('click', () => {
        if (!button || submitting) return;
        // Fijar el destino del formulario conserva la acción incluso si otro
        // componente reenvía el formulario sin el botón que inició el envío.
        form.action = button.formAction;
        approved = true;
        accept.disabled = true;
        dialog.close();
        form.requestSubmit(button);
        if (!submitting) { approved = false; accept.disabled = false; }
    });
})();
