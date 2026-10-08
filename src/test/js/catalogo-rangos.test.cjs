const assert=require('node:assert/strict');
const fs=require('node:fs'); const vm=require('node:vm');
function input(){return {value:'',events:{},addEventListener(k,fn){this.events[k]=fn;},setCustomValidity(v){this.error=v;}};}
function probar(hoy){
 const desde=input(),hasta=input(),preset=input();
 const section={dataset:{today:hoy},querySelector:s=>s.includes('desde')?desde:s.includes('hasta')?hasta:preset};
 vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/catalogo-rangos.js','utf8'),{document:{querySelectorAll:()=>[section]},Date});
 return {desde,hasta,preset,elegir(v){preset.value=v;preset.events.change();}};
}
let r=probar('2026-01-08'); r.elegir('previous');
assert.equal(r.desde.value,'2025-12-01');assert.equal(r.hasta.value,'2025-12-31');
r.elegir('year');assert.equal(r.desde.value,'2026-01-01');assert.equal(r.hasta.value,'2026-12-31');
r=probar('2028-02-15');r.elegir('month');assert.equal(r.hasta.value,'2028-02-29');
r.desde.value='2028-03-01';r.desde.events.input();assert.equal(r.preset.value,'custom');assert.match(r.hasta.error,/posterior/);
r.hasta.value='';r.hasta.events.input();assert.equal(r.hasta.error,'');
r.elegir('custom');assert.equal(r.desde.value,'2028-03-01');assert.equal(r.hasta.value,'');
console.log('Rangos: meses cortos/bisiestos, cambio de año, rango personalizado, extremos abiertos y validación correctos.');
