const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const registry = [];
const observers = [];
let doc;
class Element {
    constructor(tag = 'div') {
        this.tag = tag; this.attrs = {}; this.dataset = {}; this.listeners = {}; this.children = [];
        this.nodeType = 1;
        this.textContent = ''; this.id = ''; this.name = ''; this.disabled = false; this.required = false;
        this.readOnly = false; this.min = ''; this.max = ''; this.placeholder = ''; this._value = '';
        this.classList = {add() {}, remove() {}}; registry.push(this);
    }
    setAttribute(k,v) { this.attrs[k] = String(v); }
    getAttribute(k) { return this.attrs[k]; }
    hasAttribute(k) { return k in this.attrs; }
    matches(s) { return s === 'input[data-money]' && this.tag === 'input' && this.hasAttribute('data-money'); }
    addEventListener(k, fn) { (this.listeners[k] ||= []).push(fn); }
    dispatchEvent(e) { for (const fn of this.listeners[e.type] || []) fn(e); return !e.defaultPrevented; }
    before(el) { el.form = this.form; el.labelOwner = this.labelOwner; }
    closest(selector) {
        if (selector === 'label') return this.labelOwner || null;
        return selector.split(',').includes(this.tag) ? this : null;
    }
    append(...nodes) { this.children.push(...nodes); }
    replaceChildren() { this.children = []; }
    focus() { doc.activeElement = this; }
    blur() { if (doc.activeElement === this) doc.activeElement = null; this.dispatchEvent({type:'blur'}); }
    select() {}
    showModal() { this.open = true; }
    close() { this.open = false; }
    querySelectorAll(s) {
        if (s === 'input[data-money]') return registry.filter(el => el.matches(s) && (!this.isForm || el.form === this));
        if (s === 'label[for]') return registry.filter(el => el.tag === 'label' && el.htmlFor);
        return [];
    }
    querySelector(s) {
        if (s.startsWith('#')) return registry.find(el => el.id === s.slice(1));
        const match = s.match(/^\[name="(.*?)"\]$/);
        return match ? registry.find(el => el.name === match[1] && el.form === this) : null;
    }
}
class Input extends Element {
    constructor() { super('input'); this.type = 'text'; this.nativeError = ''; }
    get value() { return this._value; }
    set value(v) { this._value = String(v ?? ''); }
    get validity() { return {customError: !!this.nativeError}; }
    get validationMessage() { return this.nativeError; }
    setCustomValidity(v) { this.nativeError = v; }
    checkValidity() { return this.disabled || (!this.nativeError && (!this.required || !!this.value)); }
    reportValidity() { return this.checkValidity(); }
}
doc = new Element('document'); doc.readyState = 'complete'; doc.body = new Element('body');
doc.createElement = tag => tag === 'input' ? new Input() : new Element(tag);
doc.getElementById = id => registry.find(el => el.id === id);
const form = new Element('form'); form.isForm = true; form.dataset = {moneyConfirm:'ajuste',confirmCharge:'42'};
form.reportValidity = () => registry.filter(el => el.form === form && el instanceof Input && el.type !== 'hidden').every(el => el.checkValidity());
form.submissions = 0;
const submitter = new Element('button');
function submit() {
    const e = {type:'submit',target:form,submitter,defaultPrevented:false,preventDefault() {this.defaultPrevented = true;}};
    doc.dispatchEvent(e); if (!e.defaultPrevented) form.submissions++;
}
form.requestSubmit = submit;
const input = new Input(); input.id = 'monto'; input.name = 'monto'; input.value = '400'; input.required = true;
input.min = '0.01'; input.form = form; input.dataset = {money:''}; input.setAttribute('data-money','');
const label = new Element('label'); label.htmlFor = 'monto';
input.labelOwner = label;
for (const [name,value] of [['tipo','DESCUENTO'],['efecto','DISMINUCION'],['motivo','Prueba <script> literal']]) {
    const el = new Element('select'); el.name = name; el.value = value; el.form = form;
}
const context = {document:doc,HTMLInputElement:Input,Intl,BigInt,setTimeout,
    MutationObserver:class {constructor(fn) {this.fn=fn; observers.push(this);} observe(target, options) {this.target=target;this.options=options;}},
    Event:class {constructor(type) {this.type=type;}}};
