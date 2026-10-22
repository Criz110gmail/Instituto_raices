/* Sólo selección de la página actual. Los importes y la elegibilidad se verifican en servidor. */
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
