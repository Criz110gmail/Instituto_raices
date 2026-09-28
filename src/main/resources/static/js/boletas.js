(() => {
    const formulario = document.getElementById('boleta-filtros');
    const institucion = document.getElementById('institucionId');
    const ciclo = document.getElementById('cicloId');
    const plantel = document.getElementById('plantelId');
    if (!formulario || !institucion || !ciclo || !plantel) return;

    const filtrarPlanteles = () => {
        [...plantel.options].forEach(opcion => {
            if (!opcion.value) return;
            opcion.hidden = opcion.dataset.institucion !== institucion.value;
        });
        if (plantel.selectedOptions[0]?.hidden) plantel.value = '';
    };
    filtrarPlanteles();
    institucion.addEventListener('change', () => {
        ciclo.disabled = true;
        plantel.value = '';
        formulario.submit();
    });
})();
