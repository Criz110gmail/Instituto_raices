(() => {
    'use strict';
    const zone = document.querySelector('[data-web-drop]');
    if (!zone) return;
    const input = zone.querySelector('input[type="file"]');
    const pick = zone.querySelector('[data-drop-pick]');
    const title = zone.querySelector('[data-drop-title]');
    const selected = zone.querySelector('[data-drop-selected]');
    const preview = zone.querySelector('[data-drop-preview]');
    const name = zone.querySelector('[data-drop-name]');
    const details = zone.querySelector('[data-drop-details]');
    const dimensions = zone.querySelector('[data-drop-dimensions]');
    const status = zone.querySelector('[role="status"]');
    const error = zone.querySelector('[role="alert"]');
    let accepted = null, objectUrl = null, generation = 0;
    const clearError = () => { error.hidden = true; error.textContent = ''; input.removeAttribute('aria-invalid'); };
    const fail = message => { error.textContent = message; error.hidden = false; input.setAttribute('aria-invalid', 'true'); };
    const revoke = () => { if (objectUrl) URL.revokeObjectURL(objectUrl); objectUrl = null; };
    function reset() {
        generation++; revoke(); accepted = null; input.value = '';
        selected.hidden = true; preview.removeAttribute('src'); dimensions.textContent = '';
        title.textContent = 'Arrastra tu imagen aquí'; pick.textContent = 'Seleccionar imagen';
        zone.classList.remove('has-image'); status.textContent = 'Todavía no has seleccionado una imagen.';
        clearError();
    }
    function assign(file) {
        const transfer = new DataTransfer();
        if (file) transfer.items.add(file);
        input.files = transfer.files;
    }
    function reject(message) {
        try { if (accepted) assign(accepted); else input.value = ''; }
        catch (_) { reset(); }
        fail(message);
    }
    function choose(files, dropped = false) {
        if (!files?.length) return;
        if (files.length !== 1) { reject('Agrega una sola imagen a la vez.'); return; }
        const file = files[0];
        const typeOk = ['image/png', 'image/jpeg'].includes(file.type)
            || (!file.type && /\.(png|jpe?g)$/i.test(file.name));
        if (!typeOk) { reject('Selecciona una imagen PNG o JPEG.'); return; }
        if (file.size === 0 || file.size > 5 * 1024 * 1024) {
            reject('La imagen debe tener contenido y pesar como máximo 5 MB.'); return;
        }
        if (dropped) try { assign(file); }
        catch (_) { fail('No se pudo agregar el archivo. Usa Seleccionar imagen.'); return; }
        clearError(); revoke(); accepted = file; const current = ++generation;
        objectUrl = URL.createObjectURL(file); selected.hidden = false;
        name.textContent = file.name;
        details.textContent = `${file.type === 'image/png' ? 'PNG' : file.type === 'image/jpeg' ? 'JPEG' : 'Imagen'} · ${new Intl.NumberFormat('es-MX', {maximumFractionDigits: 2}).format(file.size / (1024 * 1024))} MB`;
        dimensions.textContent = 'Preparando vista previa…';
        title.textContent = 'Tu imagen está seleccionada'; pick.textContent = 'Cambiar imagen';
        zone.classList.add('has-image'); status.textContent = 'Revisa la imagen y pulsa Revisar y cargar para continuar.';
        preview.onload = () => {
            if (current !== generation) return;
            if (preview.naturalWidth * preview.naturalHeight > 25_000_000) {
                reset(); fail('La imagen no debe superar 25 megapíxeles.'); return;
            }
            dimensions.textContent = `${preview.naturalWidth} × ${preview.naturalHeight} píxeles`;
        };
        preview.onerror = () => { if (current === generation) { reset(); fail('No fue posible abrir esta imagen. Selecciona otro PNG o JPEG.'); } };
        preview.src = objectUrl;
    }
    zone.classList.add('is-enhanced'); pick.hidden = false;
    zone.querySelector('[data-drop-or]').hidden = false;
    pick.addEventListener('click', () => input.click());
    input.addEventListener('change', () => { if (!input.files?.length) reset(); else choose(input.files); });
    input.addEventListener('invalid', event => { event.preventDefault(); fail('Selecciona una imagen antes de continuar.'); pick.focus(); });
    zone.querySelector('[data-drop-remove]').addEventListener('click', () => { reset(); pick.focus(); });
    for (const eventName of ['dragenter', 'dragover']) zone.addEventListener(eventName, event => {
        if (!Array.from(event.dataTransfer?.types || []).includes('Files')) return;
        event.preventDefault(); event.dataTransfer.dropEffect = 'copy'; zone.classList.add('is-dragging');
    });
    zone.addEventListener('dragleave', event => { if (!zone.contains(event.relatedTarget)) zone.classList.remove('is-dragging'); });
    zone.addEventListener('drop', event => {
        event.preventDefault(); zone.classList.remove('is-dragging'); choose(event.dataTransfer?.files, true);
    });
    // Evita que el navegador abandone el formulario al soltar una imagen fuera de la zona.
    for (const eventName of ['dragover', 'drop']) document.addEventListener(eventName, event => {
        if (Array.from(event.dataTransfer?.types || []).includes('Files')) event.preventDefault();
    });
    addEventListener('pagehide', revoke);
})();
