const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
function setup(fetcher) {
 class E {
  constructor(){this.dataset={};this.listeners={};this.textContent='';this.hidden=false;this.classList={add(){},remove(){}};this.attrs={};}
  setAttribute(k,v){this.attrs[k]=v;}addEventListener(n,f){(this.listeners[n]??=[]).push(f);}
  fire(n,e={}){return Promise.all((this.listeners[n]||[]).map(f=>f(e)));}
  focus(){this.focused=true;}showModal(){this.open=true;}close(){this.open=false;this.fire('close');}
 }
 const trigger=new E(),dialog=new E(),button=new E(),status=new E(),details=new E();
 const fields=['usuario','correo','institucion','acceso','alcance'].map(k=>{const f=new E();f.dataset.profileField=k;return f;});
 const document=new E();document.readyState='complete';document.body=new E();document.body.append=()=>{};
 document.querySelector=q=>q==='[data-session-profile]'?dialog:trigger;
 dialog.querySelector=q=>({'[data-session-profile-close]':button,'[data-profile-status]':status,'[data-profile-details]':details}[q]);
 dialog.querySelectorAll=()=>fields;dialog.getBoundingClientRect=()=>({left:20,right:400,top:20,bottom:700});
 const calls=[];
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/session-profile.js','utf8'),{
  document,AbortController,fetch:(...args)=>{calls.push(args);return fetcher(...args);}
 });
 return {trigger,dialog,button,status,details,fields,calls};
}
const response=data=>({ok:true,redirected:false,headers:{get:()=> 'application/json'},json:async()=>data});
test('perfil sólo consulta al pulsar, muestra texto literal y cierra sin enviar formularios',async()=>{
 const ui=setup(async()=>response({usuario:'<img onerror=alert(1)>',correo:'qa@example.com',institucion:'Escuela QA',acceso:'Administración',alcance:'1 plantel autorizado'}));
 assert.equal(ui.calls.length,0);await ui.trigger.fire('click');
 assert.equal(ui.dialog.open,true);assert.equal(ui.trigger.attrs['aria-expanded'],'true');
 assert.equal(ui.calls.length,1);assert.equal(ui.calls[0][0],'/admin/perfil/resumen');
 assert.equal(ui.calls[0][1].credentials,'same-origin');assert.equal(ui.calls[0][1].cache,'no-store');
 assert.equal(ui.fields[0].textContent,'<img onerror=alert(1)>');assert.equal(ui.details.hidden,false);
 await ui.button.fire('click');assert.equal(ui.dialog.open,false);assert.equal(ui.trigger.attrs['aria-expanded'],'false');
 assert.equal(ui.fields[0].textContent,'');assert.equal(ui.trigger.focused,true);assert.equal(ui.calls.length,1);
});
test('Escape y fondo cierran, pero clic dentro de la ficha no la cierra',async()=>{
 const ui=setup(async()=>response({usuario:'qa'}));await ui.trigger.fire('click');
 await ui.dialog.fire('click',{target:ui.dialog,clientX:50,clientY:60});assert.equal(ui.dialog.open,true);
 await ui.dialog.fire('cancel',{preventDefault(){}});assert.equal(ui.dialog.open,false);
 await ui.trigger.fire('click');await ui.dialog.fire('click',{target:ui.dialog,clientX:1,clientY:5});assert.equal(ui.dialog.open,false);
});
test('sesión caducada, HTML o fallo de red mantienen cierre sin datos obsoletos',async()=>{
 for(const fetcher of [async()=>({ok:false}),async()=>({ok:true,redirected:true}),async()=>({ok:true,headers:{get:()=> 'text/html'}}),async()=>{throw new Error('sin red');}]) {
  const ui=setup(fetcher);await ui.trigger.fire('click');assert.equal(ui.dialog.open,true);
  assert.equal(ui.details.hidden,true);assert.match(ui.status.textContent,/inicia sesión nuevamente/);
  await ui.button.fire('click');assert.equal(ui.dialog.open,false);
 }
});
test('cerrar aborta la consulta; respuestas atrasadas no reemplazan la nueva apertura',async()=>{
 const resolvers=[];const ui=setup(()=>new Promise(resolve=>resolvers.push(resolve)));
 const first=ui.trigger.fire('click');await ui.button.fire('click');assert.equal(ui.calls[0][1].signal.aborted,true);
 const second=ui.trigger.fire('click');resolvers[1](response({usuario:'vigente'}));await second;
 resolvers[0](response({usuario:'anterior'}));await first;assert.equal(ui.fields[0].textContent,'vigente');
 await ui.trigger.fire('click');assert.equal(ui.calls.length,2);
});
