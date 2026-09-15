document.addEventListener('DOMContentLoaded', () => {
    const modalidad = document.querySelector('#modalidad');
    if (!modalidad) return;
    const actualizar = () => {
        const porcentaje = modalidad.value === 'PORCENTAJE';
        document.querySelectorAll('[data-beca-porcentaje]').forEach(e => e.hidden = !porcentaje);
        document.querySelectorAll('[data-beca-monto]').forEach(e => e.hidden = porcentaje);
    };
    modalidad.addEventListener('change', actualizar);
    actualizar();
});
