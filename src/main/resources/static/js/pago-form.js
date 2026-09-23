(() => {
    const institucion = document.querySelector('#institucion');
    const plantel = document.querySelector('#plantel');
    const tutorId = document.querySelector('#tutorId');
    const cuentaId = document.querySelector('#cuentaDeclaradaId');
    const metodo = document.querySelector('#metodo');
    const monto = document.querySelector('#monto');
    const moneda = document.querySelector('#moneda');
    const fecha = document.querySelector('#fechaPago');
    const lista = document.querySelector('#solicitudes');
    const plantilla = document.querySelector('#solicitud-template');
    const vacio = document.querySelector('#distribution-empty');
    const archivos = document.querySelector('#comprobantes');
    const opcionesPlantel = [...plantel.options].slice(1);

    if (!fecha.value) {
        const ahora = new Date(Date.now() - new Date().getTimezoneOffset() * 60000);
        fecha.value = ahora.toISOString().slice(0, 16);
    }

    function actualizarAlcance(conservar) {
        opcionesPlantel.forEach(o => o.hidden = !institucion.value
            || o.dataset.institucion !== institucion.value || o.dataset.activo !== 'true');
        if (plantel.selectedOptions[0]?.hidden) plantel.value = '';
        const seleccion = institucion.selectedOptions[0];
        if (seleccion?.dataset.moneda) moneda.value = seleccion.dataset.moneda;
        if (!conservar) {
            limpiarAutocompletado(document.querySelector('[data-payment-autocomplete="tutor"]'));
            limpiarAutocompletado(document.querySelector('[data-payment-autocomplete="cuenta"]'));
            limpiarCargos();
        }
    }

    institucion.addEventListener('change', () => actualizarAlcance(false));
    plantel.addEventListener('change', () => {
        limpiarAutocompletado(document.querySelector('[data-payment-autocomplete="cuenta"]'));
    });
    tutorId.addEventListener('change', limpiarCargos);

    document.querySelectorAll('[data-payment-autocomplete]').forEach(inicializarAutocomplete);

    document.querySelector('#agregar-cargo').addEventListener('click', () => {
        lista.append(plantilla.content.cloneNode(true));
        const fila = lista.lastElementChild;
        inicializarAutocomplete(fila.querySelector('[data-payment-autocomplete]'));
        conectarFila(fila);
        reindexar();
        fila.querySelector('input[type="search"]').focus();
    });

    lista.querySelectorAll('.distribution-row').forEach(conectarFila);
    reindexar();
    monto.addEventListener('input', actualizarTotales);
    archivos.addEventListener('change', () => {
        const seleccionados = [...archivos.files];
        document.querySelector('#file-summary').textContent = seleccionados.length
            ? `${seleccionados.length} archivo(s): ${seleccionados.map(a => a.name).join(', ')}`
            : 'No se han seleccionado archivos.';
    });
    metodo.addEventListener('change', () => {
        actualizarMetodo();
        limpiarAutocompletado(document.querySelector('[data-payment-autocomplete="cuenta"]'));
    });
    actualizarMetodo();

    function conectarFila(fila) {
        fila.querySelector('.remove-distribution').addEventListener('click', () => {
            fila.remove(); reindexar();
        });
        fila.querySelector('.monto-solicitado').addEventListener('input', actualizarTotales);
    }

    function reindexar() {
        lista.querySelectorAll('.distribution-row').forEach((fila, indice) => {
            fila.querySelector('.row-number').textContent = indice + 1;
            const busqueda = fila.querySelector('input[type="search"]');
            busqueda.name = `solicitudes[${indice}].cargoEtiqueta`;
            busqueda.id = `cargo-${indice}`;
            fila.querySelector('label').htmlFor = busqueda.id;
            fila.querySelector('.cargo-id').name = `solicitudes[${indice}].cargoId`;
            fila.querySelector('.monto-solicitado').name = `solicitudes[${indice}].montoSolicitado`;
        });
        vacio.hidden = lista.children.length > 0;
        actualizarTotales();
    }

    function limpiarCargos() {
        lista.querySelectorAll('[data-payment-autocomplete="cargo"]').forEach(limpiarAutocompletado);
    }

    function actualizarTotales() {
        const total = numero(monto.value);
        const solicitado = [...lista.querySelectorAll('.monto-solicitado')]
            .reduce((suma, input) => suma + numero(input.value), 0);
        const restante = total - solicitado;
        const formato = new Intl.NumberFormat('es-MX', {style: 'currency', currency: moneda.value || 'MXN'});
        document.querySelector('#total-pago').textContent = formato.format(total);
        document.querySelector('#total-solicitado').textContent = formato.format(solicitado);
        const salida = document.querySelector('#total-restante');
        salida.textContent = formato.format(restante);
        salida.closest('.remaining').classList.toggle('over', restante < 0);
    }

    function actualizarMetodo() {
        const transferencia = metodo.value === 'TRANSFERENCIA';
        document.querySelector('#receipt-title').textContent = transferencia
            ? 'Comprobante de transferencia *' : 'Comprobante de efectivo (opcional)';
    }

    function numero(valor) {
        const n = Number.parseFloat(valor); return Number.isFinite(n) ? n : 0;
    }

    function inicializarAutocomplete(contenedor) {
        if (!contenedor || contenedor.dataset.ready) return;
        contenedor.dataset.ready = 'true';
        const tipo = contenedor.dataset.paymentAutocomplete;
        const entrada = contenedor.querySelector('input[type="search"]');
        const valor = tipo === 'tutor' ? tutorId : tipo === 'cuenta' ? cuentaId : contenedor.querySelector('.cargo-id');
        const resultados = contenedor.querySelector('.autocomplete-results');
        const estado = contenedor.querySelector('.autocomplete-status');
        const limpiar = contenedor.querySelector('.autocomplete-clear');
        let timer;
        let controlador;
        let etiqueta = entrada.value;
        const mostrarEstado = mensaje => { estado.textContent = mensaje; estado.hidden = !mensaje; };
        const reiniciarBusqueda = () => {
            clearTimeout(timer);
            controlador?.abort();
            resultados.hidden = true;
            resultados.replaceChildren();
        };

        entrada.addEventListener('focus', () => {
            if (entrada.value.trim() || resultados.children.length || requisitos(tipo)) return;
            mostrarEstado('Cargando opciones…');
            consultar('');
        });

        entrada.addEventListener('input', () => {
            if (entrada.value !== etiqueta) { valor.value = ''; valor.dispatchEvent(new Event('change', {bubbles: true})); etiqueta = ''; }
            reiniciarBusqueda();
            const requisito = requisitos(tipo);
            if (requisito) { mostrarEstado(requisito); return; }
            if (entrada.value.trim().length < 3) { mostrarEstado('Escribe al menos 3 caracteres.'); return; }
            mostrarEstado('Buscando…');
            timer = setTimeout(() => consultar(entrada.value.trim()), 280);
        });
        async function consultar(consulta) {
                controlador = new AbortController();
                try {
                    const respuesta = await fetch(endpoint(tipo, consulta), {headers: {'Accept': 'application/json'}, signal: controlador.signal});
                    if (respuesta.redirected && new URL(respuesta.url).pathname === '/login') { location.assign('/login?sesionExpirada'); return; }
                    if (!respuesta.ok) throw new Error();
                    const datos = await respuesta.json();
                    resultados.replaceChildren();
                    (datos.resultados || []).forEach(opcion => {
                        const boton = document.createElement('button'); boton.type = 'button'; boton.className = 'autocomplete-option';
                        const titulo = document.createElement('strong'); titulo.textContent = opcion.titulo;
                        const detalle = document.createElement('small'); detalle.textContent = opcion.detalle || '';
                        boton.append(titulo, detalle); boton.addEventListener('click', () => {
                            entrada.value = opcion.titulo; etiqueta = opcion.titulo; valor.value = opcion.id;
                            valor.dispatchEvent(new Event('change', {bubbles: true})); resultados.hidden = true;
                            mostrarEstado('');
                        }); resultados.append(boton);
                    });
                    resultados.hidden = !resultados.children.length;
                    mostrarEstado(resultados.children.length ? `${resultados.children.length} coincidencia(s).` : 'No encontramos coincidencias disponibles.');
                } catch (e) { if (e.name !== 'AbortError') mostrarEstado('No fue posible completar la búsqueda.'); }
        }
        limpiar.addEventListener('click', () => {
            entrada.value = ''; etiqueta = ''; valor.value = '';
            valor.dispatchEvent(new Event('change', {bubbles: true}));
            reiniciarBusqueda();
            mostrarEstado(requisitos(tipo) || 'Escribe al menos 3 caracteres.');
            entrada.focus();
        });
        contenedor.addEventListener('payment-autocomplete-reset', () => {
            etiqueta = '';
            reiniciarBusqueda();
            mostrarEstado(requisitos(tipo) || 'Escribe al menos 3 caracteres.');
        });
        document.addEventListener('click', e => { if (!contenedor.contains(e.target)) resultados.hidden = true; });
    }

    function requisitos(tipo) {
        if (!institucion.value) return 'Selecciona primero una institución.';
        if (tipo === 'cuenta' && !plantel.value) return 'Selecciona primero un plantel.';
        if (tipo === 'cargo' && !tutorId.value) return 'Selecciona primero un tutor.';
        return '';
    }

    function endpoint(tipo, consulta) {
        const p = new URLSearchParams({q: consulta, institucionId: institucion.value});
        if (tipo === 'tutor') return `/admin/autocompletado/tutores?${p}`;
        if (tipo === 'cuenta') { p.set('plantelId', plantel.value); p.set('metodo', metodo.value); return `/admin/autocompletado/cuentas-pago?${p}`; }
        p.set('tutorId', tutorId.value); return `/admin/autocompletado/cargos-pago?${p}`;
    }

    function limpiarAutocompletado(contenedor) {
        if (!contenedor) return;
        contenedor.querySelector('input[type="search"]').value = '';
        const tipo = contenedor.dataset.paymentAutocomplete;
        const valor = tipo === 'tutor' ? tutorId : tipo === 'cuenta' ? cuentaId : contenedor.querySelector('.cargo-id');
        valor.value = ''; valor.dispatchEvent(new Event('change', {bubbles: true}));
        contenedor.dispatchEvent(new Event('payment-autocomplete-reset'));
    }

    actualizarAlcance(true);
})();
