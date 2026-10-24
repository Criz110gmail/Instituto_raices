const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('configuración integrada: resumen por secciones, fechas, cancelar y envío único',()=>{
 class E{
  constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}
  append(...n){this.children.push(...n);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}
  addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}
 }
 const body=new E('body');body.classList={add(){},remove(){}};const document=new E('document');document.body=body;document.readyState='complete';document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={cambiarSueldo:{checked:false},cambiarCalendario:{checked:true},'calendario.calendarioTexto':{value:'MENSUAL · Nómina mensual'},motivo:{value:'Deducción desde junio'}};
 const cf={'.conceptoTexto':{value:'DED-TEST · Deducción'},'.importe':{value:'500.00'},'.inicio':{value:'2030-06-01'},'.fin':{value:''},'.anteriorId':{value:''}};
 const fila={querySelector:s=>s==='[data-concept-effect]'?{value:'Deducción · MXN'}:cf[s.match(/name\$="([^"]+)"/)[1]]};
 const form=new E('form');form.dataset={moneyConfirm:'configuracion-integrada',confirmSubject:'Persona Ficticia'};form.reportValidity=()=>true;form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];form.querySelectorAll=s=>s==='[data-config-concepto]'?[fila]:[];
 let enviados=0;const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 emitir();const dialog=body.children.find(e=>e.tag==='dialog'),det=dialog.children[2],actions=dialog.children[4];const textos=()=>det.children.map(e=>e.textContent);
 assert.ok(dialog.open);assert.equal(enviados,0);assert.ok(textos().includes('Persona Ficticia'));assert.ok(textos().includes('MENSUAL · Nómina mensual'));assert.ok(textos().some(t=>t.includes('$500.00 · 01/06/2030 — Sin fecha de fin')));assert.ok(!textos().includes('Sueldo bruto — percepción base'));
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);
 fields.cambiarSueldo.checked=true;Object.assign(fields,{'sueldo.puestoTexto':{value:'AUX · Auxiliar'},'sueldo.importeBruto':{value:'12000'},'sueldo.base':{selectedOptions:[{textContent:'Mensual'}]},'sueldo.frecuencia':{selectedOptions:[{textContent:'Mensual'}]},'sueldo.fechaInicio':{value:'2030-01-01'},'sueldo.fechaFin':{value:'2030-12-31'}});
 emitir();assert.ok(textos().includes('$12,000.00'));assert.ok(textos().includes('01/01/2030 — 31/12/2030'));actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
