(() => {
    const root = document.documentElement;
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
    const saved = localStorage.getItem('nexo-theme');
    const preferred = matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    root.dataset.theme = saved || preferred;
    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('[data-theme-toggle]').forEach(button => {
            const refresh = () => {
                const dark = root.dataset.theme === 'dark';
                button.setAttribute('aria-label', dark ? 'Cambiar a tema claro' : 'Cambiar a tema oscuro');
                button.setAttribute('title', dark ? 'Tema claro' : 'Tema oscuro');
                button.dataset.activeTheme = root.dataset.theme;
            };
            button.addEventListener('click', () => {
                root.dataset.theme = root.dataset.theme === 'dark' ? 'light' : 'dark';
                localStorage.setItem('nexo-theme', root.dataset.theme);
                refresh();
            });
            refresh();
        });
    });
})();
