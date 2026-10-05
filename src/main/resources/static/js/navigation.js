(() => {
    const navegacion = document.querySelector('.sidebar nav');
    if (!navegacion) return;

    const ordenSecciones = [
        'Operación escolar · Control escolar',
        'Operación escolar · Gestión académica',
        'Operación escolar · Cobranza escolar',
        'Operación escolar · Comunicación',
        'Administración · Finanzas',
        'Administración · Compras y proveedores',
        'Administración · Configuración escolar',
        'Administración · Seguridad y soporte'
    ];
    const ordenModulos = {
        'Operación escolar · Control escolar': ['Alumnos', 'Tutores', 'Familiares del alumno', 'Inscripciones', 'Grupos', 'Actualizaciones familiares'],
        'Operación escolar · Gestión académica': ['Materias', 'Maestros', 'Horarios y clases', 'Calendario escolar', 'Planeaciones', 'Asistencia', 'Calificaciones', 'Boletas'],
        'Operación escolar · Cobranza escolar': ['Conceptos de cobro', 'Cuotas por alumno', 'Tipos de beca', 'Becas por alumno', 'Adeudos de alumnos', 'Convenios de pago', 'Historial de ajustes', 'Políticas de recargo'],
        'Administración · Finanzas': ['Pagos recibidos', 'Motivos financieros', 'Cuentas financieras', 'Movimientos financieros', 'Retiros de fondos', 'Reportes financieros']
    };
    const clavePreferencias = 'nexo.menu.secciones-abiertas';
    const secciones = new Map();
    let seccionActual = null;

    [...navegacion.children].forEach(elemento => {
        if (elemento.matches('.nav-label')) {
            const nombre = elemento.textContent.trim();
            if (!secciones.has(nombre)) secciones.set(nombre, { nombre, enlaces: [] });
            seccionActual = secciones.get(nombre);
            return;
        }
        if (elemento.matches('a') && seccionActual) seccionActual.enlaces.push(elemento);
    });

    const preferencias = leerPreferencias();
    const fragmento = document.createDocumentFragment();
    const ordenadas = [...secciones.values()].sort((a, b) => {
        const izquierda = ordenSecciones.indexOf(a.nombre);
        const derecha = ordenSecciones.indexOf(b.nombre);
        return (izquierda < 0 ? 999 : izquierda) - (derecha < 0 ? 999 : derecha);
    });

    let areaAnterior = '';
    ordenadas.forEach((seccion, indice) => {
        ordenarEnlaces(seccion);
        const partes = seccion.nombre.split(' · ');
        const area = partes.shift() || seccion.nombre;
        const nombreVisible = partes.join(' · ') || area;
        if (area !== areaAnterior) {
            const tituloArea = document.createElement('p');
            const esOperacion = area === 'Operación escolar';
            tituloArea.className = `nav-area-title ${esOperacion ? 'school-area' : 'admin-area'}`;
            tituloArea.innerHTML = `<span aria-hidden="true">${esOperacion ? '◎' : '◆'}</span><strong>${escapar(area)}</strong>`;
            fragmento.append(tituloArea);
            areaAnterior = area;
        }
        const contenedor = document.createElement('section');
        contenedor.className = 'nav-section';
        contenedor.dataset.section = seccion.nombre;

        const id = `nav-section-${indice}`;
        const boton = document.createElement('button');
        boton.type = 'button';
        boton.className = 'nav-section-toggle';
        boton.setAttribute('aria-controls', id);
        boton.innerHTML = `<span>${escapar(nombreVisible)}</span><i aria-hidden="true"></i>`;

        const lista = document.createElement('div');
        lista.className = 'nav-section-items';
        lista.id = id;
        seccion.enlaces.forEach(enlace => lista.append(enlace));

        const contieneActivo = Boolean(lista.querySelector('a.active'));
        const abierta = contieneActivo || preferencias.has(seccion.nombre);
        establecerEstado(boton, lista, abierta);
        boton.addEventListener('click', () => {
            const mostrar = boton.getAttribute('aria-expanded') !== 'true';
            establecerEstado(boton, lista, mostrar);
            guardarPreferencias(navegacion);
            if (mostrar) boton.scrollIntoView({ block: 'nearest' });
        });

        contenedor.append(boton, lista);
        fragmento.append(contenedor);
    });

    navegacion.replaceChildren(fragmento);
    centrarModuloActivo(navegacion);

    function ordenarEnlaces(seccion) {
        const orden = ordenModulos[seccion.nombre];
        if (!orden) return;
        seccion.enlaces.sort((a, b) => {
            const izquierda = orden.indexOf(a.textContent.trim());
            const derecha = orden.indexOf(b.textContent.trim());
            return (izquierda < 0 ? 999 : izquierda) - (derecha < 0 ? 999 : derecha);
        });
    }

    function establecerEstado(boton, lista, abierta) {
        boton.setAttribute('aria-expanded', String(abierta));
        lista.hidden = !abierta;
    }

    function leerPreferencias() {
        try {
            const guardadas = JSON.parse(localStorage.getItem(clavePreferencias) || '[]');
            return new Set(Array.isArray(guardadas) ? guardadas : []);
        } catch (_) {
            return new Set();
        }
    }

    function guardarPreferencias(nav) {
        const abiertas = [...nav.querySelectorAll('.nav-section')]
            .filter(seccion => seccion.querySelector('.nav-section-toggle')?.getAttribute('aria-expanded') === 'true')
            .map(seccion => seccion.dataset.section);
        try {
            localStorage.setItem(clavePreferencias, JSON.stringify(abiertas));
        } catch (_) {
            // El menú sigue funcionando aunque el navegador bloquee almacenamiento local.
        }
    }

    function centrarModuloActivo(nav) {
        const moduloActivo = nav.querySelector('a.active');
        if (!moduloActivo) return;
        requestAnimationFrame(() => {
            const rectNavegacion = nav.getBoundingClientRect();
            const rectActivo = moduloActivo.getBoundingClientRect();
            if (rectActivo.top < rectNavegacion.top || rectActivo.bottom > rectNavegacion.bottom) {
                nav.scrollTop = Math.max(0, nav.scrollTop + rectActivo.top - rectNavegacion.top
                    - (nav.clientHeight - rectActivo.height) / 2);
            }
        });
    }

    function escapar(valor) {
        const nodo = document.createElement('span');
        nodo.textContent = valor;
        return nodo.innerHTML;
    }
})();
