(() => {
    const panel=document.querySelector('[data-generation-selection]');
    if(!panel)return;
    const form=panel.querySelector('[data-selection-confirm]');
    if(!form)return;
    const button=form.querySelector('button[type="submit"]'),hidden=form.querySelector('[data-selection-hidden]');
    const rows=[...panel.querySelectorAll('[data-selection-row]')],page=panel.querySelector('[data-select-page]');
    const summaries=[...panel.querySelectorAll('[data-selection-summary]')];
    const total=Number(panel.dataset.selectionTotal),amount=Math.round(Number(panel.dataset.selectionAmount)*100);
    const token=panel.dataset.selectionId,key='nexo-generacion:'+token;
    const money=new Intl.NumberFormat('es-MX',{style:'currency',currency:'MXN'});
    let mode='all',changes=new Map(),ready=false;
    const selected=id=>mode==='all'?!changes.has(id):changes.has(id);
    const cents=row=>Math.round(Number(row.dataset.selectionAmount)*100);
    const totals=()=>{
        const changed=[...changes.values()].reduce((a,b)=>a+b,0);
        return {count:mode==='all'?total-changes.size:changes.size,amount:mode==='all'?amount-changed:changed};
    };
    function error(message){ready=false;button.disabled=true;summaries.forEach(s=>s.textContent=message);}
    function persist(){
        try{sessionStorage.setItem(key,JSON.stringify({mode,changes:[...changes]}));return true;}
        catch(e){error('No se pudo conservar la selección. No se puede confirmar: vuelve a visualizar en un navegador con almacenamiento de sesión disponible.');return false;}
    }
    function render(){
        rows.forEach(row=>{row.checked=selected(row.value);row.closest('tr')?.classList.toggle('generation-omitted',!row.checked);});
        if(page){page.checked=rows.every(r=>r.checked);page.indeterminate=rows.some(r=>r.checked)&&!page.checked;}
        const t=totals();
        const valid=ready&&changes.size<=5000&&t.count>0&&t.count<=total&&t.amount>=0;
        button.disabled=!valid;
        summaries.forEach(s=>s.textContent=changes.size>5000?'Máximo 5000 cambios por selección; reduce el alcance o usa Seleccionar todos/Desmarcar todos.':
            t.count+' de '+total+' registros seleccionados · Importe seleccionado: '+money.format(t.amount/100));
    }
    function change(row){
        if((mode==='all'&&row.checked)||(mode==='only'&&!row.checked))changes.delete(row.value);
        else changes.set(row.value,cents(row));
    }
    function inputs(){
        hidden.replaceChildren();
        const add=(name,value)=>{const e=document.createElement('input');e.type='hidden';e.name=name;e.value=value;hidden.appendChild(e);};
        add('seleccionIndividual',mode==='only'?'true':'false');
        for(const id of changes.keys())add(mode==='only'?'incluidos':'excluidos',id);
    }
    try{
        if(!token||token==='null')throw new Error('token');
        const stored=sessionStorage.getItem(key);
        if(stored){const data=JSON.parse(stored);if(!['all','only'].includes(data.mode)||!Array.isArray(data.changes)||data.changes.some(p=>!Array.isArray(p)||typeof p[0]!=='string'||!Number.isInteger(p[1])||p[1]<0))throw new Error('estado');mode=data.mode;changes=new Map(data.changes);}
        if(!persist())return;ready=true;render();
    }catch(e){error('No se pudo recuperar esta selección. Vuelve a pulsar Visualizar para iniciar una vista previa nueva.');return;}
    rows.forEach(row=>row.addEventListener('change',()=>{change(row);if(persist())render();}));
    page?.addEventListener('change',()=>{rows.forEach(row=>{row.checked=page.checked;change(row);});if(persist())render();});
    panel.querySelector('[data-selection-all]')?.addEventListener('click',()=>{mode='all';changes.clear();if(persist())render();});
    panel.querySelector('[data-selection-none]')?.addEventListener('click',()=>{mode='only';changes.clear();if(persist())render();});
    form.addEventListener('submit',event=>{
        const t=totals();if(!ready||changes.size>5000||t.count<=0||t.amount<0){event.preventDefault();return;}
        inputs();button.disabled=true;button.textContent='Generando selección…';
    });
})();
