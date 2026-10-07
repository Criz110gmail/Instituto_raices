/* Presentación monetaria separada del decimal canónico que leen los cálculos y el servidor. */
(() => {
    function parseMoney(value, scale = 2) {
        let text = String(value ?? '').trim();
        if (!text) return {raw: '', error: ''};
        const currency = /^(?:[A-Z]{3}\s*)?\$\s*/.test(text);
        text = text.replace(/^(?:[A-Z]{3}\s*)?\$\s*/, '');
        if (/\s/.test(text) || /[eE+-]/.test(text)) return {raw: '', error: 'Usa un importe positivo, sin letras ni notación científica.'};
        if (text.includes(',') && text.includes('.')) {
            if (!/^\d{1,3}(,\d{3})+\.\d+$/.test(text)) return {raw: '', error: 'Usa el formato 1,400.50 o captura 1400.50.'};
            text = text.replaceAll(',', '');
        } else if (text.includes(',')) {
            if (currency && /^\d{1,3}(,\d{3})+$/.test(text)) text = text.replaceAll(',', '');
            else if (/^\d+,\d{1,2}$/.test(text)) text = text.replace(',', '.');
            else return {raw: '', error: 'El separador es ambiguo. Captura 1400.00, sin separadores de miles.'};
        }
        if (!/^\d*(?:\.\d*)?$/.test(text) || !/\d/.test(text)) return {raw: '', error: 'Captura un importe válido.'};
        let [integer, fraction = ''] = text.split('.');
        integer = (integer || '0').replace(/^0+(?=\d)/, '');
        if (fraction.length > scale) return {raw: '', error: `El importe admite hasta ${scale} decimales; no se redondeará automáticamente.`};
        if (integer.length > 17) return {raw: '', error: 'El importe es demasiado grande.'};
        return {raw: integer + '.' + fraction.padEnd(scale, '0'), error: ''};
    }
    function compareDecimals(a, b) {
        const parts = v => { const [i, f = ''] = String(v).split('.'); return [i || '0', f]; };
        const x = parts(a), y = parts(b), scale = Math.max(x[1].length, y[1].length);
        const n = p => BigInt(p[0] + p[1].padEnd(scale, '0'));
        return n(x) < n(y) ? -1 : n(x) > n(y) ? 1 : 0;
    }
    function formatMoney(raw, currency = 'MXN') {
        if (!raw) return '';
        const [integer, fraction = '00'] = raw.split('.');
        return (currency === 'MXN' ? '$' : currency + ' $')
            + new Intl.NumberFormat('es-MX').format(BigInt(integer)) + '.' + fraction;
    }
    if (typeof module !== 'undefined') module.exports = {parseMoney, compareDecimals, formatMoney};
    if (typeof document === 'undefined') return;

    const fields = new WeakMap();
    let sequence = 0;
    function initialize(input) {
        if (fields.has(input) || input.type === 'hidden') return;
        const originalValue = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value');
        const initial = input.value, required = input.required;
        const businessInitial = input.validity.customError ? input.validationMessage : '';
        const visible = document.createElement('input');
        visible.type = 'text'; visible.inputMode = 'decimal'; visible.autocomplete = 'off';
        visible.className = 'money-entry';
        visible.id = (input.id || 'money-' + (++sequence)) + '-display';
        visible.required = required; visible.placeholder = input.placeholder || '0.00';
        for (const attr of ['aria-label', 'aria-describedby', 'aria-labelledby'])
            if (input.hasAttribute(attr)) visible.setAttribute(attr, input.getAttribute(attr));
        const labels = input.id ? [...document.querySelectorAll('label[for]')]
            .filter(label => label.htmlFor === input.id) : [];
        labels.forEach(label => { label.htmlFor = visible.id; });
        input.type = 'hidden'; input.required = false;
        input.before(visible);
        const surroundingLabel = visible.closest('label');
        if (surroundingLabel) labels.push(surroundingLabel);
        new Set(labels).forEach(label => label.addEventListener('click', event => {
            // Mantiene el nombre accesible y el foco por Tab; evita que el título o su
            // espacio active la edición monetaria mediante el comportamiento del label.
            if (event.detail === 0 || event.target === visible) return;
            if (event.target.closest('button,a,input,select,textarea')) return;
            event.preventDefault();
            if (document.activeElement === visible) visible.blur();
        }));
        let businessError = businessInitial, parsed = {raw: '', error: ''};
        const scale = Number(input.dataset.moneyScale || 2);
        const condition = input.dataset.moneyWhen?.split(':');
        const selector = condition ? document.getElementById(condition[0]) || document.querySelector(`[name="${condition[0]}"]`) : null;
        const monetary = () => !condition || selector?.value === condition[1];
        const currency = () => {
            const candidate = input.dataset.currency || input.form?.dataset.currency
                || input.form?.querySelector('[name="moneda"]')?.value || 'MXN';
            return /^[A-Z]{3}$/.test(candidate) ? candidate : 'MXN';
        };
        function validate() {
            visible.disabled = input.disabled;
            visible.readOnly = input.readOnly;
            visible.required = required || input.required;
            let error = parsed.error;
            if (!error && parsed.raw && input.min && compareDecimals(parsed.raw, input.min) < 0)
                error = `El importe mínimo es ${input.min}.`;
            if (!error && parsed.raw && input.max && compareDecimals(parsed.raw, input.max) > 0)
                error = `El importe máximo es ${input.max}.`;
            visible.setCustomValidity(error || businessError);
            visible.setAttribute('aria-invalid', String(Boolean(error || businessError)));
            return !error && !businessError;
        }
        function paint(force = false) {
            if (force || document.activeElement !== visible)
                visible.value = parsed.error ? visible.value : monetary() ? formatMoney(parsed.raw, currency()) : parsed.raw;
            validate();
        }
        function set(value) {
            parsed = parseMoney(value, scale);
            originalValue.set.call(input, parsed.raw);
            if (document.activeElement === visible) validate();
            else { visible.value = parsed.error ? String(value ?? '') : ''; paint(); }
        }
        Object.defineProperty(input, 'value', {
            configurable: true, get() { return originalValue.get.call(input); }, set
        });
        input.setCustomValidity = message => { businessError = message; validate(); };
        input.focus = options => visible.focus(options);
        input.select = () => visible.select();
        input.reportValidity = () => { validate(); return visible.reportValidity(); };
        input.checkValidity = () => { validate(); return visible.checkValidity(); };
        visible.addEventListener('focus', () => {
            validate(); if (!parsed.error && !input.readOnly) visible.value = parsed.raw;
        });
        visible.addEventListener('input', () => {
            if (input.readOnly || input.disabled) return;
            businessError = '';
            parsed = parseMoney(visible.value, scale);
            originalValue.set.call(input, parsed.raw); validate();
            input.dispatchEvent(new Event('input', {bubbles: true}));
        });
        visible.addEventListener('blur', () => { paint(true); });
        visible.addEventListener('change', () => input.dispatchEvent(new Event('change', {bubbles: true})));
        // El input visible es texto: la rueda nunca incrementa ni disminuye su importe.
        const state = {visible, paint, validate, reset: () => { businessError = ''; set(initial); }}; fields.set(input, state);
        new MutationObserver(() => { paint(); }).observe(input, {
            attributes: true, attributeFilter: ['disabled', 'readonly', 'min', 'max', 'required', 'data-currency']
        });
        selector?.addEventListener('change', () => paint());
        input.form?.querySelector('[name="moneda"]')?.addEventListener('change', () => paint());
        set(initial);
    }
    function scan(root = document) {
        if (root.matches?.('input[data-money]')) initialize(root);
        root.querySelectorAll?.('input[data-money]').forEach(initialize);
    }
    function start() {
        scan();
        new MutationObserver(records => records.forEach(record => record.addedNodes.forEach(node => {
            if (node.nodeType === 1) scan(node);
        }))).observe(document.body, {childList: true, subtree: true});
        document.addEventListener('submit', event => {
            for (const input of event.target.querySelectorAll('input[data-money]')) {
                initialize(input);
                const state = fields.get(input);
                if (state && !input.disabled && !state.validate()) {
                    event.preventDefault(); state.visible.reportValidity(); state.visible.focus(); return;
                }
            }
        }, true);
        document.addEventListener('reset', event => setTimeout(() => {
            event.target.querySelectorAll('input[data-money]').forEach(input => {
                fields.get(input)?.reset();
            });
        }, 0));
        installConfirmation();
    }

    function installConfirmation() {
        const dialog = document.createElement('dialog'); dialog.className = 'money-confirm-dialog';
        dialog.setAttribute('aria-labelledby', 'money-confirm-title');
        dialog.setAttribute('aria-describedby', 'money-confirm-note');
        const title = document.createElement('h2'); title.id = 'money-confirm-title';
        const badge = document.createElement('span'); badge.className = 'money-confirm-badge'; badge.textContent = 'Revisa antes de confirmar';
        const details = document.createElement('dl');
        const note = document.createElement('p'); note.id = 'money-confirm-note';
        const actions = document.createElement('div'); actions.className = 'money-confirm-actions';
        const cancel = document.createElement('button'); cancel.type = 'button'; cancel.className = 'money-confirm-cancel'; cancel.textContent = 'Volver a revisar';
        const accept = document.createElement('button'); accept.type = 'button'; accept.className = 'money-confirm-accept'; accept.textContent = 'Confirmar operación';
        actions.append(cancel, accept); dialog.append(badge, title, details, note, actions); document.body.append(dialog);
        let pending, submitter;
        const approved = new WeakSet();
        function close() {
            dialog.close(); document.body.classList.remove('money-confirm-open');
            submitter?.focus(); pending = undefined; submitter = undefined;
        }
        cancel.addEventListener('click', close);
        dialog.addEventListener('cancel', event => { event.preventDefault(); close(); });
        dialog.addEventListener('click', event => {
            if (event.target === dialog) {
                const r = dialog.getBoundingClientRect();
                if (event.clientX < r.left || event.clientX > r.right || event.clientY < r.top || event.clientY > r.bottom) close();
            }
        });
        accept.addEventListener('click', () => {
            if (!pending) return;
            const form = pending, button = submitter;
            accept.disabled = true; close(); approved.add(form);
            try { form.requestSubmit(button || undefined); } finally { approved.delete(form); }
        });
        document.addEventListener('submit', event => {
            const form = event.target, kind = form.dataset.moneyConfirm;
            if (!kind || event.defaultPrevented || approved.has(form)) return;
            event.preventDefault();
            if (!form.reportValidity()) return;
            const currency = form.dataset.confirmCurrency || form.dataset.currency || 'MXN';
            const money = raw => formatMoney(parseMoney(raw).raw, currency) || 'Sin importe';
            const add = (label, value) => {
                const dt = document.createElement('dt'), dd = document.createElement('dd');
                dt.textContent = label; dd.textContent = value; details.append(dt, dd);
            };
            details.replaceChildren();
            if (kind === 'devolucion') {
                title.textContent = 'Confirmar devolución';
                add('Dinero a devolver', money(form.querySelector('#refund-amount').value));
                add('Cuenta de origen', form.querySelector('#refund-account').value);
                add('Beneficiario', form.querySelector('[name="beneficiario"]').value);
                add('Abono seleccionado que permanecerá aplicado', form.querySelector('#refund-preview-applied').textContent);
                add('Deuda que se recuperará', form.querySelector('#refund-preview-reopened').textContent);
                note.textContent = 'Se publicará un egreso y se liberarán los abonos necesarios. Esto no aplica un descuento al cargo.';
            } else if (kind === 'ajuste') {
                title.textContent = 'Confirmar ajuste al saldo';
                const type = form.querySelector('[name="tipo"]').value;
                const effect = type === 'DESCUENTO' ? 'DISMINUCION' : type === 'RECARGO' ? 'AUMENTO' : form.querySelector('[name="efecto"]').value;
                add('Cargo', '#' + form.dataset.confirmCharge);
                if (form.dataset.confirmSubject) add('Alumno', form.dataset.confirmSubject);
                add('Ajuste', type === 'DESCUENTO' ? 'Descuento' : type === 'RECARGO' ? 'Recargo' : 'Corrección');
                add('Efecto', effect === 'DISMINUCION' ? 'Disminuye el importe del cargo' : 'Aumenta el importe del cargo');
                add('Monto', money(form.querySelector('[name="monto"]').value));
                add('Motivo', form.querySelector('[name="motivo"]').value);
                note.textContent = 'Este ajuste modifica la deuda. No registra un ingreso ni una salida de dinero de la cuenta.';
            } else {
                title.textContent = 'Confirmar validación del pago';
                add('Importe recibido', money(form.dataset.confirmAmount));
                if (form.dataset.confirmSubject) add('Titular del pago', form.dataset.confirmSubject);
                add('Cuenta destino', form.querySelector('#cuentaDestinoBusqueda').value);
                note.textContent = 'Confirma que recibiste el dinero. Se publicará el ingreso y se aplicarán los abonos del pago.';
            }
            pending = form; submitter = event.submitter;
            accept.disabled = false;
            document.body.classList.add('money-confirm-open'); dialog.showModal(); cancel.focus();
        });
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start);
    else start();
})();
