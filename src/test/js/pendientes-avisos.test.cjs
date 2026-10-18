const {test}=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
async function run(total){
 class E{constructor(){this.children=[];this.attributes={};}append(...x){this.children.push(...x);}setAttribute(k,v){this.attributes[k]=v;}}
 const link=new E(),contador=new E(),header=new E();link.getAttribute=()=>'/admin/pendientes';link.querySelector=()=>contador;let tarjeta;
 header.after=x=>tarjeta=x;const llamadas=[];
 const document={readyState:'complete',querySelector:s=>s==='[data-personal-agenda]'?link:header,createElement:()=>new E()};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/pendientes.js','utf8'),{document,fetch:async(url,opciones)=>{llamadas.push({url,opciones});return{ok:true,redirected:false,json:async()=>({total,vencidos:1,hoy:0,proximos:0,primeros:[{id:3,titulo:'<script>nota privada</script>',fechaCompromiso:'2026-10-16',vencido:true}]})};}});
 await new Promise(setImmediate);return {contador,tarjeta,llamadas};
}
test('avisos consultan sólo endpoint propio y presentan texto sin interpretar HTML',async()=>{
 const ui=await run(1);assert.equal(ui.llamadas.length,1);assert.equal(ui.llamadas[0].url,'/admin/pendientes/avisos');assert.equal(ui.llamadas[0].opciones.cache,'no-store');assert.equal(ui.contador.textContent,'1');
 const enlace=ui.tarjeta.children[2].children[0].children[0];assert.equal(enlace.href,'/admin/pendientes/3');assert.equal(enlace.children[0].textContent,'<script>nota privada</script>');assert.equal(enlace.children[1].textContent,'16/10/2026 · Vencido');
});
test('sin avisos se conserva acceso pero no muestra tarjeta ni contador',async()=>{const ui=await run(0);assert.equal(ui.contador.hidden,true);assert.equal(ui.tarjeta,undefined);});
