const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const source=fs.readFileSync('src/main/resources/static/js/planeacion-confirmacion.js','utf8');
function fixture(kind,{reason='',valid=true,prevented=false}={}){
 const el=()=>({events:{},disabled:false,textContent:'',addEventListener(n,cb){this.events[n]=cb;},focus(){this.focused=true;}});
 const nodes=new Map();const node=s=>{if(!nodes.has(s))nodes.set(s,el());return nodes.get(s);};
 const trigger=el(),why={...el(),value:reason,required:true,setCustomValidity(value){this.validation=value;}};
 const form={...el(),dataset:{planeacionConfirm:kind},noValidate:kind==='guardar',querySelector(selector){
  if(selector==='[name=motivo]')return ['ajustes','reabrir','descartar'].includes(kind)?why:null;
  if(selector==='[data-plan-group]')return {selectedOptions:[{textContent:'Primero A · Plantel QA'}]};
  return {value:selector==='[name=fechaInicio]'?'2026-10-19':'2026-10-23'};
 },querySelectorAll(){return [{closest(){return {querySelector(){return {textContent:'Matemáticasi',childNodes:[{nodeType:3,textContent:'Matemáticas'},{nodeType:1,textContent:'i'}]};}};}}];},reportValidity(){return valid&&!why.validation;}};
 const event=()=>({defaultPrevented:prevented,submitter:trigger,preventDefault(){this.defaultPrevented=true;}});
 form.requestSubmit=()=>{const e=event();form.events.submit(e);if(!e.defaultPrevented)form.posts=(form.posts||0)+1;};
 const dialog={...el(),open:false,querySelector:node,showModal(){this.open=true;},close(){this.open=false;this.events.close();},getBoundingClientRect(){return {left:10,right:100,top:10,bottom:100};}};
 vm.runInNewContext(source,{document:{querySelector:()=>dialog,querySelectorAll:()=>[form],activeElement:trigger}});
 return {form,dialog,trigger,why,node,submit(){const e=event();form.events.submit(e);return e;}};
}
test('publicar, reabrir, iniciar, ajustes, guardar y descartar requieren confirmar antes del POST',()=>{
 for(const kind of ['publicar','reabrir','revision','ajustes','guardar','descartar']){
  const f=fixture(kind,{reason:'Motivo de prueba'});assert.equal(f.submit().defaultPrevented,true);assert.equal(f.dialog.open,true);assert.equal(f.form.posts,undefined);
  f.node('[data-pc-cancel]').events.click();assert.equal(f.dialog.open,false);assert.equal(f.form.posts,undefined);assert.equal(f.trigger.focused,true);
  f.submit();f.node('[data-pc-accept]').events.click();assert.equal(f.form.posts,1);f.node('[data-pc-accept]').events.click();assert.equal(f.form.posts,1);assert.equal(f.form.dataset.planConfirmationReady,undefined);
 }
});
test('motivo obligatorio, validación previa y confirmación no se pueden omitir',()=>{
 for(const reason of ['','   ']){const f=fixture('reabrir',{reason});f.submit();assert.equal(f.dialog.open,false);assert.equal(f.form.posts,undefined);assert.match(f.why.validation,/motivo/);}
 const invalid=fixture('publicar',{valid:false});invalid.submit();assert.equal(invalid.dialog.open,false);
 const blocked=fixture('guardar',{prevented:true});blocked.submit();assert.equal(blocked.dialog.open,false);assert.equal(blocked.form.posts,undefined);
});
test('resumen usa fechas dd/mm/yyyy y motivo como texto; Escape/fondo no envían',()=>{
 const save=fixture('guardar');save.submit();assert.equal(save.node('[data-pc-week]').textContent,'19/10/2026 al 23/10/2026');assert.equal(save.node('[data-pc-subjects]').textContent,'Matemáticas');
 const f=fixture('ajustes',{reason:'Corregir <script>sin ejecutar</script>'});f.submit();assert.equal(f.node('[data-pc-reason]').textContent,'Corregir <script>sin ejecutar</script>');assert.equal(f.node('[data-pc-reason-row]').hidden,false);
 f.dialog.close();assert.equal(f.form.posts,undefined);f.submit();f.dialog.events.click({target:f.dialog,clientX:0,clientY:0});assert.equal(f.dialog.open,false);assert.equal(f.form.posts,undefined);
});
test('al volver a validar antes de confirmar se evita enviar un motivo que ya no es válido',()=>{
 const f=fixture('descartar',{reason:'Registro equivocado'});f.submit();f.why.value=' ';f.node('[data-pc-accept]').events.click();assert.equal(f.form.posts,undefined);assert.equal(f.dialog.open,false);
});
