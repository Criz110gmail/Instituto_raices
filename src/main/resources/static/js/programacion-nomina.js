/* Sólo consulta el calendario elegido; no prepara ni calcula nómina. */
(() => {
 const form=document.querySelector('[data-periodo-nomina]');if(!form||form.dataset.edicion==='true')return;
 const campo=n=>form.querySelector('[name="'+n+'"]');
 const boton=form.querySelector('[data-periodo-confirmar]'),estado=form.querySelector('[data-periodo-estado]'),error=form.querySelector('[data-periodo-error]'),frecuencia=form.querySelector('[data-periodo-frecuencia]');
 let solicitud,revision=0;
 async function consultar(){
  const actual=++revision;solicitud?.abort();boton.disabled=true;campo('calendarioVersion').value='';frecuencia.value='';error.hidden=true;error.textContent='';
  if(!campo('calendarioId').value){estado.textContent='Selecciona un calendario activo para consultar su frecuencia.';return;}
  estado.textContent='Consultando calendario…';solicitud=new AbortController();
  try{
   const response=await fetch('/admin/calendarios-nomina/'+encodeURIComponent(campo('calendarioId').value)+'/contexto',{signal:solicitud.signal,headers:{Accept:'application/json'},cache:'no-store'});
   if(!response.ok||response.redirected)throw new Error('No fue posible consultar el calendario. Comprueba que esté activo; si tu sesión terminó, vuelve a iniciar sesión.');
   const datos=await response.json();if(actual!==revision)return;
   if(datos.calendario!==campo('calendarioTexto').value)throw new Error('El calendario cambió. Quita la selección y vuelve a elegirlo antes de confirmar.');
   campo('calendarioVersion').value=datos.version;frecuencia.value=datos.frecuencia;estado.textContent='Calendario consultado. Define las fechas; no se calculan importes ni se generan pagos.';boton.disabled=false;
  }catch(e){if(actual!==revision||e.name==='AbortError')return;estado.textContent='Consulta pendiente';error.textContent=e.message;error.hidden=false;}
 }
 campo('calendarioId').addEventListener('change',consultar);
 form.addEventListener('submit',event=>{if(!campo('calendarioVersion').value){event.preventDefault();error.textContent='Selecciona el calendario y espera a que termine la consulta antes de confirmar.';error.hidden=false;}},true);
 consultar();
})();
