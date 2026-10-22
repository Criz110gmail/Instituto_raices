/* Consulta sólo el contexto de las dos selecciones; no calcula ni publica nómina. */
(() => {
 const form=document.querySelector('[data-concepto-empleado]');if(!form)return;
 const campo=name=>form.querySelector('[name="'+name+'"]');
 const boton=form.querySelector('[data-contexto-confirmar]'),estado=form.querySelector('[data-contexto-estado]'),error=form.querySelector('[data-contexto-error]'),efecto=form.querySelector('[data-contexto-efecto]');
 let solicitud,revision=0;
 async function consultar(){
  const actual=++revision;solicitud?.abort();boton.disabled=true;campo('conceptoVersion').value='';campo('laboralVersion').value='';efecto.value='';error.hidden=true;error.textContent='';
  if(!campo('empleadoId').value||!campo('conceptoId').value){estado.textContent='Selecciona empleado y concepto para consultar su tipo, efecto y moneda.';return;}
  estado.textContent='Consultando empleado y concepto…';solicitud=new AbortController();
  try{
   const params=new URLSearchParams({empleadoId:campo('empleadoId').value,conceptoId:campo('conceptoId').value});
   const response=await fetch('/admin/conceptos-empleado/contexto?'+params,{signal:solicitud.signal,headers:{Accept:'application/json'},cache:'no-store'});
   if(!response.ok||response.redirected)throw new Error('No fue posible consultar la selección. Revisa que el empleado tenga actividad e ingreso laboral confirmados y que el concepto esté activo y sea habitual. Si tu sesión terminó, vuelve a iniciar sesión.');
   const datos=await response.json();if(actual!==revision)return;
   if(datos.concepto!==campo('conceptoTexto').value||datos.empleado!==campo('empleadoTexto').value)throw new Error('El nombre o código cambió. Quita las selecciones y vuelve a elegirlas desde la lista.');
   campo('conceptoVersion').value=datos.conceptoVersion;campo('laboralVersion').value=datos.laboralVersion;form.dataset.confirmCurrency=datos.moneda;form.dataset.confirmEffect=datos.efecto;
   efecto.value=datos.moneda+' · '+datos.tipo;estado.textContent=datos.efecto+'. Importe fijo por nómina, sin cálculos ni pagos en esta etapa.';boton.disabled=false;
  }catch(e){if(actual!==revision||e.name==='AbortError')return;estado.textContent='Consulta pendiente';error.textContent=e.message;error.hidden=false;}
 }
 campo('empleadoId').addEventListener('change',consultar);campo('conceptoId').addEventListener('change',consultar);
 form.addEventListener('submit',event=>{if(!campo('conceptoVersion').value||!campo('laboralVersion').value){event.preventDefault();error.textContent='Selecciona empleado y concepto y espera la consulta antes de confirmar.';error.hidden=false;}},true);
 consultar();
})();
