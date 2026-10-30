/* Sólo formularios de consulta marcados: conservar los filtros, no saltar a resultados. */
(() => {
 document.querySelectorAll('[data-nomina-keep-filter]').forEach(form=>{
 const key='nomina-filtros-scroll:'+location.pathname+':'+form.dataset.nominaKeepFilter;
 form.addEventListener('submit',()=>{
  try{sessionStorage.setItem(key,JSON.stringify({offset:Math.max(16,form.getBoundingClientRect().top)}));}catch(_){/* La consulta normal sigue funcionando sin almacenamiento. */}
 });
 let saved;try{const raw=sessionStorage.getItem(key);sessionStorage.removeItem(key);if(raw)saved=JSON.parse(raw);}catch(_){}
 if(!saved||!Number.isFinite(saved.offset))return;
 const restore=()=>requestAnimationFrame(()=>requestAnimationFrame(()=>{
  if(form.closest('[data-student-panel]')?.hidden)return;
  window.scrollTo({top:Math.max(0,window.scrollY+form.getBoundingClientRect().top-saved.offset),behavior:'instant'});
  form.querySelector('button[type="submit"],.module-action--primary')?.focus({preventScroll:true});
 }));
 if(document.readyState==='complete')restore();else window.addEventListener('load',restore,{once:true});
 window.addEventListener('pageshow',event=>{if(!event.persisted)restore();},{once:true});
 });
})();
/* El servidor marca únicamente la cuenta recién agregada; revisar/errores no desplazan. */
(() => {
 const source=document.querySelector('.nomina-payment-source[data-new-source="true"]');if(!source)return;
 const mostrar=()=>requestAnimationFrame(()=>{
  source.scrollIntoView({block:'start',behavior:'instant'});
  source.querySelector('.autocomplete-input')?.focus({preventScroll:true});
 });
 if(document.readyState==='complete')mostrar();else window.addEventListener('load',mostrar,{once:true});
})();
/* Incluir/excluir conserva el POST validado y restaura la posición tras su redirect. */
(() => {
 const page=document.querySelector('.nomina-bulk');if(!page)return;
 const key='nomina-seleccion-scroll:'+location.pathname;
 page.querySelectorAll('form.nomina-row-select').forEach(form=>{
  form.addEventListener('submit',()=>{
   const row=form.closest('tr');if(!row)return;
   try{sessionStorage.setItem(key,JSON.stringify({fila:row.id,y:window.scrollY,offset:row.getBoundingClientRect().top}));}catch(_){/* El fragmento mantiene una alternativa sin almacenamiento. */}
  });
 });
 let saved;
 try{const raw=sessionStorage.getItem(key);sessionStorage.removeItem(key);if(raw)saved=JSON.parse(raw);}catch(_){return;}
 if(!saved||!/^nomina-empleado-\d+$/.test(saved.fila)||location.hash!=='#'+saved.fila||!Number.isFinite(saved.y)||!Number.isFinite(saved.offset))return;
 const restore=()=>requestAnimationFrame(()=>{
  const row=document.getElementById(saved.fila);
  const y=row?window.scrollY+row.getBoundingClientRect().top-saved.offset:saved.y;
  window.scrollTo({top:Math.max(0,y),behavior:'instant'});
  row?.querySelector('.nomina-row-select button')?.focus({preventScroll:true});
 });
 if(document.readyState==='complete')restore();else window.addEventListener('load',restore,{once:true});
})();
/* La selección guardada abarca todas las páginas; no contar sólo las filas visibles. */
document.querySelectorAll('.nomina-bulk-save[data-confirm-people]').forEach(form=>{
 form.addEventListener('submit',event=>{
  if(Number(form.dataset.confirmPeople)>0)return;
  event.preventDefault();
  const aviso=form.querySelector('[data-nomina-empty-warning]');
  if(aviso){aviso.hidden=false;aviso.focus({preventScroll:true});aviso.scrollIntoView({block:'center',behavior:'smooth'});}
 });
});
/* Sólo selección de la página actual. Los importes y la elegibilidad se verifican en servidor. */
/* El medio visible es informativo: el servidor lo determina de nuevo al revisar y pagar. */
document.querySelectorAll('.nomina-payment-source').forEach(fuente=>{
 const medio=fuente.querySelector('.nomina-source-medium');
 if(!medio)return;
 const mostrar=valor=>medio.value=valor==='EFECTIVO'?'Efectivo':valor==='TRANSFERENCIA'?'Transferencia':valor||'';
 mostrar(medio.value);
 fuente.querySelector('.autocomplete-value')?.addEventListener('change',event=>{
  const tipo=event.target.value?event.target.dataset.detalle:'';
  mostrar(tipo==='CAJA'?'EFECTIVO':tipo==='BANCO'?'TRANSFERENCIA':'');
 });
});
(() => {
 const form=document.querySelector('[data-preparacion-selection]');if(!form)return;
 const checks=()=>Array.from(form.querySelectorAll('input[name="seleccion"]'));
 const status=form.querySelector('[data-nomina-selection-status]'),error=form.querySelector('[data-nomina-selection-error]');
 const actualizar=()=>{if(status)status.textContent=checks().filter(e=>e.checked).length+' empleados seleccionados de esta página';if(error)error.hidden=true;};
 checks().forEach(e=>e.addEventListener('change',actualizar));
 form.querySelectorAll('[data-nomina-select]').forEach(b=>b.addEventListener('click',()=>{checks().forEach(e=>e.checked=b.dataset.nominaSelect==='todos');actualizar();}));
 form.addEventListener('submit',event=>{if(!checks().some(e=>e.checked)){event.preventDefault();if(error){error.textContent='Selecciona al menos un empleado listo para preparar en esta página.';error.hidden=false;error.scrollIntoView({block:'center',behavior:'smooth'});}}});
 actualizar();
})();
