document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-purchase-filters] [data-autocomplete]').forEach(box => {
        const input = box.querySelector('.autocomplete-input');
        const value = box.querySelector('.autocomplete-value');
        const validate = () => input.setCustomValidity(input.value.trim() && !value.value
            ? 'Selecciona una opción de la lista o borra el texto para consultar todos.' : '');
        input.addEventListener('input', validate);
        value.addEventListener('change', validate);
        validate();
    });
});
