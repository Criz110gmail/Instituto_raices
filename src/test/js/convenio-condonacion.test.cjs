const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
class Element {
 constructor(value=''){this.value=value;this.textContent='';this.dataset={};this.listeners={};this.children=[];}
 addEventListener(k,fn){this.listeners[k]=fn;}
 querySelectorAll(){return [];}
 querySelector(){return null;}
 replaceChildren(){this.children=[];}
}
const fields=Object.fromEntries(['institucion','tutorId','cargo-busqueda','buscar-cargos','cargo-resultados','cargos-seleccionados','cargo-estado','monto-acordado','saldo-original','monto-condonado','modalidad-convenio','seleccion-resumen','seleccion-vacia','convenio-monto-label','convenio-fecha-label','convenio-efecto'].map(id=>[id,new Element()]));
fields['modalidad-convenio'].value='MONTO_ACORDADO';fields['monto-acordado'].value='350.00';
const acuerdo=new Element('2026-10-08'),limite=new Element('2026-10-20');
const context={Intl,document:{querySelector:s=>s==='[name="fechaAcuerdo"]'?acuerdo:s==='[name="fechaVencimiento"]'?limite:fields[s.slice(1)]}};
const source=fs.readFileSync('src/main/resources/static/js/convenio-pago-form.js','utf8').replace(/\}\)\(\);\s*$/,'globalThis.prueba={saldos,actualizar}; })();');
vm.runInNewContext(source,context);
context.prueba.saldos.set('1',300);context.prueba.saldos.set('2',200);context.prueba.actualizar();
assert.equal(fields['monto-condonado'].textContent,'$150.00');
fields['modalidad-convenio'].value='CONDONACION_TOTAL';fields['modalidad-convenio'].listeners.change();
assert.equal(fields['monto-acordado'].value,'0.00');assert.equal(fields['monto-acordado'].readOnly,true);assert.equal(fields['monto-condonado'].textContent,'$500.00');
assert.equal(limite.value,'2026-10-08');assert.equal(limite.readOnly,true);
acuerdo.value='2026-10-09';acuerdo.listeners.change();assert.equal(limite.value,'2026-10-09');
fields['modalidad-convenio'].value='MONTO_ACORDADO';fields['modalidad-convenio'].listeners.change();
assert.equal(fields['monto-acordado'].value,'350.00');assert.equal(fields['monto-acordado'].readOnly,false);assert.equal(fields['monto-acordado'].min,'0.01');
console.log('Convenio: condonación500/por pagar0 bloqueado, fecha de efecto, cambio de modalidad y conservación de monto previos correctos.');
