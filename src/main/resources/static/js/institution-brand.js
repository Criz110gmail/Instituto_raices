/* Monochrome presentation of the original mark: never redraw or alter its pixels. */
(() => {
    'use strict';
    const defaults = /(?:^|\/)images\/(?:logo1\.png|logo_general\.png|logo_blanco\.svg)(?:[?#]|$)/;
    const parse = value => {
        if (!value || value === 'transparent') return [0, 0, 0, 0];
        if (value.startsWith('color(srgb ')) {
            const n = value.slice(11).match(/[\d.]+/g)?.map(Number) || [];
            return [n[0] * 255, n[1] * 255, n[2] * 255, n[3] ?? 1];
        }
        const n = value.match(/[\d.]+/g)?.map(Number) || [];
        return n.length >= 3 ? [n[0], n[1], n[2], n[3] ?? 1] : [0, 0, 0, 0];
    };
    const luminance = rgb => rgb.map(v => { const n = v / 255; return n <= .04045 ? n / 12.92 : ((n + .055) / 1.055) ** 2.4; })
        .reduce((sum, v, i) => sum + v * [.2126, .7152, .0722][i], 0);
    function background(image) {
        let rgb = [0, 0, 0], alpha = 0;
        for (let el = image.parentElement; el && alpha < .995; el = el.parentElement) {
            const style = getComputedStyle(el);
            let color = parse(style.backgroundColor);
            if (color[3] < .01 && style.backgroundImage.includes('gradient')) {
                const first = style.backgroundImage.match(/rgba?\([^)]*\)|color\(srgb [^)]*\)/);
                if (first) color = parse(first[0]);
            }
            const weight = (1 - alpha) * color[3];
            rgb = rgb.map((v, i) => v + color[i] * weight); alpha += weight;
        }
        return rgb.map(v => v + (1 - alpha) * 255);
    }
    let queued = false;
    const refresh = () => {
        queued = false;
        document.querySelectorAll('img.institution-logo, img.pi-logo').forEach(image => {
            if (!defaults.test(image.getAttribute('src') || '')) { image.removeAttribute('data-logo-tone'); return; }
            const whiteReadable = 1.05 / (luminance(background(image)) + .05) >= 4.5;
            image.setAttribute('data-logo-tone', whiteReadable ? 'white' : 'original');
            const source = '/images/' + (whiteReadable ? 'logo_blanco.svg' : 'logo_general.png') + '?v=20261116';
            if (image.getAttribute('src') !== source) image.setAttribute('src', source);
        });
    };
    const schedule = () => { if (!queued) { queued = true; requestAnimationFrame(refresh); } };
    const start = () => {
        schedule();
        new MutationObserver(records => {
            if (records.some(r => r.type === 'childList' || r.target === document.documentElement || r.target === document.body || (r.attributeName === 'src' && r.target.matches('img.institution-logo, img.pi-logo')))) schedule();
        }).observe(document.documentElement, {subtree: true, childList: true, attributes: true, attributeFilter: ['src', 'data-theme', 'data-pi-interior', 'style']});
        document.addEventListener('load', event => { if (event.target.matches?.('link[rel="stylesheet"], img.institution-logo, img.pi-logo')) schedule(); }, true);
        window.addEventListener('resize', schedule);
    };
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start, {once: true}); else start();
})();
