(() => {
    const periodo = document.getElementById('periodo');
    const mes = document.getElementById('mes-campo');
    if (!periodo || !mes) return;
    const actualizar = () => { mes.hidden = periodo.value === 'ANUAL'; };
    periodo.addEventListener('change', actualizar);
    actualizar();
})();
