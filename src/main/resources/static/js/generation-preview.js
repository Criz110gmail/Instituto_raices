(() => {
    const form = document.querySelector('[data-preview-form]');
    const submit = form?.querySelector('button[type="submit"]');
    form?.addEventListener('submit', () => {
        if (!submit) return;
        submit.disabled = true;
        submit.dataset.originalText = submit.textContent;
        submit.textContent = 'Preparando vista…';
        submit.setAttribute('aria-busy', 'true');
    });

    const preview = document.querySelector('[data-generation-preview]');
    if (!preview) return;
    const heading = preview.querySelector('h2');
    window.requestAnimationFrame(() => {
        const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        preview.scrollIntoView({behavior: reducedMotion ? 'auto' : 'smooth', block: 'start'});
        if (heading) {
            heading.setAttribute('tabindex', '-1');
            window.setTimeout(() => heading.focus({preventScroll: true}), 350);
        }
    });
})();
