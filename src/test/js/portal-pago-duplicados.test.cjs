const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');

// DOM mínimo: prueba comportamiento sin sesión, navegador ni operaciones reales.
class Elemento {
    constructor(value = '') { this.value = value; this.textContent = ''; this.dataset = {}; this.children = []; this.listeners = {}; this.options = []; }
    addEventListener(name, fn) { this.listeners[name] = fn; }
    replaceChildren() { this.children = []; }
    appendChild(item) { this.children.push(item); }
    append(item) { this.appendChild(item); }
    setAttribute() {}
    focus() {}
    add(option) { this.options.push(option); }
    get selectedOptions() { return this.options.filter(o => String(o.value) === String(this.value)); }
    set innerHTML(value) { this.options = [{ value: '', textContent: value }]; this.value = ''; }
}
const campos = Object.fromEntries(['cuenta','solicitudes','monto-visible','agregar','comprobantes','cuenta-ayuda',
    'total-cargos','resumen-cargos','resumen-total','resumen-cuenta','resumen-fecha','resumen-referencia',
    'resumen-archivos','archivos-seleccionados','fechaPago','referencia'].map(id => [id, new Elemento()]));
const total = new Elemento();
const form = new Elemento();
const filas = [];
const solicitudes = campos.solicitudes;
solicitudes.querySelectorAll = selector => selector === '[data-cargo-autocomplete]' ? []
    : selector === '.portal-distribution-row' ? filas
    : filas.map(f => f.querySelector(selector));
const document = {
    getElementById: id => campos[id],
    querySelector: selector => selector === '.portal-total-value' ? total
        : selector === '.portal-payment-form' ? form : { textContent: 'MXN' },
    createElement: () => new Elemento(),
};
function fila(id, etiqueta, monto) {
    const datos = { '.portal-cargo-id': new Elemento(id), '.portal-cargo-search': new Elemento(etiqueta),
        '.portal-charge-amount': new Elemento(monto) };
    return { querySelector: selector => datos[selector] };
}

document.addEventListener=()=>{};
function crearFila(id='',monto='') {
 const datos={'.portal-cargo-id':new Elemento(id),'.portal-cargo-search':new Elemento(id?'Cargo seleccionado':''),
 '.portal-charge-amount':new Elemento(monto),'.portal-charge-amount-display':new Elemento(),
 '.portal-cargo-status':new Elemento(),'.portal-cargo-results':new Elemento(),
 '.portal-autocomplete-clear':new Elemento(),'.portal-selected-charge-detail':new Elemento()};
 const fila={querySelector:s=>datos[s]};
 const contenedor={querySelector:s=>datos[s],closest:()=>fila,contains:()=>false};
 return {fila,contenedor,datos};
}
const primero=crearFila('11','320.00'),segundo=crearFila();
filas.push(primero.fila,segundo.fila);
const context=vm.createContext({document,Intl,AbortController,setTimeout,clearTimeout,
 Option:function(text,value){this.textContent=text;this.value=String(value);},
 fetch:async url=>({ok:true,json:async()=>({resultados:String(url).includes('/cargos?')?
 [{id:11,titulo:'Hijo B · Convenio',monto:320},{id:12,titulo:'Hijo A · Convenio',monto:480}]:
 [{id:9,titulo:'Banco de prueba'}],hayMas:false})})});
let script=fs.readFileSync('src/main/resources/static/js/portal-pago.js','utf8');
script=script.replace(/\}\)\(\);\s*$/, 'globalThis.flujo={inicializarCargo,recalcularTotal};})();');
vm.runInContext(script,context);
context.flujo.inicializarCargo(primero.contenedor);
context.flujo.inicializarCargo(segundo.contenedor);
const tick=()=>new Promise(resolve=>setImmediate(resolve));
(async()=>{
 const entrada=segundo.datos['.portal-cargo-search'],resultados=segundo.datos['.portal-cargo-results'];
 entrada.listeners.focus();await tick();
 assert.equal(resultados.children.length,1);
 assert.equal(resultados.children[0].children[0].textContent,'Hijo A · Convenio');
 resultados.children[0].listeners.click();
 assert.equal(segundo.datos['.portal-cargo-id'].value,12);assert.equal(total.value,'800.00');
 // Reenfocar la misma fila permite su propia selección sin duplicarla.
 entrada.listeners.focus();await tick();assert.equal(resultados.children.length,1);
 segundo.datos['.portal-autocomplete-clear'].listeners.click();assert.equal(total.value,'320.00');
 // Si se elimina el cargo original vuelve a ofrecerse.
 primero.datos['.portal-autocomplete-clear'].listeners.click();entrada.listeners.focus();await tick();
 assert.equal(resultados.children.length,2);
 const opcionVieja=resultados.children[0];
 // Lista abierta antes de agregar el cargo en otra fila: protección en el clic.
 primero.datos['.portal-cargo-id'].value='11';primero.datos['.portal-charge-amount'].value='320.00';
 context.flujo.recalcularTotal();opcionVieja.listeners.click();
 assert.equal(segundo.datos['.portal-cargo-id'].value,'');assert.equal(total.value,'320.00');
 assert.match(segundo.datos['.portal-cargo-status'].textContent,/ya está agregado/);
 // Una captura restaurada/manipulada tampoco puede enviarse duplicada.
 segundo.datos['.portal-cargo-id'].value='11';segundo.datos['.portal-charge-amount'].value='320.00';
 let blocked=false;form.listeners.submit({preventDefault(){blocked=true;}});
 assert.equal(blocked,true);assert.match(segundo.datos['.portal-cargo-status'].textContent,/ya está agregado/);
 // Todas las opciones de esta consulta ya elegidas: explica cómo buscar otros.
 primero.datos['.portal-cargo-id'].value='12';entrada.listeners.focus();await tick();
 assert.equal(resultados.children.length,1); // La propia selección 11 sí es válida en su fila.
 console.log('Portal: filtro por ID, selección propia, total800, liberar al limpiar, clic desactualizado y bloqueo de envío repetido correctos.');
})().catch(error=>{console.error(error);process.exitCode=1;});
