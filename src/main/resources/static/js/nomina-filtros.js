/* Consultas de Personal y nómina: filtrar y limpiar sin perder el lugar de trabajo. */
(() => {
 document.querySelectorAll('[data-nomina-keep-filter]').forEach(form=>{
  const prefix='nomina-filtros-scroll:';
  const key=path=>prefix+path+':'+form.dataset.nominaKeepFilter;
  const target=new URL(form.getAttribute('action')||location.href,location.href);
  const clearLinks=Array.from(form.querySelectorAll('a')).filter(link=>link.textContent.trim()==='Limpiar');
  const save=(path,clear=false)=>{
   try{sessionStorage.setItem(key(path),JSON.stringify({offset:form.getBoundingClientRect().top,clear}));}catch(_){/* El GET continúa aunque el navegador no permita almacenamiento. */}
  };
  form.addEventListener('submit',()=>save(target.pathname));
  clearLinks.forEach(link=>{
   link.setAttribute('data-nomina-clear-filter','');
   link.addEventListener('click',event=>{
    if(event.defaultPrevented||event.ctrlKey||event.metaKey||event.shiftKey||event.altKey||(event.button!=null&&event.button!==0)||link.target==='_blank')return;
    const url=new URL(link.href,location.href);
    if(url.origin!==location.origin||url.pathname!==target.pathname)return;
    const panel=form.closest('[data-student-panel]');
    if(panel&&!url.hash){
     const tab=document.querySelector('[data-student-tab="'+panel.dataset.studentPanel+'"]');
     url.hash=tab?.dataset.tabHash||location.hash;
     link.href=url.href;
    }
    save(url.pathname,true);
    if(url.pathname===location.pathname&&url.search===location.search){
     event.preventDefault();
     if(url.hash!==location.hash)history.replaceState(null,'',url.href);
     window.location.reload();
    }
   });
  });
  let saved;try{const raw=sessionStorage.getItem(key(location.pathname));sessionStorage.removeItem(key(location.pathname));if(raw)saved=JSON.parse(raw);}catch(_){}
  if(!saved||!Number.isFinite(saved.offset))return;
  let restoreScheduled=false,userInteracted=false;
  const cancelRestore=()=>{userInteracted=true;};
  // Una carga puede disparar load y pageshow: nunca volver a mover al usuario.
  ['wheel','touchstart','pointerdown'].forEach(type=>window.addEventListener(type,cancelRestore,{passive:true,once:true}));
  window.addEventListener('keydown',event=>{
   if(['ArrowUp','ArrowDown','PageUp','PageDown','Home','End',' '].includes(event.key))cancelRestore();
  });
  const restore=()=>{
   if(restoreScheduled)return;
   restoreScheduled=true;
   requestAnimationFrame(()=>requestAnimationFrame(()=>{
    if(userInteracted||form.closest('[data-student-panel]')?.hidden)return;
    window.scrollTo({top:Math.max(0,window.scrollY+form.getBoundingClientRect().top-saved.offset),behavior:'instant'});
    const control=saved.clear?clearLinks[0]:form.querySelector('[data-nomina-filter-error]')||form.querySelector('button[type="submit"],.module-action--primary,button:not([type])');
    control?.focus({preventScroll:true});
   }));
  };
  if(document.readyState==='complete')restore();else window.addEventListener('load',restore,{once:true});
  window.addEventListener('pageshow',event=>{if(!event.persisted)restore();},{once:true});
 });
})();
