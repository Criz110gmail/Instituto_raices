(() => {
    const root = document.documentElement;
    // Declarar el tema antes de cargar componentes evita una segunda adaptación
    // automática del navegador sobre la paleta que ya controla la aplicación.
    const preferred = matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    const readSaved = () => {
        try {
            const value = localStorage.getItem('nexo-theme');
            return value === 'dark' || value === 'light' ? value : null;
        } catch (_) { return null; }
    };
    const apply = theme => {
        root.dataset.theme = theme;
        root.style.colorScheme = 'only ' + theme;
    };
    apply(readSaved() || preferred);
    // Componente global sólo en pantallas del sistema, nunca en los accesos públicos.
    const ruta = location.pathname;
    if ((/^\/(admin|portal)(\/|$)/.test(ruta) || /^\/maestros(\/|$)/.test(ruta))
            && ruta !== '/maestros/acceso' && !document.querySelector('script[data-session-clock]')) {
        const sessionStyles = document.createElement('link');
        sessionStyles.rel = 'stylesheet';
        sessionStyles.href = '/css/session.css';
        document.head.appendChild(sessionStyles);
        const sessionScript = document.createElement('script');
        sessionScript.src = '/js/session-clock.js';
        sessionScript.defer = true;
        sessionScript.dataset.sessionClock = 'true';
        document.head.appendChild(sessionScript);
    }
    if (!document.querySelector('link[data-ui-polish]')) {
        const uiStyles = document.createElement('link');
        uiStyles.rel = 'stylesheet';
        uiStyles.href = '/css/ui-polish.css';
        uiStyles.dataset.uiPolish = 'true';
        document.head.appendChild(uiStyles);
    }
    if (!document.querySelector('link[data-contextual-help]')) {
        const helpStyles = document.createElement('link');
        helpStyles.rel = 'stylesheet';
        helpStyles.href = '/css/contextual-help.css';
        helpStyles.dataset.contextualHelp = 'true';
        document.head.appendChild(helpStyles);
    }
    if (!document.querySelector('script[data-contextual-help]')) {
        const helpScript = document.createElement('script');
        helpScript.src = '/js/contextual-help.js';
        helpScript.defer = true;
        helpScript.dataset.contextualHelp = 'true';
        document.head.appendChild(helpScript);
    }
    const bindButtons = () => {
        document.querySelectorAll('[data-theme-toggle]').forEach(button => {
            const refresh = () => {
                const dark = root.dataset.theme === 'dark';
                button.setAttribute('aria-label', dark ? 'Cambiar a tema claro' : 'Cambiar a tema oscuro');
                button.setAttribute('title', dark ? 'Tema claro' : 'Tema oscuro');
                button.dataset.activeTheme = root.dataset.theme;
            };
            button.addEventListener('click', () => {
                apply(root.dataset.theme === 'dark' ? 'light' : 'dark');
                try { localStorage.setItem('nexo-theme', root.dataset.theme); } catch (_) { /* El tema sigue funcionando sin almacenamiento. */ }
                refresh();
            });
            refresh();
        });
    };
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', bindButtons);
    else bindButtons();
})();
