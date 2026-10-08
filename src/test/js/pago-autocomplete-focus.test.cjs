const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
function el(value='') {
 const classes=new Set();
 return {value,dataset:{},children:[],listeners:{},hidden:false,readOnly:false,textContent:'',
 classList:{add(...v){v.forEach(x=>classes.add(x));},remove(...v){v.forEach(x=>classes.delete(x));},contains:x=>classes.has(x),toggle(){}},
 addEventListener(k,f){(this.listeners[k] ||= []).push(f);},dispatchEvent(e){for(const f of this.listeners[e.type]||[])f(e);},
 append(...v){this.children.push(...v);},replaceChildren(){this.children=[];},removeAttribute(k){delete this[k];},
 focus(){doc.activeElement=this;this.dispatchEvent({type:'focus'});},select(){},closest(){return this;}};
}
function fila(){
 const row=el(),cargo=el(),entrada=el(),resultados=el(),estado=el(),limpiar=el();
 cargo.dataset.paymentAutocomplete='cargo';
 const amount=el(),referencia=el(),parcial=el(),help=el(),currency=el(),id=el();amount.readOnly=true;
 const campos={'input[type="search"]':entrada,'.cargo-id':id,'.monto-solicitado':amount,'.full-amount-reference':referencia,'.partial-payment-toggle':parcial,'.amount-help':help,'.protected-amount-control b':currency,'.remove-distribution':el(),'.row-number':el(),'label':el(),'.distribution-charge input[type="search"]':entrada};
 row.querySelector=s=>s==='[data-payment-autocomplete]'?cargo:campos[s];
 cargo.querySelector=s=>({'input[type="search"]':entrada,'.cargo-id':id,'.autocomplete-results':resultados,'.autocomplete-status':estado,'.autocomplete-clear':limpiar}[s]);
 cargo.closest=()=>row;cargo.contains=e=>e===entrada||e===limpiar;row.dataset.fixedCharge='false';
 return {row,cargo,entrada,resultados,estado,limpiar,amount,id,parcial,help};
}
const ids={};for(const n of ['institucion','plantel','tutorId','cuentaDeclaradaId','metodo','monto','moneda','fechaPago','solicitudes','solicitud-template','distribution-empty','comprobantes','agregar-cargo','receipt-title','payment-reference-label','payment-reference-help','total-pago','total-solicitado','total-restante','allow-unassigned','payment-unassigned-options','payment-review-charges','payment-review-tutor','payment-review-total','payment-review-method','payment-review-account','payment-review-date','payment-review-files','tutor-busqueda','cuenta-busqueda'])ids['#'+n]=el();
ids['#institucion'].value='1';ids['#institucion'].selectedOptions=[{dataset:{moneda:'MXN'}}];
ids['#plantel'].value='2';ids['#plantel'].options=[{}, {dataset:{institucion:'1',activo:'true'}}];ids['#plantel'].selectedOptions=[ids['#plantel'].options[1]];
ids['#tutorId'].value='3';ids['#moneda'].value='MXN';ids['#metodo'].value='EFECTIVO';ids['#fechaPago'].value='2026-10-08T12:00';
let creada,timer;const requests=[];
ids['#solicitud-template'].content={cloneNode(){creada=fila();return creada.row;}};
const lista=ids['#solicitudes'];Object.defineProperty(lista,'lastElementChild',{get(){return this.children.at(-1);}});
lista.querySelectorAll=s=>s==='.distribution-row'?lista.children:s==='.monto-solicitado'?lista.children.map(r=>r.querySelector(s)):[];
const doc=el();doc.querySelector=s=>ids[s];doc.querySelectorAll=()=>[];doc.createElement=()=>el();
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/pago-form.js','utf8'),{
 document:doc,Date,Intl,Number,URLSearchParams,URL,AbortController,location:{assign(){}},
 Event:class {constructor(type){this.type=type;}},setTimeout(fn){timer=fn;return 1;},clearTimeout(){timer=null;},
 fetch(url,opts){return new Promise(resolve=>requests.push({url,opts,resolve}));}
});
const click=e=>e.dispatchEvent({type:'click'}),flush=async()=>{for(let i=0;i<6;i++)await Promise.resolve();};
function responder(i,datos){requests[i].resolve({ok:true,redirected:false,json:async()=>({resultados:datos})});}
(async()=>{
 click(ids['#agregar-cargo']); // La fila agregada recibe foco automáticamente.
 doc.dispatchEvent({type:'click',target:ids['#agregar-cargo']}); // Burbujeo no debe cerrar la ayuda de la fila recién enfocada.
 assert.equal(requests.length,1);assert.equal(new URL(requests[0].url,'http://test').searchParams.get('q'),'');
 click(creada.entrada);assert.equal(requests.length,1); // foco + clic no duplica consulta pendiente.
 responder(0,Array.from({length:10},(_,i)=>({id:i+1,titulo:'Alumno '+i,monto:500})));await flush();
 assert.equal(creada.resultados.children.length,10);assert.equal(creada.resultados.hidden,false);
 doc.dispatchEvent({type:'click',target:{}});assert.equal(creada.resultados.hidden,true);
 click(creada.entrada);assert.equal(requests.length,2); // Reabre aun con resultados anteriores ocultos.
 responder(1,[{id:18,titulo:'Alumno prueba',monto:500}]);await flush();
 creada.entrada.value='Al';creada.entrada.dispatchEvent({type:'input'});assert.equal(timer,null);assert.equal(requests.length,2);
 creada.entrada.value='Alu';creada.entrada.dispatchEvent({type:'input'});timer();
 assert.equal(new URL(requests[2].url,'http://test').searchParams.get('q'),'Alu');
 click(creada.entrada);assert.equal(requests.length,3);
 responder(2,[{id:18,titulo:'Alumno prueba',monto:500}]);await flush();
 click(creada.resultados.children[0]);assert.equal(creada.id.value,18);assert.equal(creada.amount.value,'500.00');assert.equal(creada.parcial.hidden,false);
 click(creada.parcial);creada.amount.value='100';creada.amount.dispatchEvent({type:'input'});
 assert.equal(ids['#monto'].value,'100.00');assert.match(creada.help.textContent,/400/);
 assert.equal(ids['#monto'].readOnly,true);
 const revision=ids['#payment-review-charges'].children[0];
 assert.equal(revision.children[1].classList.contains('payment-charge-review-metrics'),true);
 assert.deepEqual(revision.children[1].children.map(b=>b.children[1].textContent),['$500.00','$100.00','$400.00']);
 assert.equal(revision.children[1].children[2].classList.contains('payment-charge-review-balance'),true);
 assert.match(revision.children[2].textContent,/después de validar/);
 assert.equal(ids['#payment-review-total'].textContent,'$100.00');
 assert.equal(ids['#payment-review-method'].textContent,'Efectivo');
 assert.match(ids['#payment-review-date'].textContent,/08\/10\/2026/);
 ids['#allow-unassigned'].checked=true;ids['#allow-unassigned'].dispatchEvent({type:'change'});
 assert.equal(ids['#monto'].readOnly,false);ids['#monto'].value='150';ids['#monto'].dispatchEvent({type:'input'});
 assert.equal(ids['#total-restante'].textContent,'$50.00');
 creada.amount.value='80';creada.amount.dispatchEvent({type:'input'});assert.equal(ids['#monto'].value,'150');
 ids['#allow-unassigned'].checked=false;ids['#allow-unassigned'].dispatchEvent({type:'change'});
 assert.equal(ids['#monto'].value,'80.00');assert.equal(ids['#monto'].readOnly,true);
 click(creada.limpiar);assert.equal(requests.length,4); // Limpiar vuelve a consultar primeras opciones.
 ids['#tutorId'].value='9';click(creada.entrada);assert.equal(requests.length,5); // Aborta y cambia alcance sin esperar la respuesta anterior.
 responder(3,[{id:99,titulo:'Tutor anterior',monto:999}]);await flush();assert.equal(creada.resultados.children.length,0);
 click(creada.entrada);assert.equal(requests.length,5);assert.equal(new URL(requests[4].url,'http://test').searchParams.get('tutorId'),'9');
 responder(4,[{id:21,titulo:'Tutor actual',monto:200}]);await flush();assert.equal(creada.resultados.children.length,1);
 ids['#tutorId'].value='';click(creada.entrada);assert.match(creada.estado.textContent,/Selecciona primero un tutor/);assert.equal(requests.length,5);
 console.log('Pagos: primeras10/foco/clic/búsqueda3/alcance; revisión500/abono100/saldo400 y dinero extra explícito correctos.');
})().catch(e=>{console.error(e);process.exitCode=1;});
