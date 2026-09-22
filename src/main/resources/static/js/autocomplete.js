(() => {
    const MINIMO = 3;
    const DEMORA = 280;

    document.querySelectorAll('[data-autocomplete]').forEach(inicializar);

    function inicializar(contenedor) {
        const entrada = contenedor.querySelector('.autocomplete-input');
        const valor = contenedor.querySelector('.autocomplete-value');
        const lista = contenedor.querySelector('.autocomplete-results');
        const estado = contenedor.querySelector('.autocomplete-status');
        const limpiar = contenedor.querySelector('.autocomplete-clear');
        const alcance = document.getElementById(contenedor.dataset.scopeInput || contenedor.dataset.institutionInput);
        const parametroAlcance = contenedor.dataset.scopeParam || 'institucionId';
        const etiquetaAlcance = contenedor.dataset.scopeLabel || 'una institución';
        if (!entrada || !valor || !lista || !estado || !alcance || entrada.disabled) return;

        let temporizador;
        let solicitud;
        let opciones = [];
        let indiceActivo = -1;
        let etiquetaSeleccionada = entrada.value;

        actualizarLimpiar();
        entrada.addEventListener('input', () => {
            if (entrada.value !== etiquetaSeleccionada) {
                valor.value = '';
                valor.dispatchEvent(new Event('change', {bubbles: true}));
                etiquetaSeleccionada = '';
                actualizarLimpiar();
            }
            clearTimeout(temporizador);
            solicitud?.abort();
            cerrarLista();
            const consulta = entrada.value.trim();
            if (!alcance.value) {
                mostrarEstado(`Selecciona primero ${etiquetaAlcance}.`);
                return;
            }
            if (consulta.length < MINIMO) {
                mostrarEstado(`Escribe al menos ${MINIMO} caracteres para buscar.`);
                return;
            }
            mostrarEstado('Buscando…');
            temporizador = setTimeout(() => buscar(consulta), DEMORA);
        });

        entrada.addEventListener('keydown', evento => {
            if (!opciones.length) return;
            if (evento.key === 'ArrowDown') {
                evento.preventDefault();
                moverActivo(1);
            } else if (evento.key === 'ArrowUp') {
                evento.preventDefault();
                moverActivo(-1);
            } else if (evento.key === 'Enter' && indiceActivo >= 0) {
                evento.preventDefault();
                seleccionar(opciones[indiceActivo]);
            } else if (evento.key === 'Escape') {
                cerrarLista();
            }
        });

        limpiar?.addEventListener('click', () => {
            entrada.value = '';
            valor.value = '';
            valor.dispatchEvent(new Event('change', {bubbles: true}));
            etiquetaSeleccionada = '';
            cerrarLista();
            mostrarEstado(`Escribe al menos ${MINIMO} caracteres para buscar.`);
            actualizarLimpiar();
            entrada.focus();
        });

        alcance.addEventListener('change', () => {
            entrada.value = '';
            valor.value = '';
            valor.dispatchEvent(new Event('change', {bubbles: true}));
            etiquetaSeleccionada = '';
            cerrarLista();
            mostrarEstado(alcance.value
                ? `Escribe al menos ${MINIMO} caracteres para buscar.`
                : `Selecciona primero ${etiquetaAlcance}.`);
            actualizarLimpiar();
        });

        document.addEventListener('click', evento => {
            if (!contenedor.contains(evento.target)) cerrarLista();
        });

        async function buscar(consulta) {
            solicitud = new AbortController();
            const parametros = new URLSearchParams({q: consulta});
            parametros.set(parametroAlcance, alcance.value);
            if (contenedor.dataset.excludeId) parametros.set('tutorId', contenedor.dataset.excludeId);
            try {
                const respuesta = await fetch(`${contenedor.dataset.endpoint}?${parametros}`, {
                    headers: {'Accept': 'application/json'},
                    signal: solicitud.signal
                });
                if (respuesta.redirected && new URL(respuesta.url).pathname === '/login') {
                    window.location.assign('/login?sesionExpirada');
                    return;
                }
                if (!respuesta.ok) throw new Error('No fue posible consultar el catálogo');
                const datos = await respuesta.json();
                renderizar(datos.resultados || [], Boolean(datos.hayMas));
            } catch (error) {
                if (error.name !== 'AbortError') mostrarEstado('No se pudo completar la búsqueda. Intenta nuevamente.');
            }
        }

        function renderizar(resultados, hayMas) {
            lista.replaceChildren();
            opciones = resultados;
            indiceActivo = -1;
            if (!resultados.length) {
                cerrarLista();
                mostrarEstado('No encontramos coincidencias activas.');
                return;
            }
            resultados.forEach((opcion, indice) => {
                const boton = document.createElement('button');
                boton.type = 'button';
                boton.className = 'autocomplete-option';
                boton.id = `${entrada.id}-opcion-${indice}`;
                boton.setAttribute('role', 'option');
                const titulo = document.createElement('strong');
                titulo.textContent = opcion.titulo;
                const detalle = document.createElement('small');
                detalle.textContent = opcion.detalle || '';
                boton.append(titulo, detalle);
                boton.addEventListener('mousedown', evento => evento.preventDefault());
                boton.addEventListener('click', () => seleccionar(opcion));
                lista.appendChild(boton);
            });
            lista.hidden = false;
            entrada.setAttribute('aria-expanded', 'true');
            mostrarEstado(hayMas
                ? 'Se muestran 20 coincidencias. Escribe más caracteres para precisar.'
                : `${resultados.length} coincidencia${resultados.length === 1 ? '' : 's'}.`);
        }

        function seleccionar(opcion) {
            entrada.value = opcion.titulo;
            valor.value = opcion.id;
            valor.dispatchEvent(new Event('change', {bubbles: true}));
            etiquetaSeleccionada = opcion.titulo;
            cerrarLista();
            mostrarEstado('');
            contenedor.querySelectorAll('[data-autocomplete-error], :scope > small:not(.autocomplete-status)').forEach(error => {
                error.textContent = '';
            });
            actualizarLimpiar();
        }

        function moverActivo(direccion) {
            indiceActivo = (indiceActivo + direccion + opciones.length) % opciones.length;
            lista.querySelectorAll('.autocomplete-option').forEach((elemento, indice) => {
                const activo = indice === indiceActivo;
                elemento.classList.toggle('active', activo);
                elemento.setAttribute('aria-selected', String(activo));
                if (activo) {
                    entrada.setAttribute('aria-activedescendant', elemento.id);
                    elemento.scrollIntoView({block: 'nearest'});
                }
            });
        }

        function cerrarLista() {
            lista.hidden = true;
            lista.replaceChildren();
            opciones = [];
            indiceActivo = -1;
            entrada.setAttribute('aria-expanded', 'false');
            entrada.removeAttribute('aria-activedescendant');
        }

        function mostrarEstado(mensaje) {
            estado.textContent = mensaje;
            estado.hidden = !mensaje;
        }

        function actualizarLimpiar() {
            if (limpiar) limpiar.hidden = !entrada.value;
        }
    }
})();
