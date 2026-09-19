document.addEventListener('DOMContentLoaded', () => {
    const direccion = document.getElementById('direccion');
    const motivos = document.getElementById('motivoFinancieroId');
    if (!direccion || !motivos) return;
    const filtrar = () => {
        Array.from(motivos.options).forEach(option => {
            if (!option.value) return;
            const naturaleza = option.dataset.naturaleza;
            option.hidden = naturaleza !== 'AMBOS' && naturaleza !== direccion.value;
            option.disabled = option.hidden;
        });
        if (motivos.selectedOptions[0]?.disabled) motivos.value = '';
    };
    direccion.addEventListener('change', filtrar);
    filtrar();
});
