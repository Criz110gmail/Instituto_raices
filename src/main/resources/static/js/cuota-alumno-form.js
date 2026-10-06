(() => {
    const form = document.querySelector('.entity-form');
    if (!form) return;
    const campo = id => document.getElementById(id);
    const institucion = campo('institucion'), plantel = campo('plantel'), moneda = campo('moneda');
    const frecuencia = campo('frecuencia'), dia = campo('dia-vencimiento');
    const fechaUnica = campo('fecha-vencimiento-unico'), estado = campo('estado-cuota');
    const automatica = campo('generacion-automatica'), inscripcion = campo('inscripcionId');
    const inicio = campo('fechaInicio'), fin = campo('fechaFin');
    const primerMes = campo('primerMes'), ultimoMes = campo('ultimoMes');
    const planteles = [...plantel.options].slice(1);
    let rango, solicitud;
    const fecha = valor => valor ? valor.split('-').reverse().join('/') : 'pendiente';
    const mes = valor => valor ? new Intl.DateTimeFormat('es-MX', {month: 'long', year: 'numeric', timeZone: 'UTC'})
        .format(new Date(`${valor}-01T12:00:00Z`)) : 'pendiente';
    const mensual = () => frecuencia.value === 'MENSUAL';

    function resumir() {
        const importe = Number(campo('importeBase').value);
        const monto = Number.isFinite(importe) ? new Intl.NumberFormat('es-MX', {
            style: 'currency', currency: /^[A-Z]{3}$/.test(moneda.value) ? moneda.value : 'MXN'
        }).format(importe) : 'pendiente';
        campo('cuota-resumen').textContent = mensual()
            ? `Cuota de ${monto} cada mes, de ${mes(primerMes.value)} a ${mes(ultimoMes.value)}. Fecha límite: día ${dia.value || 'pendiente'} de cada mes.`
            : `Un solo cargo de ${monto}, con fecha límite de pago ${fecha(fechaUnica.value)}. No se repetirá en los siguientes meses.`;
        const ahora = form.querySelector('[name="generarCargoAhora"]')?.checked;
        campo('cuota-resumen-generacion').textContent = ahora
            ? 'Al guardar se creará el cargo único; todavía no se registra dinero recibido.'
            : automatica.checked
                ? 'Quedará preparada para Adeudos de alumnos → Generar automáticos. El generador crea sólo cargos faltantes, sin duplicarlos.'
                : 'Generación manual: guardar esta configuración no emite un cargo ni registra un pago.';
    }

    function sincronizarMeses() {
        if (!mensual()) return;
        if (primerMes.value && inicio.value.slice(0, 7) !== primerMes.value) {
            inicio.value = rango?.inicio.slice(0, 7) === primerMes.value ? rango.inicio : `${primerMes.value}-01`;
        }
        if (ultimoMes.value && fin.value.slice(0, 7) !== ultimoMes.value) {
            const [anio, numeroMes] = ultimoMes.value.split('-').map(Number);
            fin.value = rango?.fin.slice(0, 7) === ultimoMes.value ? rango.fin
                : `${ultimoMes.value}-${new Date(Date.UTC(anio, numeroMes, 0)).getUTCDate()}`;
        }
    }

    function alternarFrecuencia() {
        const esMensual = mensual();
        campo('calendario-mensual').hidden = !esMensual;
        campo('vencimiento-unico').hidden = esMensual;
        dia.disabled = !esMensual; dia.required = esMensual;
        fechaUnica.disabled = esMensual; fechaUnica.required = !esMensual;
        primerMes.disabled = !esMensual; ultimoMes.disabled = !esMensual;
        primerMes.required = esMensual; ultimoMes.required = esMensual;
        // The monthly range is entered through months; exact boundaries remain visible as reference.
        inicio.readOnly = esMensual; fin.readOnly = esMensual;
        if (!primerMes.value && inicio.value) primerMes.value = inicio.value.slice(0, 7);
        if (!ultimoMes.value && fin.value) ultimoMes.value = fin.value.slice(0, 7);
        campo('ayuda-generacion').textContent = esMensual
            ? 'Al ejecutar el generador se creará un cargo por cada mes faltante del rango. No se duplican meses ya generados.'
            : 'Al ejecutar el generador se creará un solo cargo para este alumno. No se repetirá al volver a generar.';
        sincronizarMeses(); resumir();
    }

    function alternarAutomatica() {
        automatica.disabled = estado.value !== 'ACTIVA';
        if (automatica.disabled) automatica.checked = false;
        resumir();
    }

    async function consultarVigencia(cambio = false) {
        solicitud?.abort(); rango = null;
        if (cambio) {
            inicio.value = ''; fin.value = ''; primerMes.value = ''; ultimoMes.value = '';
            fechaUnica.value = '';
        }
        if (!inscripcion.value) {
            campo('vigencia-ayuda').textContent = 'Selecciona una inscripción para consultar el rango permitido.';
            resumir(); return;
        }
        solicitud = new AbortController();
        campo('vigencia-ayuda').textContent = 'Consultando vigencia de la inscripción…';
        try {
            const respuesta = await fetch(`/admin/cuotas-alumno/vigencia-inscripcion?inscripcionId=${encodeURIComponent(inscripcion.value)}`,
                {signal: solicitud.signal, headers: {Accept: 'application/json'}});
            if (respuesta.redirected) { window.location.assign(respuesta.url); return; }
            if (!respuesta.ok) throw new Error('vigencia');
            rango = await respuesta.json();
            inicio.min = fin.min = fechaUnica.min = rango.inicio;
            inicio.max = fin.max = fechaUnica.max = rango.fin;
            primerMes.min = ultimoMes.min = rango.inicio.slice(0, 7);
            primerMes.max = ultimoMes.max = rango.fin.slice(0, 7);
            if (!inicio.value) inicio.value = rango.inicio;
            if (!fin.value) fin.value = rango.fin;
            if (!primerMes.value) primerMes.value = inicio.value.slice(0, 7);
            if (!ultimoMes.value) ultimoMes.value = fin.value.slice(0, 7);
            campo('vigencia-ayuda').textContent = `Rango permitido por la inscripción y el ciclo escolar: ${fecha(rango.inicio)} al ${fecha(rango.fin)}.`;
            alternarFrecuencia();
        } catch (error) {
            if (error.name === 'AbortError') return;
            campo('vigencia-ayuda').textContent = 'No fue posible consultar la vigencia. Intenta seleccionar nuevamente la inscripción; el servidor comprobará las fechas al guardar.';
        }
    }

    institucion.addEventListener('change', () => {
        if (institucion.disabled) return;
        planteles.forEach(o => { o.hidden = !institucion.value || o.dataset.institucion !== institucion.value || o.dataset.activo !== 'true'; });
        if (plantel.selectedOptions[0]?.hidden) { plantel.value = ''; plantel.dispatchEvent(new Event('change')); }
        if (!moneda.value && institucion.selectedOptions[0]?.dataset.moneda) moneda.value = institucion.selectedOptions[0].dataset.moneda;
    });
    inscripcion.addEventListener('change', () => consultarVigencia(true));
    frecuencia.addEventListener('change', alternarFrecuencia);
    estado.addEventListener('change', alternarAutomatica);
    primerMes.addEventListener('input', sincronizarMeses);
    ultimoMes.addEventListener('input', sincronizarMeses);
    form.addEventListener('input', resumir);
    form.addEventListener('change', resumir);
    form.addEventListener('invalid', evento => {
        if (campo('vigencia-avanzada').contains(evento.target)) campo('vigencia-avanzada').open = true;
    }, true);
    institucion.dispatchEvent(new Event('change'));
    alternarFrecuencia(); alternarAutomatica(); consultarVigencia();
})();
