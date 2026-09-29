(() => {
    const modal = document.querySelector('[data-gradebook-confirm-modal]');
    if (!modal) return;

    const dialogo = modal.querySelector('.gradebook-confirm-dialog');
    const icono = modal.querySelector('.gradebook-confirm-icon');
    const titulo = modal.querySelector('#gradebook-confirm-title');
    const descripcion = modal.querySelector('#gradebook-confirm-description');
    const nota = modal.querySelector('.gradebook-confirm-note p');
    const aceptar = modal.querySelector('.gradebook-confirm-accept');
    const cancelar = modal.querySelector('.gradebook-confirm-cancel');
    const cerrar = modal.querySelector('.gradebook-confirm-close');
    const configuracion = {
        publicar: {
            icono: '✓',
            titulo: '¿Publicar resultados?',
            descripcion: 'Las calificaciones quedarán disponibles para las familias y el bloque se protegerá contra cambios accidentales.',
            nota: 'Si necesitas corregirlas, podrás reabrir el bloque. Mientras permanezca reabierto, dejará de mostrarse en el portal familiar.',
            accion: 'Sí, publicar resultados'
        },
        reabrir: {
            icono: '↻',
            titulo: '¿Reabrir este bloque?',
            descripcion: 'Las calificaciones dejarán de estar publicadas mientras realizas las correcciones necesarias.',
            nota: 'Cuando termines, vuelve a publicar el bloque para que las familias puedan consultar nuevamente los resultados.',
            accion: 'Sí, reabrir bloque'
        }
    };

    let formularioPendiente;
    let botonPendiente;
    let focoAnterior;

    document.querySelectorAll('[data-gradebook-confirm]').forEach(boton => {
        boton.addEventListener('click', evento => {
            evento.preventDefault();
            const tipo = boton.dataset.gradebookConfirm;
            const contenido = configuracion[tipo];
            const formulario = boton.closest('form');
            if (!contenido || !formulario) return;
            formularioPendiente = formulario;
            botonPendiente = boton;
            focoAnterior = document.activeElement;
            modal.dataset.action = tipo;
            icono.textContent = contenido.icono;
            titulo.textContent = contenido.titulo;
            descripcion.textContent = contenido.descripcion;
            nota.textContent = contenido.nota;
            aceptar.textContent = contenido.accion;
            aceptar.disabled = false;
            modal.hidden = false;
            document.body.classList.add('gradebook-modal-open');
            cerrar.focus();
        });
    });

    const cerrarModal = () => {
        modal.hidden = true;
        document.body.classList.remove('gradebook-modal-open');
        formularioPendiente = undefined;
        botonPendiente = undefined;
        focoAnterior?.focus();
    };

    aceptar.addEventListener('click', () => {
        if (!formularioPendiente || !botonPendiente) return;
        const formulario = formularioPendiente;
        const boton = botonPendiente;
        aceptar.disabled = true;
        modal.hidden = true;
        document.body.classList.remove('gradebook-modal-open');
        formulario.requestSubmit(boton);
    });

    cancelar.addEventListener('click', cerrarModal);
    cerrar.addEventListener('click', cerrarModal);
    modal.addEventListener('click', evento => {
        if (evento.target === modal) cerrarModal();
    });
    document.addEventListener('keydown', evento => {
        if (modal.hidden) return;
        if (evento.key === 'Escape') {
            evento.preventDefault();
            cerrarModal();
        } else if (evento.key === 'Tab') {
            const controles = [...dialogo.querySelectorAll('button:not([disabled])')];
            const primero = controles[0];
            const ultimo = controles.at(-1);
            if (evento.shiftKey && document.activeElement === primero) {
                evento.preventDefault();
                ultimo.focus();
            } else if (!evento.shiftKey && document.activeElement === ultimo) {
                evento.preventDefault();
                primero.focus();
            }
        }
    });
})();
