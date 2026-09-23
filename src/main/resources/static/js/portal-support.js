(function () {
    'use strict';
    const body = document.body;
    if (!body.classList.contains('support-mode')) return;
    const tutorId = body.dataset.supportTutor;
    if (!tutorId) return;
    document.addEventListener('click', function (event) {
        const link = event.target.closest('a[href^="/portal?"]');
        if (!link) return;
        event.preventDefault();
        const query = link.getAttribute('href').substring('/portal?'.length);
        window.location.assign('/admin/portal-soporte/' + encodeURIComponent(tutorId) + '?' + query);
    });
})();
