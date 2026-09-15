document.addEventListener('DOMContentLoaded', () => {
    const institucion = document.querySelector('#institucion');
    const plantel = document.querySelector('#plantel');
    const tipo = document.querySelector('#tipo');

    const filtrarPlanteles = () => {
        if (!institucion || !plantel) return;
        const seleccion = institucion.value;
        Array.from(plantel.options).forEach((opcion, indice) => {
            if (indice === 0) return;
            opcion.hidden = Boolean(seleccion) && opcion.dataset.institucion !== seleccion;
        });
        if (plantel.selectedOptions[0]?.hidden) plantel.value = '';
    };

    const mostrarDatosBancarios = () => {
        const esCaja = tipo?.value === 'CAJA';
        document.querySelectorAll('[data-datos-bancarios]').forEach(elemento => {
            elemento.hidden = esCaja;
        });
    };

    institucion?.addEventListener('change', filtrarPlanteles);
    tipo?.addEventListener('change', mostrarDatosBancarios);
    filtrarPlanteles();
    mostrarDatosBancarios();
});
