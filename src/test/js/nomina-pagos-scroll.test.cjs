const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const script=fs.readFileSync('src/main/resources/static/js/preparacion-nomina.js','utf8');
function escenario({saved,hash='#empleados',hidden=false}={}){
 const storage=new Map(),key='nomina-pagos-filtros-scroll:/admin/preparacion-nomina/7/pagos';
 if(saved)storage.set(key,JSON.stringify(saved));
 let submit,focused=false,anchor=false;
 const window={scrollY:0,scrollTo({top}){this.scrollY=top;},addEventListener(){}};
 const form={addEventListener(type,fn){if(type==='submit')submit=fn;},querySelector(){return {focus(){focused=true;}};}};
 const table={getBoundingClientRect(){return {top:500};},closest(){return {hidden};},scrollIntoView(){anchor=true;}};
 const document={readyState:'complete',querySelector(s){return s==='[data-nomina-payments-filter]'?form:s==='[data-nomina-payments-table]'?table:null;},querySelectorAll(){return [];}};
 vm.runInNewContext(script,{document,window,location:{pathname:'/admin/preparacion-nomina/7/pagos',hash},sessionStorage:{getItem:k=>storage.get(k),removeItem:k=>storage.delete(k),setItem:(k,v)=>storage.set(k,v)},requestAnimationFrame:fn=>fn()});
 return {window,storage,key,submit,focused,anchor};
}
const restore=escenario({saved:{y:1200,offset:300}});
assert.equal(restore.window.scrollY,200);assert.equal(restore.focused,true);assert.equal(restore.storage.has(restore.key),false);
restore.window.scrollY=450;restore.submit();assert.deepEqual(JSON.parse(restore.storage.get(restore.key)),{y:450,offset:500});
assert.equal(escenario().anchor,true);
assert.equal(escenario({hash:'#resumen'}).anchor,false);
const error=escenario({saved:{y:1200,offset:300},hidden:true});assert.equal(error.window.scrollY,0);assert.equal(error.focused,false);
console.log('Filtros: posición, consumo único, alternativa de tabla y respeto a otras pestañas correctos.');
