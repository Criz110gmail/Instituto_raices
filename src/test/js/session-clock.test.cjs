const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const source = fs.readFileSync('src/main/resources/static/js/session-clock.js', 'utf8');
class Element {
    constructor() { this.dataset = {}; this.listeners = {}; this.children = []; this.textContent = ''; this.open = false;
        this.classList = { toggle() {}, add() {} }; }
    addEventListener(event, fn) { this.listeners[event] = fn; }
    setAttribute() {}
    appendChild(child) { this.children.push(child); }
    insertBefore(child, reference) {
        const index = this.children.indexOf(reference);
        if (index < 0) this.children.push(child); else this.children.splice(index, 0, child);
    }
    querySelector(selector) { return this.parts[selector]; }
    set innerHTML(_) { this.parts = Object.fromEntries(['[data-session-remaining]', '[data-session-message]', '[data-session-renew]', '[data-session-exit]', '#session-warning-title'].map(key => [key, new Element()])); }
    showModal() { this.open = true; }
    close() { this.open = false; }
    submit() { this.submitted = true; }
}
function entorno(pathname) {
    let now = 0;
    const body = new Element(), header = new Element(), listeners = {}, intervals = [], requests = [], redirects = [];
    const controls = new Element(), theme = new Element(), logout = new Element();
    controls.parts = { '[data-theme-toggle]': theme };
    controls.children = [new Element(), theme, logout];
    header.appendChild(controls);
    let state = { ahora: 1000000, vence: 2800000, csrfCabecera: 'X-CSRF-TOKEN', csrfToken: 'csrf-prueba' };
    let status = 200;
    const channel = { listeners: {}, postMessage() {}, addEventListener(name, fn) { this.listeners[name] = fn; } };
    const document = { readyState: 'complete', hidden: false, body,
        querySelector: selector => selector.includes('.topbar') ? header
            : selector.includes('.form-session-actions') ? controls : null,
        createElement: () => new Element(), addEventListener: (name, fn) => { listeners[name] = fn; } };
    const window = { fetch: async (url, options) => {
        requests.push({ url, options });
        if (url === '/sesion/renovar') state = { ...state, ahora: 1000000 + now, vence: 2800000 + now };
        return { status, ok: status === 200, redirected: false, json: async () => state,
            headers: { get: name => name === 'X-Session-Now' ? String(state.ahora) : name === 'X-Session-Expires' ? String(state.vence) : null } };
    }, location: { assign: url => redirects.push(url) }, addEventListener: (name, fn) => { listeners[name] = fn; } };
    vm.runInNewContext(source, { document, window, location: { pathname }, performance: { now: () => now },
        BroadcastChannel: function() { return channel; }, setInterval: fn => intervals.push(fn) });
    return { body, header, controls, theme, logout, intervals, requests, redirects, listeners, window, channel,
        avanzar: ms => { now += ms; }, estado: value => { state = value; }, caducar: () => { status = 401; } };
}
const flush = () => new Promise(resolve => setImmediate(resolve));
(async () => {
    const e = entorno('/admin/pagos');
    await flush();
    const badge = e.controls.children.find(child => child.dataset.sessionIndicator), modal = e.body.children[0];
    assert.equal(e.controls.children.at(-1), e.logout);
    assert.equal(e.controls.children.indexOf(badge) + 1, e.controls.children.indexOf(e.theme));
    assert.equal(badge.textContent, 'Sesión · 30:00');
    assert.equal(e.requests[0].options.method, 'GET');
    e.avanzar(1680000); e.intervals[0]();
    assert.equal(badge.textContent, 'Sesión · 02:00');
    assert.equal(modal.open, true);
    modal.querySelector('[data-session-renew]').listeners.click();
    await flush();
    assert.equal(e.requests.at(-1).url, '/sesion/renovar');
    assert.equal(e.requests.at(-1).options.method, 'POST');
    assert.equal(e.requests.at(-1).options.headers['X-CSRF-TOKEN'], 'csrf-prueba');
    assert.equal(badge.textContent, 'Sesión · 30:00');
    assert.equal(modal.open, false);
    e.intervals[1](); await flush();
    assert.equal(e.requests.at(-1).url, '/sesion/estado'); // El sondeo nunca renueva.
    e.avanzar(1680000); e.intervals[0]();
    assert.equal(modal.open, true);
    e.channel.listeners.message({ data: { ahora: 4360001, fin: 6160001 } });
    assert.equal(badge.textContent, 'Sesión · 30:00');
    assert.equal(modal.open, false);
    e.channel.listeners.message({ data: { ahora: 1000000, fin: 1000001 } });
    assert.equal(badge.textContent, 'Sesión · 30:00'); // Respuesta vieja no acorta renovación.
    for (const [path, login] of [['/portal/pagos', '/familias'], ['/maestros/planeaciones', '/maestros/acceso'], ['/admin/pagos', '/login']]) {
        const caso = entorno(path); await flush();
        assert.equal(caso.controls.children.at(-1), caso.logout);
        if (path.startsWith('/portal')) {
            assert.equal(caso.controls.children.some(child => child.dataset.sessionIndicator), false);
            assert.equal(caso.body.children.some(child => child.dataset.sessionIndicator), false);
            caso.avanzar(1680000); caso.intervals[0]();
            assert.equal(caso.body.children[0].open, true);
            assert.equal(caso.body.children[0].querySelector('[data-session-remaining]').textContent, '02:00');
            caso.body.children[0].querySelector('[data-session-renew]').listeners.click(); await flush();
            assert.equal(caso.requests.at(-1).url, '/sesion/renovar');
            assert.equal(caso.body.children[0].open, false);
        } else assert.ok(caso.controls.children.find(child => child.dataset.sessionIndicator));
        caso.caducar(); caso.listeners.focus(); await flush();
        assert.equal(caso.redirects.at(-1), login + '?sesionExpirada');
    }
    modal.querySelector('[data-session-exit]').listeners.click();
    const logout = e.body.children.at(-1);
    assert.equal(logout.action, '/logout');
    assert.equal(logout.method, 'post');
    assert.equal(logout.children[0].name, '_csrf');
    assert.equal(logout.children[1].value, 'admin');
    assert.equal(logout.submitted, true);
    console.log('Sesión: reloj servidor, aviso2min, renovación30min con CSRF, sondeo sin renovación, sincronía entre pestañas y acceso propio correctos.');
})().catch(error => { console.error(error); process.exitCode = 1; });
