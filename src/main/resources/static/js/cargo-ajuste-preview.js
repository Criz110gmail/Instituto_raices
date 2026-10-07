function calcularVistaAjusteCargo(total, pagado, tipo, efecto, monto) {
    const cents = value => {
        const s = String(value ?? '');
        if (!/^\d+(?:\.\d{1,2})?$/.test(s)) return null;
        const [i,f=''] = s.split('.'); return BigInt(i + f.padEnd(2,'0'));
    };
    const current = cents(total), paid = cents(pagado), amount = cents(monto);
    if (current === null || paid === null) return {valid:false,message:'No se pudo calcular el saldo. Recarga el detalle antes de ajustar.'};
    if (!['DESCUENTO','RECARGO','CORRECCION'].includes(tipo) || amount === null || amount <= 0n)
        return {valid:false,current,paid,message:'Captura un importe válido para ver cómo quedaría el cargo.'};
    const direction = tipo === 'DESCUENTO' ? 'DISMINUCION' : tipo === 'RECARGO' ? 'AUMENTO' : efecto;
    if (!['DISMINUCION','AUMENTO'].includes(direction)) return {valid:false,current,paid,message:'Selecciona el efecto del ajuste.'};
    const next = direction === 'DISMINUCION' ? current-amount : current+amount;
    if (next < 0n) return {valid:false,current,paid,message:'El ajuste no puede dejar el total del cargo por debajo de cero.'};
    if (next < paid) return {valid:false,current,paid,message:'El total no puede ser menor que lo ya pagado. Revisa el importe o la devolución antes de descontar.'};
    return {valid:true,current,paid,amount,direction,next,balance:next-paid,
        message:'Sólo se modificará la deuda. No se registra dinero recibido ni devuelto.'};
}
if (typeof module !== 'undefined') module.exports = {calcularVistaAjusteCargo};
if (typeof document !== 'undefined') {
    const start = () => {
        const form = document.querySelector('[data-charge-adjustment]'); if (!form) return;
        const type = form.querySelector('[name="tipo"]'), effect = form.querySelector('[name="efecto"]'), amount = form.querySelector('[name="monto"]');
        const submit = form.querySelector('button[type="submit"]');
        const raw = cents => { const n=cents < 0n ? -cents : cents; return (n/100n).toString()+'.'+(n%100n).toString().padStart(2,'0'); };
        const money = cents => cents == null ? '—' : MoneyValues.formatMoney(raw(cents),form.dataset.chargeCurrency || 'MXN');
        function update() {
            const forced=type.value==='DESCUENTO'?'DISMINUCION':type.value==='RECARGO'?'AUMENTO':null;
            if(forced)effect.value=forced;
            [...effect.options].forEach(option=>option.disabled=Boolean(forced)&&option.value!==forced);
            const v = calcularVistaAjusteCargo(form.dataset.chargeTotal,form.dataset.chargePaid,type.value,effect.value,amount.value);
            form.querySelector('#charge-preview-current').textContent=money(v.current);
            form.querySelector('#charge-preview-paid').textContent=money(v.paid);
            form.querySelector('#charge-preview-change').textContent=v.valid ? (v.direction==='DISMINUCION'?'− ':'+ ')+money(v.amount) : '—';
            form.querySelector('#charge-preview-total').textContent=v.valid?money(v.next):'—';
            form.querySelector('#charge-preview-balance').textContent=v.valid?money(v.balance):'—';
            form.querySelector('#charge-preview-warning').textContent=v.message;
            form.querySelector('.charge-adjustment-preview').dataset.valid=String(v.valid);
            submit.disabled=!v.valid;
            return v.valid;
        }
        type.addEventListener('change',update);effect.addEventListener('change',update);
        amount.addEventListener('input',update);amount.addEventListener('change',update);
        form.addEventListener('submit',event=>{if(!update())event.preventDefault();});
        update();
    };
    if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',start);else start();
}
