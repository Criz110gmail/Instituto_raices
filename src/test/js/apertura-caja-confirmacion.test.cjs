const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('apertura de corte: resumen vivo, cancelar sin abrir, validación y confirmación única',()=>{
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
 const fields={institucionId:{selectedOptions:[{textContent:'Escuela de prueba'}]},cuentaId:{value:'2'},cuentaTexto:{value:'CAJA-CORTE-TEST · Caja de prueba'},observaciones:{value:'Prueba de faltante <script>literal</script>'}};
 const form=new E('form');form.dataset.moneyConfirm='apertura-caja';
 let valido=true,enviados=0;
 form.reportValidity=()=>valido;form.querySelectorAll=()=>[];form.querySelector=s=>fields[s.match(/name="([^"]+)"/)[1]];
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};
 form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,MutationObserver:class{observe(){}},Intl,WeakMap,WeakSet,setTimeout});
 const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];
 valido=false;emitir();assert.notEqual(dialog.open,true);assert.equal(enviados,0);
 valido=true;emitir();assert.equal(dialog.open,true);assert.equal(enviados,0);
 let textos=details.children.map(e=>e.textContent);
 assert.ok(textos.includes('Escuela de prueba'));assert.ok(textos.includes(fields.cuentaTexto.value));assert.ok(textos.includes(fields.observaciones.value));
 assert.match(dialog.children[3].textContent,/No registra ingresos ni egresos/);
 assert.equal(actions.children[0].textContent,'Cancelar');assert.equal(actions.children[1].textContent,'Confirmar y abrir corte');
 actions.children[0].fire('click');assert.equal(enviados,0);assert.equal(dialog.open,false);assert.equal(fields.cuentaId.value,'2');
 fields.cuentaId.value='';fields.observaciones.value='';emitir();textos=details.children.map(e=>e.textContent);
 assert.ok(textos.includes('Sin seleccionar'));assert.ok(textos.includes('Sin observaciones'));
 dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);
 fields.cuentaId.value='3';fields.cuentaTexto.value='OTRA-CAJA';fields.observaciones.value='Observación corregida';emitir();
 textos=details.children.map(e=>e.textContent);assert.ok(textos.includes('OTRA-CAJA'));assert.ok(textos.includes('Observación corregida'));
 actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
});
