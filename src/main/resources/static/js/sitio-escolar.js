(() => {
    'use strict';
    const menu = document.querySelector('.menu-toggle');
    const nav = document.getElementById('navegacion');
    if (menu && nav) {
        menu.hidden = false;
        nav.classList.add('collapsed');
        const close = () => { nav.classList.add('collapsed'); menu.setAttribute('aria-expanded', 'false'); };
        menu.addEventListener('click', () => {
            const expanded = menu.getAttribute('aria-expanded') !== 'true';
            menu.setAttribute('aria-expanded', String(expanded));
            nav.classList.toggle('collapsed', !expanded);
        });
        nav.addEventListener('click', event => { if (event.target.closest('a')) close(); });
        document.addEventListener('keydown', event => { if (event.key === 'Escape' && menu.getAttribute('aria-expanded') === 'true') { close(); menu.focus(); } });
        // Desktop navigation always remains visible through CSS.
        const mobile = window.matchMedia('(max-width: 700px)');
        const adapt = () => { menu.hidden = !mobile.matches; close(); };
        mobile.addEventListener('change', adapt); adapt();
    }
    const welcome = document.querySelector('[data-welcome]');
    if (!welcome || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    try { if (sessionStorage.getItem('raices-bienvenida-v2')) return; } catch (_) { /* Storage may be disabled. */ }
    const skip = welcome.querySelector('[data-skip]');
    const bar = welcome.querySelector('[role="progressbar"]');
    const percent = welcome.querySelector('[data-percent]');
    const resources = [...document.querySelectorAll('[data-essential-image]')];
    let completed = 0, finished = false, maximumTimer;
    const background = [...document.querySelectorAll('header, main, footer, .skip-link')];
    const previousFocus = document.activeElement;
    const progress = value => {
        welcome.style.setProperty('--progress', value + '%');
        bar.setAttribute('aria-valuenow', String(value)); percent.textContent = value + ' %';
    };
    const finish = () => {
        if (finished) return; finished = true; clearTimeout(maximumTimer);
        try { sessionStorage.setItem('raices-bienvenida-v2', '1'); } catch (_) { /* Optional persistence. */ }
        welcome.classList.add('leaving');
        document.body.classList.remove('welcoming');
        background.forEach(element => { element.inert = false; });
        if (document.activeElement === skip) {
            if (previousFocus && previousFocus !== document.body) previousFocus.focus();
            else document.querySelector('#hero-title')?.focus({preventScroll:true});
        }
        setTimeout(() => { welcome.hidden = true; }, 360);
    };
    welcome.hidden = false; document.body.classList.add('welcoming'); progress(0);
    background.forEach(element => { element.inert = true; });
    skip.focus({preventScroll:true});
    skip.addEventListener('click', finish);
    document.addEventListener('keydown', event => { if (event.key === 'Escape') finish(); });
    maximumTimer = setTimeout(finish, 3500);
    const tasks = resources.map(img => new Promise(resolve => {
        const ready = () => { completed++; progress(Math.round(completed / Math.max(1, resources.length) * 100)); resolve(); };
        if (img.complete) ready(); else { img.addEventListener('load', ready, {once:true}); img.addEventListener('error', ready, {once:true}); }
    }));
    Promise.all(tasks).then(() => { progress(100); setTimeout(finish, 1900); });
})();
