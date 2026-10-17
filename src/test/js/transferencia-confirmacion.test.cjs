const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
function setup(valid=true) {
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){this.focused=true;}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={institucionId:{selectedOptions:[{textContent:' Escuela de prueba '}]},cuentaOrigenId:{value:'4'},cuentaOrigenTexto:{value:'BANCO-COMPRAS-TEST · Cuenta de prueba'},cuentaDestinoId:{value:'5'},cuentaDestinoTexto:{value:'BANCO-TRASPASO-TEST · Destino <prueba>'},monto:{value:'300.00'},fecha:{value:'2026-10-16T19:30'},referencia:{value:'TRASPASO-TEST-001'},observaciones:{value:'Prueba <sin HTML>'}};
 const form=new E('form');form.dataset.moneyConfirm='transferencia-cuentas';form.reportValidity=()=>valid;
 form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 const submitter=new E('button');let enviados=0;
 const emitir=()=>{const e={target:form,submitter,defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=b=>{assert.equal(b,submitter);emitir();};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 const dialog=body.children.find(e=>e.tag==='dialog');
 return {fields,emitir,dialog,submitter,enviados:()=>enviados};
}
test('traspaso: detalle actualizado, Cancelar/Escape sin POST y confirmación única',()=>{
 const ui=setup();ui.emitir();assert.equal(ui.enviados(),0);
 const dialog=ui.dialog,details=dialog.children[2],actions=dialog.children[4];
 assert.equal(dialog.open,true);assert.equal(dialog.children[1].textContent,'Confirmar transferencia entre cuentas');
 assert.equal(actions.children[0].textContent,'Cancelar');assert.equal(actions.children[1].textContent,'Confirmar y aplicar transferencia');
 for(const text of ['Escuela de prueba',ui.fields.cuentaOrigenTexto.value,ui.fields.cuentaDestinoTexto.value,'$300.00','16/10/2026 · 19:30','TRASPASO-TEST-001','Prueba <sin HTML>'])assert.ok(details.children.some(x=>x.textContent===text));
 assert.match(dialog.children[3].textContent,/no envía dinero al banco/);
 actions.children[0].fire('click');assert.equal(dialog.open,false);assert.equal(ui.enviados(),0);assert.equal(ui.fields.monto.value,'300.00');assert.equal(ui.submitter.focused,true);
 ui.fields.monto.value='350.50';ui.fields.referencia.value='Referencia corregida';ui.emitir();assert.ok(details.children.some(x=>x.textContent==='$350.50'));assert.ok(details.children.some(x=>x.textContent==='Referencia corregida'));
 dialog.fire('cancel',{preventDefault(){}});assert.equal(ui.enviados(),0);assert.equal(dialog.open,false);
 ui.emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(ui.enviados(),1);
});
test('datos inválidos no abren el resumen y campos opcionales vacíos tienen leyenda',()=>{
 const invalid=setup(false);invalid.emitir();assert.ok(!invalid.dialog.open);assert.equal(invalid.enviados(),0);
 const ui=setup();ui.fields.referencia.value='';ui.fields.observaciones.value='';ui.emitir();
 const values=ui.dialog.children[2].children.map(x=>x.textContent);
 assert.ok(values.includes('Sin referencia'));assert.ok(values.includes('Sin observaciones'));
});
