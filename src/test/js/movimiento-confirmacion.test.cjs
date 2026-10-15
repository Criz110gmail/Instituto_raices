const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('movimiento: resumen actualizado, cancelar sin guardar y confirmar sólo una vez',()=>{
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
 const fields={institucionId:{selectedOptions:[{textContent:'Escuela Prueba'}]},cuentaTexto:{value:'CAJA-CORTE-TEST'},direccion:{value:'EGRESO'},motivoTexto:{value:'CORTE-EGR-TEST · Egreso de prueba'},monto:{value:'50.00'},fechaOperacion:{value:'2026-10-15T12:00'},concepto:{value:'Salida de prueba'},referencia:{value:''},terceroNombre:{value:''}};
 const form=new E('form');form.dataset.moneyConfirm='movimiento-manual';form.reportValidity=()=>true;
 form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};
 form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 emitir();assert.equal(enviados,0);
 const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];
 assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');
 let textos=details.children.map(e=>e.textContent);
 assert.ok(textos.includes('CAJA-CORTE-TEST'));assert.ok(textos.includes('Escuela Prueba'));assert.ok(textos.includes('Sin referencia'));
 assert.ok(textos.includes('$50.00'));assert.ok(textos.includes('15/10/2026 · 12:00'));assert.ok(textos.includes('Egreso'));assert.match(dialog.children[3].textContent,/Se descontará/);
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);
 fields.concepto.value='Nombre corregido';emitir();assert.ok(details.children.some(e=>e.textContent==='Nombre corregido'));
 actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
