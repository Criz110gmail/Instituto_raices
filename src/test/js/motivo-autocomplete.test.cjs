const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
test('motivo: diez iniciales, tres caracteres, cambio de tipo y respuestas antiguas',async()=>{
 class E{constructor(){this.value='';this.hidden=true;this.dataset={};this.children=[];this.listeners={};}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}dispatchEvent(e){for(const f of this.listeners[e.type]||[])f(e);}setAttribute(){}removeAttribute(){}replaceChildren(){this.children=[];}append(...v){this.children.push(...v);}appendChild(v){this.append(v);}focus(){this.dispatchEvent({type:'focus'});}contains(){return true;}setCustomValidity(v){this.validity=v;}}
 const entrada=new E(),id=new E(),lista=new E(),estado=new E(),limpiar=new E(),institucion=new E(),direccion=new E(),contenedor=new E();
 entrada.id='motivoTexto';institucion.value='1';direccion.value='INGRESO';
 contenedor.dataset={institutionInput:'institucionId',extraInput:'direccion',extraParam:'direccion',refreshOnFocus:'true',endpoint:'/admin/autocompletado/motivos-movimiento'};
 contenedor.hasAttribute=()=>false;contenedor.querySelectorAll=()=>[];
 contenedor.querySelector=s=>({'.autocomplete-input':entrada,'.autocomplete-value':id,'.autocomplete-results':lista,'.autocomplete-status':estado,'.autocomplete-clear':limpiar}[s]);
 const doc=new E();doc.querySelectorAll=()=>[contenedor];doc.getElementById=n=>({institucionId:institucion,direccion,motivoTexto:entrada,motivoFinancieroId:id}[n]);doc.createElement=()=>new E();
 let timer;const requests=[];
 const ctx={document:doc,URLSearchParams,URL,AbortController,Event:class{constructor(type){this.type=type;}},setTimeout(fn){timer=fn;},clearTimeout(){timer=null;},fetch(url,opts){return new Promise(resolve=>requests.push({url,opts,resolve}));}};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/autocomplete.js','utf8'),ctx);
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/movimiento-manual.js','utf8'),ctx);doc.dispatchEvent({type:'DOMContentLoaded'});
 const flush=async()=>{for(let i=0;i<8;i++)await Promise.resolve();};
 const responder=(n,datos)=>requests[n].resolve({ok:true,json:async()=>({resultados:datos})});
 entrada.focus();entrada.dispatchEvent({type:'click'});assert.equal(requests.length,1);
 assert.equal(new URL(requests[0].url,'http://test').searchParams.get('direccion'),'INGRESO');
 direccion.value='EGRESO';direccion.dispatchEvent({type:'change'});assert.equal(id.value,'');
 entrada.focus();assert.equal(requests.length,2);
 responder(0,[{id:1,titulo:'Ingreso antiguo'}]);await flush();assert.equal(lista.children.length,0);
 responder(1,Array.from({length:10},(_,i)=>({id:i+2,titulo:'Egreso '+i,detalle:'Pruebas'})));await flush();assert.equal(lista.children.length,10);
 lista.children[0].dispatchEvent({type:'click'});assert.equal(id.value,2);assert.equal(entrada.validity,'');
 direccion.value='INGRESO';direccion.dispatchEvent({type:'change'});assert.equal(id.value,'');assert.match(entrada.validity,/Selecciona un motivo/);
 entrada.value='ab';entrada.dispatchEvent({type:'input'});assert.equal(timer,null);assert.equal(requests.length,2);
 entrada.value='jab';entrada.dispatchEvent({type:'input'});timer();assert.equal(new URL(requests[2].url,'http://test').searchParams.get('q'),'jab');
 institucion.value='9';institucion.dispatchEvent({type:'change'});responder(2,[{id:99,titulo:'Institución anterior'}]);await flush();assert.equal(lista.children.length,0);
 entrada.focus();assert.equal(new URL(requests[3].url,'http://test').searchParams.get('institucionId'),'9');
});
