(() => {
    const form = document.querySelector('form.entity-form');
    if (!form) return;

    const modos = [...form.querySelectorAll('input[name="modoPeriodo"]')];
    const paneles = [...form.querySelectorAll('[data-period-panel]')];
    const mes = form.querySelector('#mesPeriodo');
    const fecha = form.querySelector('#fechaEspecifica');
    const inicio = form.querySelector('#periodoCobroInicio');
    const fin = form.querySelector('#periodoCobroFin');
    const vencimiento = form.querySelector('#fechaVencimiento');
    const fechaRegistro = form.querySelector('#fechaEmision');
    const cambiarFechaRegistro = form.querySelector('input[name="modificarFechaRegistro"]');
    const opcionesFechaRegistro = form.querySelector('[data-registration-date-options]');
    const motivoFechaRegistro = form.querySelector('#motivoFechaRegistroDiferente');
    const descripcion = form.querySelector('#descripcion');
    const titulo = form.querySelector('[data-period-summary-title]');
    const cobertura = form.querySelector('[data-period-summary-coverage]');
    const limite = form.querySelector('[data-period-summary-due]');
    const registro = form.querySelector('[data-period-summary-registration]');
    const formato = new Intl.DateTimeFormat('es-MX', {day: 'numeric', month: 'long', year: 'numeric'});
    const formatoMes = new Intl.DateTimeFormat('es-MX', {month: 'long', year: 'numeric'});

    const fechaLocal = valor => {
        if (!valor) return null;
        const partes = valor.split('-').map(Number);
        return partes.length === 3 ? new Date(partes[0], partes[1] - 1, partes[2]) : null;
    };
    const mesLocal = valor => {
        if (!valor) return null;
        const partes = valor.split('-').map(Number);
        return partes.length === 2 ? new Date(partes[0], partes[1] - 1, 1) : null;
    };
    const capitalizar = texto => texto ? texto.charAt(0).toUpperCase() + texto.slice(1) : texto;

    const actualizarResumen = () => {
        const modo = modos.find(item => item.checked)?.value;
        const concepto = descripcion?.value.trim();
        titulo.textContent = concepto || (modo === 'MES_COMPLETO' ? 'Cargo mensual' : 'Cargo individual');
        if (modo === 'MES_COMPLETO') {
            const seleccionado = mesLocal(mes?.value);
            if (seleccionado) {
                const ultimo = new Date(seleccionado.getFullYear(), seleccionado.getMonth() + 1, 0);
                cobertura.textContent = `Cubre del 1 al ${ultimo.getDate()} de ${formatoMes.format(seleccionado)}.`;
            } else cobertura.textContent = 'Selecciona el mes que se está cobrando.';
        } else if (modo === 'FECHA_ESPECIFICA') {
            const seleccionada = fechaLocal(fecha?.value);
            cobertura.textContent = seleccionada
                ? `Corresponde al ${formato.format(seleccionada)}.`
                : 'Selecciona la fecha a la que corresponde el cobro.';
        } else {
            const desde = fechaLocal(inicio?.value);
            const hasta = fechaLocal(fin?.value);
            cobertura.textContent = desde && hasta
                ? `Cubre del ${formato.format(desde)} al ${formato.format(hasta)}.`
                : 'Completa el inicio y el fin del periodo que se está cobrando.';
        }
        const fechaLimite = fechaLocal(vencimiento?.value);
        const registrada = fechaLocal(fechaRegistro?.value);
        registro.textContent = registrada
            ? `Quedará registrado el ${formato.format(registrada)}${cambiarFechaRegistro?.checked ? ' con motivo documentado.' : '.'}`
            : 'Falta definir la fecha de registro del cargo.';
        limite.textContent = fechaLimite
            ? `Debe pagarse a más tardar el ${formato.format(fechaLimite)}.`
            : 'Falta elegir la fecha límite para pagar.';
        titulo.textContent = capitalizar(titulo.textContent);
    };

    const actualizarFechaRegistro = () => {
        if (!cambiarFechaRegistro || !fechaRegistro || !opcionesFechaRegistro) return;
        const excepcional = cambiarFechaRegistro.checked;
        fechaRegistro.readOnly = !excepcional;
        fechaRegistro.setAttribute('aria-readonly', String(!excepcional));
        opcionesFechaRegistro.hidden = !excepcional;
        if (motivoFechaRegistro) {
            motivoFechaRegistro.disabled = !excepcional;
            motivoFechaRegistro.required = excepcional;
        }
        if (!excepcional && fechaRegistro.dataset.defaultDate) {
            fechaRegistro.value = fechaRegistro.dataset.defaultDate;
        }
        actualizarResumen();
    };

    const actualizarModo = () => {
        const activo = modos.find(item => item.checked)?.value || 'MES_COMPLETO';
        paneles.forEach(panel => {
            const visible = panel.dataset.periodPanel === activo;
            panel.hidden = !visible;
            panel.querySelectorAll('input').forEach(input => {
                input.disabled = !visible;
                input.required = visible;
            });
        });
        actualizarResumen();
    };

    modos.forEach(item => item.addEventListener('change', actualizarModo));
    [mes, fecha, inicio, fin, vencimiento, descripcion, fechaRegistro].filter(Boolean)
        .forEach(item => item.addEventListener('input', actualizarResumen));
    cambiarFechaRegistro?.addEventListener('change', actualizarFechaRegistro);
    actualizarModo();
    actualizarFechaRegistro();
})();
