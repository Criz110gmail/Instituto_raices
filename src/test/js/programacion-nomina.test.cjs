const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
class E{
 constructor(tag){this.tag=tag;this.children=[];this.listeners={};this.dataset={};this.value='';this.hidden=true;}
 append(...v){this.children.push(...v);}replaceChildren(){this.children=[];}setAttribute(){}focus(){}close(){this.open=false;}showModal(){this.open=true;}
 addEventListener(n,f){(this.listeners[n]??=[]).push(f);}fire(n,e={}){for(const f of this.listeners[n]||[])f(e);}
}
for(const tipo of ['nomina-calendario','nomina-periodo','nomina-periodo-cancelar'])test(tipo+': confirmar una sola vez, cancelar y Escape no guardan',()=>{
 const document=new E('document'),body=new E('body');document.body=body;document.readyState='complete';body.classList={add(){},remove(){}};document.querySelectorAll=()=>[];document.createElement=t=>new E(t);
 const form=new E('form');form.dataset={moneyConfirm:tipo,confirmSubject:'ENE-2027 · Enero',confirmCalendar:'NOM-MEN · Mensual',confirmStart:'01/01/2027',confirmEnd:'31/01/2027'};
 const fields={codigo:{value:'ENE-2027'},nombre:{value:'Enero de prueba'},descripcion:{value:'Sin pagos'},frecuencia:{value:'MENSUAL',selectedOptions:[{textContent:'Mensual'}]},activo:{checked:true},calendarioTexto:{value:'NOM-MEN · Mensual'},inicio:{value:'2027-01-01'},fin:{value:'2027-01-31'},corte:{value:'2027-01-28'},pago:{value:'2027-01-31'},motivo:{value:'Organización sin movimientos'}};
 form.querySelector=s=>fields[s.match(/name="([^"]+)"/)?.[1]]||null;form.querySelectorAll=()=>[];form.reportValidity=()=>true;let enviados=0;
 const emitir=()=>{const e={target:form,submitter:new E('button'),defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}};document.fire('submit',e);if(!e.defaultPrevented)enviados++;};form.requestSubmit=emitir;
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'),{document,Intl,WeakMap,WeakSet,setTimeout,MutationObserver:class{observe(){}}});
 emitir();assert.equal(enviados,0);const dialog=body.children.find(e=>e.tag==='dialog'),details=dialog.children[2],actions=dialog.children[4];assert.equal(dialog.open,true);assert.equal(actions.children[0].textContent,'Cancelar');assert.ok(details.children.length>2);
 actions.children[0].fire('click');assert.equal(enviados,0);emitir();dialog.fire('cancel',{preventDefault(){}});assert.equal(enviados,0);emitir();actions.children[1].fire('click');actions.children[1].fire('click');assert.equal(enviados,1);
 if(tipo==='nomina-periodo'){fields.corte.value='';emitir();assert.ok(details.children.some(e=>e.textContent==='Código'));assert.ok(details.children.some(e=>e.textContent==='Días cubiertos'));assert.ok(!details.children.some(e=>e.textContent==='Calendario'));actions.children[0].fire('click');assert.equal(enviados,1);}
});
test('periodo: consulta al seleccionar, ignora respuestas atrasadas y bloquea contexto eliminado',async()=>{
 const form=new E('form'),id=new E(),texto=new E(),version=new E(),boton=new E(),estado=new E(),error=new E(),frecuencia=new E();form.dataset.edicion='false';
 form.querySelector=s=>({'[name="calendarioId"]':id,'[name="calendarioTexto"]':texto,'[name="calendarioVersion"]':version,'[data-periodo-confirmar]':boton,'[data-periodo-estado]':estado,'[data-periodo-error]':error,'[data-periodo-frecuencia]':frecuencia}[s]);
 const requests=[];vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/programacion-nomina.js','utf8'),{document:{querySelector:()=>form},AbortController,encodeURIComponent,fetch(url,opts){return new Promise(resolve=>requests.push({url,opts,resolve}));}});
 const flush=async()=>{for(let n=0;n<8;n++)await Promise.resolve();};assert.equal(boton.disabled,true);assert.equal(requests.length,0);
 id.value='1';texto.value='CAL1';id.fire('change');id.value='2';texto.value='CAL2';id.fire('change');assert.equal(requests[0].opts.signal.aborted,true);
 requests[0].resolve({ok:true,json:async()=>({version:9,calendario:'CAL1',frecuencia:'Semanal'})});await flush();assert.equal(boton.disabled,true);
 requests[1].resolve({ok:true,json:async()=>({version:0,calendario:'CAL2',frecuencia:'Mensual'})});await flush();assert.equal(boton.disabled,false);assert.equal(version.value,0);assert.equal(frecuencia.value,'Mensual');
 id.value='';id.fire('change');assert.equal(boton.disabled,true);assert.equal(version.value,'');let prevenido=false;form.fire('submit',{preventDefault(){prevenido=true;}});assert.equal(prevenido,true);assert.equal(error.hidden,false);
});
test('periodo: selección renombrada no habilita guardar y el error se presenta en el formulario',async()=>{
 const form=new E('form'),fields={calendarioId:new E(),calendarioTexto:new E(),calendarioVersion:new E()},nodes={};fields.calendarioId.value='1';fields.calendarioTexto.value='Viejo';
 form.querySelector=s=>s.startsWith('[name=')?fields[s.match(/name="([^"]+)"/)[1]]:(nodes[s]??=new E());
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/programacion-nomina.js','utf8'),{document:{querySelector:()=>form},AbortController,encodeURIComponent,fetch:async()=>({ok:true,json:async()=>({version:2,calendario:'Nuevo',frecuencia:'Mensual'})})});for(let n=0;n<10;n++)await Promise.resolve();assert.equal(nodes['[data-periodo-confirmar]'].disabled,true);assert.match(nodes['[data-periodo-error]'].textContent,/cambió/);
});
