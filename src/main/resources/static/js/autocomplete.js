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
        const institucion = document.getElementById(contenedor.dataset.institutionInput);
        if (!entrada || !valor || !lista || !estado || !institucion || entrada.disabled) return;

        let temporizador;
        let solicitud;
        let opciones = [];
        let indiceActivo = -1;
        let etiquetaSeleccionada = entrada.value;

        actualizarLimpiar();
        entrada.addEventListener('input', () => {
            if (entrada.value !== etiquetaSeleccionada) {
                valor.value = '';
                etiquetaSeleccionada = '';
                actualizarLimpiar();
            }
            clearTimeout(temporizador);
            solicitud?.abort();
            cerrarLista();
            const consulta = entrada.value.trim();
            if (!institucion.value) {
                mostrarEstado('Selecciona primero una institución.');
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
            etiquetaSeleccionada = '';
            cerrarLista();
            mostrarEstado(`Escribe al menos ${MINIMO} caracteres para buscar.`);
            actualizarLimpiar();
            entrada.focus();
        });

        institucion.addEventListener('change', () => {
            entrada.value = '';
            valor.value = '';
            etiquetaSeleccionada = '';
            cerrarLista();
            mostrarEstado(institucion.value
                ? `Escribe al menos ${MINIMO} caracteres para buscar.`
                : 'Selecciona primero una institución.');
            actualizarLimpiar();
        });

        document.addEventListener('click', evento => {
            if (!contenedor.contains(evento.target)) cerrarLista();
        });

        async function buscar(consulta) {
            solicitud = new AbortController();
            const parametros = new URLSearchParams({institucionId: institucion.value, q: consulta});
            if (contenedor.dataset.excludeId) parametros.set('tutorId', contenedor.dataset.excludeId);
            try {
                const respuesta = await fetch(`${contenedor.dataset.endpoint}?${parametros}`, {
                    headers: {'Accept': 'application/json'},
                    signal: solicitud.signal
                });
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
            etiquetaSeleccionada = opcion.titulo;
            cerrarLista();
            mostrarEstado(`Seleccionado: ${opcion.titulo}`);
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
        }

        function actualizarLimpiar() {
            if (limpiar) limpiar.hidden = !entrada.value;
        }
    }
})();
