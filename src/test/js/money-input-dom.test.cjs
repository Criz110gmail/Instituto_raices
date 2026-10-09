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
// Nueva cuota: resumen literal, sin enviar al cancelar; recalcula después de corregir.
form.dataset.moneyConfirm='cuota';
for (const [id,value] of [['moneda','MXN'],['inscripcion-busqueda','A-1 · Ana <script>'],['concepto-busqueda','TEST · Inscripción'],['importeBase','600.00'],['fecha-vencimiento-unico','2026-10-20'],['fechaInicio','2026-09-01'],['fechaFin','2027-06-30'],['primerMes','2026-09'],['ultimoMes','2026-11'],['dia-vencimiento','20']]) {
    const el=new Input();el.id=id;el.value=value;el.form=form;
}
for (const [id,value,text] of [['institucion','1','Escuela'],['plantel','2','Plantel norte'],['frecuencia','UNICA','Una sola vez'],['estado-cuota','ACTIVA','Activa']]) {
    const el=new Element('select');el.id=id;el.value=value;el.selectedOptions=[{textContent:text}];el.form=form;
}
const auto=new Input();auto.id='generacion-automatica';auto.checked=true;auto.form=form;
const now=new Input();now.name='generarCargoAhora';now.checked=false;now.form=form;
const rows=()=>Object.fromEntries(dialog.children.find(el=>el.tag==='dl').children.reduce((pairs,el,i,all)=>i%2?pairs:[...pairs,[el.textContent,all[i+1].textContent]],[]));
submit();assert.equal(dialog.open,true);assert.equal(form.submissions,2);
assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar nueva cuota');
assert.equal(rows()['Importe único'],'$600.00');assert.equal(rows()['Alumno e inscripción'],'A-1 · Ana <script>');
assert.equal(rows()['Fecha límite de pago'],'20/10/2026');assert.match(rows()['Generación automática'],/^Sí/);
assert.equal(cancel.textContent,'Cancelar');cancel.dispatchEvent({type:'click'});
assert.equal(form.submissions,2);assert.equal(doc.querySelector('#importeBase').value,'600.00');
doc.querySelector('#frecuencia').value='MENSUAL';doc.querySelector('#importeBase').value='500.00';
submit();assert.equal(rows()['Importe por mes'],'$500.00');assert.equal(rows()['Meses a cobrar'],'09/2026 — 11/2026');
assert.match(rows()['Fecha límite de pago'],/Día 20/);
assert.ok(!('Importe único' in rows()));assert.equal(accept.textContent,'Confirmar y crear cuota');
accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,3);
// Escape no guarda; modalidad asistida señala si creará el cargo ahora.
doc.querySelector('#frecuencia').value='UNICA';now.checked=true;submit();
assert.match(rows()['Crear cargo al confirmar'],/^Sí/);
dialog.dispatchEvent({type:'cancel',preventDefault(){}});assert.equal(form.submissions,3);
form.dataset.moneyConfirm='validacion';form.dataset.confirmFolio='PAG-TEST-600';form.dataset.confirmAmount='600.00';
submit();assert.equal(cancel.textContent,'Cancelar');
assert.equal(accept.textContent,'Confirmar validación');assert.equal(rows()['Folio del pago'],'PAG-TEST-600');
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,3);
form.dataset.moneyConfirm='rechazo';submit();
assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar rechazo del pago');
assert.equal(accept.textContent,'Confirmar rechazo');assert.equal(rows()['Motivo del rechazo'],'Prueba <script> literal');
assert.match(rows()['Resultado'],/la deuda no cambia/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,3);
submit();accept.dispatchEvent({type:'click'});assert.equal(form.submissions,4);
form.dataset.moneyConfirm='cancelacion';form.dataset.confirmState='PENDIENTE_VALIDACION';submit();
assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar cancelación del pago');
assert.equal(accept.textContent,'Confirmar cancelación');assert.match(rows()['Efecto en los saldos'],/No cambiarán/);
assert.ok(!('Egreso compensatorio' in rows()));dialog.dispatchEvent({type:'cancel',preventDefault(){}});
assert.equal(form.submissions,4);
form.dataset.confirmState='VALIDADO';form.dataset.confirmAccount='Caja prueba';submit();
assert.equal(rows()['Cuenta del ingreso que se compensará'],'Caja prueba');
assert.equal(rows()['Egreso compensatorio'],'$600.00');assert.match(rows()['Efecto en los adeudos'],/recuperará la deuda/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,4);
submit();accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,5);
// Registro de pago desde adeudo o alta general: pendiente, distribución y archivos conservados.
form.dataset.moneyConfirm='registro-pago';
for (const [id,name,value] of [['monto','monto','600.00'],['tutor-busqueda','','María'],['metodo','','EFECTIVO'],['cuenta-busqueda','','Caja prueba'],['fechaPago','fechaPago','2026-10-07T16:00'],['referencia','referencia','TEST-600']]) {
    const el=new Input();el.id=id;el.name=name;el.value=value;el.form=form;
}
const receipt=new Input();receipt.id='comprobantes';receipt.files=[{name:'Prueba <script>.pdf'}];receipt.form=form;
const assigned=new Input();assigned.value='600.00';
const charge=new Input();charge.value='A-1 · Ana · TEST-CANCELACION';
const row=new Element();row.querySelector=selector=>selector==='.monto-solicitado'?assigned:charge;
const queryAll=form.querySelectorAll.bind(form);
form.querySelectorAll=selector=>selector==='.distribution-row'?[row]:queryAll(selector);
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar registro del pago');
assert.equal(rows()['Total recibido'],'$600.00');assert.equal(rows()['Dinero pendiente de asignar'],'$0.00');
assert.equal(rows()['Fecha y hora del pago'],'07/10/2026 · 16:00');
assert.match(rows()['Asignación 1'],/TEST-CANCELACION · \$600.00/);
assert.equal(rows()['Comprobantes seleccionados'],'Prueba <script>.pdf');
assert.match(doc.querySelector('#money-confirm-note').textContent,/pendiente de validación/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,5);assert.equal(receipt.files.length,1);
assigned.value='300.00';doc.querySelector('#monto').value='300.00';submit();
assert.equal(rows()['Total recibido'],'$300.00');assert.match(rows()['Asignación 1'],/\$300.00/);
assert.equal(accept.textContent,'Confirmar y registrar como pendiente');
accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,6);
form.dataset={moneyConfirm:'cancelacion-cargo',confirmCharge:'80',confirmSubject:'A-1 · Ana',confirmConcept:'TEST-CANCELACION',confirmAmount:'600.00',confirmPaid:'0.00',confirmBalance:'600.00',confirmCurrency:'MXN'};
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar cancelación del adeudo');
assert.equal(rows()['Abonos vigentes'],'$0.00');assert.equal(rows()['Falta por pagar actualmente'],'$600.00');
assert.match(rows()['Efecto en caja y bancos'],/Ninguno/);assert.equal(cancel.textContent,'Cancelar');
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,6);
submit();dialog.dispatchEvent({type:'cancel',preventDefault(){}});assert.equal(form.submissions,6);
submit();assert.equal(accept.textContent,'Confirmar cancelación del adeudo');
accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,7);
assert.match(doc.querySelector('#money-confirm-note').textContent,/no habilita su regeneración/);
const baseQuery=form.querySelector.bind(form);
const selection=new Element();selection.textContent='2 registros seleccionados · $1,000.00';
const cutoff=new Input();cutoff.name='fechaCorte';cutoff.value='2026-10-08';cutoff.form=form;
form.querySelector=selector=>selector==='[data-selection-summary]'?selection:baseQuery(selector);
form.dataset.moneyConfirm='generacion-cargos';submit();
assert.equal(rows()['Selección de todas las páginas'],selection.textContent);
assert.equal(rows()['Fecha de corte'],'08/10/2026');assert.equal(cancel.textContent,'Cancelar');
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,7);
submit();accept.dispatchEvent({type:'click'});assert.equal(form.submissions,8);
form.dataset.moneyConfirm='convenio';
for(const [name,value] of [['montoAcordado','800.00'],['fechaAcuerdo','2026-10-08'],['fechaVencimiento','2026-10-20'],['descripcion','Acuerdo de prueba'],['condiciones','Liquidar antes del vencimiento']]) {
 const el=new Input();el.name=name;el.value=value;el.form=form;
}
const originalTotal=new Element();originalTotal.id='saldo-original';originalTotal.textContent='$1,000.00';
const condonation=new Element();condonation.id='monto-condonado';condonation.textContent='$200.00';
const agreementRow=new Element();agreementRow.querySelector=selector=>({textContent:selector==='strong'?'Ana · A-1':'$600.00'});
const allBefore=form.querySelectorAll.bind(form);
form.querySelectorAll=selector=>selector==='#cargos-seleccionados article'?[agreementRow]:allBefore(selector);
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar convenio de pago');
assert.equal(rows()['Nuevo monto total'],'$800.00');assert.equal(rows()['Monto condonado estimado'],'$200.00');
assert.equal(rows()['Adeudo original 1'],'Ana · A-1 · $600.00');assert.equal(rows()['Fecha del acuerdo'],'08/10/2026');
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,8);
submit();accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,9);
// Reporte familiar reutiliza el modal sin enviar ni perder archivos al cancelar.
form.dataset = { moneyConfirm:'transferencia-familiar', confirmCurrency:'MXN' };
const transferTotal = { value:'800.00' };
const transferAccount = { selectedOptions:[{textContent:'Banco prueba · Plantel Centro'}] };
const transferDate = { value:'2026-10-08T12:45' };
const transferReference = { value:'TEST-FAMILIA-800' };
const beforeTransferQuery = form.querySelector.bind(form);
form.querySelector = selector => ({'.portal-total-value':transferTotal, '#cuenta':transferAccount,
    '[name="fechaPago"]':transferDate, '[name="referencia"]':transferReference,
    '[name="nombrePagador"]':{value:'María <script>'}, '[name="observaciones"]':{value:'Pago de dos hijos'}
}[selector] || beforeTransferQuery(selector));
const portalRows = [['Ana · Cargo A','480.00'],['Luis · Cargo B','320.00']].map(([name, amount]) => ({
    querySelector: selector => ({value:selector === '.portal-cargo-search' ? name : amount})
}));
const beforeTransferAll = form.querySelectorAll.bind(form);
form.querySelectorAll = selector => selector === '.portal-distribution-row' ? portalRows : beforeTransferAll(selector);
submit();
assert.equal(form.submissions,9);
assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar envío de transferencia');
assert.equal(rows()['Total reportado'],'$800.00');
assert.equal(rows()['Cargo del alumno 1'],'Ana · Cargo A · $480.00');
assert.equal(rows()['Cargo del alumno 2'],'Luis · Cargo B · $320.00');
assert.equal(rows()['Cuenta destino'],'Banco prueba · Plantel Centro');
assert.equal(rows()['Fecha y hora'],'08/10/2026 · 12:45 h');
assert.equal(rows()['Referencia bancaria'],'TEST-FAMILIA-800');
assert.equal(rows()['Quién realizó la transferencia'],'María <script>');
assert.equal(rows()['Comprobantes adjuntos'],'Prueba <script>.pdf');
assert.equal(cancel.textContent,'Cancelar');
assert.equal(accept.textContent,'Confirmar y enviar a revisión');
assert.match(doc.querySelector('#money-confirm-note').textContent,/no se hará un cargo a tu banco/);
cancel.dispatchEvent({type:'click'});
assert.equal(form.submissions,9); assert.equal(receipt.files.length,1); assert.equal(transferTotal.value,'800.00');
submit();dialog.dispatchEvent({type:'cancel',preventDefault(){}});assert.equal(form.submissions,9);
transferReference.value='TEST-CORREGIDO'; submit();assert.equal(rows()['Referencia bancaria'],'TEST-CORREGIDO');
accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,10);
// Cancelar convenio sin pagos: mostrar qué se reactiva/cancela, sin tocar saldos al cerrar.
form.dataset = {moneyConfirm:'cancelacion-convenio',confirmFolio:'CV-CANCEL-TEST',confirmSubject:'Tutor <script>',
    confirmOriginal:'500.00',confirmAmount:'400.00',confirmCondoned:'100.00',confirmCurrency:'MXN'};
