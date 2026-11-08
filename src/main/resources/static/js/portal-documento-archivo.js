(() => {
    const field = document.querySelector('[data-document-upload]');
    if (!field) return;
    const input = field.querySelector('input[type="file"]');
    const title = field.querySelector('[data-document-title]');
    const status = field.querySelector('.document-file-status');
    if (!input || !title || !status) return;
    field.classList.add('document-upload-ready');
    const update = () => {
        const file = input.files?.[0];
        title.textContent = file ? 'Archivo seleccionado' : 'Arrastra tu archivo aquí';
        status.textContent = file ? `${file.name} · ${(file.size / 1024 / 1024).toFixed(2)} MB` : 'Aún no has seleccionado un archivo.';
        field.classList.toggle('has-file', Boolean(file));
    };
    input.addEventListener('change', update);
    for (const eventName of ['dragenter', 'dragover']) field.addEventListener(eventName, event => {
        if (!event.dataTransfer?.types.includes('Files')) return;
        event.preventDefault();
        event.dataTransfer.dropEffect = 'copy';
        field.classList.add('is-dragging');
    });
    field.addEventListener('dragleave', event => {
        if (!field.contains(event.relatedTarget)) field.classList.remove('is-dragging');
    });
    field.addEventListener('drop', event => {
        event.preventDefault();
        field.classList.remove('is-dragging');
        const files = event.dataTransfer?.files;
        if (!files?.length) return;
        if (files.length !== 1) { input.value = ''; update(); status.textContent = 'Selecciona solamente un archivo.'; return; }
        try {
            input.files = files;
            input.dispatchEvent(new Event('change', {bubbles:true}));
        } catch (_) {
            status.textContent = 'No se pudo agregar el archivo. Haz clic para seleccionarlo.';
        }
    });
    update();
})();
