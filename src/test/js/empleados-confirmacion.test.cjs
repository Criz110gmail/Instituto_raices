const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
function iniciar(kind,valid=true){
 class E{constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){this.focused=true;}close(){this.open=false;}showModal(){this.open=true;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}}
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={nombres:{value:'Persona <ficticia>'},puesto:{value:'Docente'},fechaIngreso:{type:'date',value:'2026-10-01'},plantelTexto:{value:'Plantel norte'},principal:{type:'checkbox',checked:true},activo:{value:'false'}};
 const form=new E('form');form.dataset={moneyConfirm:kind};form.reportValidity=()=>valid;form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 let enviados=0;const submitter=new E('button');const enviar=()=>{const e={target:form,submitter,defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=enviar;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});return {fields,enviar,dialog:body.children.find(x=>x.tag==='dialog'),enviados:()=>enviados};
}
test('personal: todos los modales conservan captura al cancelar y confirman un único envío',()=>{
 for(const kind of ['empleado-registro','empleado-laboral','empleado-plantel','empleado-vigencia','empleado-perfil']){
  const ui=iniciar(kind);ui.enviar();const d=ui.dialog,detalle=d.children[2],acciones=d.children[4];assert.equal(ui.enviados(),0);assert.equal(d.open,true);
  for(const t of ['Persona <ficticia>','01/10/2026','Sí','No'])assert.ok(detalle.children.some(x=>x.textContent===t));
  assert.equal(acciones.children[0].textContent,'Cancelar');acciones.children[0].fire('click');assert.equal(ui.enviados(),0);assert.equal(ui.fields.puesto.value,'Docente');
  ui.enviar();d.fire('cancel',{preventDefault(){}});assert.equal(ui.enviados(),0);
  ui.enviar();acciones.children[1].fire('click');acciones.children[1].fire('click');assert.equal(ui.enviados(),1);
 }
});
test('datos personales inválidos no abren confirmación',()=>{const ui=iniciar('empleado-registro',false);ui.enviar();assert.ok(!ui.dialog.open);assert.equal(ui.enviados(),0);});