const originalCharges = [{dataset:{label:'#30 · Ana · Original',amount:'300.00'}},{dataset:{label:'#31 · Luis · Original',amount:'200.00'}}];
const newCharges = [{dataset:{label:'#32 · Ana · Convenio',amount:'240.00'}},{dataset:{label:'#33 · Luis · Convenio',amount:'160.00'}}];
const beforeCancelAll = form.querySelectorAll.bind(form);
form.querySelectorAll = selector => selector === '[data-convenio-reactivar] span' ? originalCharges
    : selector === '[data-convenio-cancelar] span' ? newCharges : beforeCancelAll(selector);
submit();assert.equal(form.submissions,10);assert.equal(dialog.open,true);
assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar cancelación del convenio');
assert.equal(accept.textContent,'Confirmar cancelación del convenio');assert.equal(cancel.textContent,'Cancelar');
assert.equal(rows()['Folio del convenio'],'CV-CANCEL-TEST');
assert.equal(rows()['Tutor responsable'],'Tutor <script>');
assert.equal(rows()['Total original que volverá a ser exigible'],'$500.00');
assert.equal(rows()['Total de nuevos adeudos que se cancelarán'],'$400.00');
assert.equal(rows()['Condonación del acuerdo que dejará de aplicar'],'$100.00');
const detailsText = dialog.children[2].children.map(child=>child.textContent).join('\n');
for (const expected of ['#30 · Ana · Original · $300.00','#31 · Luis · Original · $200.00',
    '#32 · Ana · Convenio · $240.00','#33 · Luis · Convenio · $160.00']) assert.ok(detailsText.includes(expected));
