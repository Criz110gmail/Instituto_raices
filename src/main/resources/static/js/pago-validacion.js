(() => {
    const contenedor = document.querySelector('#validation-account-search');
    if (!contenedor) return;
    const entrada = contenedor.querySelector('input[type="search"]');
    const valor = document.querySelector('#cuentaDestinoId');
    const resultados = contenedor.querySelector('.autocomplete-results');
    const estado = contenedor.querySelector('.autocomplete-status');
    const limpiar = contenedor.querySelector('.autocomplete-clear');
    const formulario = document.querySelector('#validation-form');
    const cambiar = document.querySelector('#change-destination-account');
    const bloqueMotivo = document.querySelector('#destination-change-reason');
    const motivo = bloqueMotivo?.querySelector('textarea');
    const cuentaDeclaradaId = contenedor.dataset.declaredAccountId || '';
    const cuentaDeclaradaEtiqueta = contenedor.dataset.declaredAccountLabel || '';
    let timer; let controlador; let etiqueta = entrada.value;
    const mostrarEstado = mensaje => { estado.textContent = mensaje; estado.hidden = !mensaje; };
    const actualizarCambio = () => {
        const modificada = Boolean(cuentaDeclaradaId) && valor.value !== cuentaDeclaradaId;
        if (bloqueMotivo) bloqueMotivo.hidden = !modificada;
        if (motivo) motivo.required = modificada;
    };
    entrada.addEventListener('focus', () => { if (!entrada.readOnly && !entrada.value.trim() && !resultados.children.length) buscar(''); });
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
        actualizarCambio();
        entrada.focus();
    });
    cambiar?.addEventListener('click', () => {
        if (entrada.readOnly) {
            entrada.readOnly = false; limpiar.hidden = false;
            cambiar.innerHTML = '<span aria-hidden="true">↶</span> Conservar cuenta declarada';
            contenedor.classList.add('destination-change-active');
            mostrarEstado('Busca y selecciona la cuenta correcta. Si es diferente, indica el motivo.');
            entrada.focus(); entrada.select();
            return;
        }
        entrada.value = cuentaDeclaradaEtiqueta; etiqueta = cuentaDeclaradaEtiqueta;
        valor.value = cuentaDeclaradaId; entrada.readOnly = true; limpiar.hidden = true;
        resultados.hidden = true; contenedor.classList.remove('destination-change-active');
        cambiar.innerHTML = '<span aria-hidden="true">↻</span> Cambiar cuenta destino';
        if (motivo) motivo.value = '';
        actualizarCambio(); mostrarEstado('Cuenta cargada desde el registro del pago.');
    });
    formulario.addEventListener('submit', evento => {
        if (!valor.value) { evento.preventDefault(); mostrarEstado('Selecciona una cuenta destino de la lista.'); entrada.focus(); return; }
        actualizarCambio();
        if (motivo?.required && !motivo.value.trim()) { evento.preventDefault(); motivo.focus(); }
    });
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
                boton.append(titulo, detalle); boton.addEventListener('click', () => { entrada.value = opcion.titulo; etiqueta = opcion.titulo; valor.value = String(opcion.id); resultados.hidden = true; actualizarCambio(); mostrarEstado(valor.value === cuentaDeclaradaId ? 'Cuenta declarada conservada.' : 'Cuenta destino modificada; explica el motivo antes de validar.'); }); resultados.append(boton);
            });
            resultados.hidden = !resultados.children.length; mostrarEstado(resultados.children.length ? `${resultados.children.length} cuenta(s) compatible(s).` : 'No encontramos cuentas compatibles.');
        } catch (error) { if (error.name !== 'AbortError') mostrarEstado('No fue posible consultar las cuentas.'); }
    }
})();

(() => {
 const panel=document.querySelector('.received-amount-review');if(!panel)return;
 const form=document.querySelector('#validation-form'),state=document.querySelector('#received-change-value'),button=document.querySelector('#received-change'),fields=document.querySelector('#received-fields'),amount=document.querySelector('#received-amount'),reason=document.querySelector('#received-reason');
 const reported=Number(panel.dataset.reported),requested=Number(panel.dataset.requested),currency=form.dataset.confirmCurrency||'MXN';
 const money=n=>new Intl.NumberFormat('es-MX',{style:'currency',currency}).format(n);
 function update(){const enabled=state.value==='true';fields.hidden=!enabled;amount.disabled=!enabled;reason.disabled=!enabled;amount.required=enabled;reason.required=enabled;button.setAttribute('aria-expanded',String(enabled));button.textContent=enabled?'Conservar importe reportado':'El importe recibido es mayor al reportado';const value=enabled?Number(amount.value):reported;amount.setCustomValidity(enabled&&(!Number.isFinite(value)||value<=reported)?'Captura un importe mayor al reportado.':'');document.querySelector('#received-total').textContent=money(value||0);document.querySelector('#received-credit').textContent=money(Math.max(0,(value||0)-requested));form.dataset.confirmAmount=String(value||0);}
 button.addEventListener('click',()=>{state.value=state.value==='true'?'false':'true';if(state.value!=='true'){amount.value='';reason.value='';}update();});amount.addEventListener('input',update);update();
})();
