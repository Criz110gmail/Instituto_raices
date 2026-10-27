const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const tipo of ['configurar','cancelar','finalizar'])test('configuración nómina: resumen '+tipo+', Cancelar/Escape y confirmación única',()=>{
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:'nomina-'+tipo,confirmSubject:'TRANS · Transporte',confirmCurrency:'MXN',confirmEffect:'Suma al empleado'};
 const fields={empleadoTexto:{value:'EMP · Diana'},conceptoTexto:{value:'TRANS · Transporte'},importe:{value:'50.00'},inicio:{value:'2027-06-01'},fin:{value:''},motivo:{value:'Motivo sintético'},anteriorId:{value:''},modalidad:{value:'RECURRENTE'},frecuenciaImporte:{value:'MENSUAL',selectedOptions:[{textContent:'Un mes'}]}};
 form.querySelector=s=>fields[s.match(/name="([^"]+)"/)?.[1]];form.querySelectorAll=()=>[];form.reportValidity=()=>true;let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 emitir();assert.equal(enviados,0);const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.some(e=>e.textContent==='TRANS · Transporte'));assert.ok(details.children.some(e=>e.textContent==='Motivo sintético'));
 if(tipo==='configurar'){assert.ok(details.children.some(e=>e.textContent==='Un mes'));assert.ok(details.children.some(e=>e.textContent==='01/06/2027'));}
 actions.children[0].fire('click');assert.equal(enviados,0);emitir();dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
test('concepto único exige periodo; no ofrece cálculo ni petición de guardado al seleccionar',()=>{
 const s=fs.readFileSync('src/main/resources/static/js/empleado-nomina.js','utf8');assert.ok(s.includes("field('fin').required=unique()"));assert.ok(s.includes('conceptRevision'));assert.ok(s.includes('periodRevision'));assert.ok(s.includes("field('periodoVersion').value=''"));assert.ok(!s.includes("method:'POST'"));
});
