(() => {
    const form = document.querySelector('[data-event-form]');
    if (!form) return;
    const institution = document.getElementById('evento-institucion');
    const cycle = document.getElementById('evento-ciclo');
    const campus = document.getElementById('evento-plantel');
    const scope = document.getElementById('evento-alcance');
    const builder = document.querySelector('[data-event-recipients]');
    const type = builder.querySelector('[data-recipient-type]');
    const query = builder.querySelector('[data-recipient-query]');
    const results = builder.querySelector('[data-recipient-results]');
    const chips = builder.querySelector('[data-recipient-chips]');
    const empty = builder.querySelector('[data-recipient-empty]');
    const status = builder.querySelector('[data-recipient-status]');
    const count = builder.querySelector('[data-recipient-count]');
    let timer;

    const filterOptions = (select, value) => {
        if (!select) return;
        let keep = !select.value;
        [...select.options].forEach(option => {
            if (!option.value) return;
            const visible = option.dataset.institucion === value;
            option.hidden = !visible; option.disabled = !visible;
            if (visible && option.value === select.value) keep = true;
        });
        if (!keep) select.value = '';
    };
    const updateContext = () => { filterOptions(cycle, institution.value); filterOptions(campus, institution.value); };
    const updateScope = () => {
        const selection = scope.value === 'SELECCION';
        builder.hidden = !selection;
        document.querySelector('[data-event-plantel]').hidden = scope.value === 'INSTITUCION';
        if (scope.value === 'INSTITUCION') campus.value = '';
        if (!selection) [...chips.querySelectorAll('.recipient-chip')].forEach(chip => chip.remove());
        updateCount();
    };
    const updateCount = () => {
        const total = chips.querySelectorAll('.recipient-chip').length;
        count.textContent = `${total} ${total === 1 ? 'destino' : 'destinos'}`;
        empty.hidden = total > 0;
    };
    const escape = value => String(value ?? '').replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
    const add = (id, title, detail) => {
        const key = `${type.value}:${id}`;
        if (chips.querySelector(`[data-key="${key}"]`)) { status.textContent = 'Ese destinatario ya está agregado.'; return; }
        const chip = document.createElement('div'); chip.className = 'recipient-chip'; chip.dataset.key = key;
        chip.innerHTML = `<input type="hidden" name="destinatarios" value="${escape(key)}"><span><strong>${escape(title)}</strong><small>${escape(type.value)} · ${escape(detail)}</small></span><button type="button" data-remove-recipient aria-label="Quitar destinatario">×</button>`;
        chips.appendChild(chip); query.value = ''; results.hidden = true; status.textContent = 'Destinatario agregado.'; updateCount();
    };
    const search = async () => {
        const text = query.value.trim(); if (text.length < 3) { results.hidden = true; status.textContent = 'Escribe al menos 3 caracteres.'; return; }
        if (!institution.value || !cycle.value) { status.textContent = 'Selecciona institución y ciclo antes de buscar.'; return; }
        const params = new URLSearchParams({tipo:type.value,institucionId:institution.value,cicloId:cycle.value,q:text});
        if (campus.value) params.set('plantelId', campus.value);
        status.textContent = 'Buscando…';
        try {
            const response = await fetch(`/admin/autocompletado/destinatarios-evento?${params}`, {headers:{Accept:'application/json'}});
            if (!response.ok) throw new Error(); const data = await response.json(); results.replaceChildren();
            data.resultados.forEach(item => { const button=document.createElement('button'); button.type='button'; button.className='recipient-result';
                button.innerHTML=`<strong>${escape(item.titulo)}</strong><small>${escape(item.detalle)}</small>`;
                button.addEventListener('click',()=>add(item.id,item.titulo,item.detalle)); results.appendChild(button); });
            results.hidden = data.resultados.length === 0; status.textContent = data.resultados.length ? (data.hayMas?'Hay más coincidencias; afina la búsqueda.':'Selecciona una coincidencia.') : 'No se encontraron coincidencias.';
        } catch (_) { results.hidden = true; status.textContent = 'No fue posible consultar los destinatarios.'; }
    };
    builder.addEventListener('click', event => { const button=event.target.closest('[data-remove-recipient]'); if(button){button.closest('.recipient-chip').remove();updateCount();} });
    query.addEventListener('input',()=>{clearTimeout(timer);timer=setTimeout(search,280);});
    type.addEventListener('change',()=>{query.value='';results.hidden=true;status.textContent='';});
    institution.addEventListener('change',()=>{updateContext();}); scope.addEventListener('change',updateScope);
    updateContext(); updateScope(); updateCount();
})();
