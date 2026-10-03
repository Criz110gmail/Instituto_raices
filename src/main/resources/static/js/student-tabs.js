document.addEventListener('DOMContentLoaded', () => {
    const navigation = document.querySelector('[data-student-tabs]');
    if (!navigation) return;

    const tabs = [...navigation.querySelectorAll('[data-student-tab]')];
    const panels = [...document.querySelectorAll('[data-student-panel]')];
    const validTabs = new Set(tabs.map(tab => tab.dataset.studentTab));
    const legacyHashTabs = {
        '#expediente-documental': 'documentos',
        '#ficha-medica': 'medica',
        '#datos-expediente': 'informacion',
        '#informacion-alumno': 'informacion',
        '#contacto-alumno': 'contacto',
        '#notas-alumno': 'notas',
        '#panel-ficha-resumen': 'ficha'
    };

    const activate = (name, updateUrl = false) => {
        const selected = validTabs.has(name) ? name : 'ficha';
        tabs.forEach(tab => {
            const active = tab.dataset.studentTab === selected;
            tab.setAttribute('aria-selected', String(active));
            tab.tabIndex = active ? 0 : -1;
        });
        panels.forEach(panel => {
            panel.hidden = panel.dataset.studentPanel !== selected;
        });
        if (updateUrl) {
            const selectedTab = tabs.find(tab => tab.dataset.studentTab === selected);
            const hash = selectedTab?.dataset.tabHash || '#panel-ficha-resumen';
            history.replaceState(null, '', hash);
        }
    };

    tabs.forEach((tab, index) => {
        tab.addEventListener('click', () => activate(tab.dataset.studentTab, true));
        tab.addEventListener('keydown', event => {
            if (event.key !== 'ArrowLeft' && event.key !== 'ArrowRight') return;
            event.preventDefault();
            const direction = event.key === 'ArrowRight' ? 1 : -1;
            const next = tabs[(index + direction + tabs.length) % tabs.length];
            activate(next.dataset.studentTab, true);
            next.focus();
        });
    });

    document.querySelectorAll('[data-open-student-tab]').forEach(link => {
        link.addEventListener('click', () => activate(link.dataset.openStudentTab, false));
    });

    const panelWithErrors = panels.find(panel => [...panel.querySelectorAll('small[id$="errors"],.field-error')]
        .some(error => error.textContent.trim().length > 0));
    const requestedByHash = tabs.find(tab => tab.dataset.tabHash === window.location.hash)?.dataset.studentTab
        || legacyHashTabs[window.location.hash];
    activate(panelWithErrors?.dataset.studentPanel || requestedByHash || navigation.dataset.activeTab || 'ficha');
});
