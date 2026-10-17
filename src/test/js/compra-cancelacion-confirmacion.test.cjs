const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('cancelación de compra: motivo vigente, volver/Escape sin reversa y confirmación única',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const motivo={value:''},form=new E('form');form.dataset={moneyConfirm:'compra-cancelacion',confirmFolio:'COM-00000001',confirmSupplier:'Proveedor de prueba',confirmAccount:'Cuenta de prueba',confirmPlantel:'Plantel de prueba',confirmReference:'TICKET-PRUEBA-001',confirmAmount:'406.00',confirmMovement:'4'};
 form.querySelectorAll=()=>[];form.querySelector=()=>motivo;form.reportValidity=()=>Boolean(motivo.value.trim());
 let enviados=0;function emitir(){const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;}form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 const d=body.children.find(x=>x.tag==='dialog'),details=d.children[2],actions=d.children[4];
 emitir();assert.equal(enviados,0);assert.ok(!d.open);
 motivo.value='Prueba <sin borrar>\nMotivo capturado';emitir();assert.equal(d.open,true);assert.equal(enviados,0);
 for(const value of ['COM-00000001','Cuenta de prueba','#4','$406.00',motivo.value])assert.ok(details.children.some(x=>x.textContent===value));
 assert.equal(actions.children[0].textContent,'Volver sin cancelar');assert.equal(actions.children[1].textContent,'Confirmar cancelación');
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(d.open,false);assert.equal(motivo.value,'Prueba <sin borrar>\nMotivo capturado');
 motivo.value='Motivo corregido';emitir();assert.ok(details.children.some(x=>x.textContent==='Motivo corregido'));
 d.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);assert.equal(d.open,false);assert.equal(motivo.value,'Motivo corregido');
 emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
