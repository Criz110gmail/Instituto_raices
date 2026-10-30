const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const script=fs.readFileSync('src/main/resources/static/js/preparacion-nomina.js','utf8');
function escenario({saved,hash='#empleados',hidden=false,offset=500,bloqueado=false,tipo='pagos'}={}){
 const storage=new Map(),key='nomina-filtros-scroll:/admin/preparacion-nomina/7/pagos:'+tipo;
 if(saved)storage.set(key,JSON.stringify(saved));
 let submit,focused=false;const events={};
 const window={scrollY:0,scrollTo({top}){this.scrollY=top;},addEventListener(type,fn){events[type]=fn;}};
 const form={dataset:{nominaKeepFilter:tipo},getBoundingClientRect(){return {top:offset};},closest(){return {hidden};},addEventListener(type,fn){if(type==='submit')submit=fn;},querySelector(){return {focus(){focused=true;}};}};
 const document={readyState:'complete',querySelector(){return null;},querySelectorAll(s){return s==='[data-nomina-keep-filter]'?[form]:[];}};
 vm.runInNewContext(script,{document,window,location:{pathname:'/admin/preparacion-nomina/7/pagos',hash},sessionStorage:{getItem:k=>{if(bloqueado)throw Error();return storage.get(k);},removeItem:k=>storage.delete(k),setItem:(k,v)=>{if(bloqueado)throw Error();storage.set(k,v);}},requestAnimationFrame:fn=>fn()});
 return {window,storage,key,submit,focused,events};
}
const restore=escenario({saved:{offset:300}});
assert.equal(restore.window.scrollY,200);assert.equal(restore.focused,true);assert.equal(restore.storage.has(restore.key),false);
restore.window.scrollY=450;restore.submit();assert.deepEqual(JSON.parse(restore.storage.get(restore.key)),{offset:500});
assert.equal(escenario().window.scrollY,0);
const error=escenario({saved:{offset:300},hidden:true});assert.equal(error.window.scrollY,0);assert.equal(error.focused,false);
const parcial=escenario({offset:-120});parcial.submit();assert.deepEqual(JSON.parse(parcial.storage.get(parcial.key)),{offset:16});
for(const tipo of ['preparacion','conceptos','simulacion','pendientes']){const caso=escenario({saved:{offset:100},hash:'',tipo});assert.equal(caso.window.scrollY,400);assert.equal(caso.focused,true);}
const llegada=escenario({saved:{offset:100}});llegada.window.scrollY=0;llegada.events.pageshow({persisted:false});assert.equal(llegada.window.scrollY,400);
llegada.window.scrollY=700;llegada.events.pageshow({persisted:true});assert.equal(llegada.window.scrollY,700);
const cerrado=escenario({bloqueado:true});assert.doesNotThrow(()=>cerrado.submit());
assert.equal(escenario({saved:{offset:'invalido'}}).window.scrollY,0);
console.log('Filtros: posición del formulario, foco sin salto, almacenamiento seguro y paneles separados correctos.');
