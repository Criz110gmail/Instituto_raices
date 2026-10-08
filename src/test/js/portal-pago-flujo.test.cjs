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
let consultas = 0;
const redirecciones = [];
const context = vm.createContext({ document, Intl, URL, AbortController, setTimeout, clearTimeout,
    window: { location: { assign: destino => redirecciones.push(destino) } },
    Option: function(text, value) { this.textContent = text; this.value = String(value); },
    fetch: async () => { consultas++; return { ok: true, json: async () => ({ resultados: [
        { id: 9, titulo: 'Banco de prueba', detalle: 'Plantel Centro' } ] }) }; },
});
let script = fs.readFileSync('src/main/resources/static/js/portal-pago.js', 'utf8');
script = script.replace(/\}\)\(\);\s*$/, 'globalThis.flujo = { recalcularTotal, sincronizarCuentas, redirigirSiSesionCaducada }; })();');
vm.runInContext(script, context);
assert.equal(context.flujo.redirigirSiSesionCaducada({ redirected: false }), false);
assert.equal(context.flujo.redirigirSiSesionCaducada({ redirected: true, url: 'http://localhost/familias?sesionExpirada' }), true);
assert.equal(context.flujo.redirigirSiSesionCaducada({ redirected: true, url: 'http://localhost/nexo/login' }), true);
assert.equal(context.flujo.redirigirSiSesionCaducada({ redirected: true, url: 'http://localhost/acceso-denegado' }), false);
assert.deepEqual(redirecciones, ['/familias?sesionExpirada', '/nexo/familias?sesionExpirada']);
assert.equal(campos.cuenta.disabled, true);
assert.equal(consultas, 0);
assert.match(campos['resumen-total'].textContent, /0\.00/);
filas.push(fila('1', 'Ana · Cuota', '600'), fila('2', 'Luis · Cuota', '400'));
context.flujo.recalcularTotal();
assert.equal(total.value, '1000.00');
assert.match(campos['monto-visible'].value, /1,000\.00/);
assert.equal(campos['resumen-cargos'].children.length, 2);
assert.match(campos['total-cargos'].textContent, /2 cargos/);
context.flujo.sincronizarCuentas();
assert.equal(campos.cuenta.disabled, true);
setImmediate(() => {
    assert.equal(campos.cuenta.disabled, false);
    campos.cuenta.value = '9';
    campos.fechaPago.value = '2026-10-06T12:30';
    campos.referencia.value = 'TEST-TRANSFERENCIA';
    campos.comprobantes.files = [{ name: 'recibo-prueba.pdf', size: 1024 }];
    form.listeners.change();
    assert.match(campos['resumen-cuenta'].textContent, /Banco de prueba/);
    assert.equal(campos['resumen-fecha'].textContent, '06/10/2026 · 12:30 h');
    assert.equal(campos['resumen-referencia'].textContent, 'TEST-TRANSFERENCIA');
    assert.equal(campos['resumen-archivos'].textContent, 'recibo-prueba.pdf');
    assert.equal(campos['archivos-seleccionados'].children.length, 1);
    campos.comprobantes.files = [{ name: 'reemplazo.png', size: 2048 }];
    form.listeners.change();
    assert.equal(campos['resumen-archivos'].textContent, 'reemplazo.png');
    assert.equal(campos['archivos-seleccionados'].children.length, 1);
    filas.length = 0;
    context.flujo.recalcularTotal(); context.flujo.sincronizarCuentas();
    assert.equal(campos.cuenta.disabled, true);
    assert.equal(total.value, '');
    assert.equal(campos['resumen-cuenta'].textContent, 'Pendiente de seleccionar');
    assert.equal(consultas, 1);
    console.log('Flujo de transferencia: total, cuenta, revisión y reemplazo de archivos correctos.');
});
