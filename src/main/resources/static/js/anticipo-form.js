(() => {
 const form=document.querySelector('#anticipation-form');if(!form)return;
 const preview=document.querySelector('#anticipation-preview-section');
 if(preview){
  const reveal=()=>{preview.focus({preventScroll:true});preview.scrollIntoView({behavior:window.matchMedia('(prefers-reduced-motion: reduce)').matches?'auto':'smooth',block:'start'});};
  if(document.readyState==='complete')requestAnimationFrame(reveal);
  else window.addEventListener('load',()=>requestAnimationFrame(reveal),{once:true});
 }
 const search=document.querySelector('#anticipo-query'),results=document.querySelector('#anticipo-results'),status=document.querySelector('#anticipo-status'),selected=document.querySelector('#anticipation-selected'),institution=document.querySelector('#institucion'),tutor=document.querySelector('#tutorId'),month=document.querySelector('#cargoBonificadoId'),kind=document.querySelector('#tipoBeneficio');let timer,abort,seq=0;
 const invalidate=()=>{const save=document.querySelector('#anticipation-save');if(save){save.disabled=true;save.textContent='Actualiza la vista previa para guardar';}form.querySelector('[name=huella]').value='';};
 function options(){const old=month.value;month.replaceChildren();const empty=document.createElement('option');empty.value='';empty.textContent='Selecciona una mensualidad agregada';month.append(empty);selected.querySelectorAll('.anticipation-row').forEach(r=>{const o=document.createElement('option');o.value=r.dataset.id;o.textContent=r.dataset.title;month.append(o);});month.value=old;}
 function attach(row){row.querySelector('[data-remove-anticipo]').addEventListener('click',()=>{row.remove();options();invalidate();});}
 selected.querySelectorAll('.anticipation-row').forEach(attach);
 function mode(){document.querySelector('#bonus-month-field').hidden=kind.value!=='MENSUALIDAD';document.querySelector('#bonus-value-field').hidden=kind.value==='MENSUALIDAD';document.querySelector('#bonus-value-label').textContent=kind.value==='PORCENTAJE'?'Porcentaje adicional (%)':'Cantidad fija a bonificar';}kind.addEventListener('change',mode);mode();
 form.addEventListener('input',invalidate);form.addEventListener('change',invalidate);
 [institution,tutor].forEach(e=>e.addEventListener('change',()=>{abort?.abort();seq++;selected.replaceChildren();results.replaceChildren();results.hidden=true;options();invalidate();}));
 async function load(q) {
  if(!institution.value||!tutor.value){status.textContent='Selecciona primero institución y tutor responsable.';return;}
  abort?.abort();abort=new AbortController();const version=++seq;status.textContent='Buscando mensualidades…';
  const params=new URLSearchParams({institucionId:institution.value,tutorId:tutor.value,q});
  const excluded=[...selected.querySelectorAll('.anticipation-row')].map(r=>r.dataset.id);if(excluded.length)params.set('excluir',excluded.join(','));
  try {
   const r=await fetch('/admin/convenios-pago/anticipados/cargos?'+params,{headers:{Accept:'application/json'},signal:abort.signal});if(r.redirected){location.assign('/login?sesionExpirada');return;}if(!r.ok)throw new Error();const data=await r.json();if(version!==seq)return;results.replaceChildren();
   data.filter(o=>![...selected.querySelectorAll('.anticipation-row')].some(row=>row.dataset.id===String(o.cargoId))).forEach(o=>{
    const b=document.createElement('button');b.type='button';b.className='autocomplete-option';const title=document.createElement('strong'),detail=document.createElement('small');title.textContent=o.alumno;detail.textContent=o.concepto+' · '+o.periodo+' · '+new Intl.NumberFormat('es-MX',{style:'currency',currency:o.moneda}).format(o.totalActual);b.append(title,detail);
    b.addEventListener('click',()=>{
     const current=[...selected.querySelectorAll('.anticipation-row')];if(current.some(row=>row.dataset.id===String(o.cargoId)))return;if(current.length>=100){status.textContent='Máximo100 mensualidades por acuerdo.';return;}
     const row=document.createElement('article');row.className='anticipation-row';row.dataset.id=String(o.cargoId);row.dataset.title=o.alumno+' · '+o.periodo;const hidden=document.createElement('input');hidden.type='hidden';hidden.name='cargoIds';hidden.value=String(o.cargoId);const body=document.createElement('div'),strong=document.createElement('strong'),small=document.createElement('small'),amount=document.createElement('small'),remove=document.createElement('button');strong.textContent=o.alumno;small.textContent=o.concepto+' · '+o.periodo;amount.textContent='Saldo actual: '+new Intl.NumberFormat('es-MX',{style:'currency',currency:o.moneda}).format(o.totalActual);body.append(strong,small,amount);remove.type='button';remove.className='change-destination-button';remove.dataset.removeAnticipo='';remove.textContent='Quitar';row.append(hidden,body,remove);selected.append(row);attach(row);options();invalidate();search.value='';results.hidden=true;search.setAttribute('aria-expanded','false');status.textContent='Mensualidad agregada; puedes seleccionar otras o visualizar el acuerdo.';
    });results.append(b);
   });results.hidden=!results.children.length;search.setAttribute('aria-expanded',String(!results.hidden));status.textContent=results.children.length?'Selecciona una mensualidad para agregarla.':'No hay otras mensualidades disponibles. Revisa su generación, abonos, permisos o acuerdos existentes.';
  }catch(e){if(e.name!=='AbortError'&&version===seq)status.textContent='No fue posible consultar mensualidades.';}
 }
 const open=()=>load(search.value.trim().length>=3?search.value.trim():'');search.addEventListener('focus',open);search.addEventListener('click',open);search.addEventListener('input',()=>{clearTimeout(timer);abort?.abort();seq++;results.hidden=true;const q=search.value.trim();if(!q)load('');else if(q.length>=3)timer=setTimeout(()=>load(q),280);else status.textContent='Escribe al menos tres caracteres para buscar.';});document.querySelector('#anticipo-clear').addEventListener('click',()=>{search.value='';search.focus();open();});document.addEventListener('click',e=>{if(!document.querySelector('#anticipo-search').contains(e.target))results.hidden=true;});
})();
