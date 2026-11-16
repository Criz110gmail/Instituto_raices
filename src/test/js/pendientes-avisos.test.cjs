const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
async function run(total,{base='/admin/pendientes',status=200,redirected=false}={}){
 const link={attributes:{},getAttribute:()=>base,setAttribute(k,v){this.attributes[k]=v;},querySelector:()=>contador};
 const contador={hidden:true},llamadas=[];
 const document={readyState:'complete',querySelector:s=>{assert.equal(s,'[data-personal-agenda]');return link;},createElement:()=>{throw new Error('Los avisos no deben insertar tarjetas en el contenido del módulo');}};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/pendientes.js','utf8'),{document,fetch:async(url,opciones)=>{llamadas.push({url,opciones});return{ok:status===200,status,redirected,json:async()=>({total,vencidos:1,hoy:1,proximos:0,primeros:[{id:3,titulo:'<script>nota privada</script>',fechaCompromiso:'2026-10-16',vencido:true}]})};}});
 await new Promise(setImmediate);return {link,contador,llamadas};
}
test('administrador y docente muestran dos avisos sólo en el contador, sin tarjetas dentro del módulo',async()=>{
 for(const base of ['/admin/pendientes','/maestros/pendientes']){
  const ui=await run(2,{base});assert.equal(ui.llamadas.length,1);assert.equal(ui.llamadas[0].url,base+'/avisos');assert.equal(ui.llamadas[0].opciones.cache,'no-store');assert.equal(ui.llamadas[0].opciones.credentials,'same-origin');assert.equal(ui.contador.textContent,'2');assert.equal(ui.contador.hidden,false);assert.match(ui.link.attributes['aria-label'],/2 vencidos o próximos/);
 }
});
test('sin avisos se conserva el acceso y se oculta solamente el contador',async()=>{const ui=await run(0);assert.equal(ui.contador.hidden,true);assert.notEqual(ui.link.hidden,true);});
test('sin autorización se oculta acceso, y una redirección no se interpreta como avisos',async()=>{assert.equal((await run(2,{status:403})).link.hidden,true);assert.equal((await run(2,{redirected:true})).contador.hidden,true);});
