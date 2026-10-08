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
    globalThis.MoneyValues = {parseMoney, compareDecimals, formatMoney};

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
        document.addEventListener('submit', event => {
            if (approved.has(event.target)) event.moneyConfirmed = true;
        }, true);
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
            const currency = (['cuota', 'registro-pago'].includes(kind) ? form.querySelector('#moneda')?.value : kind === 'convenio' ? form.querySelector('#institucion')?.selectedOptions?.[0]?.dataset?.moneda : null) || form.dataset.confirmCurrency || form.dataset.currency || 'MXN';
            const money = raw => formatMoney(parseMoney(raw).raw, currency) || 'Sin importe';
            const add = (label, value) => {
                const dt = document.createElement('dt'), dd = document.createElement('dd');
                dt.textContent = label; dd.textContent = value; details.append(dt, dd);
            };
            details.replaceChildren();
            const paymentDecision = ['validacion', 'rechazo', 'cancelacion'].includes(kind);
            cancel.textContent = ['cuota', 'registro-pago', 'cancelacion-cargo', 'convenio', 'generacion-cargos', 'transferencia-familiar'].includes(kind) || paymentDecision ? 'Cancelar' : 'Volver a revisar';
            accept.textContent = kind === 'cuota' ? 'Confirmar y crear cuota' : 'Confirmar operación';
            if (paymentDecision) {
                if (form.dataset.confirmFolio) add('Folio del pago', form.dataset.confirmFolio);
                if (form.dataset.confirmSubject) add('Titular del pago', form.dataset.confirmSubject);
            }
            if (kind === 'transferencia-familiar') {
                title.textContent = 'Confirmar envío de transferencia';
                accept.textContent = 'Confirmar y enviar a revisión';
                const value = selector => form.querySelector(selector)?.value || '';
                form.querySelectorAll('.portal-distribution-row').forEach((row, index) => {
                    add('Cargo del alumno ' + (index + 1),
                        (row.querySelector('.portal-cargo-search')?.value || 'Sin cargo seleccionado') + ' · ' +
                        money(row.querySelector('.portal-charge-amount')?.value || ''));
                });
                add('Total reportado', money(value('.portal-total-value')));
                add('Cuenta destino', form.querySelector('#cuenta')?.selectedOptions?.[0]?.textContent || 'Sin seleccionar');
                const [date, time] = value('[name="fechaPago"]').split('T');
                add('Fecha y hora', date ? date.split('-').reverse().join('/') + (time ? ' · ' + time.slice(0,5) + ' h' : '') : 'Sin capturar');
                add('Referencia bancaria', value('[name="referencia"]'));
                if (value('[name="nombrePagador"]')) add('Quién realizó la transferencia', value('[name="nombrePagador"]'));
                if (value('[name="observaciones"]')) add('Observaciones', value('[name="observaciones"]'));
                const files = form.querySelector('#comprobantes')?.files;
                add('Comprobantes adjuntos', files?.length ? Array.from(files, file => file.name).join('\n') : 'Sin archivos');
                note.textContent = 'Sólo estás reportando una transferencia que ya realizaste; no se hará un cargo a tu banco. Quedará en revisión y se aplicará a los adeudos cuando la escuela la valide. Cancelar conserva tus datos y comprobantes sin enviar.';
            } else if (kind === 'generacion-cargos') {
                title.textContent = 'Confirmar generación de adeudos';
                accept.textContent = 'Confirmar y generar adeudos';
                add('Selección de todas las páginas', form.querySelector('[data-selection-summary]')?.textContent || 'Revisa la selección');
                const date = form.querySelector('[name="fechaCorte"]')?.value || '';
                add('Fecha de corte', date.split('-').reverse().join('/'));
                note.textContent = 'Sólo se generarán los adeudos seleccionados, incluyendo la selección de otras páginas. No registra dinero recibido. Cancelar conserva tu selección sin generar; el servidor comprobará nuevamente su vigencia y los importes.';
            } else if (kind === 'convenio') {
                title.textContent = 'Confirmar convenio de pago';
                accept.textContent = 'Confirmar y crear convenio';
                const value = name => form.querySelector('[name="' + name + '"]')?.value || '';
                const date = name => value(name).split('-').reverse().join('/');
                add('Tutor responsable', form.querySelector('#tutor-busqueda')?.value || '');
                add('Concepto del nuevo cargo', form.querySelector('#concepto-busqueda')?.value || '');
                form.querySelectorAll('#cargos-seleccionados article').forEach((row, index) => {
                    add('Adeudo original ' + (index + 1), row.querySelector('strong').textContent + ' · ' + row.querySelector('span').textContent);
                });
                add('Saldo pendiente seleccionado', form.querySelector('#saldo-original')?.textContent || '');
                add('Nuevo monto total', money(value('montoAcordado')));
                add('Monto condonado estimado', form.querySelector('#monto-condonado')?.textContent || '');
                add('Fecha del acuerdo', date('fechaAcuerdo'));
                add('Nueva fecha límite', date('fechaVencimiento'));
                add('Descripción', value('descripcion'));
                add('Motivo', value('motivo'));
                if (value('condiciones')) add('Condiciones', value('condiciones'));
                note.textContent = 'Los adeudos originales dejarán de ser exigibles sin borrarse y el nuevo monto se distribuirá proporcionalmente por alumno. No mueve dinero. Cancelar conserva la captura sin crear el convenio; el servidor volverá a comprobar los saldos.';
            } else if (kind === 'registro-pago') {
                title.textContent = 'Confirmar registro del pago';
                accept.textContent = 'Confirmar y registrar como pendiente';
                const value = selector => form.querySelector(selector)?.value || '';
                add('Tutor titular', value('#tutor-busqueda'));
                add('Total recibido', money(value('#monto')));
                const method = value('#metodo');
                add('Método de pago', method === 'EFECTIVO' ? 'Efectivo' : method === 'TRANSFERENCIA' ? 'Transferencia' : 'Tarjeta');
                add('Cuenta declarada', value('#cuenta-busqueda'));
                const when = value('[name="fechaPago"]');
                const [date, time] = when.split('T');
                add('Fecha y hora del pago', date ? date.split('-').reverse().join('/') + (time ? ' · ' + time : '') : 'Sin capturar');
                const reference = value('[name="referencia"]');
                if (reference) add('Referencia', reference);
                let distributed = 0n;
                const cents = raw => BigInt((parseMoney(raw).raw || '0.00').replace('.', ''));
                const displayCents = amount => {
                    const digits = amount.toString().padStart(3, '0');
                    return formatMoney(digits.slice(0, -2) + '.' + digits.slice(-2), currency);
                };
                form.querySelectorAll('.distribution-row').forEach((row, index) => {
                    const amount = row.querySelector('.monto-solicitado')?.value || '';
                    distributed += cents(amount);
                    add('Asignación ' + (index + 1), (row.querySelector('.distribution-charge input[type="search"]')?.value || 'Sin cargo seleccionado') + ' · ' + money(amount));
                });
                add('Distribuido entre cargos', displayCents(distributed));
                const available = cents(value('#monto')) - distributed;
                add('Dinero pendiente de asignar', available >= 0n ? displayCents(available) : 'La distribución supera el total recibido; corrige los importes');
                const files = form.querySelector('#comprobantes')?.files;
                add('Comprobantes seleccionados', files?.length ? Array.from(files, file => file.name).join('\n') : 'Sin archivos');
                note.textContent = 'El pago quedará pendiente de validación. Todavía no reducirá adeudos ni aumentará la cuenta: eso ocurre al validar y publicar. Cancelar conserva la captura y los archivos seleccionados sin guardar.';
            } else if (kind === 'cuota') {
                title.textContent = 'Confirmar nueva cuota';
                const value = id => form.querySelector('#' + id)?.value || '';
                const selected = id => form.querySelector('#' + id)?.selectedOptions?.[0]?.textContent || 'Sin seleccionar';
                const date = raw => /^\d{4}-\d{2}-\d{2}$/.test(raw) ? raw.split('-').reverse().join('/') : 'Se completará según inscripción y ciclo';
                const month = raw => /^\d{4}-\d{2}$/.test(raw) ? raw.split('-').reverse().join('/') : 'Sin capturar';
                const monthly = value('frecuencia') === 'MENSUAL';
                add('Institución', selected('institucion'));
                add('Plantel', selected('plantel'));
                add('Alumno e inscripción', value('inscripcion-busqueda'));
                add('Concepto de cobro', value('concepto-busqueda'));
                add(monthly ? 'Importe por mes' : 'Importe único', money(value('importeBase')));
                add('Frecuencia', monthly ? 'Cada mes' : 'Una sola vez · no se repite');
                if (monthly) {
                    add('Meses a cobrar', month(value('primerMes')) + ' — ' + month(value('ultimoMes')));
                    add('Fecha límite de pago', 'Día ' + value('dia-vencimiento') + ' de cada mes; en meses más cortos, el último día');
                } else add('Fecha límite de pago', date(value('fecha-vencimiento-unico')));
                add('Vigencia de la configuración', date(value('fechaInicio')) + ' — ' + date(value('fechaFin')));
                add('Estado', selected('estado-cuota'));
                add('Generación automática', form.querySelector('#generacion-automatica')?.checked ? 'Sí · se incluirá en Generar automáticos' : 'No · generación manual');
                const now = form.querySelector('[name="generarCargoAhora"]')?.checked;
                add('Crear cargo al confirmar', now ? 'Sí · se preparará el cargo único de esta inscripción' : 'No · sólo se guardará la cuota');
                note.textContent = 'Revisa antes de guardar. Crear la cuota no registra un pago recibido ni mueve dinero. Cancelar conserva los datos para corregirlos.';
            } else if (kind === 'devolucion') {
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
                const previewTotal = form.querySelector('#charge-preview-total');
                if (previewTotal) {
                    add('Nuevo total ajustado', previewTotal.textContent);
                    add('Pagado acumulado', form.querySelector('#charge-preview-paid').textContent);
                    add('Saldo después del ajuste', form.querySelector('#charge-preview-balance').textContent);
                }
                add('Motivo', form.querySelector('[name="motivo"]').value);
                note.textContent = 'Este ajuste modifica la deuda. No registra un ingreso ni una salida de dinero de la cuenta.';
            } else if (kind === 'cancelacion-cargo') {
                title.textContent = 'Confirmar cancelación del adeudo';
                accept.textContent = 'Confirmar cancelación del adeudo';
                add('Número de cargo', '#' + form.dataset.confirmCharge);
                add('Alumno', form.dataset.confirmSubject);
                add('Concepto', form.dataset.confirmConcept);
                add('Total del adeudo con ajustes', money(form.dataset.confirmAmount));
                add('Abonos vigentes', money(form.dataset.confirmPaid));
                add('Falta por pagar actualmente', money(form.dataset.confirmBalance));
                add('Motivo de cancelación', form.querySelector('[name="motivo"]').value);
                add('Resultado', 'Adeudo cancelado · deja de ser exigible y conserva su historial');
                add('Efecto en caja y bancos', 'Ninguno · no registra ingresos, egresos ni devoluciones');
                note.textContent = 'Esto cancela el adeudo, no un pago. Sólo se permite sin abonos vigentes; el servidor volverá a comprobarlo. Cancelar no ejecuta la operación. Si proviene de una cuota, cancelarlo no habilita su regeneración automática.';
            } else if (kind === 'rechazo') {
                title.textContent = 'Confirmar rechazo del pago';
                accept.textContent = 'Confirmar rechazo';
                add('Importe del reporte', money(form.dataset.confirmAmount));
                add('Motivo del rechazo', form.querySelector('[name="motivo"]').value);
                add('Resultado', 'Pago rechazado · sin ingreso ni aplicaciones; la deuda no cambia');
                note.textContent = 'El motivo quedará registrado y podrá consultarlo el tutor. Se conservarán el pago y sus comprobantes. Cancelar cierra esta ventana sin rechazar.';
            } else if (kind === 'cancelacion') {
                title.textContent = 'Confirmar cancelación del pago';
                accept.textContent = 'Confirmar cancelación';
                const validated = form.dataset.confirmState === 'VALIDADO';
                add('Estado actual', validated ? 'Validado' : 'Pendiente de validación');
                add('Importe del pago', money(form.dataset.confirmAmount));
                add('Motivo de cancelación', form.querySelector('[name="motivo"]').value);
                if (validated) {
                    add('Cuenta del ingreso que se compensará', form.dataset.confirmAccount || 'Cuenta del ingreso original');
                    add('Egreso compensatorio', money(form.dataset.confirmAmount));
                    add('Efecto en los adeudos', 'Se liberarán los abonos vigentes y se recuperará la deuda correspondiente');
                } else add('Efecto en los saldos', 'No cambiarán la cuenta ni los adeudos: este pago todavía no se había validado');
                note.textContent = 'Se cancelará un registro creado por error y se conservará el historial. No es una devolución real al tutor. Cancelar cierra esta ventana sin ejecutar la cancelación del pago.';
            } else {
                title.textContent = 'Confirmar validación del pago';
                accept.textContent = 'Confirmar validación';
                add('Importe recibido', money(form.dataset.confirmAmount));
                add('Cuenta destino', form.querySelector('#cuentaDestinoBusqueda').value);
                const reason = form.querySelector('[name="motivoCambioCuenta"]')?.value;
                if (reason) add('Motivo del cambio de cuenta', reason);
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
