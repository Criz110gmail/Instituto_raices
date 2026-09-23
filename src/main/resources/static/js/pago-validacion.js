(() => {
    const contenedor = document.querySelector('#validation-account-search');
    if (!contenedor) return;
    const entrada = contenedor.querySelector('input[type="search"]');
    const valor = document.querySelector('#cuentaDestinoId');
    const resultados = contenedor.querySelector('.autocomplete-results');
    const estado = contenedor.querySelector('.autocomplete-status');
    const limpiar = contenedor.querySelector('.autocomplete-clear');
    const formulario = document.querySelector('#validation-form');
    let timer; let controlador; let etiqueta = '';
    const mostrarEstado = mensaje => { estado.textContent = mensaje; estado.hidden = !mensaje; };
    entrada.addEventListener('focus', () => { if (!entrada.value.trim() && !resultados.children.length) buscar(''); });
    entrada.addEventListener('input', () => {
        if (entrada.value !== etiqueta) valor.value = '';
        clearTimeout(timer); controlador?.abort(); resultados.hidden = true;
        const consulta = entrada.value.trim();
        if (consulta.length < 3) { mostrarEstado('Escribe al menos 3 caracteres para buscar.'); return; }
        mostrarEstado('Buscando cuentas compatibles…'); timer = setTimeout(() => buscar(consulta), 280);
    });
    limpiar.addEventListener('click', () => {
        clearTimeout(timer); controlador?.abort();
        entrada.value = ''; etiqueta = ''; valor.value = ''; resultados.hidden = true;
        mostrarEstado('Escribe al menos 3 caracteres para buscar.');
        entrada.focus();
    });
    formulario.addEventListener('submit', evento => { if (!valor.value) { evento.preventDefault(); mostrarEstado('Selecciona una cuenta destino de la lista.'); entrada.focus(); } });
    document.addEventListener('click', evento => { if (!contenedor.contains(evento.target)) resultados.hidden = true; });
    async function buscar(consulta) {
        controlador = new AbortController();
        const parametros = new URLSearchParams({q: consulta, institucionId: document.querySelector('#validation-institution').value, plantelId: document.querySelector('#validation-campus').value, metodo: document.querySelector('#validation-method').value});
        try {
            const respuesta = await fetch(`/admin/autocompletado/cuentas-pago?${parametros}`, {headers: {'Accept': 'application/json'}, signal: controlador.signal});
            if (respuesta.redirected && new URL(respuesta.url).pathname === '/login') { location.assign('/login?sesionExpirada'); return; }
            if (!respuesta.ok) throw new Error();
            const datos = await respuesta.json(); resultados.replaceChildren();
            (datos.resultados || []).forEach(opcion => {
                const boton = document.createElement('button'); boton.type = 'button'; boton.className = 'autocomplete-option';
                const titulo = document.createElement('strong'); titulo.textContent = opcion.titulo; const detalle = document.createElement('small'); detalle.textContent = opcion.detalle || '';
                boton.append(titulo, detalle); boton.addEventListener('click', () => { entrada.value = opcion.titulo; etiqueta = opcion.titulo; valor.value = opcion.id; resultados.hidden = true; mostrarEstado(''); }); resultados.append(boton);
            });
            resultados.hidden = !resultados.children.length; mostrarEstado(resultados.children.length ? `${resultados.children.length} cuenta(s) compatible(s).` : 'No encontramos cuentas compatibles.');
        } catch (error) { if (error.name !== 'AbortError') mostrarEstado('No fue posible consultar las cuentas.'); }
    }
})();
