(() => {
    const form=document.querySelector('[data-planeacion-form]');
    if(!form||form.dataset.readonly==='true')return;
    const group=form.querySelector('[data-plan-group]'),start=form.querySelector('[data-plan-start]'),end=form.querySelector('[data-plan-end]'),box=form.querySelector('[data-plan-subjects]');
    const names=new Map([...form.querySelectorAll('[data-subject-select] option')].filter(o=>o.value).map(o=>[o.value,o.textContent]));
    let subjects=[...box.querySelectorAll('input[name=materiaIds]')].map(c=>({id:c.value,nombre:names.get(c.value)||c.closest('label').textContent.trim()}));
    let request,sequence=0;
    const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
    const changed=()=>form.dispatchEvent(new Event('planeacion:rows-changed',{bubbles:true}));
    function syncSelects(){
        const selected=new Set([...box.querySelectorAll('input[name=materiaIds]:checked')].map(c=>c.value));
        const options=subjects.filter(x=>selected.has(String(x.id)));
        form.querySelectorAll('[data-subject-select]').forEach(s=>{
            const current=s.value,first=s.querySelector('option')?.outerHTML||'<option value="">Selecciona</option>';
            s.innerHTML=first+options.map(x=>'<option value="'+esc(x.id)+'">'+esc(x.nombre)+'</option>').join('');
            s.value=options.some(x=>String(x.id)===current)?current:'';
        });
    }
    async function load(){
        const current=++sequence;request?.abort();subjects=[];box.dataset.loading='false';
        if(!group?.value||!start.value||!end.value){box.innerHTML='<p class="subject-help">Selecciona grupo y fechas para consultar tus materias.</p>';syncSelects();changed();return;}
        request=new AbortController();box.dataset.loading='true';box.innerHTML='<p class="subject-help">Consultando materias asignadas…</p>';syncSelects();changed();
        try{
            const u=new URL('/maestros/api/materias',location.origin);u.searchParams.set('grupoId',group.value);u.searchParams.set('fechaInicio',start.value);u.searchParams.set('fechaFin',end.value);
            const r=await fetch(u,{headers:{Accept:'application/json'},signal:request.signal});if(!r.ok||r.redirected)throw new Error();
            const data=await r.json();if(current!==sequence)return;if(!Array.isArray(data))throw new Error();
            subjects=data;
            box.innerHTML=subjects.length?subjects.map(x=>'<label><input type="checkbox" name="materiaIds" value="'+esc(x.id)+'"><span>'+esc(x.codigo)+' · '+esc(x.nombre)+'</span></label>').join(''):'<p class="subject-help">No tienes materias asignadas durante todo ese rango.</p>';
        }catch(e){if(current!==sequence||e.name==='AbortError')return;box.innerHTML='<p class="subject-help error" role="alert">No fue posible consultar las materias. Vuelve a seleccionar el grupo o las fechas.</p>';}
        finally{if(current===sequence){box.dataset.loading='false';syncSelects();changed();}}
    }
    [group,start,end].forEach(x=>x?.addEventListener('change',load));
    box.addEventListener('change',()=>{syncSelects();changed();});
    function reindex(container,prefix,label){
        [...container.querySelectorAll('.dynamic-card')].forEach((card,i)=>{
            card.querySelector('[data-plan-row-title]').textContent=label+' '+(i+1);
            card.querySelectorAll('[name]').forEach(c=>{c.name=c.name.replace(/^(alineaciones|actividades)\[\d+\]/,prefix+'['+i+']');c.id='plan-'+c.name.replace(/[^a-zA-Z0-9_-]/g,'-');});
        });
    }
    function add(templateId,container,prefix,label){
        const node=document.getElementById(templateId).content.cloneNode(true);
        node.querySelectorAll('[data-name]').forEach(x=>{x.name=x.dataset.name.replace('{i}',container.children.length);x.removeAttribute('data-name');});
        container.append(node);reindex(container,prefix,label);syncSelects();
        if(templateId==='activity-template')container.lastElementChild.querySelector('input[type=date]').value=start.value;
        changed();container.lastElementChild.querySelector('input,select,textarea')?.focus();
    }
    const alignments=form.querySelector('[data-alignments]'),activities=form.querySelector('[data-activities]');
    form.querySelector('[data-add-alignment]').addEventListener('click',()=>add('alignment-template',alignments,'alineaciones','Alineación'));
    form.querySelector('[data-add-activity]').addEventListener('click',()=>add('activity-template',activities,'actividades','Actividad'));
    form.addEventListener('click',e=>{const button=e.target.closest('.remove-row');if(!button)return;const card=button.closest('.dynamic-card'),container=card.parentElement;card.remove();reindex(container,container===activities?'actividades':'alineaciones',container===activities?'Actividad':'Alineación');changed();form.querySelector(container===activities?'[data-add-activity]':'[data-add-alignment]').focus();});
    reindex(alignments,'alineaciones','Alineación');reindex(activities,'actividades','Actividad');syncSelects();
})();
