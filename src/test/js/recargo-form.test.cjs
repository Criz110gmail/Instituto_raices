const assert = require('node:assert/strict'), fs = require('node:fs'), vm = require('node:vm');
class E {
    constructor(value=''){this.value=value;this.children=[];this.dataset={};this.listeners={};this.textContent='';}
    addEventListener(name,fn){this.listeners[name]=fn;}
    removeAttribute(name){delete this[name];}
    replaceChildren(){this.children=[];}
    appendChild(item){this.children.push(item);}
}
const ids=['modalidad','tipoLimite','valorLimite','porcentaje','montoFijo','periodicidad','ejemplo-original','ejemplo-descuento',
    'recargo-ejemplo-filas','ejemplo-base','ejemplo-tope','ejemplo-conclusion','tope-valor-etiqueta','tope-valor-ayuda','tope-ayuda','recargo-ejemplo-valores','recargo-ejemplo-calcular'];
const el=Object.fromEntries(ids.map(id=>[id,new E()]));
Object.entries({modalidad:'PORCENTAJE',tipoLimite:'MONTO_FIJO',valorLimite:'150',porcentaje:'10',periodicidad:'MENSUAL','ejemplo-original':'1000','ejemplo-descuento':'200'}).forEach(([id,value])=>el[id].value=value);
const form=new E(); let ready;
const document={readyState:'loading',getElementById:id=>el[id],querySelector:s=>s==='.entity-form'?form:el[s.slice(1)],
    querySelectorAll:()=>[],createElement:()=>new E(),addEventListener:(name,fn)=>ready=fn};
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/recargo-form.js','utf8'),{document,Intl,Number});ready();
const recargos=()=>el['recargo-ejemplo-filas'].children.map(tr=>tr.children[1].textContent);
assert.deepEqual(recargos(),['$80.00','$70.00','$0.00']);
el['recargo-ejemplo-filas'].replaceChildren();el['recargo-ejemplo-calcular'].listeners.click();
assert.deepEqual(recargos(),['$80.00','$70.00','$0.00']);
assert.match(el['ejemplo-conclusion'].textContent,/950\.00/);
el.tipoLimite.value='PORCENTAJE_ORIGINAL';el.valorLimite.value='15';el.tipoLimite.listeners.change();
assert.deepEqual(recargos(),['$80.00','$70.00','$0.00']);assert.match(el['ejemplo-tope'].textContent,/importe original/);
el.periodicidad.value='UNICA';el.valorLimite.value='5';el.periodicidad.listeners.change();
assert.deepEqual(recargos(),['$50.00']);
el.tipoLimite.value='SIN_LIMITE';el.tipoLimite.listeners.change();
assert.deepEqual(recargos(),['$80.00']);assert.equal(el.valorLimite.required,false);
el.modalidad.value='MONTO_FIJO';el.montoFijo.value='30';el.periodicidad.value='MENSUAL';el.modalidad.listeners.change();
assert.deepEqual(recargos(),['$30.00','$30.00','$30.00']);
el['ejemplo-descuento'].value='1000';form.listeners.input();assert.deepEqual(recargos(),['$0.00','$0.00','$0.00']);
el['ejemplo-descuento'].value='2000';form.listeners.input();assert.equal(el['recargo-ejemplo-filas'].children[0].children[0].colSpan,4);
assert.match(el['ejemplo-tope'].textContent,/Revisa los valores/);
el.porcentaje.value='';el.modalidad.value='PORCENTAJE';form.listeners.change();
assert.match(el['recargo-ejemplo-filas'].children[0].children[0].textContent,/captura el Porcentaje/);
// También inicializa si el script termina de cargar después de DOMContentLoaded.
el.porcentaje.value='10';el['ejemplo-descuento'].value='200';el.tipoLimite.value='MONTO_FIJO';el.valorLimite.value='150';
document.readyState='complete';
document.querySelector=s=>s==='.entity-form'?form:s.startsWith('[name=')?el[s.slice(7,-2)]:el[s.slice(1)];
document.getElementById=id=>['porcentaje','periodicidad','montoFijo'].includes(id)?null:el[id];
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/recargo-form.js','utf8'),{document,Intl,Number});
assert.deepEqual(recargos(),['$80.00','$70.00','$0.00']);
console.log('Ejemplo de recargos: topes en dinero/porcentaje, único, mensual y base cero correctos.');
