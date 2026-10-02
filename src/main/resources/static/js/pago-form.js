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
        document.querySelectorAll('.protected-amount-control b')
            .forEach(etiqueta => etiqueta.textContent = moneda.value || 'MXN');
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
        const importe = fila.querySelector('.monto-solicitado');
        const parcial = fila.querySelector('.partial-payment-toggle');
        importe.addEventListener('input', actualizarTotales);
        parcial.addEventListener('click', () => alternarPagoParcial(fila));
        fila.querySelector('.protected-amount-control b').textContent = moneda.value || 'MXN';
        if (fila.querySelector('.cargo-id').value) {
            protegerAsignacion(fila, fila.querySelector('.full-amount-reference').value || importe.value);
        }
    }

    function protegerAsignacion(fila, saldoCompleto) {
        const importe = fila.querySelector('.monto-solicitado');
        const entradaCargo = fila.querySelector('.distribution-charge input[type="search"]');
        const parcial = fila.querySelector('.partial-payment-toggle');
        const fijo = fila.dataset.fixedCharge === 'true';
        const saldo = numero(saldoCompleto);
        if (saldo > 0) {
            importe.dataset.fullAmount = saldo.toFixed(2);
            fila.querySelector('.full-amount-reference').value = saldo.toFixed(2);
        }
        importe.readOnly = true;
        entradaCargo.readOnly = true;
        fila.classList.add('distribution-selected');
        parcial.hidden = fijo;
        parcial.textContent = 'Registrar pago parcial';
        fila.querySelector('.amount-help').textContent = fijo
            ? 'Importe protegido: corresponde al saldo vigente del pago seleccionado.'
            : 'Saldo completo protegido contra cambios accidentales.';
    }

    function restablecerAsignacion(fila) {
        if (fila.dataset.fixedCharge === 'true') return;
        const importe = fila.querySelector('.monto-solicitado');
        importe.value = '';
        delete importe.dataset.fullAmount;
        fila.querySelector('.full-amount-reference').value = '';
        importe.readOnly = true;
        fila.querySelector('.distribution-charge input[type="search"]').readOnly = false;
        fila.querySelector('.partial-payment-toggle').hidden = true;
        fila.querySelector('.amount-help').textContent = 'Selecciona un cargo para obtener su saldo pendiente.';
        fila.classList.remove('distribution-selected', 'partial-payment-active');
        actualizarTotales();
    }

    function alternarPagoParcial(fila) {
        const importe = fila.querySelector('.monto-solicitado');
        const boton = fila.querySelector('.partial-payment-toggle');
        const ayuda = fila.querySelector('.amount-help');
        if (importe.readOnly) {
            importe.readOnly = false;
            fila.classList.add('partial-payment-active');
            boton.textContent = 'Usar saldo completo';
            ayuda.textContent = 'Modo parcial activo. Captura exclusivamente el importe realmente recibido para este cargo.';
            importe.focus();
            importe.select();
            return;
        }
        importe.value = importe.dataset.fullAmount || importe.value;
        importe.readOnly = true;
        fila.classList.remove('partial-payment-active');
        boton.textContent = 'Registrar pago parcial';
        ayuda.textContent = 'Saldo completo protegido contra cambios accidentales.';
        actualizarTotales();
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
            fila.querySelector('.full-amount-reference').name = `solicitudes[${indice}].saldoReferencia`;
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
        const tarjeta = metodo.value === 'TARJETA';
        document.querySelector('#receipt-title').textContent = transferencia
            ? 'Comprobante de transferencia *'
            : tarjeta ? 'Voucher o comprobante de tarjeta (opcional)'
                : 'Comprobante de efectivo (opcional)';
        document.querySelector('#payment-reference-label').textContent = tarjeta
            ? 'Autorización o referencia de la terminal'
            : transferencia ? 'Referencia bancaria' : 'Referencia o recibo interno';
        document.querySelector('#payment-reference-help').textContent = tarjeta
            ? 'Captura el código de autorización o referencia que entrega la terminal.'
            : transferencia ? 'Captura el folio o clave disponible en el comprobante bancario.'
                : 'Opcional: anota el folio del recibo entregado en ventanilla.';
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

        if (entrada.readOnly) {
            limpiar.hidden = true;
            resultados.hidden = true;
            return;
        }

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
                            if (tipo === 'cargo' && opcion.monto != null) {
                                const fila = contenedor.closest('.distribution-row');
                                const importe = fila?.querySelector('.monto-solicitado');
                                if (importe) importe.value = Number(opcion.monto).toFixed(2);
                                if (fila) protegerAsignacion(fila, opcion.monto);
                                const solicitado = [...lista.querySelectorAll('.monto-solicitado')]
                                    .reduce((suma, input) => suma + numero(input.value), 0);
                                monto.value = solicitado.toFixed(2);
                                actualizarTotales();
                            }
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
            if (tipo === 'cargo') restablecerAsignacion(contenedor.closest('.distribution-row'));
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
        if (tipo === 'cargo') restablecerAsignacion(contenedor.closest('.distribution-row'));
        contenedor.dispatchEvent(new Event('payment-autocomplete-reset'));
    }

    actualizarAlcance(true);
})();
