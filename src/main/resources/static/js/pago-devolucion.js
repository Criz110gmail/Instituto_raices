function calcularVistaDevolucion(disponible, limite, monto, abonos) {
    const seleccionado = abonos.reduce((s, a) => s + a.monto, 0);
    const maximo = Math.min(limite, disponible + seleccionado);
    let liberar = Math.max(monto - disponible, 0);
    const deudaRecuperada = Math.min(liberar, seleccionado);
    const cargos = new Map();
    for (const abono of abonos) {
        const liberado = Math.min(liberar, abono.monto);
        liberar -= liberado;
        const cargo = cargos.get(abono.cargo) || {
            titulo: abono.titulo, saldo: abono.saldo, liberado: 0
        };
        cargo.liberado += liberado;
        cargos.set(abono.cargo, cargo);
    }
    return {maximo, deudaRecuperada, aplicado: seleccionado - deudaRecuperada,
        cargos: [...cargos.values()]};
}

(() => {
    const form = document.querySelector('#refund-form');
    if (!form) return;
    const modo = form.querySelector('#refund-mode');
    const importe = form.querySelector('#refund-amount');
    const opciones = [...form.querySelectorAll('.refund-applications input[type="checkbox"]')];
    const centavos = valor => Math.round((Number(valor) || 0) * 100);
    const formato = new Intl.NumberFormat('es-MX', {style: 'currency', currency: form.dataset.currency || 'MXN'});
    const dinero = valor => formato.format(valor / 100);

    function actualizar() {
        const abonos = opciones.filter(o => o.checked).map(o => ({
            monto: centavos(o.dataset.amount), cargo: o.dataset.charge,
            saldo: centavos(o.dataset.balance), titulo: o.dataset.title
        }));
        const disponible = centavos(form.dataset.available);
        const limite = centavos(form.dataset.limit);
        const maximo = Math.min(limite, disponible + abonos.reduce((s, a) => s + a.monto, 0));
        const total = modo.value === 'TOTAL';
        importe.readOnly = total;
        importe.setAttribute('aria-readonly', String(total));
        if (total) importe.value = maximo > 0 ? (maximo / 100).toFixed(2) : '';
        importe.max = (maximo / 100).toFixed(2);
        const monto = centavos(importe.value);
        const valido = monto > 0 && monto <= maximo;
        importe.setCustomValidity(valido ? '' : 'Selecciona abonos suficientes e indica un importe mayor a cero dentro del máximo disponible.');
        form.querySelector('#refund-amount-help').textContent = total
            ? 'Importe calculado y protegido. Cambia a devolución parcial para devolver una cantidad menor.'
            : `Captura lo que realmente devolverás. Máximo para esta selección: ${dinero(maximo)}.`;
        const vista = calcularVistaDevolucion(disponible, limite, Math.min(monto, maximo), abonos);
        form.querySelector('#refund-preview-money').textContent = dinero(monto);
        form.querySelector('#refund-preview-applied').textContent = dinero(vista.aplicado);
        form.querySelector('#refund-preview-reopened').textContent = dinero(vista.deudaRecuperada);
        const lista = form.querySelector('#refund-preview-charges');
        lista.replaceChildren();
        if (valido) for (const cargo of vista.cargos) {
            const fila = document.createElement('article');
            const nombre = document.createElement('strong');
            nombre.textContent = cargo.titulo;
            const saldo = document.createElement('b');
            saldo.textContent = `Saldo por pagar después de devolver: ${dinero(cargo.saldo + cargo.liberado)}`;
            fila.append(nombre, saldo);
            lista.append(fila);
        }
        form.querySelector('#refund-preview-warning').textContent = !maximo
            ? 'Selecciona al menos un abono para preparar la devolución.'
            : !valido ? `El importe debe ser mayor a cero y no superar ${dinero(maximo)}.`
                : 'Revisa el importe y el saldo por alumno antes de ejecutar la devolución.';
        form.querySelector('button[type="submit"]').disabled = !valido;
    }
    modo.addEventListener('change', actualizar);
    importe.addEventListener('input', actualizar);
    opciones.forEach(o => o.addEventListener('change', actualizar));
    actualizar();
})();
