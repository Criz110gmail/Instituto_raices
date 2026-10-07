const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
function elemento(value = '') {
    return {value, dataset: {}, listeners: {}, checked: false, readOnly: false,
        textContent: '', hidden: false, required: false, children: [],
        addEventListener(name, fn) { (this.listeners[name] ||= []).push(fn); },
        dispatchEvent(e) { for (const fn of this.listeners[e.type] || []) fn(e); },
        attributes: {}, setAttribute(k, v) { this.attributes[k] = v; }, setCustomValidity(v) { this.validity = v; },
        focus() {}, select() {}, reportValidity() {},
        replaceChildren() { this.children = []; }, append(...items) { this.children.push(...items); }};
}
const selectores = {};
for (const id of ['refund-mode', 'refund-amount', 'refund-account', 'refund-account-search',
    'refund-change-account', 'refund-change-account-value', 'refund-account-reason', 'refund-account-change-reason',
    'refund-amount-help', 'refund-preview-money', 'refund-account-summary',
    'refund-preview-applied', 'refund-preview-reopened', 'refund-preview-charges', 'refund-preview-warning'])
    selectores['#' + id] = elemento();
selectores['.autocomplete-value'] = elemento('8');
selectores['button[type="submit"]'] = elemento();
const e = id => selectores['#' + id];
e('refund-mode').value = 'PARCIAL'; e('refund-amount').value = '100';
e('refund-account').value = 'Banco original'; e('refund-account').readOnly = true;
e('refund-change-account-value').value = 'false';
e('refund-account-search').dataset = {originalId: '8', originalLabel: 'Banco original'};
const abono = elemento(); abono.checked = true;
abono.dataset = {amount: '400', charge: '19', balance: '0', title: 'Alumno prueba'};
const form = elemento(); form.dataset = {available: '0', limit: '400', currency: 'MXN'};
form.querySelector = s => selectores[s]; form.querySelectorAll = () => [abono];
const contexto = {document: {querySelector: () => form, createElement: () => elemento()},
    Intl, Event: class {constructor(type) {this.type = type;}}};
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/pago-devolucion.js', 'utf8'), contexto);
const cambio = el => el.dispatchEvent({type: 'change'});
assert.equal(e('refund-preview-money').textContent, '$100.00');
assert.equal(e('refund-preview-applied').textContent, '$300.00');
assert.equal(e('refund-preview-reopened').textContent, '$100.00');
assert.match(e('refund-account-summary').textContent, /100.*Banco original/);
assert.equal(e('refund-account-change-reason').hidden, true);
assert.equal(e('refund-change-account').attributes['aria-expanded'], 'false');
e('refund-change-account').dispatchEvent({type: 'click'});
assert.equal(e('refund-change-account-value').value, 'true');
assert.equal(e('refund-change-account').textContent, 'Conservar cuenta original');
assert.equal(e('refund-account').readOnly, false);
assert.equal(e('refund-account-change-reason').hidden, false);
selectores['.autocomplete-value'].value = '9'; e('refund-account').value = 'Caja';
cambio(selectores['.autocomplete-value']);
assert.equal(e('refund-account-reason').required, true);
assert.match(e('refund-account-summary').textContent, /100.*Caja/);
let prevented = false; form.dispatchEvent({type: 'submit', preventDefault() {prevented = true;}});
assert.equal(prevented, true);
e('refund-account-reason').value = 'Devolver efectivo';
e('refund-change-account').dispatchEvent({type: 'click'});
assert.equal(e('refund-change-account-value').value, 'false');
assert.equal(e('refund-change-account').attributes['aria-expanded'], 'false');
assert.equal(e('refund-account-change-reason').hidden, true);
assert.equal(e('refund-account').readOnly, true);
assert.equal(selectores['.autocomplete-value'].value, '8');
assert.equal(e('refund-account').value, 'Banco original');
assert.equal(e('refund-account-reason').value, '');
assert.equal(e('refund-account-reason').required, false);
assert.match(e('refund-account-summary').textContent, /Banco original/);
console.log('Devolución: cuenta protegida, cambio, motivo, restauración y vista $100/$300/$100 correctos.');
