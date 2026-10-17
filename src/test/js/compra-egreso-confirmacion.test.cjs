const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('egreso de compra: resumen, cancelar y Escape no envían; confirmar envía una sola vez',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);document.getElementById=()=>({querySelectorAll:()=>[{querySelector:s=>({textContent:s==='div strong'?'Papel de prueba':s==='small'?'2 × $100.00 · Impuesto 16%':'$232.00'})}]});
 const form=new E('form');form.dataset={moneyConfirm:'compra-egreso',confirmFolio:'COM-00000007',confirmSupplier:'Proveedor <prueba>',confirmAccount:'Cuenta de prueba',confirmPlantel:'Plantel de prueba',confirmCategory:'Papelería',confirmReference:'TICKET-PRUEBA-001',confirmLines:'compra-partidas',confirmSubtotal:'350.00',confirmTax:'56.00',confirmAmount:'406.00'};
 form.querySelectorAll=()=>[];form.reportValidity=()=>true;
 let enviados=0;function emitir(){const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;}form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 emitir();assert.equal(enviados,0);const d=body.children.find(x=>x.tag==='dialog'),details=d.children[2],actions=d.children[4];
 assert.equal(d.open,true);assert.equal(actions.children[0].textContent,'Cancelar');
 for(const value of ['COM-00000007','Cuenta de prueba','Proveedor <prueba>','$350.00','$56.00','$406.00'])assert.ok(details.children.some(x=>x.textContent===value));
 assert.ok(details.children.some(x=>x.textContent.includes('Papel de prueba')));
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(d.open,false);
 emitir();d.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);assert.equal(d.open,false);
 emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
