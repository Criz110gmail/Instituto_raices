const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const source = fs.readFileSync('src/main/resources/static/js/sitio-escolar.js', 'utf8');
function environment({reduced=false, visited=false, storageFails=false, pending=false}={}) {
 const timers=[], handlers={}, attributes={}, classes=new Set(), bg=[{},{}];
 const skip={addEventListener:(name,fn)=>handlers[name]=fn,focus(){document.activeElement=this;}};
 const welcome={hidden:true,style:{setProperty(){}},classList:{add:name=>classes.add(name)},
  querySelector:selector=>selector==='[data-skip]'?skip:selector==='[role="progressbar"]'?{setAttribute:(key,value)=>attributes[key]=value}:{textContent:''}};
 const image={complete:!pending,addEventListener:(name,fn)=>handlers['image:'+name]=fn};
 const body={classList:{add:name=>classes.add(name),remove:name=>classes.delete(name)}};
 const document={body,activeElement:body,getElementById:()=>null,querySelector:selector=>selector==='[data-welcome]'?welcome:null,
  querySelectorAll:selector=>selector==='[data-essential-image]'?[image]:bg,addEventListener:(name,fn)=>handlers[name]=fn};
 let remembered=visited;
 vm.runInNewContext(source,{document,window:{matchMedia:()=>({matches:reduced})},sessionStorage:{getItem(){if(storageFails)throw Error('blocked');return remembered;},setItem(){if(storageFails)throw Error('blocked');remembered=true;}},setTimeout:(fn,ms)=>{timers.push({fn,ms});return timers.length;},clearTimeout(){}});
 return {welcome,bg,handlers,attributes,classes,timers,remembered:()=>remembered};
}
(async()=>{
 for(const settings of [{reduced:true},{visited:true}]) assert.equal(environment(settings).welcome.hidden,true);
 const ready=environment(); await new Promise(setImmediate);
 assert.equal(ready.attributes['aria-valuenow'],'100');
 assert.ok(ready.bg.every(e=>e.inert));
 ready.handlers.click(); ready.timers.find(t=>t.ms===360).fn();
 assert.equal(ready.welcome.hidden,true); assert.ok(ready.bg.every(e=>!e.inert));assert.equal(ready.remembered(),true);
 const stalled=environment({pending:true,storageFails:true});
 assert.equal(stalled.attributes['aria-valuenow'],'0');
 stalled.timers.find(t=>t.ms===3500).fn();stalled.timers.find(t=>t.ms===360).fn();
 assert.equal(stalled.welcome.hidden,true);assert.ok(!stalled.classes.has('welcoming'));
 const failed=environment({pending:true});failed.handlers['image:error']();await new Promise(setImmediate);
 assert.equal(failed.attributes['aria-valuenow'],'100');failed.handlers.keydown({key:'Escape'});
 assert.ok(failed.bg.every(e=>!e.inert));
 console.log('Sitio escolar: carga, omitir, Escape, movimiento reducido, repetición, fallo de imagen, almacenamiento bloqueado y límite de espera correctos.');
})().catch(error=>{console.error(error);process.exitCode=1;});
