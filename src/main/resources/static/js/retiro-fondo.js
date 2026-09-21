document.addEventListener('DOMContentLoaded', () => {
    const motivos = document.getElementById('motivoFinancieroId');
    if (!motivos) return;
    Array.from(motivos.options).forEach(option => {
        if (!option.value) return;
        option.hidden = !['EGRESO', 'AMBOS'].includes(option.dataset.naturaleza)
            || ['COBROS_ESCOLARES', 'TRASPASO_INTERNO', 'DEVOLUCION_PAGO'].includes(option.dataset.codigo);
        option.disabled = option.hidden;
    });
    if (motivos.selectedOptions[0]?.disabled) motivos.value = '';
});
