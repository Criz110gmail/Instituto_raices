const iniciarEjemploRecargos = () => {
    const modalidad = document.querySelector('#modalidad') || document.querySelector('[name="modalidad"]'),
        limite = document.querySelector('#tipoLimite') || document.querySelector('[name="tipoLimite"]');
    if (!modalidad || !limite) return;
    const campo = id => document.getElementById(id) || document.querySelector('[name="' + id + '"]');
    const dinero = new Intl.NumberFormat('es-MX', {style:'currency', currency:'MXN'});
    const texto = (id, valor) => { if (campo(id)) campo(id).textContent = valor; };
    const numero = id => { const valor = campo(id)?.value; return valor == null || valor.trim() === '' ? NaN : Number(valor); };
    const centavos = valor => Math.round((valor + Number.EPSILON) * 100);
    const pesos = valor => dinero.format(valor / 100);
    function pendiente(filas, mensaje) {
        const tr = document.createElement('tr'), td = document.createElement('td');
        td.colSpan = 4; td.textContent = mensaje; tr.appendChild(td); filas.appendChild(tr);
        texto('ejemplo-base', 'Ejemplo pendiente de calcular');
        texto('ejemplo-tope', mensaje);
        texto('ejemplo-conclusion', 'Al completar esos campos, la tabla se actualiza automáticamente. No necesitas guardar.');
    }
    function actualizar() {
        const porcentaje = modalidad.value === 'PORCENTAJE', fijo = limite.value === 'MONTO_FIJO', sinTope = limite.value === 'SIN_LIMITE';
        document.querySelectorAll('[data-recargo-porcentaje]').forEach(e => e.hidden = !porcentaje);
        document.querySelectorAll('[data-recargo-monto]').forEach(e => e.hidden = porcentaje);
        document.querySelectorAll('[data-recargo-limite]').forEach(e => e.hidden = sinTope);
        const valor = campo('valorLimite');
        if (valor) {
            // Conserva la precisión admitida históricamente; el servicio redondea el tope monetario.
            valor.required = !sinTope; valor.min = '0.0001'; valor.step = '0.0001';
            if (fijo || sinTope) valor.removeAttribute('max'); else valor.max = '1000';
        }
        texto('tope-valor-etiqueta', fijo ? 'Máximo de recargos en dinero *' : 'Porcentaje máximo del cargo original *');
        texto('tope-valor-ayuda', fijo ? 'Monto máximo acumulado por cargo, en su moneda; por ejemplo $150.00.'
                : 'Se calcula sobre el importe original, antes de becas y ajustes; por ejemplo 15% de $1,000 = $150.');
        texto('tope-ayuda', sinTope ? 'Sin tope adicional: se aplica según la periodicidad elegida. Una sola vez no se convierte en un cobro mensual.'
                : 'Al llegar al tope acumulado no se agregan más recargos de esta política para ese cargo.');
        simular();
    }
    function simular() {
        const filas = campo('recargo-ejemplo-filas');
        if (!filas) return;
        filas.replaceChildren();
        const original = numero('ejemplo-original'), descuento = numero('ejemplo-descuento');
        const tasa = modalidad.value === 'PORCENTAJE' ? numero('porcentaje') : numero('montoFijo');
        const topeValor = numero('valorLimite'), sinTope = limite.value === 'SIN_LIMITE';
        if (!Number.isFinite(tasa) || tasa <= 0) {
            pendiente(filas, modalidad.value === 'PORCENTAJE'
                ? 'Arriba, en Cálculo del recargo, captura el Porcentaje; por ejemplo 10.'
                : 'Arriba, en Cálculo del recargo, captura el Monto fijo; por ejemplo 80.');
            return;
        }
        if (!sinTope && (!Number.isFinite(topeValor) || topeValor <= 0)) {
            pendiente(filas, 'Arriba, en Tope de recargos, captura el máximo; por ejemplo 150 en dinero o 15 en porcentaje.');
            return;
        }
        if (!Number.isFinite(original) || original <= 0 || original > 1000000 || !Number.isFinite(descuento)
                || descuento < 0 || descuento > original || !Number.isFinite(tasa) || tasa <= 0
                || (modalidad.value === 'PORCENTAJE' && tasa > 100)
                || (!sinTope && (!Number.isFinite(topeValor) || topeValor <= 0
                    || (limite.value === 'PORCENTAJE_ORIGINAL' && topeValor > 1000)))) {
            pendiente(filas, 'Revisa los valores: importe mayor que cero, descuentos entre cero y el importe; recargo porcentual hasta 100% y tope porcentual hasta 1000%. Usa números sin símbolos ni separadores.');
            return;
        }
        const base = Math.max(centavos(original) - centavos(descuento), 0);
        const recargo = base === 0 ? 0 : modalidad.value === 'PORCENTAJE'
                ? Math.round(base * tasa / 100 + Number.EPSILON) : centavos(tasa);
        const tope = sinTope ? null : limite.value === 'MONTO_FIJO'
                ? centavos(topeValor) : Math.round(centavos(original) * topeValor / 100 + Number.EPSILON);
        texto('ejemplo-base', 'Base sin recargos: ' + pesos(base) + ' (' + dinero.format(original) + ' − ' + dinero.format(descuento) + ')');
        texto('ejemplo-tope', tope === null ? 'Sin tope adicional. Recargo calculado por aplicación: ' + pesos(recargo) + '.'
                : 'Tope acumulado por cargo: ' + pesos(tope) + (limite.value === 'PORCENTAJE_ORIGINAL'
                    ? ' (' + topeValor + '% del importe original, no de la base con beca)' : '') + '. Recargo calculado: ' + pesos(recargo) + '.');
        const unica = campo('periodicidad')?.value === 'UNICA';
        let acumulado = 0;
        for (let i = 1; i <= (unica ? 1 : 3); i++) {
            const nuevo = tope === null ? recargo : Math.min(recargo, Math.max(tope - acumulado, 0));
            acumulado += nuevo;
            const tr = document.createElement('tr');
            [unica ? 'Una sola vez' : 'Mes de mora ' + i, pesos(nuevo), pesos(acumulado), pesos(base + acumulado)]
                .forEach((valor, indice) => {
                    const td = document.createElement('td');
                    td.dataset.label = ['Aplicación', 'Recargo nuevo', 'Acumulado', 'Deuda sin pagos'][indice];
                    td.textContent = valor; tr.appendChild(td);
                });
            filas.appendChild(tr);
        }
        texto('ejemplo-conclusion', base === 0 ? 'Con base cero no se generan recargos. No hay deuda en este ejemplo.'
                : unica ? 'Sólo se agrega una vez ' + pesos(acumulado) + '; la deuda ilustrativa queda en ' + pesos(base + acumulado) + '.'
                : tope !== null && acumulado >= tope ? 'Tope alcanzado: los siguientes recargos de esta política serán $0.00 mientras no cambie su configuración o acumulado. Deuda ilustrativa ' + pesos(base + acumulado) + '.'
                : 'Se muestran sólo los primeros tres meses de mora; no son fechas reales ni el saldo máximo definitivo. La cuenta no cambia hasta validar un pago.');
    }
    modalidad.addEventListener('change', actualizar); limite.addEventListener('change', actualizar);
    document.querySelector('.entity-form')?.addEventListener('input', simular);
    document.querySelector('.entity-form')?.addEventListener('change', simular);
    campo('recargo-ejemplo-calcular')?.addEventListener('click', actualizar);
    campo('periodicidad')?.addEventListener('change', simular);
    campo('recargo-ejemplo-valores')?.addEventListener('keydown', evento => { if (evento.key === 'Enter') evento.preventDefault(); });
    actualizar();
};
if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', iniciarEjemploRecargos, {once:true});
else iniciarEjemploRecargos();
