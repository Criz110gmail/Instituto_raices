/* Frecuencia y contexto privados. Nada se calcula ni guarda al seleccionar. */
(() => {
 const form=document.querySelector('[data-nomina-concepto]');if(!form)return;
 const field=n=>form.querySelector('[name="'+n+'"]');
 const status=form.querySelector('[data-nomina-status]'),effect=form.querySelector('[data-nomina-efecto]'),submit=form.querySelector('[data-nomina-submit]');
 let conceptReady=false,periodReady=false,conceptRevision=0,periodRevision=0,conceptRequest,periodRequest;
 const unique=()=>field('modalidad').value==='UNICA';
 const refresh=()=>{submit.disabled=!conceptReady||(unique()&&!periodReady);};
 async function concept(){
  const revision=++conceptRevision;conceptRequest?.abort();conceptReady=false;field('conceptoVersion').value='';field('laboralVersion').value='';effect.value='';refresh();
  if(!field('conceptoId').value){status.textContent='Selecciona una percepción o deducción de las sugerencias.';return;}
  conceptRequest=new AbortController();status.textContent='Consultando concepto…';
  try{const r=await fetch(form.dataset.contextUrl+'?'+new URLSearchParams({conceptoId:field('conceptoId').value}),{cache:'no-store',headers:{Accept:'application/json'},signal:conceptRequest.signal});
   if(!r.ok||r.redirected)throw Error('No se pudo consultar el concepto. Revisa permisos, sesión, actividad laboral y que no sea Sueldo base.');
   const d=await r.json();if(revision!==conceptRevision)return;
   if(d.concepto!==field('conceptoTexto').value||d.empleado!==field('empleadoTexto').value)throw Error('La selección cambió. Vuelve a elegir el concepto.');
   field('conceptoVersion').value=d.conceptoVersion;field('laboralVersion').value=d.laboralVersion;effect.value=d.tipo+' · '+d.moneda;form.dataset.confirmCurrency=d.moneda;form.dataset.confirmEffect=d.efecto;
   conceptReady=true;status.textContent=d.efecto+'. Sólo configuración: sin cálculo ni pago.';
  }catch(e){if(revision!==conceptRevision||e.name==='AbortError')return;status.textContent=e.message;}refresh();
 }
 async function period(){
  const revision=++periodRevision;periodRequest?.abort();periodReady=false;field('periodoVersion').value='';refresh();if(!unique())return;
  field('inicio').value='';field('fin').value='';if(!field('periodoId').value)return;
  periodRequest=new AbortController();
  try{const r=await fetch(form.dataset.periodUrl+'?'+new URLSearchParams({periodoId:field('periodoId').value}),{cache:'no-store',headers:{Accept:'application/json'},signal:periodRequest.signal});if(!r.ok||r.redirected)throw Error('El periodo no está disponible. Vuelve a seleccionarlo.');const d=await r.json();if(revision!==periodRevision)return;
   if(d.texto!==field('periodoTexto').value)throw Error('El periodo cambió; vuelve a seleccionarlo.');
   field('inicio').value=d.inicio;field('fin').value=d.fin;field('periodoVersion').value=d.version;periodReady=true;
  }catch(e){if(revision!==periodRevision||e.name==='AbortError')return;status.textContent=e.message;}refresh();
 }
 function mode(initial=false){
  ++periodRevision;periodRequest?.abort();periodReady=false;
  form.querySelector('[data-nomina-unica]').hidden=!unique();form.querySelector('[data-nomina-recurrente]').hidden=unique();
  field('periodoTexto').required=unique();field('inicio').readOnly=unique();field('fin').readOnly=unique();field('fin').required=unique();
  if(!unique()){field('periodoId').value='';field('periodoTexto').value='';field('periodoVersion').value='';if(!initial){field('inicio').value='';field('fin').value='';}}
  else period();refresh();
 }
 field('conceptoId').addEventListener('change',concept);field('periodoId').addEventListener('change',period);field('modalidad').addEventListener('change',()=>mode());
 form.addEventListener('submit',e=>{if(!conceptReady||unique()&&!periodReady){e.preventDefault();e.stopImmediatePropagation();status.textContent='Espera la consulta válida de concepto y periodo antes de confirmar.';}},true);
 mode(true);concept();
})();
