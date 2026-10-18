const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
function setup(transferencia=true,valid=true) {
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){this.focused=true;}close(){this.open=false;}showModal(){this.open=true;}getBoundingClientRect(){return {left:10,top:10,right:500,bottom:700};}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={fecha:{value:'2026-10-17T16:30'},motivo:{value:'Prueba <sin borrar>\nConservar historial'}};
 const form=new E('form');form.dataset={moneyConfirm:'reversion-financiera',confirmTransfer:String(transferencia),confirmId:'1',confirmTitle:'Transferencia entre cuentas',confirmDetail:'Cuenta origen → Cuenta destino',confirmAmount:'300.00',confirmCurrency:'MXN',confirmOriginalDate:'16 oct 2026 · 20:01'};
 form.reportValidity=()=>valid;form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 const submitter=new E('button');let enviados=0;
 const emitir=()=>{const e={target:form,submitter,defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=b=>{assert.equal(b,submitter);emitir();};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 return {form,fields,emitir,dialog:body.children.find(e=>e.tag==='dialog'),submitter,enviados:()=>enviados};
}
test('reversa: detalle vigente, Cancelar/Escape/fondo sin POST y confirmación única',()=>{
 const ui=setup();ui.emitir();assert.equal(ui.enviados(),0);
 const d=ui.dialog,details=d.children[2],actions=d.children[4];assert.equal(d.open,true);
 assert.equal(d.children[1].textContent,'Confirmar reversa de transferencia');
 assert.equal(actions.children[0].textContent,'Cancelar');assert.equal(actions.children[1].textContent,'Confirmar y registrar reversa');
 for(const value of ['#1','Cuenta origen → Cuenta destino','$300.00','16 oct 2026 · 20:01','17/10/2026 · 16:30',ui.fields.motivo.value,'Dos movimientos inversos: ingreso al origen y egreso del destino'])assert.ok(details.children.some(x=>x.textContent===value));
 actions.children[0].fire('click');assert.equal(d.open,false);assert.equal(ui.enviados(),0);assert.equal(ui.submitter.focused,true);
 ui.fields.motivo.value='Motivo corregido';ui.fields.fecha.value='2026-10-17T16:31';ui.emitir();
 assert.ok(details.children.some(x=>x.textContent==='Motivo corregido'));assert.ok(details.children.some(x=>x.textContent==='17/10/2026 · 16:31'));
 d.fire('cancel',{preventDefault(){}});assert.equal(d.open,false);assert.equal(ui.enviados(),0);
 ui.emitir();d.fire('click',{target:d,clientX:0,clientY:0});assert.equal(d.open,false);assert.equal(ui.enviados(),0);
 assert.equal(ui.fields.motivo.value,'Motivo corregido');ui.emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(ui.enviados(),1);
});
test('reversa inválida no abre modal ni envía',()=>{const ui=setup(true,false);ui.emitir();assert.ok(!ui.dialog.open);assert.equal(ui.enviados(),0);});
test('operación manual conserva moneda y describe una sola compensación',()=>{
 const ui=setup(false);ui.form.dataset.confirmCurrency='USD';ui.emitir();
 assert.equal(ui.dialog.children[1].textContent,'Confirmar reversa de operación');
 assert.ok(ui.dialog.children[2].children.some(x=>x.textContent==='USD $300.00'));
 assert.ok(ui.dialog.children[2].children.some(x=>x.textContent==='Un movimiento opuesto al original'));
 assert.match(ui.dialog.children[3].textContent,/misma cuenta/);assert.equal(ui.enviados(),0);
});
