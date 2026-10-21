(() => {
 'use strict';
 document.querySelectorAll('[data-labor-assignment]').forEach(form => {
  const campo = nombre => form.querySelector('[name="'+nombre+'"]');
  const boton = form.querySelector('[data-labor-submit]');
  const status = form.querySelector('[data-labor-status]');
  const versiones=['versionEsquema','versionJornada','versionPuesto','versionLaboral','revision'];
  let solicitud=0;
  async function actualizar(){
   const turno=++solicitud;boton.disabled=true;versiones.forEach(n=>campo(n).value='');form.dataset.confirmSummary='';
   form.querySelectorAll('[data-labor-value]').forEach(n=>n.textContent='—');
   const empleado=campo('empleadoId').value,esquema=campo('esquemaId').value;
   if(!empleado||!esquema){status.textContent='Selecciona empleado y esquema desde las ayudas.';return;}
   status.textContent='Consultando configuración vigente del catálogo…';
   try{
    const response=await fetch('/admin/asignaciones/contexto?'+new URLSearchParams({empleadoId:empleado,esquemaId:esquema}),{headers:{Accept:'application/json'},cache:'no-store'});
    if(!response.ok||!response.headers.get('content-type')?.includes('application/json'))throw new Error('consulta');
    const contexto=await response.json();if(turno!==solicitud)return;
    versiones.forEach(n=>campo(n).value=contexto[n]);campo('inicio').min=contexto.hoy;
    const s=contexto.esquema;
    const etiquetas={MENSUAL:'Mensual',SEMANAL:'Semanal',DIARIA:'Diaria',POR_HORA:'Por hora',QUINCENAL:'Quincenal'};
    const texto={puesto:s.puestoTexto,jornada:s.jornadaTexto+' · '+s.horas+' horas semanales',sueldo:new Intl.NumberFormat('es-MX',{style:'currency',currency:s.moneda}).format(s.importe),frecuencia:etiquetas[s.base]+' / '+etiquetas[s.frecuencia]};
    Object.entries(texto).forEach(([n,t])=>form.querySelector('[data-labor-value="'+n+'"]').textContent=t);
    form.dataset.confirmSummary=Object.values(texto).join('\n');form.dataset.confirmSubject=contexto.empleado;form.dataset.confirmScheme=s.codigo+' · '+s.nombre;
    status.textContent='Configuración consultada. Revisa vigencia y motivo antes de confirmar.';boton.disabled=false;
   }catch(error){if(turno===solicitud)status.textContent='No fue posible consultar la configuración. Vuelve a seleccionar empleado y esquema; si tu sesión caducó inicia sesión nuevamente.';}
  }
  campo('empleadoId').addEventListener('change',actualizar);campo('esquemaId').addEventListener('change',actualizar);
  form.addEventListener('submit',event=>{if(boton.disabled||!campo('revision').value){event.preventDefault();event.stopImmediatePropagation();status.textContent='Selecciona empleado y esquema y espera la consulta antes de confirmar.';}},true);
  actualizar();
 });
})();
