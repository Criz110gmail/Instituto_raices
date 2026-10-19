(() => {
    function mensajeCampo(campo, rango = {}) {
        const valor=String(campo.valor ?? '').trim();
        if(campo.requerido&&!valor)return 'Este campo es obligatorio.';
        if(!valor)return '';
        if(campo.maxLength>0&&valor.length>campo.maxLength)return `Usa como máximo ${campo.maxLength} caracteres.`;
        if(campo.tipo==='number'&&(!Number.isFinite(Number(valor))||!Number.isInteger(Number(valor))||Number(valor)<Number(campo.min)||Number(valor)>Number(campo.max)))return 'Indica una duración entre 1 y 1440 minutos, sin decimales.';
        if(campo.nombre==='fechaFin'&&rango.inicio&&valor<rango.inicio)return 'La fecha final no puede ser anterior a la inicial.';
        if(campo.nombre==='fechaFin'&&rango.inicio&&((Date.parse(valor+'T00:00:00Z')-Date.parse(rango.inicio+'T00:00:00Z'))/86400000)>6)return 'La planeación puede abarcar como máximo siete días.';
        if(/^actividades\[\d+\]\.fecha$/.test(campo.nombre)&&((rango.inicio&&valor<rango.inicio)||(rango.fin&&valor>rango.fin)))return 'La actividad debe quedar dentro de las fechas de la semana.';
        return '';
    }
    if(typeof module!=='undefined'&&module.exports)module.exports={mensajeCampo};
    if(typeof document==='undefined')return;
    const form=document.querySelector('[data-planeacion-form]');if(!form)return;
    const tabs=[...form.querySelectorAll('[data-plan-tab]')],panels=[...form.querySelectorAll('[data-plan-panel]')];
    const summary=form.querySelector('[data-plan-error-summary]'),list=form.querySelector('[data-plan-error-list]'),title=form.querySelector('[data-plan-error-title]');
    const prev=form.querySelector('[data-plan-prev]'),next=form.querySelector('[data-plan-next]');let active=0,attempted=false;
    function activate(index,focus=false){active=Math.max(0,Math.min(tabs.length-1,index));tabs.forEach((tab,i)=>{tab.setAttribute('aria-selected',String(i===active));tab.tabIndex=i===active?0:-1;});panels.forEach((panel,i)=>{panel.hidden=i!==active;});prev.hidden=active===0;next.hidden=active===tabs.length-1;form.querySelector('[data-plan-current-section]').textContent=tabs[active].querySelector('strong').textContent;if(focus)tabs[active].focus();}
    tabs.forEach((tab,i)=>{tab.addEventListener('click',()=>activate(i));tab.addEventListener('keydown',e=>{if(!['ArrowLeft','ArrowRight','Home','End'].includes(e.key))return;e.preventDefault();activate(e.key==='Home'?0:e.key==='End'?tabs.length-1:(i+(e.key==='ArrowRight'?1:tabs.length-1))%tabs.length,true);});});
    prev.addEventListener('click',()=>{activate(active-1);tabs[active].scrollIntoView({block:'center',behavior:'smooth'});});next.addEventListener('click',()=>{activate(active+1);tabs[active].scrollIntoView({block:'center',behavior:'smooth'});});
    function paint(control,message){
        const label=control.closest('label')||control;let error=label.querySelector('[data-plan-field-error]');
        if(!error){error=document.createElement('small');error.className='field-error';error.dataset.planFieldError='';label.append(error);}
        if(!control.id)control.id='plan-'+(control.name||'campo').replace(/[^a-zA-Z0-9_-]/g,'-');
        const previousId=error.id;error.id=control.id+'-validation';error.textContent=message;control.classList.toggle('plan-invalid',Boolean(message));control.setAttribute('aria-invalid',String(Boolean(message)));
        const described=new Set((control.getAttribute('aria-describedby')||'').split(/\s+/).filter(x=>x&&x!==previousId));described.add(error.id);control.setAttribute('aria-describedby',[...described].join(' '));
    }
    function labelOf(control){const label=control.closest('label')?.querySelector('span');return label?[...label.childNodes].filter(n=>n.nodeType===3).map(n=>n.textContent).join(' ').replace('*','').trim():'Campo';}
    function collectServer(){return [...form.querySelectorAll('[data-plan-field-error]')].filter(e=>e.textContent.trim()).map(error=>{const control=error.closest('label')?.querySelector('input:not([type=hidden]),select,textarea');if(control)paint(control,error.textContent.trim());return {control:control||error,panel:error.closest('[data-plan-panel]'),message:error.textContent.trim(),label:control?labelOf(control):'Revisa la sección'};});}
    function present(errors,focus=false){
        summary.hidden=!errors.length;list.replaceChildren();title.textContent=`Revisa ${errors.length} ${errors.length===1?'campo':'campos'} antes de guardar`;
        tabs.forEach((tab,i)=>{const count=errors.filter(e=>e.panel===panels[i]).length,badge=tab.querySelector('[data-plan-error-count]');badge.textContent=count;badge.hidden=!count;tab.classList.toggle('has-errors',count>0);});
        errors.forEach(error=>{const li=document.createElement('li'),button=document.createElement('button');button.type='button';const row=error.control.closest('.dynamic-card')?.querySelector('[data-plan-row-title]')?.textContent;button.textContent=`${row?row+' · ':''}${error.label}: ${error.message}`;button.addEventListener('click',()=>{activate(Math.max(0,panels.indexOf(error.panel)));error.control.focus();error.control.scrollIntoView({block:'center',behavior:'smooth'});});li.append(button);list.append(li);});
        if(focus&&errors.length){activate(Math.max(0,panels.indexOf(errors[0].panel)));summary.focus();summary.scrollIntoView({block:'center',behavior:'smooth'});}
    }
    activate(0);const serverErrors=collectServer();if(serverErrors.length){attempted=true;present(serverErrors);activate(Math.max(0,panels.indexOf(serverErrors[0].panel)));}
    if(form.dataset.readonly==='true')return;
    const subjectBox=form.querySelector('[data-plan-subjects]'),subjectPicker=form.querySelector('[data-plan-subject-picker]'),activityBox=form.querySelector('[data-activities]');
    function groupError(container,message,id){let error=container.querySelector(':scope > [data-plan-field-error]');if(!error){error=document.createElement('small');error.className='field-error';error.dataset.planFieldError='';container.append(error);}error.textContent=message;error.id=id;container.classList.toggle('plan-invalid',Boolean(message));return error;}
    function validate(){
        const errors=[],rango={inicio:form.querySelector('[data-plan-start]').value,fin:form.querySelector('[data-plan-end]').value};
        [...form.querySelectorAll('.teacher-fields input:not([type=hidden]),.teacher-fields select,.teacher-fields textarea')].filter(c=>!c.disabled).forEach(control=>{
            let message=mensajeCampo({valor:control.value,requerido:control.required,tipo:control.type,nombre:control.name,maxLength:control.maxLength,min:control.min,max:control.max},rango);
            if(control.validity?.badInput)message='Captura un valor válido para este campo.';
            paint(control,message);if(message)errors.push({control,panel:control.closest('[data-plan-panel]'),message,label:labelOf(control)});
        });
        let subjectMessage=subjectBox.dataset.loading==='true'?'Espera a que termine la consulta de materias.':!subjectBox.querySelector('input[name=materiaIds]:checked')?'Selecciona al menos una materia para esta semana.':'';
        groupError(subjectPicker,subjectMessage,'plan-materias-error');subjectBox.setAttribute('aria-invalid',String(Boolean(subjectMessage)));subjectBox.setAttribute('aria-describedby','plan-materias-error');subjectBox.tabIndex=-1;
        if(subjectMessage)errors.push({control:subjectBox,panel:subjectPicker.closest('[data-plan-panel]'),message:subjectMessage,label:'Materias que trabajarás'});
        const activityMessage=activityBox.querySelector('.dynamic-card')?'':'Agrega al menos una actividad.';const activityError=form.querySelector('[data-plan-activities-error]');groupError(activityError,activityMessage,'plan-actividades-error');
        if(activityMessage)errors.push({control:form.querySelector('[data-add-activity]'),panel:activityBox.closest('[data-plan-panel]'),message:activityMessage,label:'Actividades'});
        // Orden visual por sección para que el primer error sea predecible.
        errors.sort((a,b)=>panels.indexOf(a.panel)-panels.indexOf(b.panel));present(errors);return errors;
    }
    form.addEventListener('submit',event=>{attempted=true;const errors=validate();if(errors.length){event.preventDefault();present(errors,true);}});
    ['input','change','planeacion:rows-changed'].forEach(name=>form.addEventListener(name,()=>{if(attempted)validate();}));
})();
