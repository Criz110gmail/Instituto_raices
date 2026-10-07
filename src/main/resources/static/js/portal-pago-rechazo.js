(() => {
    const modal = document.getElementById('motivo-rechazo-modal');
    if (!modal) return;
    const motivo = modal.querySelector('[data-rechazo-motivo]');
    const folio = modal.querySelector('[data-rechazo-folio]');
    const cerrar = modal.querySelector('[data-rechazo-cerrar]');
    let origen;
    document.querySelectorAll('[data-ver-rechazo]').forEach(boton => {
        boton.addEventListener('click', () => {
            origen = boton;
            folio.textContent = boton.dataset.folio || 'Pago reportado';
            motivo.textContent = boton.dataset.motivo?.trim()
                || 'No hay un motivo registrado para este pago. Comunícate con la administración de la escuela para aclararlo.';
            modal.showModal();
            document.body.classList.add('family-payment-modal-open');
            cerrar.focus();
        });
    });
    cerrar.addEventListener('click', () => modal.close());
    modal.addEventListener('click', evento => {
        if (evento.target !== modal) return;
        const rect = modal.getBoundingClientRect();
        if (evento.clientX < rect.left || evento.clientX > rect.right
            || evento.clientY < rect.top || evento.clientY > rect.bottom) modal.close();
    });
    modal.addEventListener('close', () => {
        document.body.classList.remove('family-payment-modal-open');
        origen?.focus({preventScroll: true});
    });
})();
