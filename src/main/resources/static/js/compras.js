/* Redondeo por partida igual al servidor: subtotal y después impuesto a centavos. */
function calcularPartidaCompra(cantidad, precio, tasa) {
    const entero = (valor, escala) => {
        const [i, d = ''] = String(valor || '0').split('.');
        if (!/^\d+$/.test(i || '0') || !/^\d*$/.test(d) || d.length > escala) return 0n;
        return BigInt((i || '0') + d.padEnd(escala, '0'));
    };
    const subtotal = (entero(cantidad, 3) * entero(precio, 2) + 500n) / 1000n;
    const impuesto = (subtotal * entero(tasa, 2) + 5000n) / 10000n;
    return {subtotal, impuesto, total: subtotal + impuesto};
}
if (typeof module !== 'undefined') module.exports = {calcularPartidaCompra};
if (typeof document !== 'undefined') document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('[data-purchase-form]');
    if (!form) return;
    const lines = form.querySelector('[data-lines]'), tpl = document.querySelector('[data-line-template]');
    const inst = form.querySelector('[data-purchase-institution]'), plantel = form.querySelector('[data-purchase-plantel]');
    let mostrandoError = false;
    form.addEventListener('invalid', event => {
        if (mostrandoError) return;
        mostrandoError = true;
        const panel = event.target.closest('[data-student-panel]');
        if (panel?.hidden) form.querySelector('[data-student-tab="' + panel.dataset.studentPanel + '"]')?.click();
        setTimeout(() => { mostrandoError = false; }, 0);
    }, true);
    const money = cents => '$' + new Intl.NumberFormat('es-MX').format(cents / 100n) + '.' + String(cents % 100n).padStart(2, '0');
    function renumber() {
        [...lines.querySelectorAll('.line-item')].forEach((row, i) => {
            row.querySelector('.line-number').textContent = i + 1;
            row.querySelectorAll('[name]').forEach(el => el.name = el.name.replace(/partidas\[\d+\]/, 'partidas[' + i + ']'));
        });
    }
    function calculate() {
        let subtotal = 0n, taxes = 0n;
        lines.querySelectorAll('.line-item').forEach(row => {
            const x = calcularPartidaCompra(row.querySelector('[data-quantity]').value, row.querySelector('[data-price]').value, row.querySelector('[data-tax]').value);
            subtotal += x.subtotal; taxes += x.impuesto;
            row.querySelector('[data-line-total]').textContent = money(x.total);
        });
        form.querySelector('[data-subtotal]').textContent = money(subtotal);
        form.querySelector('[data-taxes]').textContent = money(taxes);
        form.querySelector('[data-grand-total]').textContent = money(subtotal + taxes);
    }
    function bind(row) {
        row.querySelectorAll('input').forEach(x => x.addEventListener('input', calculate));
        for (const selector of ['[data-quantity]', '[data-tax]']) {
            const input = row.querySelector(selector);
            const validate = () => input.setCustomValidity(/[eE]/.test(input.value)
                ? 'Captura el número directamente, sin notación científica.' : '');
            input.addEventListener('input', validate); validate();
        }
        row.querySelector('[data-remove-line]').addEventListener('click', () => {
            if (lines.children.length === 1) return;
            row.remove(); renumber(); calculate();
        });
    }
    function filterOptions() {
        let changed = false;
        plantel.querySelectorAll('option[data-institution]').forEach(o => {
            o.hidden = o.dataset.institution !== inst.value; o.disabled = o.hidden;
            if (o.selected && o.disabled) {plantel.value = ''; changed = true;}
        });
        if (changed) plantel.dispatchEvent(new Event('change', {bubbles: true}));
    }
    for (const [id, text] of [['proveedorId', 'proveedorTexto'], ['cuentaId', 'cuentaTexto'], ['motivoFinancieroId', 'motivoTexto']]) {
        const value = document.getElementById(id), input = document.getElementById(text);
        const validate = () => input.setCustomValidity(value.value && input.value ? '' : 'Selecciona una opción de la lista de resultados.');
        value.addEventListener('change', validate); input.addEventListener('input', validate); validate();
    }
    lines.querySelectorAll('.line-item').forEach(bind);
    form.querySelector('[data-add-line]').addEventListener('click', () => {
        if (lines.children.length >= 100) return;
        const box = document.createElement('div');
        box.innerHTML = tpl.innerHTML.replaceAll('__INDEX__', lines.children.length).trim();
        const row = box.firstElementChild;
        lines.append(row); bind(row); renumber(); calculate(); row.querySelector('input').focus();
    });
    inst?.addEventListener('change', filterOptions);
    form.addEventListener('submit', calculate);
    filterOptions(); calculate();
});
