const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
class E{
 constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};this.hidden=true;this.checked=false;}
 append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}scrollIntoView(){this.scrolled=true;}
 addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}
}
for(const modo of ['guardar','actualizar','cancelar'])test('borrador: modal '+modo+' cancela sin enviar y confirma una sola vez',()=>{
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:modo==='cancelar'?'nomina-borrador-cancelar':'nomina-borrador',confirmUpdate:String(modo==='actualizar'),confirmSubject:'BOR-000001',confirmPeriod:'ENE-TEST · Enero',confirmPeople:'1',confirmAmount:'12500',confirmBefore:'12000',confirmCurrency:'MXN'};
 form.querySelector=()=>({value:'Motivo obligatorio del ensayo'});form.querySelectorAll=()=>[];form.reportValidity=()=>true;let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 emitir();assert.equal(enviados,0);const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.some(e=>e.textContent==='ENE-TEST · Enero'));assert.ok(details.children.some(e=>e.textContent==='Motivo obligatorio del ensayo'));
 actions.children[0].fire('click');assert.equal(enviados,0);emitir();dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
test('selección de página: marcar/desmarcar, contador y bloqueo vacío sin guardar',()=>{
 const form=new E(),status=new E(),error=new E(),checks=[new E(),new E()],todos=new E(),ninguno=new E();todos.dataset.nominaSelect='todos';ninguno.dataset.nominaSelect='ninguno';
 form.querySelector=s=>s.includes('status')?status:error;form.querySelectorAll=s=>s.startsWith('input')?checks:[todos,ninguno];
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/preparacion-nomina.js','utf8'),{document:{querySelector:()=>form}});
 let prevenido=false;form.fire('submit',{preventDefault(){prevenido=true;}});assert.equal(prevenido,true);assert.equal(error.hidden,false);assert.equal(error.scrolled,true);
 todos.fire('click');assert.equal(checks.filter(e=>e.checked).length,2);assert.match(status.textContent,/2 empleados/);assert.equal(error.hidden,true);
 prevenido=false;form.fire('submit',{preventDefault(){prevenido=true;}});assert.equal(prevenido,false);ninguno.fire('click');assert.match(status.textContent,/0 empleados/);checks[1].checked=true;checks[1].fire('change');assert.match(status.textContent,/1 empleados/);
});
