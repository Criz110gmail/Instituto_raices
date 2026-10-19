const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const source=fs.readFileSync('src/main/resources/static/js/planeacion-vista.js','utf8');
function fixture({readonly=false,initial='captura',pending=false,modal=false}={}){
 const element=dataset=>({dataset,attrs:{},events:{},setAttribute(k,v){this.attrs[k]=v;},addEventListener(k,v){this.events[k]=v;},focus(){this.focused=true;}});
 const tabs=['captura','resumen','pdf','gestion'].map(planView=>element({planView}));
 const panels=tabs.map(tab=>({...element({planViewPanel:tab.dataset.planView}),hidden:false}));
 const form={...element({readonly:String(readonly)}),hasAttribute:()=>false,values:[['proposito','Original'],['_csrf','ficticio']]};
 const button={disabled:false,focus(){this.focused=true;}},action={...element({}),hasAttribute:name=>modal&&name==='data-plan-send',querySelectorAll:()=>[button]},warning={hidden:true},events={};
 const cancel=element({}),accept={...element({}),disabled:false};
 const dialog={...element({}),open:false,querySelector:selector=>selector==='[data-plan-send-cancel]'?cancel:accept,showModal(){this.open=true;},close(){this.open=false;this.events.close();}};
 action.requestSubmit=()=>{action.submissions=(action.submissions||0)+1;action.events.submit({preventDefault(){throw new Error('Confirmación inesperada');}});};
 const root={dataset:{initialView:initial,pendingSave:String(pending)},querySelector:selector=>selector==='[data-planeacion-form]'?form:selector==='[data-plan-send-dialog]'?(modal?dialog:null):selector==='[data-plan-unsaved]'?warning:null,
  querySelectorAll:selector=>selector==='[data-plan-view]'?tabs:selector==='[data-plan-view-panel]'?panels:[action]};
 vm.runInNewContext(source,{document:{querySelector:()=>root,activeElement:button},window:{addEventListener:(key,value)=>{events[key]=value;}},FormData:class {constructor(f){return f.values;}},console});
 return {tabs,panels,form,button,action,warning,events,dialog,cancel,accept};
}
test('pestañas de vista conservan la captura y soportan teclado sin enviar formularios',()=>{
 const f=fixture();f.form.values[0][1]='Texto sin guardar';f.form.events.input();
 f.tabs[2].events.click();assert.equal(f.panels[2].hidden,false);assert.equal(f.panels[0].hidden,true);
 assert.equal(f.form.values[0][1],'Texto sin guardar');assert.equal(f.tabs[2].attrs['aria-selected'],'true');
 f.tabs[2].events.keydown({key:'Home',preventDefault(){}});assert.equal(f.tabs[0].focused,true);assert.equal(f.panels[0].hidden,false);
});
test('no permite enviar ni descartar si hay captura pendiente; avisa antes de abandonar',()=>{
 const f=fixture({initial:'resumen'});assert.equal(f.panels[1].hidden,false);
 f.form.values[0][1]='Modificado';f.form.events.change();assert.equal(f.warning.hidden,false);assert.equal(f.button.disabled,true);
 let blocked=false;f.action.events.submit({preventDefault(){blocked=true;}});assert.equal(blocked,true);assert.equal(f.panels[0].hidden,false);
 let unload=false;f.events.beforeunload({preventDefault(){unload=true;}});assert.equal(unload,true);
 f.form.values[0][1]='Original';f.form.events.input();assert.equal(f.button.disabled,false);assert.equal(f.warning.hidden,true);
});
test('soporte sólo navega y no instala acciones de escritura o cambios pendientes',()=>{
 const f=fixture({readonly:true});assert.equal(f.form.events.input,undefined);assert.equal(f.events.beforeunload,undefined);
 f.tabs[2].events.click();assert.equal(f.panels[2].hidden,false);
});
test('guardado inválido no desactiva aviso de cambios pendientes',()=>{
 const f=fixture();f.form.values[0][1]='Cambio';f.form.events.input();f.form.events.submit({defaultPrevented:true});
 let prevented=false;f.events.beforeunload({preventDefault(){prevented=true;}});assert.equal(prevented,true);
});
test('respuesta de guardado rechazado mantiene captura pendiente aunque no se edite otra vez',()=>{
 const f=fixture({pending:true});assert.equal(f.warning.hidden,false);assert.equal(f.button.disabled,true);
 let prevented=false;f.action.events.submit({preventDefault(){prevented=true;}});assert.equal(prevented,true);
});
test('abrir confirmación de guardado no desactiva aviso de salida; confirmar sí permite navegar',()=>{
 const f=fixture();f.form.hasAttribute=name=>name==='data-planeacion-confirm';f.form.values[0][1]='Cambio';f.form.events.input();
 f.form.events.submit({defaultPrevented:false});let blocked=false;f.events.beforeunload({preventDefault(){blocked=true;}});assert.equal(blocked,true);
 f.form.dataset.planConfirmationReady='true';f.form.events.submit({defaultPrevented:false});blocked=false;f.events.beforeunload({preventDefault(){blocked=true;}});assert.equal(blocked,false);
});
test('enviar abre modal; cancelar no envía y restaura foco; confirmar envía una sola vez',()=>{
 const f=fixture({modal:true});let blocked=false;
 f.action.events.submit({preventDefault(){blocked=true;}});assert.equal(blocked,true);assert.equal(f.dialog.open,true);assert.equal(f.action.submissions,undefined);
 f.cancel.events.click();assert.equal(f.dialog.open,false);assert.equal(f.button.focused,true);assert.equal(f.action.submissions,undefined);
 f.action.events.submit({preventDefault(){}});f.accept.events.click();assert.equal(f.action.submissions,1);assert.equal(f.accept.disabled,true);
 f.accept.events.click();assert.equal(f.action.submissions,1);
});
test('Escape o cierre del modal no envían y la captura pendiente bloquea confirmación',()=>{
 const f=fixture({modal:true});f.action.events.submit({preventDefault(){}});f.dialog.close();assert.equal(f.action.submissions,undefined);
 f.action.events.submit({preventDefault(){}});f.form.values[0][1]='Cambio externo mientras el modal está abierto';f.form.events.input();f.accept.events.click();
 assert.equal(f.action.submissions,undefined);assert.equal(f.dialog.open,false);assert.equal(f.panels[0].hidden,false);
});
