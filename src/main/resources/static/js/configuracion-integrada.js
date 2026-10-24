/* Captura coordinada; el servidor valida de nuevo selecciones, versiones y vigencias. */
(() => {
 const form=document.querySelector('[data-config-integrada]');if(!form)return;
 const filas=form.querySelector('[data-conceptos]'),status=form.querySelector('[data-config-status]');
 const campo=(root,sufijo)=>root.querySelector('[name$="'+sufijo+'"]');
 let secuencia=filas.children.length;
 function sincronizar(){
  form.querySelectorAll('[data-config-toggle]').forEach(toggle=>{
   form.querySelector('[data-config-fields="'+toggle.dataset.configToggle+'"]').disabled=!toggle.checked;
  });
 }
 form.querySelectorAll('[data-config-toggle]').forEach(t=>t.addEventListener('change',sincronizar));sincronizar();
 function inicializar(fila){
  const id=campo(fila,'.conceptoId'),texto=campo(fila,'.conceptoTexto'),version=fila.querySelector('[data-concept-version]');
  const efecto=fila.querySelector('[data-concept-effect]'),estado=fila.querySelector('[data-concept-status]');
  let solicitud,revision=0;
  if(campo(fila,'.anteriorId').value){texto.readOnly=true;fila.querySelector('.autocomplete-clear').hidden=true;}
  async function contexto(){
   const actual=++revision;solicitud?.abort();version.value='';efecto.value='';
   if(!id.value){estado.textContent='Selecciona un concepto habitual de las sugerencias.';return;}
   solicitud=new AbortController();estado.textContent='Consultando tipo y versión…';
   try{
    const params=new URLSearchParams({empleadoId:form.dataset.employeeId,conceptoId:id.value});
    const r=await fetch('/admin/conceptos-empleado/contexto?'+params,{headers:{Accept:'application/json'},cache:'no-store',signal:solicitud.signal});
    if(!r.ok||r.redirected)throw new Error('No fue posible consultar el concepto. Revisa la sesión, sus permisos y la actividad laboral del empleado.');
    const c=await r.json();if(actual!==revision)return;
    if(c.concepto!==texto.value)throw new Error('El concepto cambió. Quita la selección y vuelve a elegirlo.');
    version.value=c.conceptoVersion;efecto.value=c.tipo+' · '+c.moneda;estado.textContent=c.efecto;
   }catch(e){if(actual===revision&&e.name!=='AbortError')estado.textContent=e.message;}
  }
  id.addEventListener('change',contexto);
  fila.querySelector('[data-quitar-concepto]').addEventListener('click',()=>{solicitud?.abort();revision++;fila.remove();renumerar();});
  contexto();
 }
 function renumerar(){
  // Índices contiguos: evita filas vacías y mantiene los importes visibles asociados.
  [...filas.children].forEach((fila,n)=>{
   fila.querySelector('[data-concept-title]').textContent='Concepto adicional '+(n+1);
   fila.querySelectorAll('[name]').forEach(input=>input.name=input.name.replace(/^conceptos\[\d+\]/,'conceptos['+n+']'));
  });
 }
 form.querySelector('[data-agregar-concepto]').addEventListener('click',()=>{
  if(filas.children.length>=20){status.textContent='Puedes guardar hasta 20 conceptos por confirmación.';return;}
  const fragment=document.querySelector('#config-concept-template').content.cloneNode(true),fila=fragment.querySelector('[data-config-concepto]');
  fila.querySelectorAll('*').forEach(el=>[...el.attributes].forEach(attr=>{if(attr.value.includes('ROWINDEX'))el.setAttribute(attr.name,attr.value.replaceAll('ROWINDEX',String(secuencia)));}));
  secuencia++;filas.append(fila);renumerar();fila.dispatchEvent(new CustomEvent('autocomplete:initialize',{bubbles:true}));inicializar(fila);
 });
 [...filas.children].forEach(inicializar);renumerar();
 form.addEventListener('invalid',event=>{
  const panel=event.target.closest('[data-student-panel]');
  if(panel?.hidden)form.querySelector('[data-student-tab="'+panel.dataset.studentPanel+'"]').click();
 },true);
 form.addEventListener('submit',event=>{
  const cambios=[...form.querySelectorAll('[data-config-toggle]')].some(t=>t.checked);
  const incompleto=[...filas.children].some(f=>!f.querySelector('[data-concept-version]').value);
  if((!cambios&&!filas.children.length)||incompleto){event.preventDefault();event.stopImmediatePropagation();status.textContent=incompleto?'Espera la consulta de todos los conceptos, o quita la fila pendiente.':'Selecciona un cambio o agrega un concepto antes de guardar.';status.scrollIntoView({block:'center'});}
 },true);
})();
