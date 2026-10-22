const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');

test('conceptos habituales: conserva captura=true al enfocar, escribir tres letras y quitar selección',async()=>{
 class E{
  constructor(){this.value='';this.hidden=true;this.dataset={};this.children=[];this.listeners={};}
  addEventListener(n,f){(this.listeners[n]??=[]).push(f);}dispatchEvent(e){for(const f of this.listeners[e.type]||[])f(e);}
  setAttribute(){}removeAttribute(){}replaceChildren(){this.children=[];}append(...v){this.children.push(...v);}appendChild(v){this.append(v);}focus(){this.dispatchEvent({type:'focus'});}contains(){return false;}
 }
 const entrada=new E(),id=new E(),lista=new E(),estado=new E(),limpiar=new E(),contenedor=new E(),doc=new E();entrada.id='concepto-texto';
 contenedor.dataset={refreshOnFocus:'true',endpoint:'/admin/conceptos-empleado/conceptos/buscar?captura=true'};contenedor.hasAttribute=n=>n==='data-no-scope';contenedor.querySelectorAll=()=>[];
 contenedor.querySelector=s=>({'.autocomplete-input':entrada,'.autocomplete-value':id,'.autocomplete-results':lista,'.autocomplete-status':estado,'.autocomplete-clear':limpiar}[s]);doc.querySelectorAll=()=>[contenedor];doc.getElementById=()=>null;doc.createElement=()=>new E();
 let timer;const requests=[];
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/autocomplete.js','utf8'),{document:doc,URLSearchParams,URL,AbortController,Event:class{constructor(type){this.type=type;}},setTimeout(fn){timer=fn;},clearTimeout(){timer=null;},fetch(url,opts){return new Promise(resolve=>requests.push({url,opts,resolve}));}});
 const flush=async()=>{for(let n=0;n<8;n++)await Promise.resolve();};
 const verificar=(n,q)=>{const u=new URL(requests[n].url,'http://test');assert.equal(u.pathname,'/admin/conceptos-empleado/conceptos/buscar');assert.equal(u.searchParams.get('captura'),'true');assert.equal(u.searchParams.get('q'),q);assert.equal(u.searchParams.getAll('q').length,1);assert.equal((requests[n].url.match(/\?/g)||[]).length,1);};
 const responder=n=>requests[n].resolve({ok:true,json:async()=>({resultados:[{id:1,titulo:'BON-PUNT-TEST · Bono de puntualidad',detalle:'Percepción · Habitual · Activo'},{id:2,titulo:'APORT-TEST · Aportación',detalle:'Aportación patronal · Habitual · Activo'}]})});
 entrada.focus();entrada.dispatchEvent({type:'click'});assert.equal(requests.length,1);verificar(0,'');responder(0);await flush();assert.equal(lista.hidden,false);assert.equal(lista.children.length,2);
 entrada.value='BO';entrada.dispatchEvent({type:'input'});assert.equal(requests.length,1);assert.equal(timer,null);
 entrada.value='BON';entrada.dispatchEvent({type:'input'});timer();verificar(1,'BON');responder(1);await flush();lista.children[0].dispatchEvent({type:'click'});assert.equal(id.value,1);
 limpiar.dispatchEvent({type:'click'});verificar(2,'');responder(2);await flush();assert.equal(lista.hidden,false);
 const html=fs.readFileSync('src/main/resources/templates/admin/concepto-empleado-form.html','utf8');assert.ok(html.includes('/admin/conceptos-empleado/conceptos/buscar?captura=true'));
});
