(() => {
 document.body.classList.add('sv-ready');
 const button=document.querySelector('.sv-menu-button'),nav=document.querySelector('.sv-nav');
 if(button&&nav){button.hidden=false;button.addEventListener('click',()=>{const open=nav.classList.toggle('is-open');button.setAttribute('aria-expanded',String(open));});nav.addEventListener('click',e=>{if(e.target.closest('a')){nav.classList.remove('is-open');button.setAttribute('aria-expanded','false');}});}
 if(!matchMedia('(prefers-reduced-motion: reduce)').matches&&'IntersectionObserver' in window){document.body.classList.add('sv-motion-ready');const observer=new IntersectionObserver(entries=>entries.forEach(e=>{if(e.isIntersecting){e.target.classList.add('sv-in-view');observer.unobserve(e.target);}}),{threshold:.05});document.querySelectorAll('[data-animacion]').forEach(b=>observer.observe(b));}
})();
