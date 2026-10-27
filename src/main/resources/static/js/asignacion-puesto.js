/* Datos privados del catálogo; el servidor revalida versión e importe al confirmar. */
(() => {
 const form=document.querySelector('[data-asignacion-puesto]');if(!form)return;
 const id=form.querySelector('[name="puestoId"]'),texto=form.querySelector('[name="puestoTexto"]'),version=form.querySelector('[data-puesto-version]');
 const horas=form.querySelector('[data-puesto-horas]'),sueldo=form.querySelector('[data-puesto-sueldo]'),status=form.querySelector('[data-puesto-status]'),submit=form.querySelector('[data-puesto-submit]');
 let revision=0,controller;
 async function consultar(){
  const actual=++revision;controller?.abort();version.value='';horas.value='';sueldo.value='';submit.disabled=true;
  if(!id.value){status.textContent='Selecciona un puesto de las sugerencias.';return;}
  controller=new AbortController();status.textContent='Consultando carga horaria y sueldo mensual…';
  try{
   const r=await fetch(form.dataset.contextUrl+'?'+new URLSearchParams({puestoId:id.value}),{headers:{Accept:'application/json'},cache:'no-store',signal:controller.signal});
   if(!r.ok||r.redirected)throw Error('No fue posible consultar el puesto. Debe estar activo y tener horas y sueldo completos; revisa también tu sesión.');
   const data=await r.json(),p=data.puesto;if(actual!==revision)return;
   if(texto.value!==p.codigo+' · '+p.nombre)throw Error('El puesto cambió: quita la selección y vuelve a elegirlo.');
   version.value=p.version;horas.value=p.horasSemanales;sueldo.value=new Intl.NumberFormat('es-MX',{style:'currency',currency:data.moneda}).format(p.sueldoMensual);
   submit.disabled=false;status.textContent='Se conservarán estas horas y este sueldo mensual como copia histórica. El catálogo no cambia sueldos ya asignados.';
  }catch(e){if(actual===revision&&e.name!=='AbortError')status.textContent=e.message;}
 }
 id.addEventListener('change',consultar);consultar();
 form.addEventListener('submit',e=>{if(!version.value||submit.disabled){e.preventDefault();e.stopImmediatePropagation();status.textContent='Selecciona y revisa un puesto válido antes de confirmar.';}},true);
})();
