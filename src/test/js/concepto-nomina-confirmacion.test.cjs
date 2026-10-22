const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('concepto de nómina: resumen actualizado, cancelar/Escape sin envío y confirmar una vez',()=>{
 class E{
  constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}
  append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}
  addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}
 }
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:'concepto-nomina-guardar'};
 const fields={codigo:{value:'BON-PUNT'},nombre:{value:'Bono de puntualidad'},descripcion:{value:'Prueba'},naturaleza:{value:'PERCEPCION',selectedOptions:[{textContent:'Percepción'}]},aplicacion:{selectedOptions:[{textContent:'Habitual'}]},activo:{checked:true}};
 form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];form.querySelectorAll=()=>[];form.reportValidity=()=>true;let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 emitir();assert.equal(enviados,0);const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];
 assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.some(e=>e.textContent==='Bono de puntualidad'));
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(fields.nombre.value,'Bono de puntualidad');
 fields.nombre.value='Bono corregido';emitir();assert.ok(details.children.some(e=>e.textContent==='Bono corregido'));dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);
 emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
