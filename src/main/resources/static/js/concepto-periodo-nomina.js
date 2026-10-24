(() => {
 const form=document.querySelector('[data-concepto-periodo]');if(!form)return;
 const campo=n=>form.querySelector('[name="'+n+'"]'),boton=form.querySelector('[data-periodo-guardar]'),estado=form.querySelector('[data-periodo-status]');
 let solicitud,revision=0;
 async function consultar(){
  const actual=++revision;solicitud?.abort();boton.disabled=true;
  for(const n of ['conceptoVersion','laboralVersion','calendarioVersion','selloPeriodo'])campo(n).value='';
  form.querySelector('[data-periodo-tipo]').value='';form.querySelector('[data-periodo-fechas]').value='';
  if(!campo('periodoId').value||!campo('conceptoId').value){estado.textContent='Selecciona periodo y concepto de las sugerencias.';return;}
  solicitud=new AbortController();estado.textContent='Consultando periodo, calendario y concepto…';
  try{
   const r=await fetch(form.dataset.contextEndpoint+'?'+new URLSearchParams({periodoId:campo('periodoId').value,conceptoId:campo('conceptoId').value}),{signal:solicitud.signal,headers:{Accept:'application/json'},cache:'no-store'});
   if(r.redirected)throw new Error('La sesión terminó; vuelve a iniciar sesión.');
   const c=await r.json();if(actual!==revision)return;
   if(!r.ok)throw new Error(c.mensaje||'No fue posible consultar esta selección. Revisa el calendario asignado y los permisos.');
   if(c.periodo!==campo('periodoTexto').value||c.concepto!==campo('conceptoTexto').value)throw new Error('El nombre o código cambió. Quita la selección y vuelve a elegirla.');
   for(const n of ['conceptoVersion','laboralVersion','calendarioVersion','selloPeriodo'])campo(n).value=c[n];
   form.querySelector('[data-periodo-tipo]').value=c.tipo+' · '+c.moneda;
   const fecha=v=>v.split('-').reverse().join('/');form.querySelector('[data-periodo-fechas]').value=fecha(c.inicio)+' — '+fecha(c.fin);
   form.dataset.confirmCurrency=c.moneda;form.dataset.confirmEffect=c.tipo;estado.textContent='Sólo se propondrá en este periodo. No se divide ni se repite mensualmente.';boton.disabled=false;
  }catch(e){if(actual===revision&&e.name!=='AbortError')estado.textContent=e.message;}
 }
 campo('periodoId').addEventListener('change',consultar);campo('conceptoId').addEventListener('change',consultar);
 form.addEventListener('submit',e=>{if(!campo('selloPeriodo').value){e.preventDefault();e.stopImmediatePropagation();estado.textContent='Espera la consulta de periodo y concepto antes de confirmar.';}},true);
 consultar();
})();
