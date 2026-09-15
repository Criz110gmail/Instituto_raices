(() => {
    const institucion = document.getElementById('institucionId');
    const plantel = document.getElementById('plantelId');
    if (!institucion || !plantel) return;

    const actualizarPlanteles = () => {
        const actual = plantel.value;
        let conservaActual = !actual;
        [...plantel.options].forEach(opcion => {
            if (!opcion.value) return;
            const visible = opcion.dataset.institucion === institucion.value;
            opcion.hidden = !visible;
            opcion.disabled = !visible;
            if (visible && opcion.value === actual) conservaActual = true;
        });
        if (!conservaActual) plantel.value = '';
    };
    actualizarPlanteles();
    institucion.addEventListener('change', actualizarPlanteles);
})();
