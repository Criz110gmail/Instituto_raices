const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const tipo of ['asignar','cancelar','terminar'])test('concepto por empleado: modal '+tipo+' con cancelar/Escape y envío único',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:'concepto-empleado-'+tipo,confirmSubject:'Bruce Ficticio',confirmConcept:'BON · Bono',confirmAmount:'500.00',confirmStart:'01/01/2027',confirmCurrency:'MXN'};
 const fields={empleadoTexto:{value:'Bruce Ficticio'},conceptoTexto:{value:'BON · Bono'},importe:{value:'500.00'},inicio:{value:'2027-01-01'},fin:{value:'2027-01-31'},motivo:{value:'Motivo obligatorio de prueba'},anteriorId:{value:'2'}};
 form.querySelector=s=>s==='[data-contexto-estado]'?{textContent:'Percepción: suma al empleado'}:fields[s.match(/name="([^"]+)"/)[1]];form.querySelectorAll=()=>[];form.reportValidity=()=>true;let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 emitir();assert.equal(enviados,0);const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.some(e=>e.textContent==='BON · Bono'));assert.ok(details.children.some(e=>e.textContent==='Motivo obligatorio de prueba'));
 actions.children[0].fire('click');assert.equal(enviados,0);emitir();dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
test('contexto ignora respuestas atrasadas y limpia versiones al quitar selección',async()=>{
 const listeners={};const field=v=>({value:v,addEventListener(n,f){listeners[this===fields.empleadoId?'empleado':this===fields.conceptoId?'concepto':'otro']=f;}});
 const fields={empleadoId:field('1'),conceptoId:field('2'),empleadoTexto:field('EMP · Bruce'),conceptoTexto:field('BON · Bono'),conceptoVersion:field(''),laboralVersion:field('')};
 const boton={disabled:true},estado={},error={},efecto={};const form={dataset:{},querySelector:s=>({'[data-contexto-confirmar]':boton,'[data-contexto-estado]':estado,'[data-contexto-error]':error,'[data-contexto-efecto]':efecto}[s]||fields[s.match(/name="([^"]+)"/)?.[1]]),addEventListener(){}};
 const pendientes=[];vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/concepto-empleado.js','utf8'),{document:{querySelector:()=>form},URLSearchParams,AbortController,fetch:()=>new Promise(resolve=>pendientes.push(resolve))});
 assert.equal(boton.disabled,true);fields.conceptoId.value='3';listeners.concepto();
 pendientes[1]({ok:true,json:async()=>({conceptoVersion:4,laboralVersion:2,moneda:'MXN',tipo:'Percepción',efecto:'Suma al empleado',concepto:'BON · Bono',empleado:'EMP · Bruce'})});await new Promise(setImmediate);assert.equal(boton.disabled,false);assert.equal(fields.conceptoVersion.value,4);
 pendientes[0]({ok:true,json:async()=>({conceptoVersion:0})});await new Promise(setImmediate);assert.equal(fields.conceptoVersion.value,4);
 fields.empleadoId.value='';listeners.empleado();assert.equal(fields.conceptoVersion.value,'');assert.equal(boton.disabled,true);
});
