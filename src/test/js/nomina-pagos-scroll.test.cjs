const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const script=fs.readFileSync('src/main/resources/static/js/nomina-filtros.js','utf8');
function escenario({saved,hidden=false,documentTop=500,bloqueado=false,tipo='pagos',search='?estadoPago=PENDIENTE',clearHref='/admin/preparacion-nomina/7/pagos',action='/admin/preparacion-nomina/7/pagos'}={}){
 const location=new URL('http://qa.local/admin/preparacion-nomina/7/pagos'+search+'#empleados');
 const key='nomina-filtros-scroll:'+location.pathname+':'+tipo,storage=new Map(),events={};
 if(saved)storage.set(key,JSON.stringify(saved));
 let submit,click,focused=false,clearFocused=false,reloads=0;
 const window={location:{reload(){reloads++;}},scrollY:0,scrollTo({top}){this.scrollY=top;},addEventListener(type,fn){events[type]=fn;}};
 const panel={hidden,dataset:{studentPanel:'empleados'}};
 const link={href:new URL(clearHref,location.href).href,textContent:'Limpiar',target:'',setAttribute(){},addEventListener(type,fn){click=fn;},focus(){clearFocused=true;}};
 const form={dataset:{nominaKeepFilter:tipo},getAttribute(){return action;},getBoundingClientRect(){return {top:documentTop-window.scrollY};},closest(){return panel;},addEventListener(type,fn){if(type==='submit')submit=fn;},querySelectorAll(){return [link];},querySelector(){return {focus(){focused=true;}};}};
 const document={readyState:'complete',querySelectorAll(){return [form];},querySelector(){return {dataset:{tabHash:'#empleados'}};}};
 vm.runInNewContext(script,{document,window,location,URL,history:{replaceState(){}},sessionStorage:{getItem:k=>{if(bloqueado)throw Error();return storage.get(k);},removeItem:k=>storage.delete(k),setItem:(k,v)=>{if(bloqueado)throw Error();storage.set(k,v);}},requestAnimationFrame:fn=>fn()});
 return {window,storage,key,submit,click,link,events,get reloads(){return reloads;},get focused(){return focused;},get clearFocused(){return clearFocused;}};
}
const evento=extra=>({button:0,defaultPrevented:false,preventDefault(){this.defaultPrevented=true;},...extra});
const restore=escenario({saved:{offset:300}});
assert.equal(restore.window.scrollY,200);assert.equal(restore.focused,true);assert.equal(restore.storage.has(restore.key),false);
restore.window.scrollY=450;restore.submit();assert.deepEqual(JSON.parse(restore.storage.get(restore.key)),{offset:50,clear:false});
const negative=escenario();negative.window.scrollY=620;negative.submit();assert.equal(JSON.parse(negative.storage.get(negative.key)).offset,-120);
assert.equal(escenario({saved:{offset:-120}}).window.scrollY,620);
const clean=escenario();clean.window.scrollY=600;clean.click(evento());assert.equal(new URL(clean.link.href).hash,'#empleados');assert.deepEqual(JSON.parse(clean.storage.get(clean.key)),{offset:-100,clear:true});
const clearRestore=escenario({saved:{offset:-100,clear:true}});assert.equal(clearRestore.window.scrollY,600);assert.equal(clearRestore.clearFocused,true);
const same=escenario({search:''});const ev=evento();same.click(ev);assert.equal(ev.defaultPrevented,true);assert.equal(same.storage.size,1);assert.equal(same.reloads,1);
for(const extra of [{ctrlKey:true},{metaKey:true},{shiftKey:true},{altKey:true},{button:1}]){const test=escenario();test.click(evento(extra));assert.equal(test.storage.size,0);}
const external=escenario({clearHref:'https://elsewhere.invalid/pagos'});external.click(evento());assert.equal(external.storage.size,0);
const hidden=escenario({saved:{offset:300},hidden:true});assert.equal(hidden.window.scrollY,0);assert.equal(hidden.focused,false);
assert.equal(escenario().window.scrollY,0);
for(const tipo of ['preparacion','conceptos','simulacion','pendientes','puestos-0'])assert.equal(escenario({saved:{offset:100},tipo}).window.scrollY,400);
const llegada=escenario({saved:{offset:100}});llegada.window.scrollY=0;llegada.events.pageshow({persisted:false});assert.equal(llegada.window.scrollY,400);
llegada.window.scrollY=700;llegada.events.pageshow({persisted:true});assert.equal(llegada.window.scrollY,700);
const cerrado=escenario({bloqueado:true});assert.doesNotThrow(()=>cerrado.submit());assert.doesNotThrow(()=>cerrado.click(evento()));
assert.equal(escenario({saved:{offset:'invalido'}}).window.scrollY,0);
const destino=escenario({action:'/admin/preparacion-nomina/seleccion-manual'});destino.submit();assert.equal(destino.storage.has('nomina-filtros-scroll:/admin/preparacion-nomina/seleccion-manual:pagos'),true);
console.log('Filtrar y Limpiar: offset negativo preservado, pestaña, foco, GET de destino, enlaces modificados y almacenamiento seguro correctos.');
