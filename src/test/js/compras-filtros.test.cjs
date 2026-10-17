const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
for(const tipo of ['proveedores','cuentas'])test('filtro '+tipo+': opcional, diez al enfocar, búsqueda3, selección y cambio de alcance',async()=>{
 class E{constructor(){this.value='';this.hidden=true;this.dataset={};this.children=[];this.listeners={};}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}dispatchEvent(e){for(const f of this.listeners[e.type]||[])f(e);}setAttribute(){}removeAttribute(){}replaceChildren(){this.children=[];}append(...v){this.children.push(...v);}appendChild(v){this.append(v);}focus(){this.dispatchEvent({type:'focus'});}contains(){return true;}setCustomValidity(v){this.error=v;}}
 const input=new E(),value=new E(),list=new E(),status=new E(),clear=new E(),institution=new E(),plantel=new E(),box=new E();institution.value='1';input.id=tipo+'Texto';
 box.dataset={institutionInput:'filtroInstitucionId',refreshOnFocus:'true',endpoint:'/admin/autocompletado/compras/filtros/'+tipo};if(tipo==='cuentas')Object.assign(box.dataset,{extraInput:'filtroPlantelId',extraParam:'plantelId'});
 box.hasAttribute=()=>false;box.querySelectorAll=()=>[];box.querySelector=s=>({'.autocomplete-input':input,'.autocomplete-value':value,'.autocomplete-results':list,'.autocomplete-status':status,'.autocomplete-clear':clear}[s]);
 const document=new E();document.querySelectorAll=()=>[box];document.getElementById=n=>({filtroInstitucionId:institution,filtroPlantelId:plantel}[n]);document.createElement=()=>new E();
 let timer;const requests=[];const flush=async()=>{for(let i=0;i<8;i++)await Promise.resolve();};
 const ctx={document,URLSearchParams,URL,AbortController,Event:class{constructor(type){this.type=type;}},setTimeout(f){timer=f;},clearTimeout(){timer=null;},fetch(url,opts){return new Promise(resolve=>requests.push({url,opts,resolve}));}};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/autocomplete.js','utf8'),ctx);vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/compras-filtros.js','utf8'),ctx);document.dispatchEvent({type:'DOMContentLoaded'});assert.equal(input.error,'');
 input.focus();input.dispatchEvent({type:'click'});assert.equal(requests.length,1);assert.equal(new URL(requests[0].url,'http://test').searchParams.get('q'),'');
 requests[0].resolve({ok:true,json:async()=>({resultados:Array.from({length:10},(_,i)=>({id:i+1,titulo:'Opción '+i}))})});await flush();assert.equal(list.children.length,10);
 list.children[0].dispatchEvent({type:'click'});assert.equal(value.value,1);assert.equal(input.error,'');
 input.value='ab';input.dispatchEvent({type:'input'});assert.equal(value.value,'');assert.match(input.error,/Selecciona/);assert.equal(timer,null);assert.equal(requests.length,1);
 input.value='pap';input.dispatchEvent({type:'input'});timer();assert.equal(new URL(requests[1].url,'http://test').searchParams.get('q'),'pap');
 const scope=tipo==='cuentas'?plantel:institution;scope.value='2';scope.dispatchEvent({type:'change'});assert.equal(value.value,'');assert.equal(input.value,'');assert.equal(input.error,'');
 requests[1].resolve({ok:true,json:async()=>({resultados:[{id:99,titulo:'Fuera del alcance anterior'}]})});await flush();assert.equal(list.children.length,0);
 input.focus();assert.equal(requests.length,3);assert.equal(new URL(requests[2].url,'http://test').searchParams.get(tipo==='cuentas'?'plantelId':'institucionId'),'2');
});
