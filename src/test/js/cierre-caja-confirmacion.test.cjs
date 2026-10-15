const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('cierre de caja: resumen ciego, cancelar conserva captura, valida y confirma una sola vez',()=>{
 class E{
  constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};}
  append(...nodes){this.children.push(...nodes);} replaceChildren(){this.children=[];}
  setAttribute(){} focus(){} close(){this.open=false;} showModal(){this.open=true;}
  addEventListener(n,fn){(this.listeners[n]??=[]).push(fn);}
  fire(n,e={}){for(const fn of this.listeners[n]||[])fn(e);}
 }
 const body=new E('body');body.classList={add(){},remove(){}};
 const document=new E('document');document.body=body;document.readyState='complete';
 document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const fields={efectivoDeclarado:{value:'1150.00'},justificacionDiferencia:{value:''},observaciones:{value:'Cierre sin diferencia <script>literal</script>'}};
 const form=new E('form');Object.assign(form.dataset,{moneyConfirm:'cierre-caja',confirmCorte:'7',confirmAccount:'CAJA-CORTE-TEST · Caja de prueba',confirmPlantel:'Plantel de prueba',confirmCurrency:'MXN'});
 let valido=true,enviados=0;
 form.reportValidity=()=>valido;form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};
 form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];
 valido=false;emitir();assert.notEqual(dialog.open,true);assert.equal(enviados,0);
 valido=true;emitir();assert.equal(dialog.open,true);assert.equal(enviados,0);
 let textos=details.children.map(e=>e.textContent);
 assert.ok(textos.includes('#7'));assert.ok(textos.includes('CAJA-CORTE-TEST · Caja de prueba'));
 assert.ok(textos.includes('$1,150.00'));assert.ok(textos.includes('Sin justificación capturada'));
 assert.ok(textos.includes(fields.observaciones.value));assert.ok(!textos.includes('Saldo esperado'));assert.ok(!textos.includes('Diferencia'));
 assert.match(dialog.children[3].textContent,/no registra ingresos ni egresos/);
 assert.equal(actions.children[0].textContent,'Cancelar');assert.equal(actions.children[1].textContent,'Confirmar y cerrar caja');
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);assert.equal(fields.efectivoDeclarado.value,'1150.00');
 fields.efectivoDeclarado.value='1149.00';fields.justificacionDiferencia.value='Faltante de prueba';emitir();
 textos=details.children.map(e=>e.textContent);assert.ok(textos.includes('$1,149.00'));assert.ok(textos.includes('Faltante de prueba'));
 dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);assert.equal(dialog.open,false);
 emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
