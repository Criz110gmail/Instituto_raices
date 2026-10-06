const fs=require('node:fs'),vm=require('node:vm'),assert=require('node:assert/strict');
class E {
    constructor(value=''){this.value=value;this.dataset={};this.listeners={};this.options=[];this.selectedOptions=[];this.disabled=false;}
    addEventListener(n,f){this.listeners[n]=f;}
    dispatchEvent(e){this.listeners[e.type]?.(e);}
    querySelector(){return null;}
    contains(){return false;}
}
const ids=['institucion','plantel','moneda','frecuencia','dia-vencimiento','fecha-vencimiento-unico','estado-cuota',
    'generacion-automatica','inscripcionId','fechaInicio','fechaFin','primerMes','ultimoMes','importeBase',
    'cuota-resumen','cuota-resumen-generacion','calendario-mensual','vencimiento-unico','ayuda-generacion','vigencia-ayuda','vigencia-avanzada'];
const el=Object.fromEntries(ids.map(id=>[id,new E()]));
Object.entries({moneda:'MXN',frecuencia:'UNICA','estado-cuota':'ACTIVA',fechaInicio:'2026-10-01',fechaFin:'2027-06-15',
    'fecha-vencimiento-unico':'2026-10-05',importeBase:'1000'}).forEach(([k,v])=>el[k].value=v);
el.institucion.disabled=true;el['generacion-automatica'].checked=true;
const form=new E();form.dataset.cuotaBloqueada='true';
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/cuota-alumno-form.js','utf8'),{
    document:{querySelector:()=>form,getElementById:id=>el[id]},Intl,Number,Date,Event,
});
assert.equal(el.fechaInicio.readOnly,true);assert.equal(el.fechaFin.readOnly,true);
assert.match(el['cuota-resumen-generacion'].textContent,/no lo vuelve a generar/);
assert.equal(el['fecha-vencimiento-unico'].value,'2026-10-05');
console.log('Cuota única generada: mantiene vigencia protegida y aviso de no regeneración.');
