(() => {
 'use strict';
 document.querySelectorAll('.sidebar nav a').forEach(a=>a.addEventListener('click',e=>e.preventDefault()));
 const dialog=document.getElementById('pi-demo-confirm');
 document.getElementById('pi-demo-open').addEventListener('click',()=>dialog.showModal());
 document.querySelectorAll('[data-demo-close]').forEach(b=>b.addEventListener('click',()=>dialog.close()));
 document.querySelector('.pi-demo-menu').addEventListener('click',()=>{document.documentElement.dataset.demoMenu=String(document.documentElement.dataset.demoMenu!=='true');});
})();
