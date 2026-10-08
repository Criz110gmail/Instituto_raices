(() => {
    const base = '/portal/pagos';
    const cuenta = document.getElementById('cuenta');
    const solicitudes = document.getElementById('solicitudes');
    const montoTotal = document.querySelector('.portal-total-value');
    const montoTotalVisible = document.getElementById('monto-visible');
    const agregar = document.getElementById('agregar');
    const form = document.querySelector('.portal-payment-form');
    const cuentaAyuda = document.getElementById('cuenta-ayuda');
    const archivos = document.getElementById('comprobantes');
    const moneda = document.querySelector('.portal-money-input em')?.textContent?.trim() || 'MXN';
    const formatoMoneda = new Intl.NumberFormat('es-MX', {style: 'currency', currency: moneda,
        minimumFractionDigits: 2, maximumFractionDigits: 2});
    let cuentaSeleccionada = cuenta?.dataset.selectedValue || '';
    let consultaCuentas;
    if (!cuenta || !solicitudes || !montoTotal || !montoTotalVisible || !agregar) return;

    solicitudes.querySelectorAll('[data-cargo-autocomplete]').forEach(inicializarCargo);
    recalcularTotal();
    sincronizarCuentas();
    form?.addEventListener('input', actualizarResumen);
    form?.addEventListener('change', actualizarResumen);
    form?.addEventListener('submit', evento => {
        const vistos = new Set();
        for (const fila of solicitudes.querySelectorAll('.portal-distribution-row')) {
            const id = fila.querySelector('.portal-cargo-id')?.value;
            if (!id) continue;
            if (vistos.has(String(id))) {
                evento.preventDefault();
                fila.querySelector('.portal-cargo-status').textContent = 'Este adeudo ya está agregado al pago. Selecciona otro cargo o limpia esta selección.';
                fila.querySelector('.portal-cargo-search').setAttribute('aria-invalid', 'true');
                fila.scrollIntoView?.({behavior: 'smooth', block: 'center'});
                return;
            }
            vistos.add(String(id));
        }
    });

    function cargoEnOtraFila(cargoId, filaActual) {
        return [...solicitudes.querySelectorAll('.portal-distribution-row')].some(fila =>
            fila !== filaActual && String(fila.querySelector('.portal-cargo-id')?.value || '') === String(cargoId));
    }

    agregar.addEventListener('click', () => {
        const indice = solicitudes.children.length;
        const fila = document.createElement('div');
        fila.className = 'portal-distribution-row';
        fila.innerHTML = `<span class="portal-row-number">${indice + 1}</span>
            <div class="portal-cargo-field portal-cargo-autocomplete" data-cargo-autocomplete><label>
                <span>Cargo pendiente del alumno <b>*</b></span>
                <div class="portal-autocomplete-control"><input class="portal-cargo-id" type="hidden" name="solicitudes[${indice}].cargoId"><input class="portal-cargo-search" type="search" name="solicitudes[${indice}].cargoEtiqueta" placeholder="Enfoca para ver cargos pendientes" autocomplete="off" role="combobox" aria-autocomplete="list" aria-expanded="false" required><button class="portal-autocomplete-clear" type="button" aria-label="Quitar cargo" hidden>×</button></div>
                <div class="portal-cargo-results" role="listbox" hidden></div><small class="portal-cargo-status" aria-live="polite">Al enfocar verás los primeros 10; escribe 3 caracteres para buscar.</small><small class="portal-selected-charge-detail" aria-live="polite" hidden></small>
            </label></div>
            <label class="portal-amount-field"><span>Saldo a pagar</span><input class="portal-charge-amount" type="hidden" name="solicitudes[${indice}].montoSolicitado"><div class="portal-money-input portal-charge-money"><b aria-hidden="true">$</b><input class="portal-charge-amount-display portal-calculated-input" type="text" placeholder="$0.00" readonly aria-readonly="true"><em>${moneda}</em></div></label>`;
        solicitudes.appendChild(fila);
        inicializarCargo(fila.querySelector('[data-cargo-autocomplete]'));
        fila.querySelector('.portal-cargo-search').focus();
    });

    async function cargarCuentas(cargoId) {
        consultaCuentas?.abort();
        const controlador = new AbortController();
        consultaCuentas = controlador;
        const seleccionPrevia = cuenta.value || cuentaSeleccionada;
        cuenta.disabled = true;
        cuenta.innerHTML = '<option value="">Consultando cuentas…</option>';
        if (cuentaAyuda) cuentaAyuda.textContent = 'Consultando las cuentas del plantel del cargo seleccionado…';
        actualizarResumen();
        try {
            const respuesta = await fetch(`${base}/cuentas?cargoId=${encodeURIComponent(cargoId)}`,
                    {headers: {Accept: 'application/json'}, signal: controlador.signal});
            if (!respuesta.ok) throw new Error();
            const datos = await respuesta.json();
            if (controlador.signal.aborted) return;
            cuenta.innerHTML = '<option value="">Selecciona una cuenta</option>';
            datos.resultados.forEach(opcion => cuenta.add(new Option(
                    `${opcion.titulo} · ${opcion.detalle}`, opcion.id)));
            if (seleccionPrevia && [...cuenta.options].some(opcion => opcion.value === String(seleccionPrevia))) {
                cuenta.value = String(seleccionPrevia);
            }
            cuentaSeleccionada = cuenta.value;
            cuenta.disabled = !datos.resultados.length;
            if (cuentaAyuda) cuentaAyuda.textContent = datos.resultados.length
                    ? 'Selecciona la cuenta donde realizaste la transferencia.'
                    : 'No hay cuentas disponibles para este cargo. Comunícate con administración.';
        } catch (_) {
            if (!controlador.signal.aborted) {
                cuenta.innerHTML = '<option value="">No fue posible consultar las cuentas</option>';
                if (cuentaAyuda) cuentaAyuda.textContent = 'No fue posible consultar las cuentas. Vuelve a seleccionar el cargo para reintentar.';
            }
        } finally {
            if (consultaCuentas === controlador) actualizarResumen();
        }
    }

    function sincronizarCuentas() {
        const cargoId = [...solicitudes.querySelectorAll('.portal-cargo-id')]
                .map(campo => campo.value).find(Boolean);
        if (!cargoId) {
            consultaCuentas?.abort();
            cuenta.innerHTML = '<option value="">Selecciona primero un cargo</option>';
            cuenta.disabled = true;
            if (cuentaAyuda) cuentaAyuda.textContent = 'Primero selecciona qué vas a pagar para consultar las cuentas disponibles.';
            cuentaSeleccionada = '';
            actualizarResumen();
            return;
        }
        cargarCuentas(cargoId);
    }

    function inicializarCargo(contenedor) {
        const entrada = contenedor.querySelector('.portal-cargo-search');
        const id = contenedor.querySelector('.portal-cargo-id');
        const resultados = contenedor.querySelector('.portal-cargo-results');
        const estado = contenedor.querySelector('.portal-cargo-status');
        const desglose = contenedor.querySelector('.portal-selected-charge-detail');
        const limpiar = contenedor.querySelector('.portal-autocomplete-clear');
        const fila = contenedor.closest('.portal-distribution-row');
        const importe = fila.querySelector('.portal-charge-amount');
        const importeVisible = fila.querySelector('.portal-charge-amount-display');
        let temporizador;
        let solicitud;
        let etiquetaSeleccionada = entrada.value;
        limpiar.hidden = !entrada.value;
        mostrarImporte(importe, importeVisible);

        entrada.addEventListener('focus', mostrarIniciales);
        entrada.addEventListener('click', () => {
            if (resultados.hidden) mostrarIniciales();
        });
        entrada.addEventListener('input', () => {
            entrada.setAttribute('aria-invalid', 'false');
            if (entrada.value !== etiquetaSeleccionada) {
                id.value = '';
                importe.value = '';
                importeVisible.value = '';
                if (desglose) { desglose.textContent = ''; desglose.hidden = true; }
                etiquetaSeleccionada = '';
                limpiar.hidden = !entrada.value;
                recalcularTotal();
                sincronizarCuentas();
            }
            clearTimeout(temporizador);
            solicitud?.abort();
            cerrar();
            const consulta = entrada.value.trim();
            if (consulta.length < 3) {
                estado.textContent = 'Escribe al menos 3 caracteres o enfoca para ver los primeros 10.';
                return;
            }
            estado.textContent = 'Buscando cargos disponibles…';
            temporizador = setTimeout(() => buscar(consulta), 250);
        });
        limpiar.addEventListener('click', () => {
            entrada.setAttribute('aria-invalid', 'false');
            entrada.value = '';
            id.value = '';
            importe.value = '';
            importeVisible.value = '';
            if (desglose) { desglose.textContent = ''; desglose.hidden = true; }
            etiquetaSeleccionada = '';
            limpiar.hidden = true;
            recalcularTotal();
            sincronizarCuentas();
            entrada.focus();
        });
        document.addEventListener('click', evento => {
            if (!contenedor.contains(evento.target)) cerrar();
        });

        async function buscar(consulta) {
            solicitud?.abort();
            solicitud = new AbortController();
            estado.textContent = 'Consultando cargos disponibles…';
            try {
                const respuesta = await fetch(`${base}/cargos?q=${encodeURIComponent(consulta)}`,
                        {headers: {Accept: 'application/json'}, signal: solicitud.signal});
                if (respuesta.redirected && new URL(respuesta.url).pathname === '/login') {
                    window.location.assign('/login?sesionExpirada');
                    return;
                }
                if (!respuesta.ok) throw new Error();
                const datos = await respuesta.json();
                resultados.replaceChildren();
                if (!datos.resultados.length) {
                    cerrar();
                    estado.textContent = 'No hay cargos disponibles. Los vencidos requieren autorización y los pagos en revisión deben resolverse primero.';
                    return;
                }
                const disponibles = datos.resultados.filter(opcion => !cargoEnOtraFila(opcion.id, fila));
                if (!disponibles.length) {
                    cerrar();
                    estado.textContent = 'Los cargos de esta búsqueda ya están agregados al pago. Escribe al menos 3 caracteres para buscar otros.';
                    return;
                }
                disponibles.forEach(opcion => {
                    const boton = document.createElement('button');
                    boton.type = 'button';
                    boton.className = 'portal-cargo-option';
                    boton.setAttribute('role', 'option');
                    const titulo = document.createElement('strong');
                    titulo.textContent = opcion.titulo;
                    const detalle = document.createElement('small');
                    detalle.textContent = opcion.detalle || '';
                    boton.append(titulo, detalle);
                    boton.addEventListener('mousedown', evento => evento.preventDefault());
                    boton.addEventListener('click', () => {
                        if (cargoEnOtraFila(opcion.id, fila)) {
                            cerrar();
                            estado.textContent = 'Este adeudo ya está agregado al pago. Selecciona otro cargo.';
                            return;
                        }
                        entrada.setAttribute('aria-invalid', 'false');
                        entrada.value = opcion.titulo;
                        id.value = opcion.id;
                        importe.value = Number(opcion.monto).toFixed(2);
                        mostrarImporte(importe, importeVisible);
                        etiquetaSeleccionada = opcion.titulo;
                        limpiar.hidden = false;
                        estado.textContent = 'Saldo completo seleccionado.';
                        if (desglose) { desglose.textContent = opcion.detalle || ''; desglose.hidden = !opcion.detalle; }
                        cerrar();
                        recalcularTotal();
                        sincronizarCuentas();
                    });
                    resultados.appendChild(boton);
                });
                resultados.hidden = false;
                entrada.setAttribute('aria-expanded', 'true');
                estado.textContent = datos.hayMas
                        ? 'Se muestran 10 cargos. Escribe 3 caracteres para precisar.'
                        : `${disponibles.length} cargo${disponibles.length === 1 ? '' : 's'} disponible${disponibles.length === 1 ? '' : 's'} sin repetir los agregados.`;
            } catch (error) {
                if (error.name !== 'AbortError') estado.textContent = 'No fue posible consultar los cargos. Intenta nuevamente.';
            }
        }

        function cerrar() {
            resultados.hidden = true;
            resultados.replaceChildren();
            entrada.setAttribute('aria-expanded', 'false');
        }

        function mostrarIniciales() {
            clearTimeout(temporizador);
            buscar('');
        }
    }

    function recalcularTotal() {
        const total = [...solicitudes.querySelectorAll('.portal-charge-amount')]
                .reduce((suma, campo) => suma + (Number(campo.value) || 0), 0);
        montoTotal.value = total > 0 ? total.toFixed(2) : '';
        montoTotalVisible.value = total > 0 ? formatoMoneda.format(total) : '';
        actualizarResumen();
    }

    function actualizarResumen() {
        const filas = [...solicitudes.querySelectorAll('.portal-distribution-row')]
                .filter(fila => fila.querySelector('.portal-cargo-id')?.value);
        const contador = document.getElementById('total-cargos');
        if (contador) contador.textContent = filas.length
                ? `${filas.length} cargo${filas.length === 1 ? '' : 's'} seleccionado${filas.length === 1 ? '' : 's'} · importe calculado`
                : 'Selecciona tus cargos para calcularlo';
        const lista = document.getElementById('resumen-cargos');
        if (lista) {
            lista.replaceChildren();
            if (!filas.length) agregarTexto(lista, 'Aún no has seleccionado cargos.');
            filas.forEach(fila => agregarTexto(lista,
                    `${fila.querySelector('.portal-cargo-search').value} · ${formatoMoneda.format(Number(fila.querySelector('.portal-charge-amount').value) || 0)}${fila.querySelector('.portal-selected-charge-detail')?.textContent ? ' · ' + fila.querySelector('.portal-selected-charge-detail').textContent : ''}`));
        }
        texto('resumen-total', formatoMoneda.format(Number(montoTotal.value) || 0));
        texto('resumen-cuenta', cuenta.value && !cuenta.disabled
                ? cuenta.selectedOptions[0].textContent : 'Pendiente de seleccionar');
        const fecha = document.getElementById('fechaPago')?.value || '';
        const partes = fecha.split('T');
        const dia = partes[0]?.split('-');
        texto('resumen-fecha', dia?.length === 3 && partes[1]
                ? `${dia[2]}/${dia[1]}/${dia[0]} · ${partes[1].slice(0,5)} h` : 'Pendiente de capturar');
        texto('resumen-referencia', document.getElementById('referencia')?.value.trim() || 'Pendiente de capturar');
        const seleccion = [...(archivos?.files || [])];
        const listaArchivos = document.getElementById('archivos-seleccionados');
        if (listaArchivos) {
            listaArchivos.replaceChildren();
            seleccion.forEach(archivo => agregarTexto(listaArchivos, `${archivo.name} · ${(archivo.size / 1024 / 1024).toFixed(2)} MB`));
        }
        texto('resumen-archivos', seleccion.length ? seleccion.map(archivo => archivo.name).join(', ') : 'Pendiente de adjuntar');
    }

    function texto(id, valor) {
        const elemento = document.getElementById(id);
        if (elemento) elemento.textContent = valor;
    }

    function agregarTexto(lista, valor) {
        const item = document.createElement('li');
        item.textContent = valor;
        lista.appendChild(item);
    }

    function mostrarImporte(origen, destino) {
        const valor = Number(origen.value);
        destino.value = valor > 0 ? formatoMoneda.format(valor) : '';
    }
})();
