const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const C=require('../../main/resources/static/js/portal-identidad.js');
const source=fs.readFileSync('src/main/resources/static/js/portal-interior.js','utf8');
async function run(path='/admin/empleados',data={},body={}){
 const requests=[],links=[],vars={},root={dataset:{},style:{setProperty(k,v){vars[k]=v;}}};
 const document={readyState:'complete',body:{dataset:body},documentElement:root,createElement(){return {};},head:{append(n){links.push(n);}}};
 vm.runInNewContext(source,{window:{NexoPortalColores:C},MutationObserver:class{observe(){}},document,location:{pathname:path},fetch:async(url,opts)=>{requests.push({url,opts});return {ok:true,json:async()=>({portal:'admin',original:false,principal:'#194f3d',fondo:'#eff5ef',acento:'#ffff00',...data})};}});
 await new Promise(resolve=>setImmediate(resolve));return {requests,links,root,vars};
}
test('aplica sólo colores publicados de la sesión y texto contrastado',async()=>{
 const e=await run();assert.equal(e.root.dataset.piInterior,'true');assert.equal(e.vars['--pi-ui-on-accent'],'#000000');assert.equal(e.vars['--pi-ui-on-primary'],'#ffffff');assert.equal(e.links.length,1);assert.equal(e.requests[0].url,'/sesion/identidad?portal=admin');assert.equal(e.requests[0].opts.cache,'no-store');
});
test('cada espacio solicita su portal sin enviar institución ni usuario',async()=>{
 for(const [route,portal]of [['/portal/expediente','familias'],['/maestros/planeaciones','maestros'],['/personal/recibos','personal']]){
  const e=await run(route,{portal});assert.equal(e.requests[0].url,'/sesion/identidad?portal='+portal);assert.equal(e.root.dataset.piPortal,portal);
 }
});
test('diseño inicial, respuesta de otro portal y valores inseguros conservan el estilo anterior',async()=>{
 for(const data of [{portal:'familias'},{acento:'red'},{fondo:'#fff;display:none'}]){
  const e=await run('/admin',data);assert.equal(e.links.length,0);assert.deepEqual(e.vars,{});
 }
});
test('las vistas previas y los accesos públicos no cargan identidad interior ni consultan sesión',async()=>{
 for(const [route,body]of [['/login',{}],['/maestros/acceso',{}],['/personal/acceso',{}],['/accesos/QA/admin',{}],['/admin/portales/admin/vista-previa',{piPreview:'true'}]]){
  const e=await run(route,{},body);assert.equal(e.requests.length,0);
 }
});

test('Administración sin personalización utiliza la paleta institucional predeterminada',async()=>{const e=await run('/admin',{original:true,principal:'#ffffff'});assert.equal(e.root.dataset.piInterior,'true');assert.equal(e.vars['--pi-ui-primary'],C.adminDefaults.principal);const family=await run('/portal',{portal:'familias',original:true});assert.equal(family.links.length,0);});