vm.runInNewContext(fs.readFileSync('src/main/resources/static/js/money-input.js','utf8'), context);
const display = doc.querySelector('#monto-display');
assert.equal(input.type,'hidden'); assert.equal(input.value,'400.00');
assert.equal(display.type,'text'); assert.equal(display.value,'$400.00');
assert.equal(display.inputMode,'decimal'); assert.equal(label.htmlFor,'monto-display');
assert.ok(!display.name); // Sólo el valor canónico tiene nombre y se envía.
assert.equal(display.className,'money-entry');
const heading = new Element('span');
let blocked = false;
label.dispatchEvent({type:'click',detail:1,target:heading,preventDefault() {blocked=true;}});
assert.equal(blocked,true); assert.notEqual(doc.activeElement,display);
blocked=false;
label.dispatchEvent({type:'click',detail:1,target:display,preventDefault() {blocked=true;}});
assert.equal(blocked,false);
label.dispatchEvent({type:'click',detail:0,target:heading,preventDefault() {blocked=true;}});
assert.equal(blocked,false); // Activación asistida no bloqueada.
label.dispatchEvent({type:'click',detail:1,target:new Element('button'),preventDefault() {blocked=true;}});
assert.equal(blocked,false); // Botones de ayuda siguen funcionando.
display.focus(); display.dispatchEvent({type:'focus'}); assert.equal(display.value,'400.00');
label.dispatchEvent({type:'click',detail:1,target:heading,preventDefault() {}});
assert.equal(doc.activeElement,null); assert.equal(display.value,'$400.00');
display.focus(); display.dispatchEvent({type:'focus'});
display.value='$1,400.50'; display.dispatchEvent({type:'input'});
assert.equal(input.value,'1400.50'); assert.equal(Number(input.value),1400.5);
doc.activeElement=null; display.dispatchEvent({type:'blur'}); assert.equal(display.value,'$1,400.50');
display.dispatchEvent({type:'wheel'}); assert.equal(input.value,'1400.50');
input.value='100.00'; assert.equal(display.value,'$100.00');
input.max='99'; assert.equal(input.checkValidity(),false);
input.max='400'; input.setCustomValidity('Error de negocio'); assert.equal(display.nativeError,'Error de negocio');
input.setCustomValidity(''); assert.equal(input.checkValidity(),true);
display.focus(); display.value='100.001'; display.dispatchEvent({type:'input'});
assert.equal(input.value,''); assert.equal(input.checkValidity(),false);
doc.activeElement=null; input.value='100';
input.readOnly=true; input.checkValidity(); assert.equal(display.readOnly,true);
input.readOnly=false; input.checkValidity(); assert.equal(display.readOnly,false);
submit(); assert.equal(form.submissions,0);
const dialog = registry.find(el => el.tag === 'dialog'); assert.equal(dialog.open,true);
const cancel = registry.find(el => el.className === 'money-confirm-cancel'); cancel.dispatchEvent({type:'click'});
assert.equal(form.submissions,0); assert.equal(dialog.open,false);
submit(); const accept = registry.find(el => el.className === 'money-confirm-accept'); accept.dispatchEvent({type:'click'});
assert.equal(form.submissions,1); assert.equal(input.value,'100.00');
assert.equal(dialog.open,false);
// Una partida nueva conserva su nombre e importe numérico, y se inicializa una sola vez.
const price = new Input(); price.id='precio'; price.name='partidas[0].precioUnitario';
price.form=form; price.value='1400.5'; price.setAttribute('data-money',''); price.dataset={money:''};
const dynamic = observers.find(o => o.target === doc.body);
const wrapped = new Input();wrapped.value='200';wrapped.form=form;wrapped.setAttribute('data-money','');wrapped.dataset={money:''};
const wrapper = new Element('label');wrapped.labelOwner=wrapper;
dynamic.fn([{addedNodes:[wrapped]}]);
blocked=false;wrapper.dispatchEvent({type:'click',detail:1,target:heading,preventDefault() {blocked=true;}});
assert.equal(blocked,true);
dynamic.fn([{addedNodes:[price]}]);
assert.equal(price.type,'hidden'); assert.equal(price.value,'1400.50');
assert.equal(doc.querySelector('#precio-display').value,'$1,400.50');
price.name='partidas[1].precioUnitario'; assert.equal(price.value,'1400.50');
const count=registry.filter(el=>el.id==='precio-display').length;
dynamic.fn([{addedNodes:[price]}]); assert.equal(registry.filter(el=>el.id==='precio-display').length,count);
// El tope híbrido conserva 4 decimales y sólo usa moneda en modo monto fijo.
const limitMode=new Element('select');limitMode.id='tipoLimite';limitMode.name='tipoLimite';limitMode.value='MONTO_FIJO';
const limit=new Input();limit.id='valorLimite';limit.form=form;limit.value='150.0001';
limit.setAttribute('data-money','');limit.dataset={money:'',moneyScale:'4',moneyWhen:'tipoLimite:MONTO_FIJO'};
dynamic.fn([{addedNodes:[limit]}]);assert.equal(doc.querySelector('#valorLimite-display').value,'$150.0001');
limitMode.value='PORCENTAJE_ORIGINAL';limitMode.dispatchEvent({type:'change'});
assert.equal(doc.querySelector('#valorLimite-display').value,'150.0001');
// Confirmaciones específicas también para devolución y validación, sin enviar al cancelar.
form.dataset.moneyConfirm='devolucion';input.id='refund-amount';
for(const [id,name,value] of [['refund-account','','Banco principal'],['beneficiario','beneficiario','María']]) {
    const el=new Input();el.id=id;el.name=name;el.value=value;el.form=form;
}
for(const [id,text] of [['refund-preview-applied','$300.00'],['refund-preview-reopened','$100.00']]) {
    const el=new Element();el.id=id;el.textContent=text;
}
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar devolución');
assert.ok(registry.some(el=>el.tag==='dd'&&el.textContent==='Banco principal'));
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,1);
form.dataset.moneyConfirm='validacion';form.dataset.confirmAmount='400.00';
const bank=new Input();bank.id='cuentaDestinoBusqueda';bank.value='Banco principal';bank.form=form;
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar validación del pago');
accept.dispatchEvent({type:'click'});assert.equal(form.submissions,2);
console.log('DOM moneda: valor canónico, foco/formato, scroll, validaciones, readonly, cancelar y confirmar un solo envío correctos.');
