(() => {
    const root = document.documentElement;
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