assert.match(rows()['Efecto en caja y bancos'],/Ninguno/);
assert.match(doc.querySelector('#money-confirm-note').textContent,/no haya pagos aplicados/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,10);assert.equal(dialog.open,false);
assert.equal(doc.activeElement,submitter);
submit();dialog.dispatchEvent({type:'cancel',preventDefault(){}});assert.equal(form.submissions,10);
submit();dialog.getBoundingClientRect=()=>({left:10,right:200,top:10,bottom:200});
dialog.dispatchEvent({type:'click',target:dialog,clientX:0,clientY:0});assert.equal(dialog.open,false);assert.equal(form.submissions,10);
const oldValidity=form.reportValidity;form.reportValidity=()=>false;submit();assert.equal(dialog.open,false);assert.equal(form.submissions,10);form.reportValidity=oldValidity;
submit();accept.dispatchEvent({type:'click'});accept.dispatchEvent({type:'click'});assert.equal(form.submissions,11);
assert.equal(originalCharges[0].dataset.amount,'300.00');
form.dataset={moneyConfirm:'convenio',confirmCurrency:'MXN'};
const oldCondonaQuery=form.querySelector.bind(form);
form.querySelector=selector=>selector==='[name="modalidad"]'?{value:'CONDONACION_TOTAL'}
    :selector==='[name="montoAcordado"]'?{value:'0.00'}
    :selector==='#monto-condonado'?{textContent:'$500.00'}:oldCondonaQuery(selector);
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar condonación total');
assert.equal(accept.textContent,'Confirmar y condonar saldo');assert.equal(rows()['Nuevo monto total'],'$0.00');
assert.equal(rows()['Monto condonado estimado'],'$500.00');assert.match(doc.querySelector('#money-confirm-note').textContent,/No se crearán cargos nuevos/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,11);
form.dataset={moneyConfirm:'cancelacion-convenio',confirmTotal:'true',confirmFolio:'CV-TOTAL',confirmSubject:'Tutor',confirmOriginal:'500.00',confirmAmount:'0.00',confirmCondoned:'500.00'};
submit();assert.match(rows()['Cargos nuevos'],/No existen/);assert.ok(!('Total de nuevos adeudos que se cancelarán' in rows()));
assert.match(doc.querySelector('#money-confirm-note').textContent,/conservando los abonos anteriores/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,11);
console.log('DOM: confirmaciones compartidas y condonación total; sin cargos nuevos, cancelación restauradora, cierre sin envío y confirmación única correctos.');
form.dataset={moneyConfirm:'saldo-favor',confirmFolio:'PAG-ORIGEN',confirmSubject:'Familia',available:'100',confirmAmount:'60',currency:'MXN'};
const creditRemaining=new Element();creditRemaining.id='credit-remaining';creditRemaining.textContent='$40.00';
const creditRow=new Element();creditRow.querySelector=s=>s==='.credit-title'?{textContent:'Hermano · Mensualidad'}:{value:'60.00'};
const oldCreditAll=form.querySelectorAll.bind(form);
form.querySelectorAll=s=>s==='.saldo-row'?[creditRow]:oldCreditAll(s);
submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar aplicación del saldo a favor');
assert.equal(rows()['Hermano · Mensualidad'],'$60.00');assert.equal(rows()['Seguirá a favor del tutor'],'$40.00');
assert.equal(rows()['Total a aplicar'],'$60.00');assert.equal(cancel.textContent,'Cancelar');
assert.match(doc.querySelector('#money-confirm-note').textContent,/No registra otro pago/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,11);
submit();accept.dispatchEvent({type:'click'});assert.equal(form.submissions,12);
form.dataset={moneyConfirm:'saldo-favor-reversion',confirmFolio:'PAG-ORIGEN',confirmSubject:'Hermano',confirmAmount:'60',currency:'MXN'};
submit();assert.equal(rows()['Importe que vuelve al saldo a favor'],'$60.00');
assert.match(doc.querySelector('#money-confirm-note').textContent,/No devuelve dinero/);
cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,12);
const anticipoOldQuery=form.querySelector.bind(form),anticipoOldAll=form.querySelectorAll.bind(form);
const anticipoFields={'[name=tipoBeneficio]':{value:'PORCENTAJE'},'[name=valor]':{value:'10'},'[name=politicaBeca]':{value:'CONSERVAR'},'[name=fechaLimite]':{value:'2026-10-20'},'[name=motivo]':{value:'Pago anual completo'},'[name=metodo]':{value:'TARJETA'},'#anticipo-cuenta':{value:'Banco de la escuela'},'[name=fecha]':{value:'2026-10-09T10:00'},'[name=referencia]':{value:'ANUAL-TEST'}};
form.querySelector=s=>anticipoFields[s]||anticipoOldQuery(s);
form.querySelectorAll=s=>s==='.anticipation-preview tbody tr'?[{querySelectorAll:()=>['Hermano · Octubre','$1,000.00','$200.00','$0.00','$800.00','$80.00','$720.00'].map(textContent=>({textContent}))}]:anticipoOldAll(s);
form.dataset={moneyConfirm:'anticipo-propuesta',confirmAmount:'8640',currency:'MXN'};
submitter.id='anticipation-save';submit();assert.equal(doc.querySelector('#money-confirm-title').textContent,'Confirmar propuesta por pago anticipado');assert.equal(rows()['Pago completo requerido'],'$8,640.00');assert.match(rows()['Política de beca'],/Conservar/);assert.equal(cancel.textContent,'Cancelar');cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,12);
submit();accept.dispatchEvent({type:'click'});assert.equal(form.submissions,13);
submitter.id='anticipation-preview';submit();assert.equal(form.submissions,14);assert.equal(dialog.open,false);
form.dataset={moneyConfirm:'anticipo-pago',confirmAmount:'8640',currency:'MXN',confirmFolio:'ANT-TEST',confirmSubject:'Familia'};
submit();assert.equal(rows()['Cuenta'],'Banco de la escuela');assert.equal(rows()['Método'],'TARJETA');assert.match(doc.querySelector('#money-confirm-note').textContent,/único pago pendiente/);cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,14);
form.dataset={moneyConfirm:'anticipo-cancelar',confirmFolio:'ANT-TEST',confirmSubject:'Familia'};
submit();assert.equal(rows()['Motivo'],'Pago anual completo');assert.match(doc.querySelector('#money-confirm-note').textContent,/No mueve dinero/);cancel.dispatchEvent({type:'click'});assert.equal(form.submissions,14);
console.log('Anticipados: propuesta, pago y cancelación con detalle, Cancelar sin enviar, confirmación única y vista previa sin modal verificados.');
