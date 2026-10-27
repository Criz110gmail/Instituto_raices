const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('fs'),vm=require('node:vm');
test('puesto: datos del catálogo, error visible y respuesta antigua no habilita envío',async()=>{
 const element=value=>({value,disabled:false,listeners:{},addEventListener(n,f){this.listeners[n]=f;}});
 const id=element(''),texto=element(''),version=element(''),horas=element(''),sueldo=element(''),status=element(''),submit=element('');
 const fields={'[name="puestoId"]':id,'[name="puestoTexto"]':texto,'[data-puesto-version]':version,'[data-puesto-horas]':horas,'[data-puesto-sueldo]':sueldo,'[data-puesto-status]':status,'[data-puesto-submit]':submit};
 const form={dataset:{contextUrl:'/contexto'},querySelector:s=>fields[s],addEventListener(n,f){this[n]=f;}};
 const pending=[];const fetch=()=>new Promise(resolve=>pending.push(resolve));
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/asignacion-puesto.js','utf8'),{document:{querySelector:()=>form},fetch,URLSearchParams,AbortController,Intl});
 assert.equal(submit.disabled,true);id.value='7';texto.value='P40 · Apoyo técnico';id.listeners.change();
 const data={puesto:{codigo:'P40',nombre:'Apoyo técnico',version:3,horasSemanales:40,sueldoMensual:10000},moneda:'MXN'};
 pending.shift()({ok:true,redirected:false,json:async()=>data});await new Promise(setImmediate);
 assert.equal(version.value,3);assert.equal(horas.value,40);assert.match(sueldo.value,/10,000\.00/);assert.equal(submit.disabled,false);
 id.value='8';texto.value='Otro';id.listeners.change();id.value='';id.listeners.change();
 pending.shift()({ok:true,redirected:false,json:async()=>data});await new Promise(setImmediate);assert.equal(version.value,'');assert.equal(submit.disabled,true);
 id.value='7';id.listeners.change();pending.shift()({ok:false});await new Promise(setImmediate);assert.match(status.textContent,/No fue posible/);assert.equal(submit.disabled,true);
 let blocked=false;form.submit({preventDefault(){blocked=true;},stopImmediatePropagation(){}});assert.equal(blocked,true);
});
