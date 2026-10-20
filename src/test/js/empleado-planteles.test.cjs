const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const source=fs.readFileSync('src/main/resources/static/js/empleado-planteles.js','utf8');
function fixture({failure=false,delayed=false}={}) {
 const el=()=>({events:{},value:'',disabled:false,hidden:true,textContent:'',children:[],addEventListener(n,f){this.events[n]=f;},focus(){this.focused=true;},append(...v){this.children.push(...v);},replaceChildren(){this.children=[];}});
 const fields={};for(const n of ['fecha','plantelId','motivo','conservarAnterior','origenId','versionOrigen','destinoId','versionDestino'])fields[n]=el();
 fields.fecha.value='2026-11-01';fields.plantelId.value='2';fields.motivo.value='Cambio <b>autorizado</b>';fields.conservarAnterior.checked=true;
 const nodes={};for(const n of ['[data-campus-summary]','[data-campus-confirm]','[data-campus-cancel]','[data-campus-error]','button[type="submit"]'])nodes[n]=el();
 const form={...el(),dataset:{previewUrl:'/preview'},querySelector(s){const m=s.match(/^\[name="(.*?)"\]$/);return m?fields[m[1]]:nodes[s];},reportValidity(){return true;}};
 const dialog={...el(),open:false,querySelector:s=>nodes[s],showModal(){this.open=true;},close(){this.open=false;}};
 const data={origenId:3,versionOrigen:4,origen:'Plantel A',destinoId:5,versionDestino:6,destino:'Plantel B',fecha:'2026-11-01',cierreAnterior:'2026-10-31',finNuevoPrincipal:null,finAnteriorAdicional:null,conservarAnterior:true};
 let resolve;
 const fetch=async()=>{if(delayed)await new Promise(r=>resolve=r);return {ok:!failure,redirected:false,json:async()=>failure?{mensaje:'Vigencias superpuestas'}:data};};
 vm.runInNewContext(source,{document:{querySelector:s=>s==='[data-campus-change]'?form:dialog,createElement:()=>el()},URLSearchParams,AbortController,fetch,window:{location:{assign(){}}}});
 const event=()=>({defaultPrevented:false,preventDefault(){this.defaultPrevented=true;}});
 form.requestSubmit=()=>{const e=event();form.events.submit(e);if(!e.defaultPrevented)form.posts=(form.posts||0)+1;};
 return {form,fields,nodes,dialog,async preview(){const e=event();await form.events.submit(e);return e;},resolve(){resolve();}};
}
test('vista previa abre resumen sin enviar; cancelar, Escape y fondo conservan captura',async()=>{
 for(const modo of ['cancelar','escape','fondo']){const f=fixture();await f.preview();assert.equal(f.dialog.open,true);assert.equal(f.form.posts,undefined);
  const contenido=f.nodes['[data-campus-summary]'].children.map(n=>n.textContent).join('|');assert.match(contenido,/31\/10\/2026/);assert.match(contenido,/Cambio <b>autorizado<\/b>/);
  if(modo==='cancelar')f.nodes['[data-campus-cancel]'].events.click();else if(modo==='escape')f.dialog.events.cancel({preventDefault(){}});else f.dialog.events.click({target:f.dialog});
  assert.equal(f.dialog.open,false);assert.equal(f.form.posts,undefined);assert.equal(f.fields.fecha.value,'2026-11-01');assert.equal(f.nodes['button[type="submit"]'].focused,true);
 }
});
test('sólo confirmar permite un POST con las versiones verificadas',async()=>{const f=fixture();await f.preview();f.nodes['[data-campus-confirm]'].events.click();f.nodes['[data-campus-confirm]'].events.click();assert.equal(f.form.posts,1);assert.equal(f.fields.versionOrigen.value,4);assert.equal(f.fields.versionDestino.value,6);});
test('cambios durante captura invalidan resumen y las respuestas obsoletas se ignoran',async()=>{
 const f=fixture();await f.preview();f.fields.fecha.value='2026-11-02';f.form.events.input();assert.equal(f.dialog.open,false);f.nodes['[data-campus-confirm]'].events.click();assert.equal(f.form.posts,undefined);
 const g=fixture({delayed:true});const p=g.preview();g.fields.plantelId.value='9';g.form.events.change();g.resolve();await p;assert.equal(g.dialog.open,false);
});
test('error de negocio o ausencia de selección no permiten confirmar',async()=>{const f=fixture({failure:true});await f.preview();assert.equal(f.dialog.open,false);assert.equal(f.nodes['[data-campus-error]'].textContent,'Vigencias superpuestas');const g=fixture();g.fields.plantelId.value='';await g.preview();assert.equal(g.dialog.open,false);assert.match(g.nodes['[data-campus-error]'].textContent,/ayuda/);});
