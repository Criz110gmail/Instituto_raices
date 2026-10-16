(() => {
    function iniciar() {
        if (document.querySelector('[data-session-clock-modal]')) return;
        const familia = /^\/portal(\/|$)/.test(location.pathname);
        const maestro = /^\/maestros(\/|$)/.test(location.pathname);
        const acceso = familia ? '/familias' : maestro ? '/maestros/acceso' : '/login';
        const origen = familia ? 'familias' : maestro ? 'maestros' : 'admin';
        const fetchOriginal = window.fetch.bind(window);
        let vence = 0, csrfCabecera, csrfToken, consultando = false, renovando = false;
        let avisoPara = 0, ultimaSincronizacion = 0, ultimoServidor = 0, saliendo = false;
        const canal = typeof BroadcastChannel === 'function' ? new BroadcastChannel('nexo-session-clock') : null;
        const indicador = document.createElement('button');
        indicador.type = 'button';
        indicador.className = 'session-indicator';
        indicador.dataset.sessionIndicator = 'true';
        indicador.title = 'Tiempo de sesión restante. Pulsa para continuar tu sesión.';
        indicador.setAttribute('aria-label', 'Consultar tiempo restante de sesión');
        indicador.textContent = 'Sesión · consultando…';
        const cabecera = document.querySelector('.topbar, .family-bar, .teacher-bar, .form-top');
        // Familias conserva el aviso, sin reloj permanente. Salir sigue siendo el último control.
        if (!familia) {
            const controles = document.querySelector('.form-session-actions, .teacher-user');
            if (controles) controles.insertBefore(indicador,
                controles.querySelector('[data-theme-toggle]') || controles.firstChild);
            else if (cabecera) cabecera.insertBefore(indicador, cabecera.lastElementChild);
            else { document.body.appendChild(indicador); indicador.classList.add('session-indicator-floating'); }
        }

        const modal = document.createElement('dialog');
        modal.className = 'session-warning-dialog';
        modal.dataset.sessionClockModal = 'true';
        modal.setAttribute('aria-labelledby', 'session-warning-title');
        modal.setAttribute('aria-describedby', 'session-warning-description');
        modal.innerHTML = '<span class="session-warning-eyebrow">Protección de tu cuenta</span>' +
            '<h2 id="session-warning-title">Tu sesión está por vencer</h2>' +
            '<p id="session-warning-description">Continúa tu sesión para seguir trabajando sin perder esta pantalla. Esta acción no guarda el formulario.</p>' +
            '<div class="session-warning-countdown"><span>Tiempo restante</span><strong data-session-remaining>02:00</strong></div>' +
            '<p data-session-message role="status" aria-live="polite"></p>' +
            '<div class="session-warning-actions"><button type="button" data-session-exit>Cerrar sesión</button>' +
            '<button type="button" data-session-renew>Continuar sesión</button></div>';
        document.body.appendChild(modal);
        const cuenta = modal.querySelector('[data-session-remaining]');
        const mensaje = modal.querySelector('[data-session-message]');
        const continuar = modal.querySelector('[data-session-renew]');

        function formato(segundos) {
            return String(Math.floor(segundos / 60)).padStart(2, '0') + ':' + String(segundos % 60).padStart(2, '0');
        }
        function abrir() {
            if (!modal.open && !document.hidden) modal.showModal();
        }
        function sincronizar(ahora, fin, publicar = true) {
            if (!Number.isFinite(ahora) || !Number.isFinite(fin) || fin <= ahora) return;
            if (ahora < ultimoServidor) return;
            ultimoServidor = ahora;
            // Diferencia del reloj del servidor, no confiar en la hora del equipo del usuario.
            vence = performance.now() + fin - ahora;
            ultimaSincronizacion = performance.now();
            if (fin - ahora > 120000) {
                if ((avisoPara || renovando) && modal.open) modal.close();
                avisoPara = 0;
            }
            if (publicar) canal?.postMessage({ ahora, fin });
            actualizar();
        }
        function actualizar() {
            if (!vence) return;
            const segundos = Math.max(0, Math.ceil((vence - performance.now()) / 1000));
            indicador.textContent = 'Sesión · ' + formato(segundos);
            indicador.classList.toggle('session-indicator-warning', segundos <= 120);
            cuenta.textContent = formato(segundos);
            modal.querySelector('#session-warning-title').textContent = segundos <= 120
                ? 'Tu sesión está por vencer' : 'Tu sesión está activa';
            if (segundos > 0 && segundos <= 120 && !avisoPara) { avisoPara = vence; abrir(); }
            if (!segundos && !consultando && performance.now() - ultimaSincronizacion > 30000) consultar();
        }
        function caducada() {
            if (saliendo) return;
            saliendo = true;
            canal?.postMessage({ cerrada: true });
            window.location.assign(acceso + '?sesionExpirada');
        }
        async function consultar(renovar = false) {
            if (consultando || saliendo) return;
            consultando = true;
            const control = new AbortController();
            const limiteConsulta = setTimeout(() => control.abort(), 4000);
            continuar.disabled = true;
            if (renovar) { renovando = true; continuar.disabled = true; mensaje.textContent = 'Renovando sesión…'; }
            try {
                const headers = { Accept: 'application/json' };
                if (renovar) {
                    if (!csrfCabecera || !csrfToken) throw new Error('No se pudo confirmar la sesión. Intenta de nuevo.');
                    headers[csrfCabecera] = csrfToken;
                }
                let response = await fetchOriginal('/sesion/' + (renovar ? 'renovar' : 'estado'), {
                    method: renovar ? 'POST' : 'GET', headers, credentials: 'same-origin', cache: 'no-store', signal: control.signal
                });
                // Una respuesta vieja puede llegar después de iniciar sesión en otra
                // pestaña. Confirmar con GET actual; nunca repetir POST ni credenciales.
                if (response.status === 401 || response.redirected) {
                    response = await fetchOriginal('/sesion/estado', { method: 'GET',
                        headers: { Accept: 'application/json' }, credentials: 'same-origin', cache: 'no-store', signal: control.signal });
                    if (response.status === 401 || response.redirected) { caducada(); return; }
                }
                if (!response.ok) throw new Error('No se pudo confirmar la sesión. Intenta de nuevo.');
                const estado = await response.json();
                csrfCabecera = estado.csrfCabecera; csrfToken = estado.csrfToken;
                sincronizar(estado.ahora, estado.vence);
                mensaje.textContent = '';
            } catch (error) {
                mensaje.textContent = 'No pudimos consultar el servidor. Revisa tu conexión y pulsa Continuar sesión para intentar de nuevo.';
                indicador.textContent = 'Sesión · sin conexión';
                ultimaSincronizacion = performance.now();
            } finally { clearTimeout(limiteConsulta); consultando = false; renovando = false; continuar.disabled = false; }
        }
        // Las peticiones normales renuevan en el servidor; sólo observamos su respuesta.
        window.fetch = async (...args) => {
            const response = await fetchOriginal(...args);
            const ahora = response.headers.get('X-Session-Now');
            const fin = response.headers.get('X-Session-Expires');
            if (ahora && fin) sincronizar(Number(ahora), Number(fin));
            if (response.status === 401) consultar();
            return response;
        };
        canal?.addEventListener('message', event => {
            if (event.data?.cerrada) consultar();
            else if (event.data) sincronizar(event.data.ahora, event.data.fin, false);
        });
        indicador.addEventListener('click', () => { abrir(); consultar(); });
        continuar.addEventListener('click', () => { if (!renovando) consultar(Boolean(csrfToken)); });
        modal.querySelector('[data-session-exit]').addEventListener('click', () => {
            if (!csrfToken) { mensaje.textContent = 'Consulta la sesión antes de cerrar. Intenta Continuar sesión.'; return; }
            const form = document.createElement('form');
            form.method = 'post'; form.action = '/logout';
            for (const [name, value] of [['_csrf', csrfToken], ['origen', origen]]) {
                const input = document.createElement('input'); input.type = 'hidden'; input.name = name; input.value = value;
                form.appendChild(input);
            }
            document.body.appendChild(form); form.submit();
        });
        document.addEventListener('visibilitychange', () => { if (!document.hidden) consultar(); });
        window.addEventListener('focus', () => consultar());
        window.addEventListener('pageshow', event => { if (event.persisted) consultar(); });
        setInterval(actualizar, 1000);
        setInterval(() => { if (!document.hidden && !saliendo) consultar(); }, 5000);
        consultar();
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', iniciar);
    else iniciar();
})();
