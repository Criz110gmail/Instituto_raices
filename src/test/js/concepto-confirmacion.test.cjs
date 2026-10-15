const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('concepto: resumen actualizado, cancelar sin guardar y confirmar sólo una vez',()=>{
 class E{
  constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}
  append(...nodes){this.children.push(...nodes);}
  replaceChildren(){this.children=[];}
  setAttribute(){} focus(){} close(){this.open=false;} showModal(){this.open=true;}
  addEventListener(n,fn){(this.listeners[n]??=[]).push(fn);}
  fire(n,e={}){for(const fn of this.listeners[n]||[])fn(e);}
 }
 const body=new E('body');body.classList={add(){},remove(){}};
 const document=new E('document');document.body=body;document.readyState='complete';
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={institucionId:{selectedOptions:[{textContent:'Escuela Prueba'}]},categoria:{selectedOptions:[{textContent:'Otro'}]},codigo:{value:'DUP-TEST'},nombre:{value:'Cuota de prueba'},descripcion:{value:''},permiteBeca:{checked:false},permiteDescuento:{checked:true},permiteRecargo:{checked:false},activo:{checked:true}};
 const form=new E('form');form.dataset.moneyConfirm='concepto-cobro';form.reportValidity=()=>true;
 form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};
 form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 emitir();assert.equal(enviados,0);
 const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];
 assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');
 let textos=details.children.map(e=>e.textContent);
 assert.ok(textos.includes('DUP-TEST'));assert.ok(textos.includes('Escuela Prueba'));assert.ok(textos.includes('Sin descripción'));
 assert.equal(textos[textos.indexOf('Permite beca')+1],'No');assert.equal(textos[textos.indexOf('Permite descuento')+1],'Sí');
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);
 fields.nombre.value='Nombre corregido';emitir();assert.ok(details.children.some(e=>e.textContent==='Nombre corregido'));
 actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
