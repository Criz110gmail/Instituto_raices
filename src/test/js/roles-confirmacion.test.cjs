const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const kind of ['crear','editar','desactivar'])test('rol '+kind+': resumen, cancelar y envío único',()=>{
 class E{
  constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}
  append(...v){this.children.push(...v);} replaceChildren(){this.children=[];}
  setAttribute(){} focus(){} close(){this.open=false;} showModal(){this.open=true;}
  addEventListener(n,f){(this.listeners[n]??=[]).push(f);} fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}
 }
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:kind==='desactivar'?'rol-desactivar':'rol-guardar',confirmEdit:String(kind==='editar'),confirmCode:'PRUEBA',confirmName:'Personal de prueba'};
 const fields={codigo:{value:'PRUEBA'},nombre:{value:'Personal de prueba'},descripcion:{value:''},activo:{checked:true},institucionId:{selectedOptions:[{textContent:'Escuela de prueba'}]}};
 form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 const selected={checked:true},label={textContent:'Empleados'};
 form.querySelectorAll=s=>s==='.permission-card'?[{querySelector:s=>s==='strong'?label:selected},{querySelector:s=>s==='strong'?{textContent:'Datos laborales de empleados'}:{checked:false}}]:[];
 form.reportValidity=()=>true;let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 emitir();assert.equal(enviados,0);
 const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];
 assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');
 assert.ok(details.children.some(e=>e.textContent==='Personal de prueba'));
 if(kind!=='desactivar'){
  assert.ok(details.children.some(e=>e.textContent==='Módulos autorizados (1)'));
  assert.ok(details.children.some(e=>e.textContent==='Empleados'));
  assert.ok(!details.children.some(e=>e.textContent==='Datos laborales de empleados'));
 }
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);
 if(kind!=='desactivar'){fields.nombre.value='Nombre corregido';selected.checked=false;}
 emitir();if(kind!=='desactivar'){
  assert.ok(details.children.some(e=>e.textContent==='Nombre corregido'));
  assert.ok(details.children.some(e=>e.textContent==='Ninguno · este rol no concede acceso a módulos'));
 }
 dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);assert.equal(dialog.open,false);
 emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
test('rol: plantilla utiliza modal compartido sin confirm del navegador',()=>{
 const html=fs.readFileSync('src/main/resources/templates/admin/rol-form.html','utf8');
 assert.ok(html.includes('data-money-confirm="rol-guardar"'));assert.ok(html.includes('data-money-confirm="rol-desactivar"'));
 assert.ok(html.includes('/js/money-input.js'));assert.ok(html.includes('/css/money-input.css'));
 assert.ok(!html.includes('return confirm('));
});
