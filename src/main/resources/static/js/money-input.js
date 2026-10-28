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
            if(kind==='anticipo-propuesta' && event.submitter?.id!=='anticipation-save')return;
            if(['nomina-multifuente','nomina-tratamiento'].includes(kind) && event.submitter?.hasAttribute('formaction'))return;
            event.preventDefault();
            if (!form.reportValidity()) return;
            dialog.setAttribute('data-confirm-kind', kind);
            const currency = (['cuota', 'registro-pago'].includes(kind) ? form.querySelector('#moneda')?.value : kind === 'convenio' ? form.querySelector('#institucion')?.selectedOptions?.[0]?.dataset?.moneda : null) || form.dataset.confirmCurrency || form.dataset.currency || 'MXN';
            const money = raw => formatMoney(parseMoney(raw).raw, currency) || 'Sin importe';
            const add = (label, value) => {
                const dt = document.createElement('dt'), dd = document.createElement('dd');
                dt.textContent = label; dd.textContent = value; details.append(dt, dd);
            };
            details.replaceChildren();
            const paymentDecision = ['validacion', 'rechazo', 'cancelacion'].includes(kind);
            cancel.textContent = ['cuota', 'registro-pago', 'cancelacion-cargo', 'convenio', 'generacion-cargos', 'transferencia-familiar', 'cancelacion-convenio'].includes(kind) || paymentDecision ? 'Cancelar' : 'Volver a revisar';
            accept.textContent = kind === 'cuota' ? 'Confirmar y crear cuota' : 'Confirmar operación';
            if (paymentDecision) {
                if (form.dataset.confirmFolio) add('Folio del pago', form.dataset.confirmFolio);
                if (form.dataset.confirmSubject) add('Titular del pago', form.dataset.confirmSubject);
            }
            if(kind==='nomina-tratamiento'||kind==='nomina-pendiente'||kind==='nomina-congelar') {
                cancel.textContent='Cancelar';accept.textContent='Confirmar operación';
                title.textContent=kind==='nomina-tratamiento'?'Confirmar tratamiento del periodo':kind==='nomina-pendiente'?'Preparar pendiente individual':'Confirmar decisiones y congelar revisión';
                add('Empleado',form.dataset.confirmPerson||'Empleados de esta revisión');
                if(form.dataset.confirmAmount)add('Pago inmediato',money(form.dataset.confirmAmount));
                if(kind==='nomina-tratamiento'){
                    add('Alcance',form.querySelector('[name="clave"]').selectedOptions[0].textContent);
                    add('Tratamiento',form.querySelector('[name="accion"]').selectedOptions[0].textContent);
                    add('Motivo',form.querySelector('[name="motivo"]').value);
                }
                note.textContent='Conserva el historial y el periodo original. Esta acción no mueve dinero ni modifica la configuración del empleado. Cancelar cierra el resumen sin guardar.';
            } else if(kind==='nomina-cancelar-todos') {
                title.textContent='Confirmar cancelación de los pagos';cancel.textContent='Cancelar';accept.textContent='Confirmar cancelación completa';
                add('Empleados',form.dataset.confirmPeople);add('Total recuperado',money(form.dataset.confirmAmount));
                form.closest('section').querySelectorAll('[data-cancel-source]').forEach(row=>add(row.querySelector('dt').textContent,row.querySelector('dd').textContent));
                add('Justificación',form.querySelector('[name="motivo"]').value);
                note.textContent='Devuelve cada importe a su cuenta original y conserva el historial. Confirma únicamente si los fondos se recuperaron realmente. Los empleados quedarán pendientes para pago individual de este mismo periodo. Cancelar cierra el resumen sin registrar cambios.';
            } else if(kind==='nomina-multifuente') {
                title.textContent='Confirmar pago de nómina';cancel.textContent='Cancelar';accept.textContent='Confirmar y registrar pagos';
                add('Empleados',form.dataset.confirmPeople);add('Total a pagar',money(form.dataset.confirmAmount));
                form.querySelectorAll('.assignment-summary-card').forEach(row=>{
                    const cuenta=row.querySelector('.autocomplete-input')?.value;
                    const monto=row.querySelector('input[name$=".importe"]')?.value;
                    if(cuenta&&monto)add(cuenta,money(monto));
                });
                note.textContent='Se registrarán salidas reales en las cuentas indicadas y se conservará su distribución por empleado. No envía transferencias al banco. Cancelar cierra este resumen sin mover dinero.';
            } else if(kind==='pendiente-guardar'||kind==='pendiente-estado') {
                cancel.textContent='Cancelar';accept.textContent=kind==='pendiente-guardar'?'Confirmar y guardar pendiente':'Confirmar cambio de estado';
                title.textContent=kind==='pendiente-guardar'?'Confirmar mi pendiente':'Confirmar seguimiento';
                const campo=name=>form.querySelector('[name="'+name+'"]');
                add('Título',kind==='pendiente-guardar'?campo('titulo').value.trim():form.dataset.confirmTitle);
                if(kind==='pendiente-guardar'){
                    add('Fecha compromiso',campo('fechaCompromiso').value.split('-').reverse().join('/'));
                    add('Descripción',campo('descripcion').value.trim()||'Sin descripción');
                }
                add('Estado',campo('estado').selectedOptions[0].textContent);
                note.textContent='Este pendiente es privado y sólo tú puedes consultarlo. Realizado y Cancelado dejan de aparecer en los avisos; puedes reabrirlo después. Cancelar sólo cierra este resumen y conserva tu captura.';
            } else if(kind==='rol-guardar'||kind==='rol-desactivar') {
                const desactivar=kind==='rol-desactivar',editar=form.dataset.confirmEdit==='true';
                const campo=name=>form.querySelector('[name="'+name+'"]');
                title.textContent=desactivar?'Confirmar desactivación del rol':editar?'Confirmar cambios del rol':'Confirmar creación del rol';
                cancel.textContent='Cancelar';
                accept.textContent=desactivar?'Confirmar y desactivar':editar?'Confirmar y guardar':'Confirmar y crear rol';
                add('Código',desactivar?form.dataset.confirmCode:campo('codigo').value.trim());
                add('Nombre',desactivar?form.dataset.confirmName:campo('nombre').value.trim());
                if(desactivar){
                    add('Resultado','Rol inactivo; se conservan sus permisos, asignaciones e historial');
                }else{
                    add('Institución',campo('institucionId').selectedOptions?.[0]?.textContent||'Sin institución');
                    add('Descripción',campo('descripcion').value.trim()||'Sin descripción');
                    add('Estado',campo('activo').checked?'Activo':'Inactivo');
                    const modulos=[...form.querySelectorAll('.permission-card')].filter(card=>card.querySelector('input[type="checkbox"]').checked).map(card=>card.querySelector('strong').textContent.trim());
                    add('Módulos autorizados ('+modulos.length+')',modulos.length?modulos.join('\n'):'Ninguno · este rol no concede acceso a módulos');
                }
                note.textContent=(desactivar?'El rol dejará de conceder acceso. No se borran usuarios ni registros.':'Cada módulo seleccionado concede su paquete completo de permisos. Al editar un rol, el cambio afecta a todas las cuentas que lo tengan asignado.')+' Los permisos de otros roles se suman; vuelve a iniciar sesión para comprobar el acceso actualizado. Cancelar conserva la captura sin enviar cambios.';
            } else if(kind==='nomina-autorizar'||kind==='nomina-empleado-pagar'||kind==='nomina-empleado-rechazar'||kind==='nomina-empleado-revertir') {
                const campo=n=>form.querySelector('[name="'+n+'"]');
                const autorizar=kind==='nomina-autorizar',pagar=kind==='nomina-empleado-pagar',reversa=kind==='nomina-empleado-revertir';
                title.textContent=autorizar?'Autorizar revisión de nómina':pagar?'Confirmar pago individual':reversa?'Revertir sólo este pago':'Registrar rechazo sin dinero';cancel.textContent='Cancelar';accept.textContent=autorizar?'Confirmar autorización':pagar?'Confirmar pago y abonos':reversa?'Confirmar devolución y reversa':'Confirmar rechazo';
                add(autorizar?'Nómina':'Empleado',form.dataset.confirmSubject);add(autorizar?'Total de revisión':'Neto individual',money(form.dataset.confirmAmount));
                if(autorizar)add('Empleados',form.dataset.confirmPeople);
                if(pagar){add('Cuenta de origen',form.dataset.confirmAccount);add('Saldo anterior',money(form.dataset.confirmBefore));add('Saldo posterior',money(form.dataset.confirmAfter));add('Préstamos con abono',form.dataset.confirmPeople);}
                if(reversa)add('Pago que se revierte','NPI-'+form.dataset.confirmPayment+' · devolución a su cuenta original');
                if(!autorizar)add('Referencia',campo('referencia').value.trim());add('Motivo',campo('motivo').value.trim());
                note.textContent=autorizar?'Congela esta revisión y sus empleados, sin pagar ni abonar préstamos. No podrá editarse o cancelarse como borrador. Cancelar conserva la revisión sin autorizar.':pagar?'Registra un egreso por el neto completo y abona únicamente las cuotas de este empleado. No envía una transferencia bancaria. Cancelar no mueve dinero ni reduce préstamos.':reversa?'Confirma que los fondos regresaron realmente: publica el ingreso compensatorio exacto, restaura sus abonos y vuelve a pendiente. Los otros empleados no cambian. No borra el pago original. Cancelar no registra devolución.':'No salió dinero: se conserva el intento rechazado, sigue pendiente y no abona a préstamos ni genera movimientos. Cancelar no registra el intento.';
            } else if(kind==='prestamo-guardar'||kind==='prestamo-cancelar'||kind==='prestamo-entregar') {
                const campo=n=>form.querySelector('[name="'+n+'"]');
                const entregar=kind==='prestamo-entregar',cancelar=kind==='prestamo-cancelar';
                title.textContent=entregar?'Confirmar entrega del préstamo':cancelar?'Cancelar préstamo sin entregar':'Guardar borrador del préstamo';
                cancel.textContent='Cancelar';accept.textContent=entregar?'Confirmar entrega y egreso':cancelar?'Confirmar cancelación':'Confirmar y guardar borrador';
                add(cancelar?'Préstamo':'Empleado',form.dataset.confirmSubject);
                if(entregar){add('Préstamo',form.dataset.confirmPrestamo);add('Cuenta de origen',form.dataset.confirmAccount);add('Motivo financiero',form.dataset.confirmFinancial);add('Importe a entregar',money(form.dataset.confirmAmount));add('Saldo anterior',money(form.dataset.confirmBefore));add('Saldo posterior',money(form.dataset.confirmAfter));add('Referencia de entrega',campo('referencia').value);}
                else if(!cancelar){add('Deducción con saldo',campo('conceptoTexto').value);add('Importe autorizado',money(campo('importe').value));add('Parcialidad mensual',money(campo('parcialidad').value));add('Primer descuento',campo('primerDescuento').value.split('-').reverse().join('/'));}
                add('Motivo',campo('motivo').value.trim());
                note.textContent=entregar?'Se registra un único egreso interno por la entrega real, sin enviar una transferencia bancaria. El préstamo queda activo y conserva su deuda íntegra; preparar nómina no registra abonos. La entrega real no se cancela como borrador. Cancelar cierra sin entregar dinero.':cancelar?'Sólo se cancela el borrador sin entregar. Se conserva historial y no se mueve dinero. Cancelar cierra sin guardar.':'Préstamo sin intereses. Guardar crea o modifica únicamente el borrador: no entrega dinero, no activa una deducción fija ni reduce saldos. Cancelar conserva la captura sin guardar.';
            } else if(kind==='nomina-concepto-periodo'||kind==='nomina-concepto-periodo-cancelar') {
                const cancelar=kind==='nomina-concepto-periodo-cancelar',campo=n=>form.querySelector('[name="'+n+'"]');
                title.textContent=cancelar?'Cancelar concepto de un solo periodo':'Confirmar concepto de un solo periodo';cancel.textContent='Cancelar';accept.textContent=cancelar?'Confirmar cancelación':'Confirmar y guardar concepto';
                add(cancelar?'Concepto':'Empleado',form.dataset.confirmSubject);
                add('Periodo',cancelar?form.dataset.confirmPeriod:campo('periodoTexto').value);
                if(!cancelar){add('Concepto',campo('conceptoTexto').value);add('Tipo',form.dataset.confirmEffect);add('Cobertura',form.querySelector('[data-periodo-fechas]').value);}
                add('Importe',money(cancelar?form.dataset.confirmAmount:campo('importe').value));add('Motivo',campo('motivo').value.trim());
                note.textContent=(cancelar?'Se conserva el registro cancelado y su historial.':'Se propone sólo en el periodo indicado, no cada mes. Preparar o guardar el borrador no paga ni consume el concepto.')+' Las revisiones guardadas no se reescriben: actualiza el borrador antes de aplicar. No modifica una nómina aplicada ni mueve dinero. Cancelar cierra este resumen sin guardar.';
            } else if(kind==='configuracion-integrada') {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                const fecha=valor=>valor?valor.split('-').reverse().join('/'):'Sin fecha de fin';
                title.textContent='Confirmar configuración de nómina';cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar configuración';
                add('Empleado',form.dataset.confirmSubject);
                if(campo('cambiarSueldo').checked){
                    add('Puesto nuevo',campo('sueldo.puestoTexto').value);
                    add('Sueldo bruto — percepción base',money(campo('sueldo.importeBruto').value));
                    add('Base / frecuencia',campo('sueldo.base').selectedOptions[0].textContent+' / '+campo('sueldo.frecuencia').selectedOptions[0].textContent);
                    add('Vigencia del sueldo',fecha(campo('sueldo.fechaInicio').value)+' — '+fecha(campo('sueldo.fechaFin').value));
                }
                if(campo('cambiarCalendario').checked)add('Calendario actual nuevo',campo('calendario.calendarioTexto').value);
                form.querySelectorAll('[data-config-concepto]').forEach((fila,n)=>{
                    const c=sufijo=>fila.querySelector('[name$="'+sufijo+'"]');
                    add('Concepto '+(n+1),c('.conceptoTexto').value+' · '+fila.querySelector('[data-concept-effect]').value);
                    add('Importe / vigencia '+(n+1),money(c('.importe').value)+' · '+fecha(c('.inicio').value)+' — '+fecha(c('.fin').value));
                    if(c('.anteriorId').value)add('Cambio de asignación', '#'+c('.anteriorId').value+'; cerrará la vigencia previa');
                });
                add('Motivo',campo('motivo').value.trim());
                note.textContent='Se guardan juntos únicamente los cambios seleccionados y los conceptos añadidos. Se conservan las fuentes e historiales originales. No se recalculan nóminas guardadas ni se mueve dinero. Cancelar sólo cierra este resumen y conserva la captura.';
            } else if(kind==='calendario-empleado') {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                title.textContent='Confirmar calendario del empleado';cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar calendario';
                add('Empleado',form.dataset.confirmSubject);add('Calendario anterior',form.dataset.confirmBefore);
                add('Nuevo calendario',campo('calendarioTexto').value);add('Motivo',campo('motivo').value.trim());
                note.textContent='Se conserva el historial de este cambio. No modifica sueldos, conceptos, borradores ni cuentas financieras. Cancelar conserva la captura sin guardar.';
            } else if(kind==='nomina-aplicar') {
                title.textContent='Aplicar nómina y registrar egreso';cancel.textContent='Cancelar';accept.textContent='Confirmar aplicación y egreso';
                add('Nómina / periodo',(form.dataset.confirmSubject||'')+' · '+form.dataset.confirmPeriod);
                add('Empleados',form.dataset.confirmPeople);add('Cuenta de origen',form.dataset.confirmAccount);
                add('Total que saldrá',money(form.dataset.confirmAmount));add('Saldo disponible',money(form.dataset.confirmBefore));add('Saldo después',money(form.dataset.confirmAfter));
                note.textContent='Se registra un único egreso interno, se bloquea la revisión y se conserva el detalle por empleado. No se envía una transferencia bancaria. Cancelar no mueve dinero. La aplicación no puede deshacerse desde movimientos manuales.';
            } else if(kind==='nomina-preparacion-descartar') {
                title.textContent='Descartar vista previa';cancel.textContent='Cancelar';accept.textContent='Confirmar descarte';
                add('Periodo',form.dataset.confirmPeriod);
                note.textContent='Se descarta únicamente esta vista previa y su selección. No se cancelan borradores existentes, no se modifican configuraciones y no se mueve dinero.';
            } else if(kind==='nomina-borrador'||kind==='nomina-borrador-cancelar') {
                const cancelar=kind==='nomina-borrador-cancelar',actualizar=form.dataset.confirmUpdate==='true';
                title.textContent=cancelar?'Confirmar cancelación del borrador':actualizar?'Confirmar nueva revisión':'Confirmar borrador de nómina';
                cancel.textContent='Cancelar';accept.textContent=cancelar?'Confirmar cancelación':actualizar?'Confirmar actualización':'Confirmar y guardar';
                if(form.dataset.confirmSubject)add('Borrador',form.dataset.confirmSubject);
                add('Periodo',form.dataset.confirmPeriod);add('Empleados',form.dataset.confirmPeople);
                if(actualizar)add('Propuesto guardado anteriormente',money(form.dataset.confirmBefore));
                add(cancelar?'Propuesto histórico, no pagado':'Propuesto sin impuestos calculados',money(form.dataset.confirmAmount));
                add('Motivo',form.querySelector('[name="motivo"]')?.value||'');
                note.textContent=cancelar?'Cancela sólo el borrador y libera a sus empleados para otra preparación. Conserva todas las revisiones; no cancela pagos ni altera salarios o cuentas.':'Conserva el cálculo revisado del periodo como borrador, no como nómina fiscal ni pago autorizado. No calcula impuestos automáticamente ni ejecuta pagos o egresos. Las aportaciones de la escuela no restan al empleado. Cancelar conserva la captura sin guardar.';
            } else if(['nomina-configurar','nomina-finalizar','nomina-cancelar'].includes(kind)) {
                const value=n=>form.querySelector('[name="'+n+'"]')?.value?.trim()||'';
                const date=n=>value(n)?value(n).split('-').reverse().join('/'):'Sin fecha de fin';
                const configuring=kind==='nomina-configurar';
                title.textContent=configuring?(value('anteriorId')?'Confirmar nueva vigencia del concepto':'Confirmar concepto de nómina'):kind==='nomina-cancelar'?'Confirmar cancelación de programación':'Confirmar fin de vigencia';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar';
                if(configuring){add('Empleado',value('empleadoTexto'));add('Concepto',value('conceptoTexto'));add('Importe de referencia · '+(form.dataset.confirmCurrency||''),money(value('importe')));add('Efecto',form.dataset.confirmEffect||'');
                    add('Aplicación',value('modalidad')==='UNICA'?'Una sola vez':'Recurrente');add('Frecuencia',value('modalidad')==='UNICA'?'Importe íntegro para el periodo':form.querySelector('[name="frecuenciaImporte"]')?.selectedOptions?.[0]?.textContent||value('frecuenciaImporte'));
                    if(value('modalidad')==='UNICA')add('Periodo único',value('periodoTexto'));add('Aplicar desde',date('inicio'));add('Último día incluido',date('fin'));
                }else{add('Concepto',form.dataset.confirmSubject||'');if(kind==='nomina-finalizar')add('Último día que sí se aplica',date('fin'));}
                add('Motivo',value('motivo'));note.textContent='Sólo configuración: no calcula ni paga nómina y no mueve dinero. Conserva historial; una nueva vigencia no cambia importes anteriores ni nóminas guardadas. Cancelar cierra este resumen sin guardar.';
            } else if(kind==='nomina-calendario'||kind==='nomina-periodo'||kind==='nomina-periodo-cancelar') {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                const texto=name=>campo(name)?.value?.trim()||'';
                const fecha=name=>texto(name)?texto(name).split('-').reverse().join('/'):'Sin capturar';
                const opcion=name=>campo(name)?.selectedOptions?.[0]?.textContent||({MENSUAL:'Mensual',QUINCENAL:'Quincenal',SEMANAL:'Semanal'}[texto(name)]||texto(name));
                cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar';
                if(kind==='nomina-calendario'){
                    title.textContent='Confirmar calendario de nómina';add('Código',texto('codigo'));add('Nombre',texto('nombre'));add('Frecuencia',opcion('frecuencia'));add('Horas semanales',texto('horasSemanales')||'Sin registrar');add('Sueldo mensual',money(texto('sueldoMensual')));add('Descripción',texto('descripcion')||'Sin descripción');add('Estado',campo('activo').checked?'Activo':'Inactivo');
                    note.textContent='Guarda sólo el catálogo. Desactivar no cancela periodos existentes. No asigna empleados ni genera periodos, nóminas o movimientos de dinero. Cancelar conserva la captura sin guardar.';
                }else if(kind==='nomina-periodo'){
                    title.textContent='Confirmar periodo de nómina';add('Código',texto('codigo'));add('Días cubiertos',fecha('inicio')+' — '+fecha('fin'));add('Pago previsto',fecha('pago'));
                    note.textContent='Sólo programa fechas. No selecciona empleados ni calcula sueldos, impuestos o pagos. Los casos parciales tendrán revisión explícita al preparar nómina. Cancelar conserva la captura sin guardar.';
                }else{
                    title.textContent='Confirmar cancelación del periodo';accept.textContent='Confirmar cancelación';add('Periodo',form.dataset.confirmSubject);add('Calendario histórico',form.dataset.confirmCalendar);add('Días cubiertos',form.dataset.confirmStart+' — '+form.dataset.confirmEnd);add('Motivo',texto('motivo'));
                    note.textContent='Cancela únicamente esta programación y conserva el historial. No cancela pagos ni modifica salarios o cuentas. No se podrá editar o reactivar este periodo. Cancelar sólo cierra este resumen.';
                }
            } else if(kind.startsWith('concepto-empleado-')) {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                const texto=name=>campo(name)?.value?.trim()||'';
                const fecha=name=>texto(name)?texto(name).split('-').reverse().join('/'):'Sin fecha de fin';
                const asignar=kind==='concepto-empleado-asignar';
                const cancelar=kind==='concepto-empleado-cancelar';
                title.textContent=asignar?(texto('anteriorId')?'Confirmar cambio de importe':'Confirmar concepto por empleado'):(cancelar?'Confirmar cancelación futura':'Confirmar fin de vigencia');
                cancel.textContent='Cancelar';accept.textContent=asignar?'Confirmar y guardar':cancelar?'Confirmar cancelación':'Confirmar fin de vigencia';
                add('Empleado',asignar?texto('empleadoTexto'):form.dataset.confirmSubject);
                add('Concepto',asignar?texto('conceptoTexto'):form.dataset.confirmConcept);
                add('Importe fijo por nómina · '+form.dataset.confirmCurrency,money(asignar?texto('importe'):form.dataset.confirmAmount));
                if(asignar){add('Inicio',fecha('inicio'));add('Último día incluido',fecha('fin'));add('Efecto',form.dataset.confirmEffect||form.querySelector('[data-contexto-estado]')?.textContent||'Habitual');}
                else if(cancelar)add('Inicio programado',form.dataset.confirmStart);
                else add('Último día incluido',fecha('fin'));
                add('Motivo',texto('motivo'));
                note.textContent=(asignar?(texto('anteriorId')?'La vigencia anterior termina el día previo al nuevo inicio; conserva su importe histórico. ':'Se registra una asignación habitual con importe fijo, sin sobrescribir otras vigencias. '):cancelar?'Sólo se cancela una programación que aún no inicia. Se restaura el fin anterior cuando corresponde y se conserva el historial. ':'Termina la asignación en la fecha indicada, que sí queda incluida; no borra el historial. ')
                    +'No duplica sueldo base ni genera nómina, impuestos, pagos o movimientos financieros. Cancelar sólo cierra este resumen sin guardar.';
            } else if(kind==='concepto-nomina-guardar') {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                const texto=name=>campo(name)?.value?.trim()||'';
                const opcion=name=>campo(name)?.selectedOptions?.[0]?.textContent||'';
                title.textContent='Confirmar concepto de nómina';cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar';
                add('Código',texto('codigo'));add('Nombre',texto('nombre'));add('Tipo de concepto',opcion('naturaleza'));add('Forma de aplicación',opcion('aplicacion'));
                add('Efecto de referencia',campo('naturaleza').value==='PERCEPCION'?'Suma al empleado':campo('naturaleza').value==='DEDUCCION'?'Resta al empleado':'Costo de la escuela; no reduce el pago del empleado');
                add('Descripción',texto('descripcion')||'Sin descripción');add('Estado',campo('activo').checked?'Activo':'Inactivo');
                note.textContent='Sólo guarda el catálogo. No asigna importes a empleados, no genera nómina, retenciones, pagos ni descuentos. Cancelar conserva la captura sin guardar.';
            } else if(kind==='laboral-catalogo'||kind==='laboral-asignar') {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                const texto=name=>campo(name)?.value?.trim()||'';
                const opcion=name=>campo(name)?.selectedOptions?.[0]?.textContent||'';
                const fecha=name=>texto(name)?texto(name).split('-').reverse().join('/'):'Sin fecha de fin';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar';
                if(kind==='laboral-catalogo'){
                    title.textContent='Confirmar '+form.dataset.confirmSubject;
                    add('Código',texto('codigo'));add('Nombre',texto('nombre'));add('Estado',campo('activo').checked?'Activo':'Inactivo');
                    if(campo('horasSemanales'))add('Horas semanales',texto('horasSemanales'));
                    if(campo('puestoTexto')){add('Puesto',texto('puestoTexto'));add('Jornada',texto('jornadaTexto'));add('Sueldo bruto base',money(texto('importeBruto')));add('Base',opcion('base'));add('Frecuencia de pago',opcion('frecuencia'));}
                    add('Descripción / distribución',texto('descripcion')||'Sin descripción');
                    note.textContent='Guarda el catálogo reutilizable. No cambia las asignaciones existentes ni genera nómina o movimientos de dinero. Cancelar conserva la captura sin guardar.';
                }else{
                    title.textContent='Confirmar asignación laboral';add('Empleado',form.dataset.confirmSubject);add('Configuración de puesto y sueldo',form.dataset.confirmScheme);add('Configuración autorizada',form.dataset.confirmSummary);add('Inicio',fecha('inicio'));add('Fin previsto',fecha('fin'));add('Motivo',texto('motivo'));
                    note.textContent='Se conserva una copia histórica del puesto, jornada y sueldo. Si se cruza con una condición anterior, ésta termina el día previo; conserva su historial. No modifica planteles, clases ni cuentas financieras. Cancelar no registra la asignación.';
                }
            } else if(kind==='salarial-guardar'||kind==='salarial-cancelar'||kind==='puesto-guardar') {
                const campo=name=>form.querySelector('[name="'+name+'"]');
                const texto=name=>campo(name)?.value?.trim()||'';
                const opcion=name=>campo(name)?.selectedOptions?.[0]?.textContent||'';
                const fecha=name=>texto(name)?texto(name).split('-').reverse().join('/'):'Sin fecha de fin';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar';
                if(kind==='puesto-guardar'){
                    title.textContent='Confirmar puesto laboral';add('Código',texto('codigo'));add('Nombre',texto('nombre'));
                    add('Descripción',texto('descripcion')||'Sin descripción');add('Estado',campo('activo').checked?'Activo':'Inactivo');
                    note.textContent='Se guarda el catálogo de tu institución. Las condiciones anteriores conservan su puesto histórico; desactivar impide nuevas asignaciones. No genera nómina ni modifica datos laborales previos. Cancelar conserva la captura sin guardar.';
                }else if(kind==='salarial-guardar'){
                    title.textContent='Confirmar condición salarial';add('Empleado',form.dataset.confirmSubject);add('Puesto',texto('puestoTexto'));
                    add('Sueldo bruto base',money(texto('importeBruto')));add('Base',opcion('base'));add('Frecuencia de pago',opcion('frecuencia'));
                    add('Inicio de vigencia',fecha('fechaInicio'));add('Fin previsto',fecha('fechaFin'));add('Motivo',texto('motivo'));
                    note.textContent='Si se cruza con la condición anterior, ésta termina el día previo al inicio indicado y conserva su sueldo histórico. No se permiten cambios retroactivos o intercalados. No se calculan impuestos ni se genera pago alguno. Cancelar conserva la captura sin registrar.';
                }else{
                    title.textContent='Confirmar cancelación de programación';accept.textContent='Confirmar cancelación';
                    add('Empleado',form.dataset.confirmSubject);add('Inicio programado',form.dataset.confirmStart);add('Sueldo bruto base',money(form.dataset.confirmAmount));add('Motivo',texto('motivo'));
                    note.textContent='Sólo puede cancelarse la última programación que aún no inicia. Se conserva el historial y se restaura la vigencia anterior cuando corresponde. No hay egresos ni devoluciones. Cancelar sólo cierra este resumen sin ejecutar cambios.';
                }
            } else if(kind==='puesto-empleado') {
                const valor=n=>form.querySelector('[name="'+n+'"]')?.value||'';
                title.textContent='Confirmar asignación de puesto';cancel.textContent='Cancelar';accept.textContent='Confirmar y asignar';
                add('Empleado',form.dataset.confirmSubject);add('Puesto',valor('puestoTexto'));
                add('Horas semanales',form.querySelector('[data-puesto-horas]')?.value||'');
                add('Sueldo mensual',form.querySelector('[data-puesto-sueldo]')?.value||'');
                add('Aplicar desde',valor('inicio').split('-').reverse().join('/'));add('Motivo',valor('motivo'));
                note.textContent='Se registra una sola fuente salarial. La asignación anterior termina el día previo sin perder su historial. No se calcula nómina ni se mueve dinero. Cancelar conserva la captura sin guardar.';
            } else if(kind.startsWith('empleado-')) {
                const titles={'empleado-registro':'Confirmar datos personales','empleado-laboral':'Confirmar información laboral','empleado-plantel':'Confirmar asignación de plantel','empleado-perfil':'Confirmar actividad docente','empleado-vigencia':'Confirmar fin de vigencia'};
                title.textContent=titles[kind]||'Confirmar operación de personal';cancel.textContent='Cancelar';accept.textContent='Confirmar y guardar';
                const labels={codigo:'Código laboral',nombres:'Nombre(s)',primerApellido:'Primer apellido',segundoApellido:'Segundo apellido',email:'Correo de contacto',telefono:'Teléfono',situacion:'Situación laboral',puesto:'Puesto',fechaIngreso:'Fecha de ingreso',fechaBaja:'Fecha de baja',motivoBaja:'Motivo de baja',plantelTexto:'Plantel',fechaInicio:'Inicio de vigencia',fechaFin:'Fin previsto',principal:'Plantel principal',codigoDocente:'Código docente',activo:'Perfil docente activo',fecha:'Fin de vigencia'};
                Object.entries(labels).forEach(([name,label])=>{const input=form.querySelector('[name="'+name+'"]');if(!input)return;let v=input.type==='checkbox'?(input.checked?'Sí':'No'):input.value.trim();if(input.selectedOptions?.length)v=input.selectedOptions[0].textContent;if(name==='activo')v=v==='true'?'Sí':'No';if(input.type==='date'&&v)v=v.split('-').reverse().join('/');add(label,v||'No capturado');});
                note.textContent=kind==='empleado-laboral'?'Guarda situación y motivo en el historial privado. No desactiva cuentas, perfil docente ni grupos y no calcula nómina.':kind==='empleado-perfil'?'Cambia sólo el perfil docente. No crea, activa ni desactiva cuentas; las dependencias académicas vigentes se revisan antes de deshabilitar.':kind==='empleado-plantel'||kind==='empleado-vigencia'?'Guarda la vigencia laboral sin conceder permisos, modificar grupos ni crear otro sueldo.':'Guarda los datos del expediente sin crear una cuenta ni reactivar funciones docentes. Cancelar conserva la captura sin guardar.';
            } else if(kind==='apertura-caja') {
                title.textContent='Confirmar apertura de corte de caja';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y abrir corte';
                const valor=name=>form.querySelector('[name="'+name+'"]').value.trim();
                add('Institución',form.querySelector('[name="institucionId"]').selectedOptions[0]?.textContent.trim()||'Sin seleccionar');
                add('Caja',valor('cuentaId')?valor('cuentaTexto'):'Sin seleccionar');
                add('Observaciones de apertura',valor('observaciones')||'Sin observaciones');
                note.textContent='Se abrirá un corte con el saldo y último folio vigentes de esta caja. Sólo puede existir un corte abierto por caja. No registra ingresos ni egresos. Cancelar conserva tu captura sin abrir el corte.';
            } else if(kind==='cierre-caja') {
                title.textContent='Confirmar cierre de caja';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y cerrar caja';
                const valor=name=>form.querySelector('[name="'+name+'"]').value.trim();
                add('Corte', '#'+form.dataset.confirmCorte);
                add('Caja',form.dataset.confirmAccount);add('Plantel',form.dataset.confirmPlantel);
                add('Efectivo declarado',money(valor('efectivoDeclarado')));
                add('Justificación de diferencia',valor('justificacionDiferencia')||'Sin justificación capturada');
                add('Observaciones de cierre',valor('observaciones')||'Sin observaciones');
                note.textContent='Al confirmar se conciliará el efectivo declarado y se intentará cerrar este corte. El saldo esperado y la diferencia se revelarán al procesar el cierre; si existe diferencia, será obligatoria una justificación. Este cierre no registra ingresos ni egresos. Cancelar conserva tu captura y mantiene el corte abierto.';
            } else if(kind==='movimiento-manual') {
                title.textContent='Confirmar movimiento financiero';cancel.textContent='Cancelar';accept.textContent='Confirmar y registrar movimiento';
                const valor=name=>form.querySelector('[name="'+name+'"]').value.trim();
                const tipo=valor('direccion');
                add('Institución',form.querySelector('[name="institucionId"]').selectedOptions[0]?.textContent.trim()||'Sin seleccionar');
                add('Cuenta financiera',valor('cuentaTexto'));add('Tipo',tipo==='INGRESO'?'Ingreso':'Egreso');
                add('Motivo',valor('motivoTexto'));add('Monto',money(valor('monto')));
                const [dia,hora]=valor('fechaOperacion').split('T');
                add('Fecha y hora',dia.split('-').reverse().join('/')+' · '+hora);
                add('Concepto',valor('concepto'));add('Referencia',valor('referencia')||'Sin referencia');
                add('Tercero',valor('terceroNombre')||'Sin tercero');
                note.textContent=tipo==='INGRESO'?'Se sumará este importe a la cuenta. Cancelar conserva la captura sin registrar.':'Se descontará este importe de la cuenta. Cancelar conserva la captura sin registrar.';
            } else if(kind==='transferencia-cuentas') {
                title.textContent='Confirmar transferencia entre cuentas';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y aplicar transferencia';
                const valor=name=>form.querySelector('[name="'+name+'"]').value.trim();
                add('Institución',form.querySelector('[name="institucionId"]').selectedOptions[0]?.textContent.trim()||'Sin seleccionar');
                add('Cuenta de origen · salida',valor('cuentaOrigenId')?valor('cuentaOrigenTexto'):'Sin seleccionar');
                add('Cuenta de destino · entrada',valor('cuentaDestinoId')?valor('cuentaDestinoTexto'):'Sin seleccionar');
                add('Importe a transferir',money(valor('monto')));
                const [dia,hora]=valor('fecha').split('T');
                add('Fecha y hora',dia.split('-').reverse().join('/')+' · '+hora);
                add('Referencia',valor('referencia')||'Sin referencia');
                add('Observaciones',valor('observaciones')||'Sin observaciones');
                note.textContent='Se descontará este importe de la cuenta de origen y se sumará a la de destino, registrando ambas operaciones juntas. Es un traspaso interno, no un ingreso ni gasto operativo consolidado, y no envía dinero al banco. Cancelar conserva tu captura sin aplicar la transferencia.';
            } else if(kind==='reversion-financiera') {
                const transferencia=form.dataset.confirmTransfer==='true';
                title.textContent=transferencia?'Confirmar reversa de transferencia':'Confirmar reversa de operación';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y registrar reversa';
                add(transferencia?'Transferencia original':'Movimiento original','#'+form.dataset.confirmId);
                add('Operación',form.dataset.confirmTitle);
                add(transferencia?'Recorrido original · origen → destino':'Cuenta y dirección original',form.dataset.confirmDetail);
                add('Importe que se compensará',money(form.dataset.confirmAmount));
                add('Fecha original',form.dataset.confirmOriginalDate);
                const [dia,hora]=form.querySelector('[name="fecha"]').value.trim().split('T');
                add('Fecha y hora de reversa',dia.split('-').reverse().join('/')+' · '+hora);
                add('Motivo de la corrección',form.querySelector('[name="motivo"]').value.trim());
                add('Resultado',transferencia?'Dos movimientos inversos: ingreso al origen y egreso del destino':'Un movimiento opuesto al original');
                note.textContent=(transferencia?'Se devolverá el importe de la cuenta destino a la de origen mediante dos movimientos compensatorios registrados juntos. El destino debe conservar fondos suficientes. ':'Se registrará un movimiento compensatorio en la misma cuenta. ')
                    +'La reversa modifica saldos internos, no envía dinero al banco ni borra el historial. Cancelar o pulsar Escape sólo cierra este resumen y conserva tu captura, sin registrar la reversa.';
            } else if(kind==='compra-cancelacion') {
                title.textContent='Confirmar cancelación de compra';
                cancel.textContent='Volver sin cancelar';accept.textContent='Confirmar cancelación';
                add('Folio de compra',form.dataset.confirmFolio);
                add('Proveedor',form.dataset.confirmSupplier);add('Cuenta que recibirá el reintegro',form.dataset.confirmAccount);
                add('Plantel',form.dataset.confirmPlantel);
                add('Factura, ticket o referencia',form.dataset.confirmReference||'Sin referencia');
                if(form.dataset.confirmMovement)add('Movimiento original que se conservará','#'+form.dataset.confirmMovement);
                add('Importe a reintegrar a la cuenta',money(form.dataset.confirmAmount));
                add('Motivo de cancelación',form.querySelector('[name="motivo"]').value.trim());
                note.textContent='Al confirmar se cancelará esta compra mediante un ingreso compensatorio en la misma cuenta. El egreso original y el motivo quedarán en el historial; no se borran. Volver sin cancelar o pulsar Escape sólo cierra este resumen y conserva tu motivo, sin registrar la reversa.';
            } else if(kind==='compra-egreso') {
                title.textContent='Confirmar y registrar egreso';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y registrar egreso';
                add('Folio de compra',form.dataset.confirmFolio);
                add('Proveedor',form.dataset.confirmSupplier);add('Cuenta de donde saldrá el dinero',form.dataset.confirmAccount);
                add('Plantel',form.dataset.confirmPlantel);add('Categoría del egreso',form.dataset.confirmCategory);
                add('Factura, ticket o referencia',form.dataset.confirmReference||'Sin referencia');
                document.getElementById(form.dataset.confirmLines)?.querySelectorAll('article').forEach((row,i)=>{
                    add('Partida '+(i+1),row.querySelector('div strong').textContent+'\n'+row.querySelector('small').textContent+'\nTotal: '+row.querySelector('article > span').textContent);
                });
                add('Subtotal',money(form.dataset.confirmSubtotal));add('Impuestos',money(form.dataset.confirmTax));
                add('Total que se descontará',money(form.dataset.confirmAmount));
                note.textContent='Al confirmar se registrará el egreso y se descontará este total de la cuenta indicada. La compra dejará de ser editable como borrador. Cancelar sólo cierra este resumen: no registra el egreso ni modifica el borrador.';
            } else if(kind==='proveedor' || kind==='compra-borrador') {
                cancel.textContent='Cancelar';accept.textContent=kind==='proveedor'?'Confirmar y guardar proveedor':'Confirmar y guardar borrador';
                const texto=name=>form.querySelector('[name="'+name+'"]').value.trim();
                const opcion=name=>form.querySelector('select[name="'+name+'"]').selectedOptions[0]?.textContent.trim()||'Sin seleccionar';
                title.textContent=kind==='proveedor'?'Confirmar datos del proveedor':'Confirmar borrador de compra';
                add('Institución',opcion('institucionId'));
                if(kind==='proveedor') {
                    for(const [name,label] of [['razonSocial','Razón social'],['nombreComercial','Nombre comercial'],['rfc','RFC'],['contactoNombre','Persona de contacto'],['telefono','Teléfono'],['correo','Correo'],['direccion','Dirección'],['notas','Notas']])add(label,texto(name)||'Sin capturar');
                    add('Proveedor activo',form.querySelector('input[type="checkbox"][name="activo"]').checked?'Sí':'No');
                    note.textContent='Se guardarán los datos del proveedor. No registra compras ni movimientos financieros. Cancelar conserva tu captura sin guardar.';
                } else {
                    add('Plantel',opcion('plantelId'));add('Proveedor',texto('proveedorTexto'));
                    add('Cuenta financiera',texto('cuentaTexto'));add('Categoría del egreso',texto('motivoTexto'));
                    const [dia,hora]=texto('fechaOperacion').split('T');
                    add('Fecha y hora',dia.split('-').reverse().join('/')+' · '+hora);
                    add('Factura, ticket o referencia',texto('documentoReferencia')||'Sin referencia');
                    add('Observaciones',texto('observaciones')||'Sin observaciones');
                    form.querySelectorAll('[data-lines] .line-item').forEach((row,i)=>{
                        add('Partida '+(i+1),row.querySelector('.line-description input').value+'\nCantidad: '+row.querySelector('[data-quantity]').value+' · Precio unitario: '+money(row.querySelector('[data-price]').value)+' · Impuesto: '+row.querySelector('[data-tax]').value+' %\nTotal: '+row.querySelector('[data-line-total]').textContent);
                    });
                    for(const [key,label] of [['subtotal','Subtotal'],['taxes','Impuestos'],['grand-total','Total de compra']])add(label,form.querySelector('[data-'+key+']').textContent);
                    note.textContent='Sólo se guardará el borrador. No se descontará dinero ni se registrará un egreso hasta confirmar desde el detalle de la compra. Cancelar conserva tu captura sin guardar.';
                }
            } else if(kind==='concepto-cobro') {
                title.textContent='Confirmar creación del concepto de cobro';
                cancel.textContent='Cancelar';accept.textContent='Confirmar y crear concepto';
                const texto=name=>form.querySelector('[name="'+name+'"]').value.trim();
                const opcion=name=>form.querySelector('[name="'+name+'"]').selectedOptions[0]?.textContent.trim() || 'Sin seleccionar';
                add('Institución',opcion('institucionId'));add('Código',texto('codigo'));
                add('Nombre',texto('nombre'));add('Categoría',opcion('categoria'));
                add('Descripción',texto('descripcion')||'Sin descripción');
                for(const [name,label] of [['permiteBeca','Permite beca'],['permiteDescuento','Permite descuento'],['permiteRecargo','Permite recargo'],['activo','Concepto activo']])
                    add(label,form.querySelector('input[type="checkbox"][name="'+name+'"]').checked?'Sí':'No');
                note.textContent='Se creará únicamente el concepto. No se generan adeudos, pagos ni ajustes automáticos; el importe se configura después en Cuotas por alumno. Cancelar conserva tu captura sin guardar.';
            } else if(kind==='anticipo-propuesta'||kind==='anticipo-pago'||kind==='anticipo-cancelar') {
                title.textContent=kind==='anticipo-propuesta'?'Confirmar propuesta por pago anticipado':kind==='anticipo-pago'?'Confirmar registro del pago completo':'Confirmar cancelación de la propuesta';
                cancel.textContent='Cancelar';accept.textContent=kind==='anticipo-propuesta'?'Confirmar propuesta':kind==='anticipo-pago'?'Confirmar y registrar pendiente':'Confirmar cancelación';
                if(kind==='anticipo-propuesta') {
                    form.querySelectorAll('.anticipation-preview tbody tr').forEach((row,i)=>{const cells=[...row.querySelectorAll('td')].map(c=>c.textContent.trim());add('Mensualidad '+(i+1),cells[0]+'\nOriginal: '+cells[1]+' · Beca previa: '+cells[2]+' · Otros ajustes: '+cells[3]+'\nBase: '+cells[4]+' · Beneficio: '+cells[5]+' · Pago requerido: '+cells[6]);});
                    const benefit=form.querySelector('[name=tipoBeneficio]').value;
                    add('Beneficio autorizado',benefit==='MENSUALIDAD'?'Una mensualidad bonificada':benefit==='PORCENTAJE'?form.querySelector('[name=valor]').value+' % adicional':money(form.querySelector('[name=valor]').value)+' de cantidad fija');
                    add('Política de beca',form.querySelector('[name=politicaBeca]').value==='CONSERVAR'?'Conservar beca y agregar beneficio':'Sustituir beca sólo en estas mensualidades');
                    add('Fecha límite',form.querySelector('[name=fechaLimite]').value);add('Motivo y condiciones',form.querySelector('[name=motivo]').value);
                }else {add('Acuerdo',form.dataset.confirmFolio);add('Familia',form.dataset.confirmSubject);}
                if(kind==='anticipo-cancelar')add('Motivo',form.querySelector('[name=motivo]').value);
                else add('Pago completo requerido',money(form.dataset.confirmAmount));
                if(kind==='anticipo-pago'){add('Método',form.querySelector('[name=metodo]').value);add('Cuenta',form.querySelector('#anticipo-cuenta').value);add('Fecha y hora',form.querySelector('[name=fecha]').value);add('Referencia',form.querySelector('[name=referencia]').value);}
                note.textContent=kind==='anticipo-propuesta'?'Sólo guarda la propuesta. No modifica becas, adeudos ni banco; el beneficio exige validar el pago completo.':kind==='anticipo-pago'?'Registra un único pago pendiente por el importe acordado. Los descuentos se aplicarán al validar, no al adjuntar un comprobante.':'Cancela sólo esta propuesta. No mueve dinero ni cambia las becas anteriores.';
            } else if (kind === 'saldo-favor' || kind === 'saldo-favor-reversion') {
                title.textContent = kind === 'saldo-favor' ? 'Confirmar aplicación del saldo a favor' : 'Confirmar reversión de saldo a favor';
                cancel.textContent = 'Cancelar'; accept.textContent = 'Confirmar operación';
                add('Pago de origen',form.dataset.confirmFolio);add('Titular o alumno',form.dataset.confirmSubject);
                if(kind === 'saldo-favor') {
                    add('Disponible de este pago',money(form.dataset.available));
                    form.querySelectorAll('.saldo-row').forEach(row=>add(row.querySelector('.credit-title').textContent,money(row.querySelector('.credit-amount').value)));
                    add('Total a aplicar',money(form.dataset.confirmAmount));
                    add('Seguirá a favor del tutor',document.querySelector('#credit-remaining').textContent);
                } else add('Importe que vuelve al saldo a favor',money(form.dataset.confirmAmount));
                add('Motivo',form.querySelector('[name="motivo"]').value);
                note.textContent = kind === 'saldo-favor' ? 'Reduce los adeudos seleccionados usando dinero ya recibido. No registra otro pago ni movimiento bancario.' : 'Libera este abono y recupera la deuda del cargo. No devuelve dinero ni mueve el banco.';
            } else if (kind === 'transferencia-familiar') {
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
            } else if (kind === 'cancelacion-convenio') {
                title.textContent = 'Confirmar cancelación del convenio';
                accept.textContent = 'Confirmar cancelación del convenio';
                add('Folio del convenio', form.dataset.confirmFolio);
                add('Tutor responsable', form.dataset.confirmSubject);
                form.querySelectorAll('[data-convenio-reactivar] span').forEach(row => {
                    add('Adeudo original que se reactivará', row.dataset.label + ' · ' + money(row.dataset.amount));
                });
                add('Total original que volverá a ser exigible', money(form.dataset.confirmOriginal));
                form.querySelectorAll('[data-convenio-cancelar] span').forEach(row => {
                    add('Adeudo nuevo que se cancelará', row.dataset.label + ' · ' + money(row.dataset.amount));
                });
                if (form.dataset.confirmTotal === 'true') add('Cargos nuevos', 'No existen: este convenio condonó el saldo sin generar cargos');
                else add('Total de nuevos adeudos que se cancelarán', money(form.dataset.confirmAmount));
                add('Condonación del acuerdo que dejará de aplicar', money(form.dataset.confirmCondoned));
                add('Motivo de cancelación', form.querySelector('[name="motivo"]').value);
                add('Efecto en caja y bancos', 'Ninguno · no registra ingresos, egresos ni devoluciones');
                note.textContent = 'Se cancelará el convenio y sus cargos nuevos; los adeudos originales volverán a ser exigibles. El historial y el motivo se conservan. El servidor comprobará que no haya pagos aplicados y que los cargos sigan vigentes. Cancelar cierra esta ventana sin ejecutar y conserva el motivo.';
                if (form.dataset.confirmTotal === 'true') note.textContent = 'Se anulará la condonación y volverán a ser exigibles los saldos originales condonados, conservando los abonos anteriores. No mueve dinero. El servidor comprobará que los originales no hayan cambiado. Cancelar cierra sin ejecutar y conserva el motivo.';
            } else if (kind === 'convenio') {
                const condona = form.querySelector('[name="modalidad"]')?.value === 'CONDONACION_TOTAL';
                title.textContent = condona ? 'Confirmar condonación total' : 'Confirmar convenio de pago';
                accept.textContent = condona ? 'Confirmar y condonar saldo' : 'Confirmar y crear convenio';
                add('Modalidad', condona ? 'Condonación total del saldo pendiente' : 'Convenio con monto por pagar');
                const value = name => form.querySelector('[name="' + name + '"]')?.value || '';
                const date = name => value(name).split('-').reverse().join('/');
                add('Tutor responsable', form.querySelector('#tutor-busqueda')?.value || '');
                add('Concepto de cobro del convenio', form.querySelector('#concepto-busqueda')?.value || '');
                form.querySelectorAll('#cargos-seleccionados article').forEach((row, index) => {
                    add('Adeudo original ' + (index + 1), row.querySelector('strong').textContent + ' · ' + row.querySelector('span').textContent);
                });
                add('Saldo pendiente seleccionado', form.querySelector('#saldo-original')?.textContent || '');
                add('Nuevo monto total', money(value('montoAcordado')));
                add('Monto condonado estimado', form.querySelector('#monto-condonado')?.textContent || '');
                add('Fecha del acuerdo', date('fechaAcuerdo'));
                add(condona ? 'Fecha de efecto' : 'Nueva fecha límite', date('fechaVencimiento'));
                add('Descripción', value('descripcion'));
                add('Motivo', value('motivo'));
                if (value('condiciones')) add('Condiciones', value('condiciones'));
                note.textContent = 'Los adeudos originales dejarán de ser exigibles sin borrarse y el nuevo monto se distribuirá proporcionalmente por alumno. No mueve dinero. Cancelar conserva la captura sin crear el convenio; el servidor volverá a comprobar los saldos.';
                if (condona) note.textContent = 'Se condonará todo el saldo pendiente seleccionado. No se crearán cargos nuevos, pagos ni movimientos bancarios; se conservarán los abonos previos y el historial. Quedará registrado quién autorizó el acuerdo, cuándo y por qué. Cancelar conserva la captura sin condonar.';
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
                if(form.querySelector('#received-change-value')?.value === 'true') {
                    add('Importe originalmente reportado',money(form.querySelector('.received-amount-review').dataset.reported));
                    add('Motivo del importe real',form.querySelector('#received-reason').value);
                    add('Saldo a favor estimado',document.querySelector('#received-credit').textContent);
                }
                add('Cuenta destino', form.querySelector('#cuentaDestinoBusqueda').value);
                const reason = form.querySelector('[name="motivoCambioCuenta"]')?.value;
                if (reason) add('Motivo del cambio de cuenta', reason);
                note.textContent = 'Confirma que recibiste el dinero. Se publicará el ingreso y se aplicarán los abonos del pago.';
            }
            pending = form; submitter = event.submitter;
            accept.disabled = false;
            document.body.classList.add('money-confirm-open'); dialog.showModal(); cancel.focus();
            if(['transferencia-cuentas','reversion-financiera','pendiente-guardar','pendiente-estado','salarial-guardar','salarial-cancelar','puesto-guardar'].includes(kind))dialog.scrollTop=0;
        });
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start);
    else start();
})();
