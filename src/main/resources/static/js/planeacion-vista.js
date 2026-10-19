(() => {
    if (typeof document === 'undefined') return;
    const root = document.querySelector('[data-plan-workspace]');
    if (!root) return;
    const tabs = [...root.querySelectorAll('[data-plan-view]')];
    const panels = [...root.querySelectorAll('[data-plan-view-panel]')];
    const form = root.querySelector('[data-planeacion-form]');
    const warning = root.querySelector('[data-plan-unsaved]');
    const savedActions = [...root.querySelectorAll('[data-plan-saved-action]')];
    function activate(name, focus = false) {
        if (!tabs.some(tab => tab.dataset.planView === name)) return;
        tabs.forEach(tab => {
            const selected = tab.dataset.planView === name;
            tab.setAttribute('aria-selected', String(selected));
            tab.tabIndex = selected ? 0 : -1;
            if (selected && focus) tab.focus();
        });
        panels.forEach(panel => { panel.hidden = panel.dataset.planViewPanel !== name; });
    }
    tabs.forEach((tab, index) => {
        tab.addEventListener('click', () => activate(tab.dataset.planView));
        tab.addEventListener('keydown', event => {
            if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) return;
            event.preventDefault();
            const next = event.key === 'Home' ? 0 : event.key === 'End' ? tabs.length - 1 : (index + (event.key === 'ArrowRight' ? 1 : tabs.length - 1)) % tabs.length;
            activate(tabs[next].dataset.planView, true);
        });
    });
    activate(root.dataset.initialView || 'captura');
    if (!form || form.dataset.readonly === 'true') return;
    const snapshot = () => JSON.stringify([...new FormData(form)].filter(([name]) => name !== '_csrf'));
    const original = snapshot();
    let dirty = false, leaving = false;
    const dialog = root.querySelector('[data-plan-send-dialog]');
    const confirm = dialog?.querySelector('[data-plan-send-confirm]');
    let pendingSend = null, confirmed = false, trigger = null;
    if (dialog) {
        dialog.querySelector('[data-plan-send-cancel]').addEventListener('click', () => dialog.close());
        dialog.addEventListener('close', () => { pendingSend = null; trigger?.focus(); });
        dialog.addEventListener('click', event => {
            const rect = dialog.getBoundingClientRect();
            if (event.target === dialog && (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom)) dialog.close();
        });
        confirm.addEventListener('click', () => {
            if (!pendingSend || confirm.disabled) return;
            update();
            if (dirty) { dialog.close(); activate('captura'); return; }
            const action = pendingSend;
            confirm.disabled = true;
            confirmed = true;
            dialog.close();
            try { action.requestSubmit(); } finally { confirmed = false; }
        });
    }
    function update() {
        dirty = root.dataset.pendingSave === 'true' || snapshot() !== original;
        if (warning) warning.hidden = !dirty;
        savedActions.forEach(action => action.querySelectorAll('button[type=submit]').forEach(button => {
            button.disabled = dirty;
            button.title = dirty ? 'Guarda primero los cambios desde Captura.' : '';
        }));
    }
    update();
    ['input', 'change', 'planeacion:rows-changed'].forEach(event => form.addEventListener(event, update));
    savedActions.forEach(action => action.addEventListener('submit', event => {
        update();
        if (dirty) { event.preventDefault(); activate('captura'); }
        else if (dialog && action.hasAttribute('data-plan-send') && !confirmed) {
            event.preventDefault();
            pendingSend = action;
            trigger = document.activeElement;
            confirm.disabled = false;
            dialog.showModal();
        } else if (!action.hasAttribute('data-planeacion-confirm') || action.dataset.planConfirmationReady === 'true') { leaving = true; }
    }));
    form.addEventListener('submit', event => { if (!event.defaultPrevented && (!form.hasAttribute('data-planeacion-confirm') || form.dataset.planConfirmationReady === 'true')) leaving = true; });
    window.addEventListener('beforeunload', event => {
        if (dirty && !leaving) { event.preventDefault(); event.returnValue = ''; }
    });
})();
