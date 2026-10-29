const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const script = fs.readFileSync('src/main/resources/static/js/preparacion-nomina.js', 'utf8');
function pantalla(total) {
    let submit;
    const aviso = {hidden: true, focus() { this.focused = true; }, scrollIntoView() { this.scrolled = true; }};
    const form = {dataset: {confirmPeople: total}, addEventListener(type, fn) { if (type === 'submit') submit = fn; }, querySelector() { return aviso; }};
    const document = {querySelectorAll(selector) { return selector === '.nomina-bulk-save[data-confirm-people]' ? [form] : []; }, querySelector() { return null; }};
    vm.runInNewContext(script, {document});
    const event = {defaultPrevented: false, preventDefault() { this.defaultPrevented = true; }};
    submit(event);
    return {aviso, event};
}
const vacia = pantalla('0');
assert.equal(vacia.event.defaultPrevented, true);
assert.equal(vacia.aviso.hidden, false);
assert.equal(vacia.aviso.focused, true);
assert.equal(vacia.aviso.scrolled, true);
// El total es global: no depende de que un empleado incluido aparezca en esta página.
for (const total of ['1', '125']) {
    const incluida = pantalla(total);
    assert.equal(incluida.event.defaultPrevented, false);
    assert.equal(incluida.aviso.hidden, true);
}
console.log('Aviso sin selección y continuación con selección global: correctos.');
