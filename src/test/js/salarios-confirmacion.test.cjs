const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('captura salarial usa autocompletado institucional de sesión y dinero sin spinner',()=>{
 const html=fs.readFileSync('src/main/resources/templates/admin/empleado-condiciones.html','utf8');
 assert.match(html,/data-autocomplete data-no-scope data-endpoint="\/admin\/puestos\/buscar" data-refresh-on-focus="true"/);
 assert.match(html,/type="text" inputmode="decimal" data-money th:field="\*\{importeBruto\}"/);
 assert.ok(!html.includes('type="number"'));assert.ok(html.includes('th:errors="*{puestoId}"'));
});
for(const kind of ['puesto-guardar','salarial-guardar','salarial-cancelar'])test(kind+': resumen actualizado, cancelar/Escape sin guardar y confirmación única',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:kind,confirmSubject:'Ana Prueba',confirmStart:'01/11/2026',confirmAmount:'12000.00',confirmCurrency:'MXN'};
 const fields={codigo:{value:'DOC'},nombre:{value:'Docente'},descripcion:{value:''},activo:{checked:true},puestoTexto:{value:'DOC · Docente'},importeBruto:{value:'12000.00'},base:{selectedOptions:[{textContent:'Mensual'}]},frecuencia:{selectedOptions:[{textContent:'Mensual'}]},fechaInicio:{value:'2026-11-01'},fechaFin:{value:''},motivo:{value:'Prueba de sueldo'}};
 form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];form.querySelectorAll=()=>[];
 let valido=true,enviados=0;form.reportValidity=()=>valido;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 valido=false;emitir();assert.equal(enviados,0);const dialog=body.children.find(e=>e.tag==='dialog');assert.ok(!dialog.open);
 valido=true;emitir();assert.equal(dialog.open,true);const details=dialog.children[2],actions=dialog.children[4];assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.some(e=>e.textContent===(kind==='puesto-guardar'?'Docente':'Ana Prueba')));
 if(kind==='salarial-guardar'){assert.ok(details.children.some(e=>e.textContent==='01/11/2026'));assert.ok(details.children.some(e=>e.textContent==='Sin fecha de fin'));assert.ok(details.children.some(e=>String(e.textContent).includes('12,000.00')));}
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);
 fields.nombre.value='Docente corregido';fields.motivo.value='Motivo corregido';emitir();assert.ok(details.children.some(e=>e.textContent===(kind==='puesto-guardar'?'Docente corregido':'Motivo corregido')));
 dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);assert.equal(dialog.open,false);
 emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
