/* La versión pertenece al concepto seleccionado, nunca a una respuesta anterior. */
(() => {
 document.querySelectorAll('[data-prestamo-form]').forEach(form => {
  const id=form.querySelector('[name="conceptoId"]'),texto=form.querySelector('[name="conceptoTexto"]'),version=form.querySelector('[name="conceptoVersion"]'),status=form.querySelector('[data-prestamo-status]'),save=form.querySelector('[data-prestamo-save]');
  let revision=0,abort;
  async function consultar(){const actual=++revision;abort?.abort();abort=new AbortController();version.value='';save.disabled=true;if(!id.value){status.textContent='Selecciona una deducción Con saldo de las sugerencias.';return;}
   const seleccionado=id.value;status.textContent='Consultando concepto…';try{const response=await fetch(form.dataset.conceptoEndpoint+'?conceptoId='+encodeURIComponent(seleccionado),{signal:abort.signal,headers:{Accept:'application/json'}});const c=await response.json();if(actual!==revision||id.value!==seleccionado)return;if(!response.ok)throw new Error(c.mensaje||'No fue posible consultar el concepto.');if(c.texto!==texto.value)throw new Error('El concepto cambió. Vuelve a seleccionarlo.');version.value=c.version;save.disabled=false;status.textContent='Deducción con saldo confirmada. Sin intereses; guardar no entrega dinero.';}catch(e){if(actual!==revision||e.name==='AbortError')return;status.textContent=e.message||'No se pudo consultar. Selecciona nuevamente el concepto.';}}
  form.addEventListener('autocomplete:change',consultar);id.addEventListener('change',consultar);texto.addEventListener('input',()=>{revision++;abort?.abort();version.value='';save.disabled=true;});form.addEventListener('submit',e=>{if(save.disabled||version.value===''){e.preventDefault();e.stopImmediatePropagation();status.textContent='Espera la consulta del concepto antes de confirmar.';}},true);consultar();
 });
})();
